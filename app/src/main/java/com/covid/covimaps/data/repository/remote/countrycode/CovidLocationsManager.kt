package com.covid.covimaps.data.repository.remote.countrycode

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.covid.covimaps.BuildConfig
import com.covid.covimaps.data.model.local.CovidDataUiState
import com.covid.covimaps.data.model.local.DistrictUiState
import com.covid.covimaps.data.model.local.Stats
import com.covid.covimaps.data.model.local.statesMapping
import com.covid.covimaps.data.model.retrofit.APIService
import com.covid.covimaps.data.model.room.CovidLocation
import com.covid.covimaps.data.model.room.LocalDatabase
import com.covid.covimaps.di.CovidBaseUrl
import com.covid.covimaps.di.GeoCodeBaseUrl
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

private const val TAG = "CovidLocationsManager"

class CovidLocationsManager @Inject constructor(
    @CovidBaseUrl private val retrofitCovid: Retrofit,
    @GeoCodeBaseUrl private val retrofitGeoCode: Retrofit,
    private val localDatabase: LocalDatabase
) {
    private lateinit var json: Response<JsonObject>
    private lateinit var covidDataUiStates: MutableList<CovidDataUiState>
    private var covidLocations: MutableList<CovidLocation> = mutableListOf()

    suspend fun init() {
        if (getLocationsCount() == 0) {
            getCovidDataUiState()
            getLocations()
            insertDatabase(covidLocations.toTypedArray())
        }
        covidLocations = retrieveLocations().toMutableList()
    }

    private suspend fun getCovidDataUiState() =
        coroutineScope { async { createData() }.await() }

    private suspend fun getLocations() {
        coroutineScope { async { getCovidGeocode() }.await() }
        Log.d(TAG, "covid locations size: ${covidLocations.size}")
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
            covidDataUiStates.subList(3, 5).forEach { state ->
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
                        val total: Stats? =
                            gson.fromJson(values?.get("total"), Stats::class.java)
                        val districts: MutableList<DistrictUiState> = mutableStateListOf()
                        values?.get("districts")?.asJsonObject?.entrySet()
                            ?.forEach { district ->
                                val data = district.value.asJsonObject.entrySet()
                                val statistics = mutableMapOf<String, Stats>()
                                data.removeIf { it.key.equals("meta") }
                                data.forEach {
                                    if (!key.equals("meta")) {
                                        current = it.key
                                        statistics[it.key] =
                                            gson.fromJson(
                                                it.value.asJsonObject,
                                                Stats::class.java
                                            )
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

    private suspend fun insertDatabase(covidLocations: Array<CovidLocation>) {
        withContext(Dispatchers.IO) {
            launch {
                localDatabase.covidLocationDao().insertAll(*covidLocations)
            }
        }
    }

    private suspend fun retrieveLocations() =
        withContext(Dispatchers.IO) {
            localDatabase.covidLocationDao().getCovidLocations()
        }

    private suspend fun getLocationsCount() =
        withContext(Dispatchers.IO) {
            localDatabase.covidLocationDao().getCount()
        }
}