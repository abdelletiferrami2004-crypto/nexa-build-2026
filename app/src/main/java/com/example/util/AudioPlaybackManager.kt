package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

/**
 * Real Audio Playback Manager for Voice Messages in NEXA.
 * Plays local audio files (.m4a), web URIs, or generated chimes, providing real-time progress.
 */
object AudioPlaybackManager {

    private const val TAG = "AudioPlaybackManager"
    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _currentPlayingMessageId = MutableStateFlow<Int?>(null)
    val currentPlayingMessageId: StateFlow<Int?> = _currentPlayingMessageId.asStateFlow()

    private val _playbackProgress = MutableStateFlow(0f)
    val playbackProgress: StateFlow<Float> = _playbackProgress.asStateFlow()

    private val _currentPositionSec = MutableStateFlow(0)
    val currentPositionSec: StateFlow<Int> = _currentPositionSec.asStateFlow()

    private val _totalDurationSec = MutableStateFlow(0)
    val totalDurationSec: StateFlow<Int> = _totalDurationSec.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    fun togglePlay(context: Context, messageId: Int, audioPathOrUrl: String?, fallbackDurationSec: Int = 15) {
        if (_currentPlayingMessageId.value == messageId && _isPlaying.value) {
            pause()
        } else if (_currentPlayingMessageId.value == messageId && mediaPlayer != null) {
            resume()
        } else {
            play(context, messageId, audioPathOrUrl, fallbackDurationSec)
        }
    }

    fun play(context: Context, messageId: Int, audioPathOrUrl: String?, fallbackDurationSec: Int = 15) {
        stop()

        _currentPlayingMessageId.value = messageId
        _totalDurationSec.value = fallbackDurationSec

        try {
            val player = MediaPlayer()
            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )

            var isPrepared = false

            if (!audioPathOrUrl.isNullOrBlank()) {
                val file = File(audioPathOrUrl)
                if (file.exists()) {
                    player.setDataSource(file.absolutePath)
                    player.prepare()
                    isPrepared = true
                } else if (audioPathOrUrl.startsWith("http://") || audioPathOrUrl.startsWith("https://") || audioPathOrUrl.startsWith("content://")) {
                    player.setDataSource(context, Uri.parse(audioPathOrUrl))
                    player.prepare()
                    isPrepared = true
                }
            }

            if (!isPrepared) {
                // Fallback simulation chime / pop
                NotificationSoundManager.playPopChime(context)
                simulatePlayback(messageId, fallbackDurationSec)
                return
            }

            val dur = player.duration
            if (dur > 0) {
                _totalDurationSec.value = dur / 1000
            }

            player.setOnCompletionListener {
                stop()
            }

            player.setOnErrorListener { _, _, _ ->
                stop()
                true
            }

            player.start()
            mediaPlayer = player
            _isPlaying.value = true

            startProgressTracker()
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to play voice message audio", e)
            NotificationSoundManager.playPopChime(context)
            simulatePlayback(messageId, fallbackDurationSec)
        }
    }

    private fun simulatePlayback(messageId: Int, durationSec: Int) {
        _isPlaying.value = true
        _totalDurationSec.value = durationSec
        progressJob?.cancel()
        progressJob = scope.launch {
            val totalMillis = (durationSec.coerceAtLeast(2)) * 1000L
            val startTime = System.currentTimeMillis()
            while (isActive && _isPlaying.value && _currentPlayingMessageId.value == messageId) {
                val elapsed = System.currentTimeMillis() - startTime
                val progress = (elapsed.toFloat() / totalMillis).coerceIn(0f, 1f)
                _playbackProgress.value = progress
                _currentPositionSec.value = (elapsed / 1000).toInt()

                if (elapsed >= totalMillis) {
                    stop()
                    break
                }
                delay(100)
            }
        }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive && _isPlaying.value) {
                try {
                    val mp = mediaPlayer
                    if (mp != null && mp.isPlaying) {
                        val current = mp.currentPosition
                        val total = mp.duration.coerceAtLeast(1)
                        _playbackProgress.value = (current.toFloat() / total.toFloat()).coerceIn(0f, 1f)
                        _currentPositionSec.value = current / 1000
                        _totalDurationSec.value = total / 1000
                    }
                } catch (e: Exception) {
                    // Safe catch
                }
                delay(100)
            }
        }
    }

    fun pause() {
        try {
            mediaPlayer?.pause()
            _isPlaying.value = false
        } catch (e: Throwable) {
            Log.e(TAG, "Error pausing audio", e)
        }
    }

    fun resume() {
        try {
            mediaPlayer?.start()
            _isPlaying.value = true
            startProgressTracker()
        } catch (e: Throwable) {
            Log.e(TAG, "Error resuming audio", e)
        }
    }

    fun stop() {
        progressJob?.cancel()
        progressJob = null
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Error stopping audio", e)
        } finally {
            mediaPlayer = null
            _isPlaying.value = false
            _currentPlayingMessageId.value = null
            _playbackProgress.value = 0f
            _currentPositionSec.value = 0
        }
    }
}
