package com.revolut.currencyconverter.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.revolut.currencyconverter.adapters.ConversionListAdapter
import com.revolut.currencyconverter.model.ConversionRate

import com.revolut.currencyconverter.model.LatestConversionRates
import com.revolut.currencyconverter.network.ConversionApi
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import javax.inject.Inject
import android.os.Handler
import com.revolut.currencyconverter.interfaces.CurrencyChangeListener
import com.revolut.currencyconverter.interfaces.CurrencyFocusListener
import com.revolut.currencyconverter.interfaces.OnItemClickListener
import com.revolut.currencyconverter.utils.DEFAULT_CURRENCY
import com.revolut.currencyconverter.utils.DEFAULT_CURRENCY_VALUE
import java.math.RoundingMode


class ConversionListViewModel:BaseViewModel(){
    @Inject
    lateinit var postApi: ConversionApi
    val conversionListAdapter: ConversionListAdapter = ConversionListAdapter()


    val loadingVisibility: MutableLiveData<Int> = MutableLiveData()
    val errorMessage:MutableLiveData<Int> = MutableLiveData()
    val errorClickListener = View.OnClickListener { loadRates() }

    private var selectedConversionRate = ConversionRate(DEFAULT_CURRENCY, DEFAULT_CURRENCY_VALUE)
    private var focusedCurrency = DEFAULT_CURRENCY

    private lateinit var subscription: Disposable

    init{
        startLoadingRates()
        // loadRates()
        val listener = Listener()
        val textListener = CurrencyListener()
        val focusListener = CurrencyFocusChangeListener()
        conversionListAdapter.updateTextListener(textListener)
        conversionListAdapter.updateOnClickListener(listener)
        conversionListAdapter.updateFocusListener(focusListener)
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
        errorMessage.value = com.revolut.currencyconverter.R.string.post_error
    }

    inner class Listener : OnItemClickListener {
        override fun onItemClick(conversionRate: ConversionRate) {
            selectedConversionRate = conversionRate
        }
    }

    inner class CurrencyListener : CurrencyChangeListener {
        override fun currencyUpdated(conversionRate: ConversionRate, s: CharSequence) {
            if (conversionRate.currency == focusedCurrency) {
                if (conversionRate.currency == selectedConversionRate.currency) {
                    selectedConversionRate = ConversionRate(selectedConversionRate.currency, s.toString().toDouble())
                } else {
                    selectedConversionRate = conversionRate
                }
            }
        }
    }

    inner class CurrencyFocusChangeListener : CurrencyFocusListener {
        override fun currencyFocusChanged(conversionRate: ConversionRate, isFocused: Boolean) {
            if (isFocused) {
                focusedCurrency = conversionRate.currency
                selectedConversionRate = conversionRate
            }
        }
    }
}