package com.revolut.currencyconverter.injection

import com.revolut.currencyconverter.viewmodel.ConversionListViewModel
import com.revolut.currencyconverter.viewmodel.CurrencyItemViewModel
import dagger.Component
import javax.inject.Singleton

/**
 * Component providing inject() methods for presenters.
 */
@Singleton
@Component(modules = [(NetworkModule::class)])
interface ViewModelInjector {

    fun inject(conversionListViewModel: ConversionListViewModel)

    fun inject(currencyItemViewModel:  CurrencyItemViewModel)

    @Component.Builder
    interface Builder {
        fun build(): ViewModelInjector

        fun networkModule(networkModule: NetworkModule): Builder
    }
}