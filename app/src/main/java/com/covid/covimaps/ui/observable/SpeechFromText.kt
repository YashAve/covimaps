package com.covid.covimaps.ui.observable

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.util.Locale

class SpeechFromText(private val context: Context) : DefaultLifecycleObserver {

    private lateinit var textToSpeech: TextToSpeech

    init {
        textToSpeech = TextToSpeech(context) {
            if (it != TextToSpeech.ERROR) {
                textToSpeech.language = Locale.UK
            }
        }
    }

    val handleTextToSpeech: (Boolean, String) -> Unit = { enabled, text ->
        textToSpeech.let {
            if (!enabled) {
                val audioManager = context.getSystemService(Activity.AUDIO_SERVICE) as AudioManager
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
                if (it.isSpeaking) it.stop()
            }
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        textToSpeech.also { if (it.isSpeaking) it.stop() }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        textToSpeech.shutdown()
    }
}