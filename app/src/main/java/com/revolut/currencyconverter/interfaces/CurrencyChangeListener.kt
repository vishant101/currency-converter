package com.revolut.currencyconverter.interfaces

import com.revolut.currencyconverter.model.ConversionRate

interface CurrencyChangeListener {
    fun currecyUpdated(conversionRate: ConversionRate, s: CharSequence)
}