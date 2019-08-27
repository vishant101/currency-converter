package com.revolut.currencyconverter.viewmodel

import android.util.Log
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
import android.text.Editable
import android.text.TextWatcher
import android.widget.AdapterView
import android.widget.Toast
import com.revolut.currencyconverter.interfaces.CurrencyChangeListener
import com.revolut.currencyconverter.interfaces.OnItemClickListener
import com.revolut.currencyconverter.interfaces.TextChangedListener
import com.revolut.currencyconverter.utils.DEFAULT_CURRENCY
import com.revolut.currencyconverter.utils.DEFAULT_CURRENCY_VALUE
import java.security.AccessController.getContext
import kotlin.math.log

class ConversionListViewModel:BaseViewModel(){
    @Inject
    lateinit var postApi: ConversionApi
    val conversionListAdapter: ConversionListAdapter = ConversionListAdapter()


    val loadingVisibility: MutableLiveData<Int> = MutableLiveData()
    val errorMessage:MutableLiveData<Int> = MutableLiveData()
    val errorClickListener = View.OnClickListener { loadRates() }

    private var selectedCurrency: String = DEFAULT_CURRENCY
    private var selectedValue: Double = DEFAULT_CURRENCY_VALUE

    private lateinit var subscription: Disposable

    init{
        // startLoadingRates()
        loadRates()
        val listener = Listener()
        val textListener = CurrencyListener()
        conversionListAdapter.updateTextListener(textListener)
        conversionListAdapter.updatePostListener(listener)

    }

    fun updateSelectedCurrency(currency: String){
        selectedCurrency = currency
    }

    fun updateSelectedValue(value: Double){
        selectedValue = value
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
                .concatMap { postApi.getLatest(selectedCurrency) }
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

    inner class Listener : OnItemClickListener {
        override fun onItemClick(conversionRate: ConversionRate) {
            updateSelectedCurrency(conversionRate.currency)
        }
    }

    inner class CurrencyListener : CurrencyChangeListener {
        override fun currecyUpdated(conversionRate: ConversionRate, s: CharSequence) {s
            Log.i("UPDATE", conversionRate.currency + " : " + s )
        }
    }


    private fun onRetrievePostListSuccess(results: LatestConversionRates){
        val resultsMap: Map<String, Double> = results.rates as Map<String, Double>
        val conversionList = mutableListOf<ConversionRate>()
        val defaultRate = ConversionRate(selectedCurrency, selectedValue)
        conversionList.add(defaultRate)
        for ((k, v) in resultsMap) {
            val conversionRate =  ConversionRate(k,v)
            conversionList.add(conversionRate)
        }

        conversionListAdapter.updatePostList(conversionList)
    }

    private fun onRetrievePostListError(){
        errorMessage.value = com.revolut.currencyconverter.R.string.post_error
    }
}