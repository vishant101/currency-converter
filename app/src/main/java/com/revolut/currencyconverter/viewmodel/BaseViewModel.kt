package com.revolut.currencyconverter.viewmodel

import androidx.lifecycle.ViewModel
import com.revolut.currencyconverter.injection.DaggerViewModelInjector
import com.revolut.currencyconverter.injection.NetworkModule
import com.revolut.currencyconverter.injection.ViewModelInjector

abstract class BaseViewModel: ViewModel(){
    private val injector: ViewModelInjector = DaggerViewModelInjector
        .builder()
        .networkModule(NetworkModule)
        .build()

    init {
        inject()
    }

    private fun inject() {
        when (this) {
            is ConversionListViewModel -> injector.inject(this)
            is CurrencyItemViewModel -> injector.inject(this)
        }
    }
}