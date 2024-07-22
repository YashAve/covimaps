package com.covid.covimaps.data.repository.local

import android.content.Context
import androidx.room.Room
import com.covid.covimaps.data.model.local.room.CovidDatabase
import com.covid.covimaps.data.model.remote.CovidDataUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


object DatabaseProvider {
    fun getDatabase(context: Context): CovidDatabase {
        return Room.databaseBuilder(
            context,
            CovidDatabase::class.java,
            "covid-database"
        )
            .build()
    }
}

suspend fun List<CovidDataUiState>.totalDeaths() =
    withContext(Dispatchers.Default) {
        var total = 0
        forEach { state ->
            total += state.total?.deceased ?: 0
        }
        total
    }

suspend fun List<CovidDataUiState>.totalRecovered() =
    withContext(Dispatchers.Default) {
        var total = 0
        forEach { state ->
            total += state.total?.recovered ?: 0
        }
        total
    }

suspend fun List<CovidDataUiState>.coviShields() =
    withContext(Dispatchers.Default) {
        var total = 0
        forEach { state ->
            total += state.total?.vaccinated1 ?: 0
        }
        total
    }

suspend fun List<CovidDataUiState>.covaxin() =
    withContext(Dispatchers.Default) {
        var total = 0
        forEach { state ->
            total += state.total?.vaccinated2 ?: 0
        }
        total
    }