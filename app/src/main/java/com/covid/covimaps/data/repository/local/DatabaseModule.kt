package com.covid.covimaps.data.repository.local

import android.content.Context
import androidx.room.Room
import com.covid.covimaps.data.model.local.room.LocalDatabase
import com.covid.covimaps.data.model.local.room.CovidLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


object DatabaseProvider {
    fun getDatabase(context: Context): LocalDatabase {
        return Room.databaseBuilder(
            context,
            LocalDatabase::class.java,
            "covid-database"
        )
            .build()
    }
}

suspend fun List<CovidLocation>.totalDeaths() =
    withContext(Dispatchers.Default) {
        var total = 0
        forEach { it ->
            total += it.totalDeceased
        }
        total
    }

suspend fun List<CovidLocation>.totalRecovered() =
    withContext(Dispatchers.Default) {
        var total = 0
        forEach {
            total += it.totalRecovered
        }
        total
    }

suspend fun List<CovidLocation>.coviShields() =
    withContext(Dispatchers.Default) {
        var total = 0
        forEach {
            total += it.totalCovishields
        }
        total
    }

suspend fun List<CovidLocation>.covaxin() =
    withContext(Dispatchers.Default) {
        var total = 0
        forEach {
            total += it.covaxin
        }
        total
    }