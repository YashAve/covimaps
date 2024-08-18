package com.covid.covimaps.data.repository.remote

import android.util.Log
import com.covid.covimaps.data.model.retrofit.APIService
import com.covid.covimaps.data.model.retrofit.StatesAndCitiesData
import com.covid.covimaps.data.model.room.CountryAndCity
import com.covid.covimaps.data.model.room.LocalDatabase
import com.covid.covimaps.di.StatesAndCitiesBaseUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import javax.inject.Inject

const val STATES_AND_CITIES_BASE_URL = "https://countriesnow.space/api/v0.1/"

private const val TAG = "StatesAndCitiesManager"

class StatesAndCitiesManager @Inject constructor(
    @StatesAndCitiesBaseUrl private val retrofitStatesAndCities: Retrofit,
    private val localDatabase: LocalDatabase,
) {

    private var countries: StatesAndCitiesData? = null

    suspend fun init() {
        coroutineScope { async { populate() }.await() }
    }

    private suspend fun populate() {
        withContext(Dispatchers.IO) {
            if (localDatabase.countriesAndCitiesDao().getCount() == 0) {
                val service = retrofitStatesAndCities.create(APIService::class.java)
                try {
                    countries = service.getStatesAndCities()
                    save()
                } catch (e: Exception) {
                    Log.d(TAG, "populate: ${e.message}")
                }
            }
        }
    }

    private suspend fun save() {
        val cities: MutableList<CountryAndCity> = mutableListOf()
        var countryAndCity: CountryAndCity
        withContext(Dispatchers.Default) {
            async {
                countries?.let {
                    it.data.forEach { country ->
                        country.cities.forEach { city ->
                            countryAndCity =
                                CountryAndCity(city = city, country = country.country)
                            cities.add(countryAndCity)
                        }
                    }
                }
                withContext(Dispatchers.IO) {
                    localDatabase.countriesAndCitiesDao().insertAll(*cities.toTypedArray())
                }
                Log.d(TAG, "save: data saved")
            }.await()
        }
    }
}