package com.covid.covimaps.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.covid.covimaps.R
import com.covid.covimaps.data.repository.local.SharedPreferenceManager
import com.covid.covimaps.ui.composable.DisclaimerDialog
import com.covid.covimaps.ui.theme.CoviMapsTheme
import com.covid.covimaps.ui.theme.DarkGreen
import com.covid.covimaps.ui.theme.GoogleFonts.shadowsIntoLightFamily
import com.covid.covimaps.ui.theme.veryLightGreen
import java.util.Locale

private const val TAG = "ProfileActivity"

private lateinit var onSurvey: () -> Unit
private lateinit var onStatistics: () -> Unit
private lateinit var sharedPreferenceManager: SharedPreferenceManager

class ProfileActivity : ComponentActivity() {

    private var textToSpeech: TextToSpeech? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        init()

        setContent {
            CoviMapsTheme {
                Profile(
                    onFinish = { finish() },
                    readOutLoud = { enabled, text ->
                        if (!isInPreview()) {
                            handleTextToSpeech(enabled, text)
                        }
                    }
                )
            }
        }
    }

    private fun init() {
        sharedPreferenceManager = SharedPreferenceManager(this)
        onSurvey = {
            val intent = Intent(this, SurveyActivity::class.java)
            startActivity(intent)
        }
        onStatistics = {
            val intent = Intent(this, StatisticsActivity::class.java)
            startActivity(intent)
        }
        if (!isInPreview()) {
            initializeTextToSpeech()
        }
    }

    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(applicationContext) {
            if (it != TextToSpeech.ERROR) {
                textToSpeech?.language = Locale.UK
            }
        }
    }

    private fun handleTextToSpeech(enabled: Boolean, text: String) {
        textToSpeech?.let {
            if (!enabled) {
                val audioManager =
                    applicationContext.getSystemService(AUDIO_SERVICE) as AudioManager
                val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    maxVolume,
                    AudioManager.FLAG_PLAY_SOUND
                )
                val params = Bundle()
                params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
                it.speak(
                    text,
                    TextToSpeech.QUEUE_FLUSH,
                    params,
                    null
                )
            } else {
                if (it.isSpeaking) it.stop() else {
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        textToSpeech?.let { if (it.isSpeaking) it.stop() }
    }

    override fun onDestroy() {
        textToSpeech?.shutdown()
        super.onDestroy()
    }

    private fun isInPreview(): Boolean {
        return false
    }
}

@Composable
fun Profile(
    modifier: Modifier = Modifier,
    onFinish: () -> Unit = {},
    readOutLoud: (Boolean, String) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current
    var agree by rememberSaveable { mutableStateOf(true) }
    var check by rememberSaveable { mutableIntStateOf(0) }

    val onClick: (Int) -> Unit = {
        check = it
        agree = sharedPreferenceManager.isAgree
        if (agree) {
            if (check == 0) onSurvey() else onStatistics()
        }
    }

    Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
        Scaffold(topBar = {
            Box(
                modifier = modifier
                    .padding(23.dp)
                    .statusBarsPadding()
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "exit app",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { onFinish() }
                )
                Text(
                    text = "covimaps",
                    style = TextStyle(
                        fontFamily = shadowsIntoLightFamily,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }) { scaffold ->
            Box(
                modifier = Modifier
                    .padding(scaffold)
                    .fillMaxSize()
            ) {
                if (!agree) DisclaimerDialog(
                    readOutLoud = readOutLoud,
                    onAgree = {
                        sharedPreferenceManager.agree()
                        agree = sharedPreferenceManager.isAgree
                        if (check == 0) onSurvey() else onStatistics()
                    },
                    onDisagree = {
                        agree = true
                    }
                )
                Column(
                    modifier = Modifier
                        .padding(bottom = 30.dp)
                        .fillMaxSize()
                ) {
                    Feature(
                        modifier = Modifier
                            .padding(24.dp)
                            .weight(0.5f),
                        title = "Covid Survey",
                        color = DarkGreen,
                        text = context.resources.getString(
                            R.string.health_survey
                        ),
                        textColor = Color.White
                    ) {
                        onClick(0)
                    }
                    Feature(
                        modifier = Modifier
                            .padding(24.dp)
                            .weight(0.5f),
                        title = "Covid Statistics",
                        color = veryLightGreen,
                        text = context.resources.getString(
                            R.string.covid_statistics
                        ),
                        textColor = Color.Black
                    ) {
                        onClick(1)
                    }
                }
            }
        }
    }
}

@Composable
fun Feature(
    modifier: Modifier = Modifier,
    title: String,
    text: String,
    color: Color,
    textColor: Color,
    onClick: () -> Unit = {},
) {
    val scrollState = rememberScrollState()
    ElevatedCard(modifier = modifier, colors = CardDefaults.cardColors(containerColor = color)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(23.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 7.dp)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = textColor,
                    modifier = Modifier.align(Alignment.Center)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "",
                    tint = textColor,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable { onClick() }
                )
            }
            Text(text = text, color = textColor, modifier = Modifier.verticalScroll(scrollState))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    CoviMapsTheme {
        Profile()
    }
}
