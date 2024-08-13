package com.covid.covimaps.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.covid.covimaps.ui.activity.ui.theme.CoviMapsTheme
import com.covid.covimaps.ui.composable.HealthCheck
import com.covid.covimaps.ui.observable.SpeechFromText

class SurveyActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val speechFromText = SpeechFromText(context = baseContext)

        lifecycle.addObserver(speechFromText)

        enableEdgeToEdge()
        setContent {
            CoviMapsTheme {
                HealthCheck(readOutLoud = speechFromText.handleTextToSpeech) {
                    finish()
                }
            }
        }
    }
}