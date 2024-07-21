package com.covid.covimaps.data.model.remote

import com.google.gson.annotations.SerializedName

const val GEOCODE_URL = "https://maps.googleapis.com/maps/api/"
const val GEOCODE_API_KEY = ""

data class CovidGeocodes(
    @SerializedName("results") val results: List<Result>,
    @SerializedName("status") val status: String
)

data class Geometry(
    @SerializedName("location") val location: Location,
    @SerializedName("location_type") val locationType: String
)

data class Location(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double
)

data class Result(
    @SerializedName("formatted_address") val formattedAddress: String,
    @SerializedName("geometry") val geometry: Geometry,
)