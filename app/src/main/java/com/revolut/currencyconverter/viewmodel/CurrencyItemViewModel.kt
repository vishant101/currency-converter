package com.revolut.currencyconverter.viewmodel

import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.revolut.currencyconverter.interfaces.OnItemClickListener
import com.revolut.currencyconverter.model.ConversionRate

class CurrencyItemViewModel:BaseViewModel() {
    private val currency = MutableLiveData<String>()
    private val currencyValue = MutableLiveData<String>()
    private lateinit var onClickListener: View.OnClickListener
    private lateinit var onTextChangeLister: TextWatcher

    fun bind(conversionRate: ConversionRate, listener: OnItemClickListener){
        currency.value = conversionRate.currency
        currencyValue.value = conversionRate.rate.toString()
        onClickListener = View.OnClickListener { listener.onItemClick(conversionRate) }
    }

    fun getCurrency():MutableLiveData<String>{
        return currency
    }

    fun getValue(): MutableLiveData<String> {
        return currencyValue
    }

    fun getPostListener(): View.OnClickListener{
        return onClickListener
    }

    fun getTextChangedListener(): TextWatcher{
        return onTextChangeLister
    }
}