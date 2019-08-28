package com.revolut.currencyconverter.viewmodel

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.revolut.currencyconverter.interfaces.CurrencyChangeListener
import com.revolut.currencyconverter.interfaces.CurrencyFocusListener
import com.revolut.currencyconverter.interfaces.OnItemClickListener
import com.revolut.currencyconverter.model.ConversionRate

class CurrencyItemViewModel:BaseViewModel() {
    private val currency = MutableLiveData<String>()
    private val currencyValue = MutableLiveData<String>()
    private lateinit var conversionRate: ConversionRate
    private lateinit var onClickListener: View.OnClickListener
    private lateinit var onTextChangeListener: CurrencyChangeListener
    private lateinit var focusListener: CurrencyFocusListener

    fun bind(conversionRate: ConversionRate, onClickListener: OnItemClickListener, textChangedListener: CurrencyChangeListener){
        currency.value = conversionRate.currency
        currencyValue.value = conversionRate.rate.toString()
        this.conversionRate = conversionRate
        this.onClickListener = View.OnClickListener { onClickListener.onItemClick(conversionRate) }
        onTextChangeListener = textChangedListener
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

    fun onTextChanged(s: CharSequence,start: Int,before : Int, count :Int){
        onTextChangeListener.currencyUpdated(conversionRate, s)
    }
}