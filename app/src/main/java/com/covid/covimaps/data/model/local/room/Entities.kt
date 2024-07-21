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
    val longitude: Double
)