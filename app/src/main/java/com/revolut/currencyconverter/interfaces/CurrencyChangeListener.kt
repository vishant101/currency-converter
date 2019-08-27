package com.revolut.currencyconverter.interfaces

import com.revolut.currencyconverter.model.ConversionRate

interface CurrencyChangeListener {
    fun currencyUpdated(conversionRate: ConversionRate, s: CharSequence)
}