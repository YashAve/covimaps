package com.covid.covimaps.data.model.retrofit

import com.google.gson.annotations.SerializedName

data class StatesAndCitiesData(
    @SerializedName("data") val data: Array<Data>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StatesAndCitiesData

        return data.contentEquals(other.data)
    }

    override fun hashCode(): Int {
        return data.contentHashCode()
    }
}

data class Data(
    @SerializedName("country") val country: String,
    @SerializedName("cities") val cities: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Data

        if (country != other.country) return false
        if (!cities.contentEquals(other.cities)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = country.hashCode()
        result = 31 * result + cities.contentHashCode()
        return result
    }
}