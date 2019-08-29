package com.revolut.currencyconverter.viewmodel

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.revolut.currencyconverter.interfaces.OnItemClickListener
import com.revolut.currencyconverter.model.ConversionRate
import com.revolut.currencyconverter.utils.DEFAULT_LONG_CURRENCY_PLACEHOLDER

class CurrencyItemViewModel:BaseViewModel() {
    private val currency = MutableLiveData<String>()
    private val currencyLong = MutableLiveData<String>()
    private val currencyValue = MutableLiveData<String>()
    private val position = MutableLiveData<Int>()
    private lateinit var conversionRate: ConversionRate
    private lateinit var onClickListener: View.OnClickListener
    private lateinit var valueWatcher: TextWatcher

    fun bind(conversionRate: ConversionRate, position: Int, onClickListener: OnItemClickListener, valueWatcher: TextWatcher){
        currency.value = conversionRate.currency
        currencyValue.value = "%.2f".format(conversionRate.rate)
        currencyLong.value = DEFAULT_LONG_CURRENCY_PLACEHOLDER
        this.position.value = position
        this.conversionRate = conversionRate
        this.onClickListener = View.OnClickListener { onClickListener.onItemClick(conversionRate) }
        this.valueWatcher = valueWatcher

    }

    fun getCurrency():MutableLiveData<String>{
        return currency
    }

    fun getLongCurrency():MutableLiveData<String>{
        return currencyLong
    }

    fun getValue(): MutableLiveData<String> {
        return currencyValue
    }

    fun getClickListener(): View.OnClickListener{
        return onClickListener
    }

    fun getIsEditTextEnabled(): Boolean {
        return position.value == 0
    }

    fun getTextWatcher(): TextWatcher{
        return if (position.value == 0){
            valueWatcher
        } else {
            return object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(newValue: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(newValue: Editable?) {}
            }
        }
    }
}