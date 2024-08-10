package com.covid.covimaps.ui.activity

import android.media.AudioManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
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
import com.covid.covimaps.ui.GoogleFonts.shadowsIntoLightFamily
import com.covid.covimaps.ui.activity.ui.theme.CoviMapsTheme
import com.covid.covimaps.ui.component.composable.DisclaimerDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

private lateinit var onFinish: () -> Unit
private lateinit var textToSpeech: TextToSpeech
private lateinit var readOutLoud: (Boolean, String) -> Unit

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        onFinish = { onFinish() }

        textToSpeech = TextToSpeech(applicationContext) {
            if (it != TextToSpeech.ERROR) {
                textToSpeech.language = Locale.UK
            }
        }
        val audioManager = applicationContext.getSystemService(AUDIO_SERVICE) as AudioManager
        readOutLoud = { enabled, text ->
            if (!enabled) {
                val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, AudioManager.FLAG_PLAY_SOUND)
                textToSpeech.run {
                    val params = Bundle()
                    params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
                    speak(
                        text,
                        TextToSpeech.QUEUE_FLUSH,
                        params,
                        null
                    )
                }
            } else {
                textToSpeech.run {
                    if (isSpeaking) stop()
                }
            }
        }

        setContent {
            CoviMapsTheme {
                Profile()
            }
        }
    }
}

@Composable
fun Profile(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    var disclaimer by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch {
            delay(2000)
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
                if (disclaimer) DisclaimerDialog(textToSpeech = textToSpeech, onAgree = {
                    disclaimer = false
                }, onDisagree = {
                    disclaimer = false
                })
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