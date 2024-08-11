package com.covid.covimaps.data.model.local.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CovidLocation::class, Countries::class], version = 1, exportSchema = false)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun covidLocationDao(): CovidLocationDoa
    abstract fun countriesDao(): CountryDao
}