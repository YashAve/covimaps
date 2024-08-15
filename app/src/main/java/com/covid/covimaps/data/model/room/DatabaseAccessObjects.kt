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

@Dao
interface CountryAndCityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg countryAndCity: CountryAndCity)
    @Query("SELECT country FROM countries_and_cities ORDER BY country")
    suspend fun getCountries(): List<String>
    @Query("SELECT city FROM countries_and_cities WHERE country = :country ORDER BY city")
    suspend fun getCities(country: String): List<String>
    @Query("SELECT COUNT(*) FROM countries_and_cities")
    suspend fun getCount(): Int
}