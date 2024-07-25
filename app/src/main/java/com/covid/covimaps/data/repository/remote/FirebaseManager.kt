package com.covid.covimaps.data.repository.remote

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

object FirebaseInstances {
    val firebaseAuth = FirebaseAuth.getInstance()
}

class FirebaseManager(private val context: Activity) {

    var flag: Boolean = false
        private set

    private val phoneAuthCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {

        }

        override fun onVerificationFailed(firebaseException: FirebaseException) {
            TODO("Not yet implemented")
        }

    }

    fun sendOtp(phoneNumber: String) {
        if (flag) return
        val options = PhoneAuthOptions.newBuilder(FirebaseInstances.firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(context)
            .setCallbacks(phoneAuthCallbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        flag = true
    }
}