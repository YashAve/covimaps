package com.covid.covimaps.data.model.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CovidLocationDoa {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg covidLocations: CovidLocation)
    @Query("SELECT * FROM covid_location")
    fun getCovidLocations(): List<CovidLocation>
    @Query("SELECT COUNT(*) FROM covid_location")
    fun getCount(): Int
}

@Dao
interface LocaleDetailDoa {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg localeDetail: LocaleDetail)
    @Query("SELECT * FROM locale_detail")
    fun getLocaleDetail(): List<LocaleDetail>
    @Query("SELECT COUNT(*) FROM locale_detail")
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