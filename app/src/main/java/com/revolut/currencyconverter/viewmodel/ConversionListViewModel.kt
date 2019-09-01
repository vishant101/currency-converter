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
import javax.inject.Inject


class ConversionListViewModel:BaseViewModel() {
    @Inject
    lateinit var postApi: ConversionApi
    val conversionListAdapter: ConversionListAdapter = ConversionListAdapter()

    val loadingVisibility: MutableLiveData<Int> = MutableLiveData()
    val errorMessage:MutableLiveData<Int> = MutableLiveData()
    val errorClickListener = View.OnClickListener { loadRates() }

    private val rateObject = Object()
    private val currencyRates: MutableLiveData<List<ConversionRate>> = MutableLiveData()
    private var baseCurrency: String = DEFAULT_CURRENCY
    private var baseValue: Float = DEFAULT_CURRENCY_VALUE

    private lateinit var subscription: Disposable

    init{
        startLoadingRates()
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
                .concatMap { postApi.getLatest(baseCurrency) }
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
        errorMessage.value = null
    }

    private fun onRetrievePostListFinish(){
        loadingVisibility.value = View.GONE
    }

    private fun onRetrievePostListSuccess(results: LatestConversionRates){
        val conversionList: MutableList<ConversionRate> = mutableListOf()
        synchronized(rateObject){
            conversionList.add(ConversionRate(results.base, 1.0F, baseValue))
            results.rates.forEach { (currency, rate) ->
                conversionList.add(ConversionRate(currency, rate, rate*baseValue))
            }
            currencyRates.value = conversionList
        }
        conversionListAdapter.updateCurrencyList(conversionList)
    }

    private fun onRetrievePostListError(){
        // errorMessage.value = com.revolut.currencyconverter.R.string.post_error
    }

    private fun updateBaseValue(value: Float) {
        if (baseValue.equals(value)) return
        synchronized(rateObject) {
            baseValue = value
            val newCurrencyRates: MutableList<ConversionRate> = mutableListOf()
            currencyRates.value?.forEach { newCurrencyRates.add(ConversionRate(it.currency,it.rate,it.rate*baseValue))}
            currencyRates.value = newCurrencyRates
        }
    }

    private fun updateBase(newBaseCurrency: String, newBaseValue: Float) {
        if (baseCurrency == newBaseCurrency) return
        baseCurrency = newBaseCurrency
        baseValue = newBaseValue
        loadRates()
    }

    inner class Listener : OnItemClickListener {
        override fun onItemClick(conversionRate: ConversionRate) {
            updateBase(conversionRate.currency, conversionRate.value)
            conversionListAdapter.scrollTo.set(0)
        }
    }

    inner class ValueWatcher : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(newValue: CharSequence, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(newValue: Editable) {
            val rate =  if (newValue.isEmpty()) 0.0F else newValue.toString().toFloat()
            conversionListAdapter.updateBaseValue(rate)
            updateBaseValue(rate)

        }
    }
}