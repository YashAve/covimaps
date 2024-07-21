package com.covid.covimaps.data.repository.local

import android.content.Context
import androidx.room.Room
import com.covid.covimaps.data.model.local.room.CovidDatabase


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