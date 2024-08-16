package com.covid.covimaps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.covid.covimaps.data.model.room.CovidLocation
import com.covid.covimaps.data.model.room.LocalDatabase
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "MainViewModel"

@HiltViewModel
class MainViewModel @Inject constructor(private val localDatabase: LocalDatabase) : ViewModel() {

    private lateinit var covidLocations: MutableList<CovidLocation>
    var covidMap: MutableMap<Int, CovidLocation> = mutableMapOf()
    lateinit var first: LatLng
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
}