package com.covid.covimaps.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.covid.covimaps.BuildConfig
import com.covid.covimaps.data.model.local.room.CovidLocation
import com.covid.covimaps.data.model.remote.CovidDataUiState
import com.covid.covimaps.data.model.remote.DistrictUiState
import com.covid.covimaps.data.model.remote.GEOCODE_URL
import com.covid.covimaps.data.model.remote.Stats
import com.covid.covimaps.data.model.remote.statesMapping
import com.covid.covimaps.data.repository.local.DatabaseProvider
import com.covid.covimaps.data.repository.remote.APIService
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

private const val TAG = "MainViewModel"

suspend fun insertDatabase(covidLocation: CovidLocation, context: Context) {
    withContext(Dispatchers.IO) {
        launch {
            DatabaseProvider.getDatabase(context).covidLocationDao().insertAll(covidLocation)
        }
    }
}

suspend fun retrieveLocations(context: Context) =
    withContext(Dispatchers.IO) {
        DatabaseProvider.getDatabase(context).covidLocationDao().getCovidLocations()
    }

@HiltViewModel
class MainViewModel @Inject constructor(private val retrofit: Retrofit) : ViewModel() {

    private lateinit var json: Response<JsonObject>
    private lateinit var covidDataUiStates: MutableList<CovidDataUiState>

    suspend fun getCovidDataUiState() =
        viewModelScope.async { createData() }.await()

    suspend fun getLocations() =
        viewModelScope.async { getCovidGeocode() }.await()

    private suspend fun covidResponse() {
        withContext(Dispatchers.IO) {
            val service = retrofit.create(APIService::class.java)
            try {
                val response = service.getCovidData()
                json = response.execute()
            } catch (e: Exception) {
                Log.e(TAG, "getCovidResponse: ${e.message}")
            }
        }
    }

    private suspend fun getCovidGeocode() =
        withContext(Dispatchers.IO) {
            val geocodeRetrofit = Retrofit
                .Builder()
                .baseUrl(GEOCODE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = geocodeRetrofit.create(APIService::class.java)
            val requests: MutableList<Deferred<CovidLocation?>> = mutableListOf()
            covidDataUiStates.subList(0, 1).forEach { state ->
                state.districts.forEach {
                    requests.add(async {
                        var covidLocation: CovidLocation? = null
                        try {
                            val response = service.getGeocodeResponse("${it.name},+${state.state}", BuildConfig.MAPS_API_KEY)
                            val latitude = response.results[0].geometry.location.lat
                            val longitude = response.results[0].geometry.location.lng
                            covidLocation = CovidLocation(state = state.state, district = it.name, latitude = latitude, longitude = longitude)
                            Log.d(TAG, "getCovidGeocode: $covidLocation")
                        } catch (e: Exception) {
                            Log.e(TAG, "getCovidGeocode: ${e.message}")
                        }
                        covidLocation
                    })
                }
            }
            requests.awaitAll()
        }

    private suspend fun createData() =
        withContext(Dispatchers.Default) {
            covidDataUiStates = mutableStateListOf()
            covidResponse()
            if (::json.isInitialized) {
                val gson = Gson()
                var current: String? = null
                for ((key, value) in json.body()?.entrySet()!!) {
                    try {
                        Log.d(TAG, "getCovidResponse: $key")
                        val values = value.asJsonObject
                        val total: Stats? = gson.fromJson(values?.get("total"), Stats::class.java)
                        val districts: MutableList<DistrictUiState> = mutableStateListOf()
                        values?.get("districts")?.asJsonObject?.entrySet()?.forEach { district ->
                            val data = district.value.asJsonObject.entrySet()
                            val statistics = mutableMapOf<String, Stats>()
                            data.removeIf { it.key.equals("meta") }
                            data.forEach {
                                if (!key.equals("meta")) {
                                    current = it.key
                                    statistics[it.key] =
                                        gson.fromJson(it.value.asJsonObject, Stats::class.java)
                                    districts.add(
                                        DistrictUiState(
                                            name = district.key,
                                            stats = statistics
                                        )
                                    )
                                }
                            }
                        }
                        covidDataUiStates.add(
                            CovidDataUiState(
                                state = statesMapping.getValue(key),
                                total = total,
                                districts = districts
                            )
                        )
                    } catch (e: Exception) {
                        e.stackTrace.forEach {
                            Log.e(TAG, "getCovidResponseError at $key's $current: $it")
                        }
                        break
                    }
                }
                Log.d(TAG, "getCovidResponse: ${json.body()}")
            }
            val districts: MutableSet<String> = mutableSetOf()
            covidDataUiStates.forEach {
                it.districts.forEach { districtUiState ->
                    districts.add(districtUiState.name)
                }
            }
            districts.forEach {
                Log.d(TAG, "createData: $it")
            }
            covidDataUiStates
        }
}