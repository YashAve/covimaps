package com.covid.covimaps.data.repository.local

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreManager(private val context: Context, var isAgree: Boolean = false) {
    
    init {
        Log.d(TAG, "initialized: ")
    }

    companion object {
        const val TAG = "DataStoreManager"
        val AGREE = booleanPreferencesKey("agree_to_disclaimer")
    }

    fun isAgreedToDisclaimer() {
        context.dataStore.data.map {
            it[AGREE] ?: false
            Log.d(TAG, "isAgreedToDisclaimer: ${it[AGREE]}")
            isAgree = it[AGREE] ?: false
        }
    }

    suspend fun agreeToDisclaimer() =
        context.dataStore.edit {
            it[AGREE] = true
            isAgree = true
        }
}