package com.covid.covimaps.data.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "covid_location", primaryKeys = ["state", "district"])
data class CovidLocation (
    @ColumnInfo(name = "state")
    val state: String,
    @ColumnInfo(name = "district")
    val district: String,
    @ColumnInfo(name = "latitude")
    val latitude: Double,
    @ColumnInfo(name = "longitude")
    val longitude: Double,
    @ColumnInfo("total_deceased")
    val totalDeceased: Int,
    @ColumnInfo("total_recovered")
    val totalRecovered: Int,
    @ColumnInfo("total_covishields")
    val totalCovishields: Int,
    @ColumnInfo("total_covaxin")
    val totalCovaxin: Int,
    @ColumnInfo("deceased")
    val deceased: Int,
    @ColumnInfo("recovered")
    val recovered: Int,
    @ColumnInfo("covishields")
    val covishields: Int,
    @ColumnInfo("covaxin")
    val covaxin: Int
)

@Entity(tableName = "locale_detail", primaryKeys = ["display_name"])
data class LocaleDetail (
    @ColumnInfo(name = "display_name")
    val displayName: String,
    @ColumnInfo(name = "country")
    val country: String,
    @ColumnInfo(name = "phone_number_code")
    val phoneNumberCode: String,
    @ColumnInfo(name = "flag")
    val flag: String
)

@Entity(tableName = "countries_and_cities", primaryKeys = ["city", "country"])
data class CountryAndCity(
    @ColumnInfo("city") val city: String,
    @ColumnInfo("country") val country: String
)