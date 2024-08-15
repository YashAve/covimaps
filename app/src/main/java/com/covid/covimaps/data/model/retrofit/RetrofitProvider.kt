package com.covid.covimaps.data.model.retrofit

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET

/*
    public Builder baseUrl(String baseUrl) {
      Objects.requireNonNull(baseUrl, "baseUrl == null");
      return baseUrl(HttpUrl.get(baseUrl));
    }
*/

interface APIService {

    @GET("data.min.json")
    fun getCovidData(): Call<JsonObject>

    @GET("geocode/json")
    suspend fun getGeocodeResponse(
        @retrofit2.http.Query("address") address: String,
        @retrofit2.http.Query("key") key: String,
    ): CovidGeocodes

    @GET("all")
    suspend fun getCountryCodes(): MutableList<CountryCodes>

    @GET("countries")
    suspend fun getStatesAndCities(): StatesAndCitiesData
}