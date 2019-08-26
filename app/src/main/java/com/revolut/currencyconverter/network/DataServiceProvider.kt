package com.revolut.currencyconverter.network

object DataServiceProvider {
    private var apiService = DataService.create()

    fun provideDataService(): DataService {
        return apiService
    }
}