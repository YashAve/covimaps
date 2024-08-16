package com.covid.covimaps.data.repository.local

import android.app.Activity
import android.content.Context

class SharedPreferenceManager(private val activity: Activity) {

    private val sharedPreference = activity.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val TAG = "SharedPreferenceManager"
        private const val FILE_NAME = "settings"
        private val AGREE = "agree_to_disclaimer"
    }

    val isAgree: Boolean
        get() = sharedPreference.getBoolean(AGREE, false)

    fun agree() {
        with(sharedPreference.edit()) {
            putBoolean(AGREE, true)
            apply()
        }
    }
}