package com.covid.covimaps.data.repository.remote.covid

import android.util.Log
import androidx.activity.ComponentActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

private const val TAG = "FirebaseManager"
object FirebaseInstances {
    val firebaseAuth = FirebaseAuth.getInstance()
}

class FirebaseManager(private val context: ComponentActivity) {

    var flag: Boolean = false

    private val phoneAuthCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted: sms code is received ${phoneAuthCredential.smsCode}")
        }

        override fun onVerificationFailed(firebaseException: FirebaseException) {
            Log.d(TAG, "onVerificationFailed: ${firebaseException.message}")
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