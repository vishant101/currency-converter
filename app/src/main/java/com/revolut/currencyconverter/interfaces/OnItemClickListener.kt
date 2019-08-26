package com.revolut.currencyconverter.interfaces
import com.revolut.currencyconverter.model.ConversionRate

interface OnItemClickListener {
    fun onItemClick(conversionRate: ConversionRate)
}