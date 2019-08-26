package com.revolut.currencyconverter.models

import com.google.gson.annotations.SerializedName

data class LatestResults (
    @SerializedName("base")  var base: String,
    @SerializedName("date")  var date: String,
    @SerializedName("rates")  var rates: Any
) {
    init {
        this.base
        this.date
        this.rates
    }
}