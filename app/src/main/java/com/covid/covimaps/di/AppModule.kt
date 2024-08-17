package com.covid.covimaps.di

import android.content.Context
import androidx.room.Room
import com.covid.covimaps.data.model.local.COVID_DATA_URL
import com.covid.covimaps.data.model.retrofit.GEOCODE_URL
import com.covid.covimaps.data.model.room.LocalDatabase
import com.covid.covimaps.data.repository.remote.STATES_AND_CITIES_BASE_URL
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CovidBaseUrl
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeoCodeBaseUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class StatesAndCitiesBaseUrl

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideLocalDatabase(@ApplicationContext appContext: Context): LocalDatabase =
        Room.databaseBuilder(appContext, LocalDatabase::class.java, "covid-database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @CovidBaseUrl
    fun provideRetrofitCovid(): Retrofit {
        return Retrofit
            .Builder().apply {
                baseUrl(COVID_DATA_URL)
                addConverterFactory(GsonConverterFactory.create())
            }.build()
    }

    @Provides
    @GeoCodeBaseUrl
    fun provideRetrofitGeoCode(): Retrofit {
        return Retrofit
            .Builder().apply {
                baseUrl(GEOCODE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
            }.build()
    }

    @Provides
    @StatesAndCitiesBaseUrl
    fun provideRetrofitStatesAndCities(): Retrofit {
        return Retrofit
            .Builder().apply {
                baseUrl(STATES_AND_CITIES_BASE_URL)
                addConverterFactory(GsonConverterFactory.create())
            }.build()
    }

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth =
        FirebaseAuth.getInstance()
}