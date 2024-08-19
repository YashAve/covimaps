package com.covid.covimaps.data.repository.local

import android.app.Activity
import android.content.Context

class SharedPreferenceManager(private val activity: Activity) {

    private val sharedPreference = activity.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val TAG = "SharedPreferenceManager"
        private const val FILE_NAME = "settings"
        private val AGREE = "agree_to_disclaimer"
        private val PHONE_NUMBER = "phone_number"
    }

    val isAgree: Boolean
        get() = sharedPreference.getBoolean(AGREE, false)
    val phoneNumber: String
        get() = sharedPreference.getString(PHONE_NUMBER, "").toString()

    fun agree() {
        with(sharedPreference.edit()) {
            putBoolean(AGREE, true)
            apply()
        }
    }

    fun save(phoneNumber: String) {
        with(sharedPreference.edit()) {
            putString(PHONE_NUMBER, phoneNumber)
            apply()
        }
    }
}