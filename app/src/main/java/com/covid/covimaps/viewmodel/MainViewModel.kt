package com.covid.covimaps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.covid.covimaps.data.model.room.CovidLocation
import com.covid.covimaps.data.model.room.LocalDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject

private const val TAG = "MainViewModel"

@HiltViewModel
class MainViewModel @Inject constructor(private val localDatabase: LocalDatabase) : ViewModel() {

    lateinit var covidLocations: MutableList<CovidLocation>
    suspend fun getLocations() =
        viewModelScope.async(Dispatchers.IO) {
            localDatabase.covidLocationDao().getCovidLocations().toMutableList()
        }.await()
}