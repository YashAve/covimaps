package com.covid.covimaps.data.repository.local

import android.util.Log
import com.covid.covimaps.data.model.room.LocalDatabase
import com.covid.covimaps.data.model.room.LocaleDetail
import com.google.i18n.phonenumbers.PhoneNumberUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

private const val TAG = "CountryDetailRepository"

class CountryDetailsRepository @Inject constructor(
    private val localDatabase: LocalDatabase,
) {

    private var localeDetails: MutableList<LocaleDetail> = mutableListOf()

    suspend fun populate() {
        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        val localeList = Locale.getAvailableLocales()
        coroutineScope {
            async(Dispatchers.Default) {
                Log.d(TAG, "populate: ${localeList.size}")
                for (index in 0..localeList.size) {
                    try {
                        val locale = localeList[index]
                        val phoneCode = "+${phoneNumberUtil.getCountryCodeForRegion(localeList[index].country)}"
                        val localDetail = LocaleDetail(
                            displayName = locale.displayCountry,
                            country = locale.country,
                            phoneNumberCode = phoneCode,
                            flag = getCountryFlag(locale.isO3Country)
                        )
                        Log.d(TAG, "populate: $localDetail")
                        localeDetails.add(localDetail)
                    } catch (e: Exception) {
                        continue
                    }
                }
                localeDetails.removeIf { it.phoneNumberCode == "+0" }
                save()
            }.await()
        }
    }

    private fun getCountryFlag(countryCode: String): String {
        val flagOffset = 0x1F1E6
        val asciiOffset = 0x41
        val firstChar = Character.codePointAt(countryCode, 0) - asciiOffset + flagOffset
        val secondChar = Character.codePointAt(countryCode, 1) - asciiOffset + flagOffset
        val flag = (String(Character.toChars(firstChar)) + String(Character.toChars(secondChar)))
        return flag
    }

    private suspend fun save() =
        coroutineScope {
            launch {
                localDatabase.localeDetailDao().insertAll(*localeDetails.toTypedArray())
            }
        }

    suspend fun retrieve() =
        coroutineScope {
            async(Dispatchers.IO) {
                localDatabase.localeDetailDao().getLocaleDetail()
            }.await()
        }
}