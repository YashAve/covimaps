package com.covid.covimaps.util

import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager

fun Activity.hideSoftKeyBoard() {
    val service = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    val view = currentFocus
    view?.let { service.hideSoftInputFromWindow(it.windowToken, 0) }
}