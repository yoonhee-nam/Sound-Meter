package com.app.soundmeter.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.soundmeter.R

import com.app.soundmeter.sound_meter.SoundMeterViewModel

@SuppressLint("DefaultLocale")
@Composable
fun DecibelText(
    modifier: Modifier = Modifier,
    viewModel: SoundMeterViewModel = hiltViewModel()
) {
    val textColor = MaterialTheme.colorScheme.onBackground

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val decibel by viewModel.decibelFlow.collectAsState(initial = 0.0)
        val dBLevel: String

        dBLevel = when {
            decibel in 0.0 .. 10.0 -> ""
            decibel in 10.0 .. 20.0 -> stringResource(id = R.string.decibel_10_20)
            decibel in 20.0..30.0 -> stringResource(id = R.string.decibel_20_30)
            decibel in 30.0..40.0 -> stringResource(id = R.string.decibel_30_40)
            decibel in 40.0..50.0 -> stringResource(id = R.string.decibel_40_50)
            decibel in 50.0..60.0 -> stringResource(id = R.string.decibel_50_60)
            decibel in 60.0..70.0 -> stringResource(id = R.string.decibel_60_70)
            decibel in 70.0..80.0 -> stringResource(id = R.string.decibel_70_80)
            decibel in 80.0..90.0 -> stringResource(id = R.string.decibel_80_90)
            decibel in 90.0..100.0 -> stringResource(id = R.string.decibel_90_100)
            decibel in 100.0..1000.0 -> stringResource(id = R.string.decibel_100_1000)
            else -> stringResource(id = R.string.unknown_measurement)
        }

        val elapsedTime = viewModel.getElapsedTime()
        val elapsedTimeText = String.format("%02d:%02d",
            (elapsedTime / 1000) / 60, // 분 계산
            (elapsedTime / 1000) % 60  // 초 계산
        )
        Text(
            elapsedTimeText,
            textAlign = TextAlign.Center,
            fontSize = 15.sp,
            color = textColor
        )
        Text(dBLevel, textAlign = TextAlign.Center, fontSize = 25.sp,color = textColor)

        val (minValue, avgValue, maxValue) = viewModel.getMinMaxAvgDecibel()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(stringResource(id = R.string.min_label), textAlign = TextAlign.Center, fontSize = 20.sp,color = textColor)
                Text(stringResource(id = R.string.average_label), textAlign = TextAlign.Center, fontSize = 20.sp,color = textColor)
                Text(stringResource(id = R.string.max_label), textAlign = TextAlign.Center, fontSize = 20.sp,color = textColor)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text("${minValue.toInt()}", textAlign = TextAlign.Center, fontSize = 20.sp,color = textColor)
                Text("    ${avgValue.toInt()}", textAlign = TextAlign.Center, fontSize = 20.sp,color = textColor)
                Text("  ${maxValue.toInt()}", textAlign = TextAlign.Center, fontSize = 20.sp,color = textColor)
            }
        }
    }
}