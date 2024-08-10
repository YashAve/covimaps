package com.covid.covimaps.data.repository

import com.covid.covimaps.data.model.remote.covid.COVID_DATA_URL
import com.covid.covimaps.data.model.remote.covid.GEOCODE_URL
import com.covid.covimaps.data.repository.remote.countrycode.COUNTRY_CODE_BASE_URL
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CovidBaseUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CountryCodeBaseUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeoCodeBaseUrl

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

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
    @CountryCodeBaseUrl
    fun provideRetrofitCountryCodes(): Retrofit {
        return Retrofit
            .Builder().apply {
                baseUrl(COUNTRY_CODE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
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
    fun provideFirebaseAuth(): FirebaseAuth =
        FirebaseAuth.getInstance()
}