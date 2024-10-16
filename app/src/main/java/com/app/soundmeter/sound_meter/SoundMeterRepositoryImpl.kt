package com.app.soundmeter.sound_meter

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject
import kotlin.math.log10

class SoundMeterRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SoundMeterRepository {

    private var recorder: MediaRecorder? = null

    override fun startRecording() {
        if (recorder == null) {
            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(getOutputFile().absolutePath)

                try {
                    prepare()
                    start()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    override fun stopRecording() {
        try {
            recorder?.apply {
                stop()
                release()
            }
        } catch (e: IllegalStateException) {
            Log.e("RecordingRepositoryImpl", "Failed to stop MediaRecorder: ${e.message}")
            e.printStackTrace()
        } catch (e: RuntimeException) {
            Log.e("RecordingRepositoryImpl", "RuntimeException during stop: ${e.message}")
            e.printStackTrace()
        } finally {
            recorder = null

        }
    }
    override fun getDbFlow(): Flow<Double> = flow {
        recorder?.let {
            while (true) {
                delay(100L)
                val amplitude = it.maxAmplitude

                val db = if (amplitude > 0) {
                    20 * log10(amplitude.toDouble())
                } else {
                    0.0
                }
                emit(db)
                Log.d("getDbFlow", "getDbFlow: $db")
            }
        }
    }.catch { e ->
        e.printStackTrace()
    }

    private fun getOutputFile(): File {
        return File(context.filesDir, "recording.3gp")
    }
}
