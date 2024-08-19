package com.covid.covimaps.data.repository.remote

import android.util.Log
import com.covid.covimaps.data.model.local.FirebaseCovidUiState
import com.covid.covimaps.data.model.retrofit.CovidGeocodes
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

private const val TAG = "FirebaseFirestoreRepository"

class FirebaseFirestoreRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) {

    val collection = firestore.collection("covid_locations")
    lateinit var covidState: FirebaseCovidUiState
    private var geocodes: CovidGeocodes? = null

    suspend fun retrieve(city: String) {
        var firebaseCovidUiState: FirebaseCovidUiState? = null
        coroutineScope {
            async(Dispatchers.IO) {
                val reference = collection.document(city)
                reference.get()
                    .addOnSuccessListener {
                        if (it != null) {
                            firebaseCovidUiState = it.toObject(FirebaseCovidUiState::class.java)
                            update(firebaseCovidUiState)
                        }
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "retrieve: exception = $it")
                    }
            }
        }
    }

    private fun update(data: FirebaseCovidUiState?) {
        var firebaseCovidUiState: FirebaseCovidUiState? = null
        data?.let { retrieved ->
            firebaseCovidUiState = FirebaseCovidUiState(
                country = covidState.country,
                city = covidState.city,
                vaccinated = covidState.vaccinated,
                covishield = retrieved.covishield + covidState.covishield,
                covaxin = retrieved.covaxin + covidState.covaxin,
                latitude = if (retrieved.latitude == 0.0) covidState.latitude else covidState.latitude,
                longitude = if (retrieved.longitude == 0.0) covidState.longitude else covidState.longitude
            )
        } ?: kotlin.run {
            val reference = collection.document(covidState.city)
            reference.set(covidState)
                .addOnSuccessListener {
                    Log.d(TAG, "update: data updated successfully")
                }
                .addOnFailureListener {
                    Log.d(TAG, "update: ${it.message}")
                }
        }
        firebaseCovidUiState?.let {
            val reference = collection.document(it.city)
            reference.set(it)
                .addOnSuccessListener {
                    Log.d(TAG, "update: data updated successfully")
                }
                .addOnFailureListener {
                    Log.d(TAG, "update: ${it.message}")
                }
        }
    }

    suspend fun getAll(): List<FirebaseCovidUiState> {
        val firebaseCovidUiStates: MutableList<FirebaseCovidUiState> = mutableListOf()
        coroutineScope {
            async(Dispatchers.IO) {
                collection.get()
                    .addOnSuccessListener {
                        it.forEach { document ->
                            firebaseCovidUiStates.add(document.toObject(FirebaseCovidUiState::class.java))
                        }
                    }
                    .addOnFailureListener {

                    }
            }.await()
        }
        return firebaseCovidUiStates
    }
}