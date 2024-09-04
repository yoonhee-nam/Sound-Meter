package com.example.soundmeter.sound_meter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SoundMeterViewModel @Inject constructor(
    private val soundMeterRepository: SoundMeterRepository,
    application: Application
) : AndroidViewModel(application){

    private val _decibelFlow = MutableStateFlow(0.0)
    val decibelFlow: StateFlow<Double> = _decibelFlow.asStateFlow()

    fun startRecording(){
        soundMeterRepository.startRecording()
        startDecibeUpdates()
    }
    fun stopRecording(){
        soundMeterRepository.stopRecording()
    }
    private fun startDecibeUpdates(){
        viewModelScope.launch {
            soundMeterRepository.getDbFlow().collect{ db ->
                _decibelFlow.value = db
            }
        }
    }
}