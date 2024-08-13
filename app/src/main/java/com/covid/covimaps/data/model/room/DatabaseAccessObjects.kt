package com.covid.covimaps.data.model.room

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

@Dao
interface CountryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg countries: Countries)
    @Query("SELECT * from countries")
    fun getCountryCodes() : List<Countries>
    @Query("SELECT COUNT(*) FROM countries")
    fun getCount(): Int
}