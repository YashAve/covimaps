package com.covid.covimaps.di

import android.app.Application
import com.covid.covimaps.data.repository.local.CountryDetailsRepository
import com.covid.covimaps.data.repository.remote.CovidLocationsManager
import com.covid.covimaps.data.repository.remote.StatesAndCitiesManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {

    @Inject lateinit var covidLocationsManager: CovidLocationsManager
    @Inject lateinit var countryDetailsRepository: CountryDetailsRepository
    @Inject lateinit var statesAndCitiesManager: StatesAndCitiesManager
    override fun onCreate() {
        super.onCreate()

        MainScope().launch {
            covidLocationsManager.init()
        }

        MainScope().launch {
            countryDetailsRepository.populate()
        }

        MainScope().launch {
            statesAndCitiesManager.init()
        }
    }
}