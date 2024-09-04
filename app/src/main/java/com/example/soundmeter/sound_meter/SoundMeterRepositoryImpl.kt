package com.example.soundmeter.sound_meter

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
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

    private val sampleRate = 44100
    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    private var audioRecord: AudioRecord? = null
//    private var recorder: MediaRecorder? = null

    @SuppressLint("MissingPermission")
    override fun startRecording() {
        if (audioRecord == null || audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )
        }

        audioRecord?.startRecording()
    }


    override fun stopRecording() {
        try {
            audioRecord?.apply {
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
            audioRecord = null
        }
    }

    override fun getDbFlow(): Flow<Double> = flow {
        val buffer = ShortArray(bufferSize)
        audioRecord?.let {
            while (true) {
                val readSize = it.read(buffer, 0, bufferSize)

                var sum = 0.0
                for (i in 0 until readSize) {
                    sum += buffer[i] * buffer[i].toDouble()
                }

                val rms = kotlin.math.sqrt(sum / readSize)
                val db = if (rms > 0) {
                    20 * log10(rms)
                } else {
                    0.0
                }


//            delay(1000L)
//            val amplitude = it.maxAmplitude
//            val db = if (amplitude > 0) {
//                20 * kotlin.math.log10(amplitude.toDouble())
//            } else {
//                0.0
//            }
                emit(db)
            }
        }
    }.flowOn(Dispatchers.Default)
}
//
//private fun getOutputFile(): File {
//    return File(context.filesDir, "recording.3gp")
//}
