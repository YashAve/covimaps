package com.covid.covimaps.util

import android.app.PendingIntent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.i18n.phonenumbers.PhoneNumberUtil

private const val TAG = "GooglePlayServicesManager"

class GooglePlayServicesManager(private val context: ComponentActivity) {

    private var phoneNumber: String = ""
    private lateinit var onClickNumber: (String) -> Unit

    private val request: GetPhoneNumberHintIntentRequest =
        GetPhoneNumberHintIntentRequest.builder().build()

    private val phoneNumberHintIntentResultLauncher =
        context.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            try {
                phoneNumber =
                    Identity.getSignInClient(context).getPhoneNumberFromIntent(result.data)
                val code = getPhoneNumberCode()
            } catch (e: Exception) {
                Log.e(TAG, "Phone Number Hint failed")
            } finally {
                onClickNumber(phoneNumber)
            }
        }

    fun getPhoneNumberHints(selectPhoneNumber: (String) -> Unit) {
        onClickNumber = selectPhoneNumber
        Identity.getSignInClient(context)
            .getPhoneNumberHintIntent(request)
            .addOnSuccessListener { result: PendingIntent ->
                try {
                    phoneNumberHintIntentResultLauncher.launch(
                        IntentSenderRequest.Builder(result).build()
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Launching the PendingIntent failed")
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "Phone Number Hint failed")
            }
    }

    private fun getPhoneNumberCode() =
        try {
            val util = PhoneNumberUtil.getInstance()
            util.parse(phoneNumber, null).countryCode
        } catch (e: Exception) {
            Log.d(TAG, "getPhoneNumberCode: ${e.message}")
            null
        }
}