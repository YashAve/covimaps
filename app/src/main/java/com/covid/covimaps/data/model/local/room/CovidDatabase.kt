package com.covid.covimaps.data.model.local.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CovidLocation::class], version = 1)
abstract class CovidDatabase : RoomDatabase() {
    abstract fun covidLocationDao(): CovidLocationDoa
}