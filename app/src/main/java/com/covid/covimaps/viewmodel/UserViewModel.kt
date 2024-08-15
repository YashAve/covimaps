package com.covid.covimaps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.covid.covimaps.data.model.room.CountryCodeUiState
import com.covid.covimaps.data.repository.remote.CustomCountryCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import javax.inject.Inject

private const val TAG = "UserViewModel"

@HiltViewModel
class UserViewModel @Inject constructor(private val customCountryCode: CustomCountryCode) :
    ViewModel() {

    var selectedCountry = "IN"
    var selectedCountryCode = "+91"
    var otp = ""
    var countryCodeUiStates: MutableList<CountryCodeUiState> = mutableListOf()
    var generated = false

    suspend fun getDetails() {
        countryCodeUiStates = viewModelScope.async {
            customCountryCode.populate()
        }.await().distinct().sortedBy { it.country }.toMutableList()
        generated = true
    }
}