package com.covid.covimaps.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.covid.covimaps.BuildConfig
import com.covid.covimaps.data.model.local.room.CovidLocation
import com.covid.covimaps.data.model.remote.covid.CovidDataUiState
import com.covid.covimaps.data.model.remote.covid.DistrictUiState
import com.covid.covimaps.data.model.remote.covid.Stats
import com.covid.covimaps.data.model.remote.covid.statesMapping
import com.covid.covimaps.data.repository.CovidBaseUrl
import com.covid.covimaps.data.repository.GeoCodeBaseUrl
import com.covid.covimaps.data.repository.local.DatabaseProvider
import com.covid.covimaps.data.repository.remote.APIService
import com.google.android.gms.maps.model.LatLng
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
import javax.inject.Inject

private const val TAG = "MainViewModel"

interface OnDataReadyCallback {
    fun onDataReady(status: String = "incomplete")
}

suspend fun insertDatabase(covidLocations: Array<CovidLocation>, context: Context) {
    withContext(Dispatchers.IO) {
        launch {
            DatabaseProvider.getDatabase(context).covidLocationDao().insertAll(*covidLocations)
        }
    }
}

suspend fun retrieveLocations(context: Context) =
    withContext(Dispatchers.IO) {
        DatabaseProvider.getDatabase(context).covidLocationDao().getCovidLocations()
    }

suspend fun getLocationsCount(context: Context) =
    withContext(Dispatchers.IO) {
        DatabaseProvider.getDatabase(context).covidLocationDao().getCount()
    }

@HiltViewModel
class MainViewModel @Inject constructor(@CovidBaseUrl private val retrofitCovid: Retrofit, @GeoCodeBaseUrl private val retrofitGeoCode: Retrofit) : ViewModel() {

    private lateinit var json: Response<JsonObject>
    lateinit var covidDataUiStates: MutableList<CovidDataUiState>
    var coordinates: MutableList<LatLng> = mutableListOf()
    var covidLocations: MutableList<CovidLocation> = mutableListOf()
    lateinit var currentCovidLocation: CovidLocation
    var loading = true

    suspend fun getCovidDataUiState() =
        viewModelScope.async { createData() }.await()

    suspend fun getLocations(onDataReadyCallback: OnDataReadyCallback) {
        viewModelScope.async { getCovidGeocode() }.await()
        Log.d(TAG, "covid locations size: ${covidLocations.size}")
        onDataReadyCallback.onDataReady("complete")
    }

    private suspend fun covidResponse() {
        withContext(Dispatchers.IO) {
            val service = retrofitCovid.create(APIService::class.java)
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
            val service = retrofitGeoCode.create(APIService::class.java)
            val requests: MutableList<Deferred<CovidLocation?>> = mutableListOf()
            //val offset = kotlin.random.Random.nextInt(0, 32)
            covidDataUiStates.subList(0, 1).forEach { state ->
                state.districts.forEach {
                    requests.add(async {
                        var covidLocation: CovidLocation? = null
                        try {
                            val response = service.getGeocodeResponse(
                                "${it.name},+${state.state}",
                                BuildConfig.MAPS_API_KEY
                            )
                            val latitude = response.results[0].geometry.location.lat
                            val longitude = response.results[0].geometry.location.lng
                            it.coordinates = LatLng(latitude, longitude)
                            covidLocation = CovidLocation(
                                state = state.state,
                                district = it.name,
                                latitude = latitude,
                                longitude = longitude,
                                totalDeceased = state.total?.deceased ?: 0,
                                totalRecovered = state.total?.recovered ?: 0,
                                totalCovishields = state.total?.vaccinated1 ?: 0,
                                totalCovaxin = state.total?.vaccinated2 ?: 0,
                                deceased = it.stats["total"]?.deceased ?: 0,
                                recovered = it.stats["total"]?.recovered ?: 0,
                                covishields = it.stats["total"]?.vaccinated1 ?: 0,
                                covaxin = it.stats["total"]?.vaccinated2 ?: 0
                            )
                            covidLocations.add(covidLocation)
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