package com.covid.covimaps.data.repository.remote.countrycode

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import com.covid.covimaps.data.model.retrofit.APIService
import com.covid.covimaps.data.model.retrofit.CountryCodes
import com.covid.covimaps.data.model.room.Countries
import com.covid.covimaps.data.model.room.CountryCodeUiState
import com.covid.covimaps.data.model.room.LocalDatabase
import com.covid.covimaps.di.CountryCodeBaseUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

const val COUNTRY_CODE_BASE_URL = "https://restcountries.com/v3.1/"

private const val TAG = "CustomCountryCode"

class CustomCountryCode @Inject constructor(
    @CountryCodeBaseUrl private val retrofitCountryCodes: Retrofit,
    private val localDatabase: LocalDatabase
) {

    private lateinit var countryCodes: MutableList<CountryCodes>
    private var countryCodeUiStates: MutableList<CountryCodeUiState> = mutableListOf()
    private val countries: MutableList<Countries> = mutableListOf()

    suspend fun populate(): List<CountryCodeUiState> =

        withContext(Dispatchers.IO) {
            if (countryCodeUiStates.isEmpty()) {
                if (localDatabase.countriesDao().getCount() == 0) {
                    try {
                        val service = retrofitCountryCodes.create(APIService::class.java)
                        countryCodes = service.getCountryCodes()
                        Log.d(TAG, "populate: size before = ${countryCodes.size}")
                        countryCodes.forEach {
                            Log.d(TAG, "populate: $it")
                        }
                        //cleanup()
                        store(countryCodes)
                        Log.d(TAG, "populate: called third")
                    } catch (e: Exception) {
                        Log.d(TAG, "populate: ${e.message}")
                    }
                }
                retrieve()
            }
            countryCodeUiStates.toList().also {
                Log.d(TAG, "populate: size after = ${it.size}")
            }
        }

    private suspend fun cleanup() {
        withContext(Dispatchers.Default) {
            Log.d(TAG, "cleanup: called first")
            async {
                countryCodes.removeIf { it.idd?.suffixes?.isEmpty() ?: false }
                countryCodes.sortBy { it.name?.common }
                countryCodes.removeIf { countryCode ->
                    countryCode.altSpellings.removeIf {
                        val characters = it.toCharArray()
                        var allLowerCase = false
                        for (character in characters) {
                            allLowerCase = character.isLowerCase() || !character.isLetter()
                            if (allLowerCase) break
                        }
                        allLowerCase
                    }
                    countryCode.altSpellings.removeIf { it.isEmpty() }
                }
            }.await()
            countryCodes.forEach {
                Log.d(TAG, "cleanup: $it")
            }
        }
    }

    private suspend fun store(countryCodes: MutableList<CountryCodes>) {
        countryCodes.forEach {
            val suffix = it.idd?.suffixes?.let { suffix ->
                if (suffix.size == 1) suffix[0] else ""
            } ?: ""
            val root = "${it.idd?.root}"
            val countryCode = "$root$suffix"

            val country = Countries(
                country = it.name?.common ?: "",
                code = it.altSpellings[0],
                countryCode = countryCode,
                flag = toByteArray(it.flags?.png!!) ?: byteArrayOf()
            )

            Log.d(TAG, "store: $country")

            withContext(Dispatchers.IO) {
                async {
                    localDatabase.countriesDao().insertAll(country)
                    Log.d(TAG, "stored in database: $country")
                }.await()
            }
            Log.d(TAG, "store: $country")
        }
    }

    private suspend fun retrieve() {
        withContext(Dispatchers.IO) {
            localDatabase.countriesDao().getCountryCodes().forEach {
                if (it.flag.isNotEmpty()) {
                    countries.add(it)
                }
            }
            countries.forEach {
                if (it.flag.isNotEmpty()) {
                    countryCodeUiStates.add(
                        CountryCodeUiState(
                            country = it.country,
                            code = it.code,
                            countryCode = it.countryCode,
                            flag = toBitmap(it.flag).asImageBitmap()
                        )
                    )
                }
            }
        }
    }

    private suspend fun toByteArray(flagUrl: String): ByteArray? =
        withContext(Dispatchers.IO) {
            try {
                val url = URL(flagUrl)
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    doInput = true
                    connect()
                }
                val inputStream = connection.inputStream

                withContext(Dispatchers.Default) {
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    val outputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.toByteArray()
                }
            } catch (e: Exception) {
                null
            }
        }

    private suspend fun toBitmap(byteArray: ByteArray) =
        withContext(Dispatchers.Default) {
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }
}