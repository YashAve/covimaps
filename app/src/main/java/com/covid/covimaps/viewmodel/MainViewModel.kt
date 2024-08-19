package com.covid.covimaps.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.covid.covimaps.data.model.local.CovidSymptoms
import com.covid.covimaps.data.model.local.FirebaseCovidUiState
import com.covid.covimaps.data.model.retrofit.CovidGeocodes
import com.covid.covimaps.data.model.room.CovidLocation
import com.covid.covimaps.data.model.room.LocalDatabase
import com.covid.covimaps.data.repository.remote.CovidLocationsManager
import com.covid.covimaps.data.repository.remote.FirebaseFirestoreRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "MainViewModel"

@HiltViewModel
class MainViewModel @Inject constructor(
    private val localDatabase: LocalDatabase,
    private val locationsManager: CovidLocationsManager,
    private val firebaseFirestoreRepository: FirebaseFirestoreRepository,
) : ViewModel() {

    private lateinit var covidLocations: MutableList<CovidLocation>
    var covidMap: MutableMap<Int, CovidLocation> = mutableMapOf()
    var survey: MutableMap<String, String> = mutableMapOf()
    private var geocodes: CovidGeocodes? = null
    lateinit var first: LatLng

    var countries: MutableList<String> = mutableListOf()
    val cities: MutableMap<String, MutableList<String>> = mutableMapOf()
    suspend fun getLocations() =
        viewModelScope.async(Dispatchers.IO) {
            covidLocations = localDatabase.covidLocationDao().getCovidLocations().toMutableList()

            firebaseFirestoreRepository.collection.get()
                .addOnSuccessListener {
                    it.forEach { document ->
                        val firebaseCovidUiState =
                            document.toObject(FirebaseCovidUiState::class.java)
                        val covidLocation = CovidLocation(
                            state = firebaseCovidUiState.country,
                            district = firebaseCovidUiState.city,
                            latitude = firebaseCovidUiState.latitude,
                            longitude = firebaseCovidUiState.longitude,
                            totalDeceased = 0,
                            totalRecovered = 0,
                            totalCovishields = 0,
                            totalCovaxin = 0,
                            deceased = 0,
                            recovered = firebaseCovidUiState.covishield + firebaseCovidUiState.covaxin,
                            covishields = firebaseCovidUiState.covishield,
                            covaxin = firebaseCovidUiState.covaxin
                        )
                        covidLocations.add(covidLocation)
                    }
                    Log.d(TAG, "getLocations: normal called")
                    var index = 1
                    covidLocations.forEach { covidLocation ->
                        covidMap[index] = covidLocation
                        index++
                    }
                    first = LatLng(covidMap[1]?.latitude!!, covidMap[1]?.longitude!!)
                    covidMap
                }
                .addOnFailureListener {

                }
            covidLocations
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

    suspend fun restructure() {
        var vaccinated = false
        var (covishield, covaxin) = arrayOf(0, 0)
        var (country, city) = arrayOf("", "")
        survey.entries.forEach {
            when (it.key) {
                CovidSymptoms.VACCINATED_OR_NOT.symptom -> {
                    vaccinated = it.value == "Yes"
                }

                CovidSymptoms.WHICH_VACCINATION.symptom -> {
                    covishield = if (it.value == "Covishield") 1 else 0
                    covaxin = if (it.value == "Covaxin") 1 else 0
                }

                "country" -> {
                    country = it.value
                }

                "city" -> {
                    city = it.value
                }
            }
        }

        geocodes = viewModelScope.async(Dispatchers.IO) {
            locationsManager.getGeocode(country = country, city = city)
        }.await()

        val latitude = geocodes?.results?.get(0)?.geometry?.location?.lat ?: 0.0
        val longitude = geocodes?.results?.get(0)?.geometry?.location?.lng ?: 0.0

        val firebaseCovidUiState = FirebaseCovidUiState(
            country = country,
            city = city,
            vaccinated = vaccinated,
            covishield = covishield,
            covaxin = covaxin,
            latitude = latitude,
            longitude = longitude
        )

        viewModelScope.async(Dispatchers.IO) {
            firebaseFirestoreRepository.covidState = firebaseCovidUiState
            firebaseFirestoreRepository.retrieve(firebaseCovidUiState.city)
        }.await()

        Log.d(TAG, "restructure: $firebaseCovidUiState")
    }
}