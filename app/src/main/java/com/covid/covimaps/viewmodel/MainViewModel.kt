package com.covid.covimaps.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.covid.covimaps.data.model.room.CovidLocation
import com.covid.covimaps.data.model.room.LocalDatabase
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "MainViewModel"

@HiltViewModel
class MainViewModel @Inject constructor(private val localDatabase: LocalDatabase) : ViewModel() {

    private lateinit var covidLocations: MutableList<CovidLocation>
    var covidMap: MutableMap<Int, CovidLocation> = mutableMapOf()
    var survey: MutableMap<String, String> = mutableMapOf()
    lateinit var first: LatLng

    var countries: MutableList<String> = mutableListOf()
    val cities: MutableMap<String, MutableList<String>> = mutableMapOf()
    suspend fun getLocations() =
        viewModelScope.async(Dispatchers.IO) {
            covidLocations = localDatabase.covidLocationDao().getCovidLocations().toMutableList()
            withContext(Dispatchers.Default) {
                var index = 1
                covidLocations.forEach {
                    covidMap[index] = it
                    index++
                }
            }
            first = LatLng(covidMap[1]?.latitude!!, covidMap[1]?.longitude!!)
            covidMap
        }.await()

    suspend fun getCountries() {
        countries = viewModelScope.async(Dispatchers.IO) {
            localDatabase.countriesAndCitiesDao().getCountries().distinct().toMutableList()
        }.await()
        Log.d(TAG, "getCountries: countries = $countries")
        countries.forEach {
            viewModelScope.launch {
                getCities(country = it)
            }
        }
    }

    private suspend fun getCities(country: String) {
        withContext(Dispatchers.IO) {
            cities[country] =
                localDatabase.countriesAndCitiesDao().getCities(country = country).distinct()
                    .toMutableList()
        }
    }
}