package com.covid.covimaps.data.model.local.room

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["state", "district"])
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