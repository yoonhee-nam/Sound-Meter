package com.yoon.simplesoundmeter.sound_meter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SoundMeterViewModel @Inject constructor(
    private val soundMeterRepository: SoundMeterRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _decibelFlow = MutableStateFlow(0.0)
    val decibelFlow: StateFlow<Double> = _decibelFlow.asStateFlow()

    private var decibelUpdatesJob: Job? = null
    private var isRecording = false
    private var recordStartTime: Long = 0
    private var elapsedTime: Long = 0
    private val decibelHistory = mutableListOf<Double>()

    fun startRecording() {
        if (!isRecording) {
            isRecording = true
            recordStartTime = System.currentTimeMillis() // 녹음 시작 시간 기록
            soundMeterRepository.startRecording()
            startDecibelUpdates()
        }
    }

    fun stopRecording() {
        if (isRecording) {
            isRecording = false
            elapsedTime = 0 // 초기화
            soundMeterRepository.stopRecording()
            decibelUpdatesJob?.cancel()
            decibelHistory.clear()
        }
    }

    fun pauseRecording() {
        if (isRecording) {
            isRecording = false
            elapsedTime += System.currentTimeMillis() - recordStartTime // 경과 시간 기록
            soundMeterRepository.stopRecording() // 녹음 정지
            decibelUpdatesJob?.cancel()
        }
    }

    fun resumeRecording() {
        if (!isRecording) {
            isRecording = true
            recordStartTime = System.currentTimeMillis() // 녹음 시작 시간 재설정
            soundMeterRepository.startRecording()
            startDecibelUpdates()
        }
    }

    private fun startDecibelUpdates() {
        viewModelScope.launch {
            decibelUpdatesJob?.cancel()

            decibelUpdatesJob = viewModelScope.launch {
                soundMeterRepository.getDbFlow().collect { db ->
                    _decibelFlow.value = db
                    decibelHistory.add(db)
                }
            }
        }
    }
    fun getMinMaxAvgDecibel(): Triple<Double, Double, Double> {
        if (decibelHistory.isEmpty()) return Triple(0.0, 0.0, 0.0)

        val minValue = decibelHistory.minOrNull() ?: 0.0
        val maxValue = decibelHistory.maxOrNull() ?: 0.0
        val avgValue = decibelHistory.average()

        return Triple(minValue, avgValue, maxValue)
    }

    fun getElapsedTime(): Long {
        return if (isRecording) {
            elapsedTime + (System.currentTimeMillis() - recordStartTime) // 현재 경과 시간 계산
        } else {
            elapsedTime // 일시 정지된 경우의 경과 시간
        }
    }

    fun getElapsedTimeInSeconds(): Long {
        return if (isRecording) {
            (System.currentTimeMillis() - recordStartTime) / 1000
        } else {
            0
        }
    }

}