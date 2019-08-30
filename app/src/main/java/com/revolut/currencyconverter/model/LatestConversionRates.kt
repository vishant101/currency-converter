package com.revolut.currencyconverter.model

data class LatestConversionRates (
    var base: String,
    var date: String,
    var rates: Map<String, Float>
)