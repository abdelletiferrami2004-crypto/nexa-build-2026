package com.example.util

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
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
 * Real Audio Recorder Manager using Android MediaRecorder API.
 * Handles recording audio notes into local .m4a files with live duration & amplitude tracking.
 */
object AudioRecordManager {

    private const val TAG = "AudioRecordManager"
    private var mediaRecorder: MediaRecorder? = null
    private var currentOutputFile: File? = null
    private var timerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordDurationSeconds = MutableStateFlow(0)
    val recordDurationSeconds: StateFlow<Int> = _recordDurationSeconds.asStateFlow()

    private val _currentAmplitude = MutableStateFlow(0)
    val currentAmplitude: StateFlow<Int> = _currentAmplitude.asStateFlow()

    fun startRecording(context: Context): Boolean {
        if (_isRecording.value) return false
        try {
            val audioDir = File(context.cacheDir, "voice_notes")
            if (!audioDir.exists()) {
                audioDir.mkdirs()
            }
            val fileName = "nexa_voice_${System.currentTimeMillis()}.m4a"
            val file = File(audioDir, fileName)
            currentOutputFile = file

            val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            recorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }

            mediaRecorder = recorder
            _isRecording.value = true
            _recordDurationSeconds.value = 0

            // Start timer & amplitude polling
            timerJob?.cancel()
            timerJob = scope.launch {
                var seconds = 0
                while (isActive && _isRecording.value) {
                    delay(100)
                    try {
                        val maxAmp = mediaRecorder?.maxAmplitude ?: 0
                        _currentAmplitude.value = maxAmp
                    } catch (e: Exception) {
                        _currentAmplitude.value = 0
                    }
                    delay(900)
                    seconds++
                    _recordDurationSeconds.value = seconds
                }
            }

            Log.d(TAG, "Recording started at: ${file.absolutePath}")
            return true
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to start audio recording", e)
            cancelRecording()
            return false
        }
    }

    /**
     * Stops the recording and returns the recorded File and duration in seconds.
     */
    fun stopRecording(): Pair<File?, Int> {
        val duration = _recordDurationSeconds.value
        timerJob?.cancel()
        timerJob = null
        _isRecording.value = false
        _currentAmplitude.value = 0

        try {
            mediaRecorder?.apply {
                try {
                    stop()
                } catch (e: Exception) {
                    Log.e(TAG, "Error stopping recorder", e)
                }
                release()
            }
            mediaRecorder = null
            val file = currentOutputFile
            Log.d(TAG, "Recording finished: ${file?.absolutePath} (duration: $duration s)")
            return Pair(file, if (duration < 1) 1 else duration)
        } catch (e: Throwable) {
            Log.e(TAG, "Error finalizing recording", e)
            mediaRecorder = null
            return Pair(currentOutputFile, if (duration < 1) 1 else duration)
        }
    }

    /**
     * Cancels and deletes the temporary recording.
     */
    fun cancelRecording() {
        timerJob?.cancel()
        timerJob = null
        _isRecording.value = false
        _recordDurationSeconds.value = 0
        _currentAmplitude.value = 0

        try {
            mediaRecorder?.apply {
                try {
                    stop()
                } catch (e: Exception) {
                    // Ignore
                }
                release()
            }
            mediaRecorder = null
            currentOutputFile?.delete()
            currentOutputFile = null
        } catch (e: Throwable) {
            Log.e(TAG, "Error canceling recording", e)
        }
    }
}
