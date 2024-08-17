package com.covid.covimaps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.covid.covimaps.data.model.room.LocaleDetail
import com.covid.covimaps.data.repository.local.CountryDetailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import javax.inject.Inject

private const val TAG = "UserViewModel"

@HiltViewModel
class UserViewModel @Inject constructor(
    private val countryDetailsRepository: CountryDetailsRepository,
) :
    ViewModel() {

    lateinit var selectedCountry: String
    lateinit var selectedCountryCode: String
    lateinit var selectedIso3Country: String
    var otp = ""
    var localDetails: MutableList<LocaleDetail> = mutableListOf()

    suspend fun getDetails() {
        localDetails = viewModelScope.async {
            countryDetailsRepository.retrieve()
        }.await().distinct().sortedBy { it.displayName }.toMutableList()

    }
}