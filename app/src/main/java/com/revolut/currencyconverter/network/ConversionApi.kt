package com.revolut.currencyconverter.network

import com.revolut.currencyconverter.model.LatestConversionRates
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * The interface which provides methods to get result of webservices
 */
interface ConversionApi {
    /**
     * Get the list of the pots from the API
     */
    @GET("/latest")
    fun getLatest(@Query("base") base: String): Observable<LatestConversionRates>
}