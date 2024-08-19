package com.covid.covimaps.data.model.local

import com.google.android.gms.maps.model.LatLng

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

val disclaimer = mapOf(
    "General Information" to
            "This application provides historical data about COVID-19 and allows users to record their vaccination status. The information presented is for informational purposes only and should not be considered as medical or professional advice.",
    "Data Accuracy" to
            "While we strive to ensure that the information provided is accurate and up-to-date, we cannot guarantee the completeness, accuracy, or timeliness of the data. Historical data is sourced from public databases and may not reflect the most current statistics.",
    "Personal Information" to
            "Any personal information recorded in the app, such as vaccination status, is stored securely and is used solely for the purpose of enhancing your experience within the app. We do not share your personal information with third parties without your explicit consent.",
    "Not Medical Advice" to
            "The information provided by this app should not be used as a substitute for professional medical advice, diagnosis, or treatment. Always seek the advice of your physician or other qualified health provider with any questions you may have regarding COVID-19 or your health status.",
    "Limitation of Liability" to
            "In no event shall the app developers or any of their affiliates be liable for any damages arising out of the use or inability to use this app, even if the developers have been advised of the possibility of such damages. This includes, without limitation, damages for loss of data or profit, or due to business interruption.",
    "Changes to Disclaimer" to
            "We may update our Disclaimer from time to time. We will notify you of any changes by posting the new Disclaimer on this page. You are advised to review this Disclaimer periodically for any changes. Changes to this Disclaimer are effective when they are posted on this page.",
    "Contact Us" to
            "If you have any questions about this Disclaimer, please contact us at."
)

data class CovidDataUiState(
    val state: String,
    val total: Stats?,
    val districts: List<DistrictUiState>,
)

data class DistrictUiState(
    val name: String,
    val stats: Map<String, Stats>,
    var coordinates: LatLng? = null,
)

data class Stats(
    val confirmed: Int = 0,
    val deceased: Int = 0,
    val recovered: Int = 0,
    val tested: Int = 0,
    val vaccinated1: Int = 0,
    val vaccinated2: Int = 0,
)

data class FirebaseCovidUiState(
    val country: String,
    val city: String,
    val vaccinated: Boolean = false,
    val covishield: Int = 0,
    val covaxin: Int = 0,
    val recovered: Int = 0,
    val latitude: Double,
    val longitude: Double
) {
    constructor() : this("", "", false, 0, 0, 0, 0.0, 0.0)
}