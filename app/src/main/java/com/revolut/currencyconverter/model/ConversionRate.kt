package com.revolut.currencyconverter.model

import androidx.room.Entity

@Entity
data class ConversionRate (
    val currency: String,
    val rate: Double
)