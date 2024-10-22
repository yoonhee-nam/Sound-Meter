package com.yoon.simplesoundmeter.screen

import android.util.Log
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yoon.simplesoundmeter.sound_meter.SoundMeterViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.scroll.AutoScrollCondition
import com.patrykandpatrick.vico.core.scroll.InitialScroll
import com.yoon.simplesoundmeter.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun GraphUi() {
    val viewModel: SoundMeterViewModel = hiltViewModel()

    var value by remember { mutableStateOf(0.0) }
    val modelProducer = remember { ChartEntryModelProducer() }
    val datasetForModel = remember { mutableStateListOf<FloatEntry>() }
    var datasetLIneSpec by remember { mutableStateOf(emptyList<LineChart.LineSpec>()) }

    val scrollState = rememberChartScrollState()

    val scrollSpec = rememberChartScrollSpec(
        isScrollEnabled = true,
        initialScroll = InitialScroll.End,
        autoScrollCondition = AutoScrollCondition.OnModelSizeIncreased,
        autoScrollAnimationSpec = spring()
    )

    val isRecording = remember { mutableStateOf(false) }
    val isPaused = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var xPos by remember { mutableStateOf(0f) }
    var recordingJob: Job? by remember { mutableStateOf(null) }

    LaunchedEffect(isRecording.value) {
        if (isRecording.value) {
            recordingJob?.cancel()

            recordingJob = coroutineScope.launch {
                viewModel.decibelFlow.collect { dbValue ->
                    withContext(Dispatchers.Main) {
                        if (dbValue > 0 && !isPaused.value) {
                            withContext(Dispatchers.Default) {
                                datasetForModel.add(FloatEntry(x = xPos, y = dbValue.toFloat()))
                            }
                            withContext(Dispatchers.Main) {
                                modelProducer.setEntries(datasetForModel)
                                value = dbValue
                                xPos += 1f
                            }
                            delay(500)
                            Log.d("GraphUi","GraphUi Updated Dataset: ${datasetForModel.joinToString()}"
                            )
                        }
                    }
                }
            }
        } else {
            recordingJob?.cancel()
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            if (!isRecording.value) {
                recordingJob?.cancel()
                datasetForModel.clear()
                datasetLIneSpec = emptyList()
                modelProducer.setEntries(emptyList<List<ChartEntry>>())
            }
        }
    }

    LaunchedEffect(value) {
        datasetLIneSpec = listOf(
            LineChart.LineSpec(
                lineColor = when (value.toInt()) {
                    in 0..40 -> Color(0xFF06D001).toArgb()
                    in 41..70 -> Color.Yellow.toArgb()
                    in 71..90 -> Color(0xFFFC4100).toArgb()
                    in 91..1000 -> Color(0xFFE4003A).toArgb()
                    else -> Green.toArgb()
                },
                lineBackgroundShader = DynamicShaders.fromBrush(
                    brush = Brush.verticalGradient(
                        colors = when (value.toInt()) {
                            in 0..40 -> listOf(
                                Color(0xFF06D001).copy(alpha = com.patrykandpatrick.vico.core.DefaultAlpha.LINE_BACKGROUND_SHADER_START),
                                Color(0xFF06D001).copy(alpha = com.patrykandpatrick.vico.core.DefaultAlpha.LINE_BACKGROUND_SHADER_END)
                            )

                            in 41..70 -> listOf(
                                Color.Yellow.copy(alpha = com.patrykandpatrick.vico.core.DefaultAlpha.LINE_BACKGROUND_SHADER_START),
                                Color.Yellow.copy(alpha = com.patrykandpatrick.vico.core.DefaultAlpha.LINE_BACKGROUND_SHADER_END)
                            )

                            in 71..90 -> listOf(
                                Color(0xFFFC4100).copy(alpha = com.patrykandpatrick.vico.core.DefaultAlpha.LINE_BACKGROUND_SHADER_START),
                                Color(0xFFFC4100).copy(alpha = com.patrykandpatrick.vico.core.DefaultAlpha.LINE_BACKGROUND_SHADER_END)
                            )

                            in 91..1000 -> listOf(
                                Color(0xFFE4003A).copy(alpha = com.patrykandpatrick.vico.core.DefaultAlpha.LINE_BACKGROUND_SHADER_START),
                                Color(0xFFE4003A).copy(alpha = com.patrykandpatrick.vico.core.DefaultAlpha.LINE_BACKGROUND_SHADER_END)
                            )

                            else -> listOf(
                                Green.copy(alpha = com.patrykandpatrick.vico.core.DefaultAlpha.LINE_BACKGROUND_SHADER_START),
                                Green.copy(alpha = com.patrykandpatrick.vico.core.DefaultAlpha.LINE_BACKGROUND_SHADER_END)
                            )
                        }
                    )
                )
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp, top = 10.dp)
                .height(250.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black
            )
        ) {
            if (datasetForModel.isNotEmpty() && datasetLIneSpec.isNotEmpty()) {
                ProvideChartStyle {
                    Chart(
                        chart = lineChart(
                            lines = datasetLIneSpec
                        ),
                        chartModelProducer = modelProducer,
                        startAxis = rememberStartAxis(
                            title = "Decibel Level",
                            tickLength = 0.dp,
                            valueFormatter = { value, _ ->
                                ((value.toInt() ).toString())
                            },
                            itemPlacer = AxisItemPlacer.Vertical.default(
                                maxItemCount = 6
                            ),
                        ),
                        bottomAxis = rememberBottomAxis(
                            title = "Time(second)",
                            tickLength = 0.dp,
                            valueFormatter = { value, _ ->
                                if((value/2) % 5 == 0f) (value/2).toInt().toString() else ""
                            },

                        ),
//                        marker =  ,
                        chartScrollState = scrollState,
                        chartScrollSpec = scrollSpec,
                        isZoomEnabled = true
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Bottom
            ) {
                TextButton(onClick = {
                    if (!isRecording.value) {
                        isRecording.value = true
                        viewModel.startRecording()
                        isPaused.value = false
                    }
                }) {
                    Text(text = stringResource(id = R.string.start))
                }

                TextButton(onClick = {
                    if (isRecording.value && !isPaused.value) {
                        isPaused.value = true
                        viewModel.pauseRecording()
                    } else if (isRecording.value && isPaused.value) {
                        isPaused.value = false
                        viewModel.resumeRecording()
                    }
                }) {
                    Text(text = if (isPaused.value) stringResource(id = R.string.resume) else stringResource(id = R.string.pause))
                }

                TextButton(onClick = {
                    if (isRecording.value) {
                        isRecording.value = false
                        viewModel.stopRecording()
                        datasetForModel.clear()
                        datasetLIneSpec = emptyList()
                        modelProducer.setEntries(emptyList<List<ChartEntry>>())
                        xPos = 0f
                        Log.d("GraphUi", "Dataset Size after clear: ${datasetForModel.size}")
                    }
                }) {
                    Text(text = stringResource(id = R.string.stop))
                }
            }
        }
    }
}
