package com.revolut.currencyconverter.injection


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.revolut.currencyconverter.viewmodel.ConversionListViewModel

class ViewModelFactory: ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConversionListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConversionListViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }
}