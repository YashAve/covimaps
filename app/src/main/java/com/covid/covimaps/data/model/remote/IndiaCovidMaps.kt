package com.covid.covimaps.data.model.remote

const val COVID_DATA_URL: String = "https://data.covid19india.org/v4/min/"

val statesMapping = mapOf(
    "AN" to "Andaman and Nicobar Islands",
    "AP" to "Andhra Pradesh",
    "AR" to "Arunachal Pradesh",
    "AS" to "Assam",
    "BR" to "Bihar",
    "CH" to "Chandigarh",
    "CT" to "Chhattisgarh",
    "DL" to "Delhi",
    "DN" to "Daman and Diu",
    "GA" to "Goa",
    "GJ" to "Gujarat",
    "HP" to "Himachal Pradesh",
    "HR" to "Haryana",
    "JH" to "Jharkhand",
    "JK" to "Jammu and Kashmir",
    "KA" to "Karnataka",
    "KL" to "Kerala",
    "LA" to "Ladakh",
    "LD" to "Lakshadweep",
    "MH" to "Maharashtra",
    "ML" to "Meghalaya",
    "MN" to "Manipur",
    "MP" to "Madhya Pradesh",
    "MZ" to "Mizoram",
    "NL" to "Nagaland",
    "OR" to "Odisha",
    "PB" to "Punjab",
    "PY" to "Puducherry",
    "RJ" to "Rajasthan",
    "SK" to "Sikkim",
    "TG" to "Telangana",
    "TN" to "Tamil Nadu",
    "TR" to "Tripura",
    "TT" to "Tripura",
    "UP" to "Uttar Pradesh",
    "UT" to "Uttarakhand",
    "WB" to "West Bengal"
)

data class CovidDataUiState(
    val state: String,
    val total: Stats?,
    val districts: List<DistrictUiState>,
)

data class DistrictUiState(
    val name: String,
    val stats: Map<String, Stats>
)

data class Stats(
    val confirmed: Int = -1,
    val deceased: Int = -1,
    val recovered: Int = -1,
    val tested: Int = -1,
    val vaccinated1: Int = -1,
    val vaccinated2: Int = -1
)