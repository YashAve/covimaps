package com.covid.covimaps.ui.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.covid.covimaps.ui.activity.ui.theme.CoviMapsTheme
import com.covid.covimaps.ui.composable.HealthCheck
import com.covid.covimaps.ui.observable.SpeechFromText
import com.covid.covimaps.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SurveyActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var onBackPressedCallback: OnBackPressedCallback

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val speechFromText = SpeechFromText(context = baseContext)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        }

        this.onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        lifecycle.addObserver(speechFromText)

        lifecycleScope.launch {
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