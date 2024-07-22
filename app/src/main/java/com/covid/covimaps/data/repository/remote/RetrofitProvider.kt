package com.covid.covimaps.data.repository.remote

import com.covid.covimaps.data.model.remote.COVID_DATA_URL
import com.covid.covimaps.data.model.remote.CovidGeocodes
import com.google.gson.JsonObject
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule {

    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl(COVID_DATA_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

interface APIService {

    @GET("data.min.json")
    fun getCovidData(): Call<JsonObject>

    @GET("geocode/json")
    suspend fun getGeocodeResponse(
        @retrofit2.http.Query("address") address: String,
        @retrofit2.http.Query("key") key: String
    ): CovidGeocodes
}