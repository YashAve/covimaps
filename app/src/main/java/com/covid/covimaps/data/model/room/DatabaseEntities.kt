package com.covid.covimaps.data.model.room

import androidx.compose.ui.graphics.ImageBitmap
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

@Entity(primaryKeys = ["country"])
data class Countries (
    @ColumnInfo(name = "country")
    val country: String,
    @ColumnInfo(name = "code")
    val code: String,
    @ColumnInfo(name = "country_code")
    val countryCode: String,
    @ColumnInfo(name = "flag")
    val flag: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Countries

        if (country != other.country) return false
        if (code != other.code) return false
        if (countryCode != other.countryCode) return false
        if (!flag.contentEquals(other.flag)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = country.hashCode()
        result = 31 * result + code.hashCode()
        result = 31 * result + countryCode.hashCode()
        result = 31 * result + flag.contentHashCode()
        return result
    }
}

data class CountryCodeUiState(
    val country: String,
    val code: String,
    val countryCode: String,
    val flag: ImageBitmap
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CountryCodeUiState

        if (country != other.country) return false
        if (code != other.code) return false
        if (countryCode != other.countryCode) return false
        if (flag != other.flag) return false

        return true
    }

    override fun hashCode(): Int {
        var result = country.hashCode()
        result = 31 * result + code.hashCode()
        result = 31 * result + countryCode.hashCode()
        result = 31 * result + flag.hashCode()
        return result
    }
}