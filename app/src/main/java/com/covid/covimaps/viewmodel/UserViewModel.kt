package com.covid.covimaps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.covid.covimaps.data.model.remote.covid.countrycode.CountryCodes
import com.covid.covimaps.data.repository.remote.countrycode.CustomCountryCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject

private const val TAG = "UserViewModel"

@HiltViewModel
class UserViewModel @Inject constructor(private val customCountryCode: CustomCountryCode) :
    ViewModel() {

    var selectedCountry = "IN"
    var selectedCountryCode = "+91"
    var otp = ""

    suspend fun getDetails(): MutableList<CountryCodes> {
        viewModelScope.async {
            customCountryCode.populate()
        }.await()

        val countryCodes = customCountryCode.countryCodes.toMutableList()

        viewModelScope.async(Dispatchers.Default) {
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

        return countryCodes
    }
}