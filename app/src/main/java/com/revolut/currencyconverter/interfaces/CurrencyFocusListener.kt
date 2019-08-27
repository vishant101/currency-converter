package com.revolut.currencyconverter.interfaces

import com.revolut.currencyconverter.model.ConversionRate

interface CurrencyFocusListener {
    fun currencyFocusChanged(conversionRate: ConversionRate, isFocused: Boolean)
}