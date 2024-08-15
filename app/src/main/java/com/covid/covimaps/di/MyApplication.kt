package com.covid.covimaps.di

import android.app.Application
import com.covid.covimaps.data.repository.remote.countrycode.CovidLocationsManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {

    @Inject lateinit var covidLocationsManager: CovidLocationsManager
    override fun onCreate() {
        super.onCreate()

        MainScope().launch {
            covidLocationsManager.init()
        }
    }
}