package com.covid.covimaps.ui.activity

import android.media.AudioManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.covid.covimaps.data.repository.local.DataStoreManager
import com.covid.covimaps.ui.composable.DisclaimerDialog
import com.covid.covimaps.ui.theme.CoviMapsTheme
import com.covid.covimaps.ui.theme.GoogleFonts.shadowsIntoLightFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

private const val TAG = "ProfileActivity"

class ProfileActivity : ComponentActivity() {

    private val dataStoreManager by lazy {
        DataStoreManager(context = baseContext)
    }

    private var textToSpeech: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (!isInPreview()) {
            initializeTextToSpeech()
        }

        setContent {
            CoviMapsTheme {
                Profile(
                    onFinish = { finish() },
                    readOutLoud = { enabled, text ->
                        if (!isInPreview()) {
                            handleTextToSpeech(enabled, text)
                        }
                    },
                    agreeToDisclaimer = {
                        lifecycleScope.launch {
                            dataStoreManager.agreeToDisclaimer()
                        }
                    }
                )
            }
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
                val audioManager = applicationContext.getSystemService(AUDIO_SERVICE) as AudioManager
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
                if (it.isSpeaking) it.stop() else TODO()
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
    agreeToDisclaimer: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var disclaimer by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch {
            delay(2000)
            Log.d(TAG, "Profile: checking disclaimer status $disclaimer")
            disclaimer = true
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
                    text = "CoviMaps",
                    style = TextStyle(
                        fontFamily = shadowsIntoLightFamily,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }) { scaffold ->
            Box(modifier = Modifier.padding(scaffold)) {
                if (disclaimer) DisclaimerDialog(
                    readOutLoud = readOutLoud,
                    onAgree = {
                        agreeToDisclaimer()
                        disclaimer = true
                    },
                    onDisagree = {
                        disclaimer = false
                    }
                )
            }
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
