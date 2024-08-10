package com.covid.covimaps.data.repository.remote.countrycode

import android.util.Log
import com.covid.covimaps.data.model.remote.covid.countrycode.CountryCodes
import com.covid.covimaps.data.repository.CountryCodeBaseUrl
import com.covid.covimaps.data.repository.remote.APIService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import javax.inject.Inject

const val COUNTRY_CODE_BASE_URL = "https://restcountries.com/v3.1/"

private const val TAG = "CustomCountryCode"

class CustomCountryCode @Inject constructor(@CountryCodeBaseUrl private val retrofitCountryCodes: Retrofit) {

    lateinit var countryCodes: List<CountryCodes>

    suspend fun populate(): List<CountryCodes> {
        withContext(Dispatchers.IO) {
            countryCodes = try {
                val service = retrofitCountryCodes.create(APIService::class.java)
                val response = service.getCountryCodes()
                response
            } catch (e: Exception) {
                Log.d(TAG, "populate: ${e.message}")
                listOf()
            }
        }
        return countryCodes
    }
}