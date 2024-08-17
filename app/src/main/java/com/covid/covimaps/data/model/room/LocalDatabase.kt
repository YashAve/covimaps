package com.covid.covimaps.data.model.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CovidLocation::class, CountryAndCity::class, LocaleDetail::class], version = 2, exportSchema = false)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun covidLocationDao(): CovidLocationDoa
    abstract fun countriesAndCitiesDao(): CountryAndCityDao
    abstract fun localeDetailDao(): LocaleDetailDoa
}