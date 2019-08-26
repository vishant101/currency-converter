package com.revolut.currencyconverter.network

object DataServiceProvider {
    var apiService = DataService.create()

    fun provideDataService(): DataService {
        return apiService
    }
}