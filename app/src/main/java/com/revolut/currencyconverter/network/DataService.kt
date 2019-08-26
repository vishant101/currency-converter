package com.revolut.currencyconverter.network

import com.revolut.currencyconverter.model.LatestResults
import com.revolut.currencyconverter.utils.BASE_URL
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface DataService {

    @GET("latest")
    fun getLatest(@Query("base") base: String): Observable<LatestResults>

    companion object Factory {
        fun create(): DataService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(DataService::class.java)
        }
    }
}