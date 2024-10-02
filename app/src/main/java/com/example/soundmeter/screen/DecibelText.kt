package com.example.soundmeter.screen

import android.annotation.SuppressLint
import android.webkit.WebSettings.TextSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.soundmeter.sound_meter.SoundMeterViewModel

@SuppressLint("DefaultLocale")
@Composable
fun DecibelText(
    modifier: Modifier = Modifier,
    viewModel: SoundMeterViewModel = hiltViewModel()
) {
    Column(
        modifier = modifier.fillMaxWidth()
            .padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val decibel by viewModel.decibelFlow.collectAsState(initial = 0.0)
        val dBLevel: String

        dBLevel = when {
            decibel in 0.0 .. 10.0 -> ""
            decibel in 10.0 .. 20.0 -> "${decibel.toInt()}dB, 고양이 걷는소리"
            decibel in 20.0..30.0 -> "${decibel.toInt()}dB, 나뭇잎 부딪치는 소리"
            decibel in 30.0..40.0 -> "${decibel.toInt()}dB, 조용한 독서실"
            decibel in 40.0..50.0 -> "${decibel.toInt()}dB, 일반적인 가정의 소리"
            decibel in 50.0..60.0 -> "${decibel.toInt()}dB, 조용한 사무실 소리"
            decibel in 60.0..70.0 -> "${decibel.toInt()}dB, 일반적인 카페소리"
            decibel in 70.0..80.0 -> "${decibel.toInt()}dB, 시끄러운 시장 소리"
            decibel in 80.0..90.0 -> "${decibel.toInt()}dB, 철도, 전철 소음"
            decibel in 90.0..100.0 -> "${decibel.toInt()}dB, 시끄러운 공장소음"
            decibel in 100.0..1000.0 -> "${decibel.toInt()}dB, 총소리,경적소리"
            else -> "측정불가"
        }
        val elapsedTime = viewModel.getElapsedTime()
        val elapsedTimeText = String.format("%02d:%02d",
            (elapsedTime / 1000) / 60, // 분 계산
            (elapsedTime / 1000) % 60  // 초 계산
        )
        Text(elapsedTimeText, textAlign = TextAlign.Center, fontSize = 15.sp)
        Text(dBLevel, textAlign = TextAlign.Center, fontSize = 25.sp)

        val (minValue, avgValue, maxValue) = viewModel.getMinMaxAvgDecibel()

        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text("최소", textAlign = TextAlign.Center, fontSize = 20.sp)
                Text("평균", textAlign = TextAlign.Center, fontSize = 20.sp)
                Text("최대", textAlign = TextAlign.Center, fontSize = 20.sp)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // 최소값, 평균값, 최대값을 ViewModel에서 가져오는 방식으로 수정하세요
                Text("${minValue.toInt()}", textAlign = TextAlign.Center, fontSize = 20.sp)
                Text("    ${avgValue.toInt()}", textAlign = TextAlign.Center, fontSize = 20.sp)
                Text("  ${maxValue.toInt()}", textAlign = TextAlign.Center, fontSize = 20.sp)
            }
        }

    }
}