package com.revolut.currencyconverter.viewmodel

import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.revolut.currencyconverter.adapters.ConversionListAdapter
import com.revolut.currencyconverter.interfaces.OnItemClickListener
import com.revolut.currencyconverter.model.ConversionRate
import com.revolut.currencyconverter.model.LatestConversionRates
import com.revolut.currencyconverter.network.ConversionApi
import com.revolut.currencyconverter.utils.DEFAULT_CURRENCY
import com.revolut.currencyconverter.utils.DEFAULT_CURRENCY_VALUE
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.math.RoundingMode
import javax.inject.Inject


class ConversionListViewModel:BaseViewModel(){
    @Inject
    lateinit var postApi: ConversionApi
    val conversionListAdapter: ConversionListAdapter = ConversionListAdapter()

    val loadingVisibility: MutableLiveData<Int> = MutableLiveData()
    val errorMessage:MutableLiveData<Int> = MutableLiveData()
    val errorClickListener = View.OnClickListener { loadRates() }

    private var selectedConversionRate = ConversionRate(DEFAULT_CURRENCY, DEFAULT_CURRENCY_VALUE)
    private lateinit var subscription: Disposable

    init{
        startLoadingRates()
        // loadRates()
        val listener = Listener()
        val valueWatcher = ValueWatcher()
        conversionListAdapter.updateOnClickListener(listener)
        conversionListAdapter.updateValueWatcher(valueWatcher)
    }

    private fun startLoadingRates(){
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                loadRates()
                handler.postDelayed(this, 1000)
            }
        }
        handler.postDelayed(runnable, 1000)
    }

    override fun onCleared() {
        super.onCleared()
        subscription.dispose()
    }

    private fun loadRates(){
        subscription = Observable.fromCallable { }
                .concatMap { postApi.getLatest(selectedConversionRate.currency) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { onRetrievePostListStart() }
                .doOnTerminate { onRetrievePostListFinish() }
                .subscribe(
                        { result -> onRetrievePostListSuccess(result) },
                        { onRetrievePostListError() }
                )
    }

    private fun onRetrievePostListStart(){
        // loadingVisibility.value = View.VISIBLE
        errorMessage.value = null
    }

    private fun onRetrievePostListFinish(){
        loadingVisibility.value = View.GONE
    }

    private fun onRetrievePostListSuccess(results: LatestConversionRates){
        val resultsMap: Map<String, Double> = results.rates as Map<String, Double>
        val conversionList = mutableListOf<ConversionRate>()
        conversionList.add(selectedConversionRate)
        for ((k, v) in resultsMap) {
            val conversionRate =  ConversionRate(k,calculateExchangeValue(v))
            conversionList.add(conversionRate)
        }

        conversionListAdapter.updateCurrencyList(conversionList)
    }

    private fun calculateExchangeValue(value: Double): Double{
        val exchangeValue = value * selectedConversionRate.rate
        return exchangeValue.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
    }

    private fun onRetrievePostListError(){
        // errorMessage.value = com.revolut.currencyconverter.R.string.post_error
    }

    inner class Listener : OnItemClickListener {
        override fun onItemClick(conversionRate: ConversionRate) {
            selectedConversionRate = conversionRate
        }
    }


    inner class ValueWatcher : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(newValue: CharSequence, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(newValue: Editable) {
            val rate =  if (newValue.isEmpty()) 0.0 else newValue.toString().toDouble()
            selectedConversionRate = ConversionRate(selectedConversionRate.currency, rate)
        }
    }
}