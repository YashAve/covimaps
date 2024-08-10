package com.covid.covimaps.domain

import android.app.PendingIntent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity

private const val TAG = "GooglePlayServicesManager"

class GooglePlayServicesManager(private val context: ComponentActivity) {

    var phoneNumber: String = ""
    private lateinit var onClickNumber: (String) -> Unit

    val request: GetPhoneNumberHintIntentRequest =
        GetPhoneNumberHintIntentRequest.builder().build()

    val phoneNumberHintIntentResultLauncher =
        context.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            try {
                phoneNumber =
                    Identity.getSignInClient(context).getPhoneNumberFromIntent(result.data)
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

}