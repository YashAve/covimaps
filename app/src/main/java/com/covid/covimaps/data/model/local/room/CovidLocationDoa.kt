package com.covid.covimaps.data.model.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CovidLocationDoa {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg covidLocations: CovidLocation)

    @Query("SELECT * FROM covidlocation")
    fun getCovidLocations(): List<CovidLocation>

    @Query("SELECT COUNT(*) FROM covidlocation")
    fun getCount(): Int
}