package com.app.soundmeter.sound_meter

import kotlinx.coroutines.flow.Flow

interface SoundMeterRepository {
    fun startRecording()
    fun stopRecording()
    fun getDbFlow(): Flow<Double>
}