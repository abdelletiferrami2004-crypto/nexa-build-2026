package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.SoundPool
import android.os.Build
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import kotlin.math.exp
import kotlin.math.sin

/**
 * Ultra-short Glass Pop / iPhone Chime SFX Manager for NEXA Notifications.
 * Plays a high-precision 0.05-second (50ms) sound effect with instant triggering (0ms delay)
 * for messages and comments.
 */
object NotificationSoundManager {

    private const val TAG = "NotificationSound"
    private var soundPool: SoundPool? = null
    private var popSoundId: Int = 0
    private var isLoaded: Boolean = false

    fun init(context: Context) {
        try {
            if (soundPool == null) {
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()

                soundPool = SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build()

                soundPool?.setOnLoadCompleteListener { _, sampleId, status ->
                    if (status == 0 && sampleId == popSoundId) {
                        isLoaded = true
                        Log.d(TAG, "Ultra-short pop sound loaded successfully into SoundPool.")
                    }
                }

                try {
                    val rawId = context.resources.getIdentifier("pop_chime", "raw", context.packageName)
                    if (rawId != 0) {
                        popSoundId = soundPool?.load(context.applicationContext, rawId, 1) ?: 0
                    }
                } catch (e: Throwable) {
                    Log.e(TAG, "Error loading pop_chime raw sound resource", e)
                }
            }
        } catch (e: Throwable) {
            Log.e(TAG, "NotificationSoundManager init exception safely caught", e)
        }
    }

    /**
     * Triggers the ultra-short (0.05s) glass pop / chime sound immediately with zero delay.
     * Uses SoundPool if preloaded, or falls back to instant PCM AudioTrack synthesizer.
     */
    fun playPopChime(context: Context? = null) {
        try {
            if (isLoaded && soundPool != null && popSoundId != 0) {
                soundPool?.play(popSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
            } else {
                // Fallback: Generate and play instant 0.05s synthetic Glass Pop Tone via AudioTrack
                playSyntheticPopChime()
                if (context != null && soundPool == null) {
                    init(context)
                }
            }
        } catch (e: Throwable) {
            Log.e(TAG, "NotificationSoundManager playPopChime exception safely caught", e)
        }
    }

    /**
     * Ultra-short (0.05s = 50ms) high-frequency PCM AudioTrack synth (1500Hz -> 2800Hz Glass Sweep)
     * guarantees immediate physical audio output with no async loading lag.
     */
    private fun playSyntheticPopChime() {
        Thread {
            try {
                val sampleRate = 44100
                val durationSeconds = 0.05 // 50 milliseconds
                val numSamples = (sampleRate * durationSeconds).toInt()
                val byteBuffer = ByteArrayOutputStream()
                val dataOutput = DataOutputStream(byteBuffer)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    // Frequency rapid sweep from 1600Hz to 2900Hz
                    val freq = 1600.0 + (1300.0 * (i.toDouble() / numSamples))
                    // Exponential decay envelope for instant crystal pop sound
                    val envelope = exp(-i.toDouble() / (numSamples * 0.25))
                    val sampleValue = sin(2.0 * Math.PI * freq * t) * envelope * 0.85
                    val pcmSample = (sampleValue * 32767).toInt().coerceIn(-32768, 32767).toShort()
                    dataOutput.writeShort(pcmSample.toInt())
                }

                val pcmData = byteBuffer.toByteArray()
                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )

                val audioTrack = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    AudioTrack.Builder()
                        .setAudioAttributes(
                            AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .build()
                        )
                        .setAudioFormat(
                            AudioFormat.Builder()
                                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                .setSampleRate(sampleRate)
                                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                .build()
                        )
                        .setBufferSizeInBytes(pcmData.size.coerceAtLeast(minBufferSize))
                        .setTransferMode(AudioTrack.MODE_STATIC)
                        .build()
                } else {
                    @Suppress("DEPRECATION")
                    AudioTrack(
                        AudioManager.STREAM_NOTIFICATION,
                        sampleRate,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        pcmData.size.coerceAtLeast(minBufferSize),
                        AudioTrack.MODE_STATIC
                    )
                }

                audioTrack.write(pcmData, 0, pcmData.size)
                audioTrack.play()

                // Release AudioTrack after duration
                Thread.sleep(70)
                audioTrack.stop()
                audioTrack.release()
            } catch (e: Throwable) {
                Log.e(TAG, "Synthetic pop sound playback error safely caught", e)
            }
        }.start()
    }
}
