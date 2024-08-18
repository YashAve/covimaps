package com.covid.covimaps.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.covid.covimaps.ui.activity.ui.theme.CoviMapsTheme
import com.covid.covimaps.ui.composable.HealthCheck
import com.covid.covimaps.ui.observable.SpeechFromText
import com.covid.covimaps.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SurveyActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val speechFromText = SpeechFromText(context = baseContext)

        lifecycle.addObserver(speechFromText)

        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getCountries()
        }

        enableEdgeToEdge()
        setContent {
            CoviMapsTheme {
                HealthCheck(
                    readOutLoud = speechFromText.handleTextToSpeech,
                    viewModel = viewModel
                ) {
                    finish()
                }
            }
        }
    }
}