package com.example.soundmeter.screen

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.soundmeter.sound_meter.SoundMeterViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import kotlin.io.path.Path
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun Meter(
    modifier: Modifier = Modifier,
    clockStyle: ClockStyle = ClockStyle(),
//    decibel: Double
    viewModel: SoundMeterViewModel = hiltViewModel()
) {

    val isDarkMode = isSystemInDarkTheme()

    val backgroundColor = if (isDarkMode) Color.Black else Color.White
    val lineColor = MaterialTheme.colorScheme.onBackground
    val textColor = MaterialTheme.colorScheme.onBackground
    val minuteHandColor = MaterialTheme.colorScheme.onPrimary

    val decibel by viewModel.decibelFlow.collectAsState(initial = 0.0)

    Canvas(modifier = modifier
        .padding(top = 50.dp)) {
        // 설정된 각도
        val startAngle = 160f
        val sweepAngle = 220f

        // 중심점과 반지름 설정
        val radius: Float = (size.minDimension / 2.5f).coerceAtLeast(450f)
        val centerX = center.x
        val centerY = center.y

        // 클립 패스를 사용하여 특정 각도로 잘라내기
        val clipPath = androidx.compose.ui.graphics.Path().apply {
            arcTo(
                rect = Rect(
                    centerX - radius,
                    centerY - radius,
                    centerX + radius,
                    centerY + radius
                ),
                startAngleDegrees = startAngle,
                sweepAngleDegrees = sweepAngle,
                forceMoveTo = true
            )
            lineTo(centerX, centerY)
            close()
        }

        // 클립 패스를 적용하여 화면을 자름
        clipPath(clipPath) {
            // 눈금 및 숫자 그리기
            val gradationCount = 101

            repeat(gradationCount) { index ->
                val angleInDegree =
                    startAngle + (index * sweepAngle / (gradationCount - 1).toDouble())
                val angleInRadian = Math.toRadians(angleInDegree)

                val isMajorGradation = index % 10 == 0
                val isMidGradation = index % 5 == 0 && !isMajorGradation

                val length = when {
                    isMajorGradation -> clockStyle.hourGradationLength.toPx() // 긴 눈금
                    isMidGradation -> clockStyle.minuteGradationLength.toPx() * 1.7f // 중간 눈금
                    else -> clockStyle.minuteGradationLength.toPx() * 1.0f // 짧은 눈금
                }

                val start = Offset(
                    x = (centerX + (radius - length) * cos(angleInRadian)).toFloat(),
                    y = (centerY + (radius - length) * sin(angleInRadian)).toFloat()
                )
                val end = Offset(
                    x = (centerX + radius * cos(angleInRadian)).toFloat(),
                    y = (centerY + radius * sin(angleInRadian)).toFloat()
                )

                // 눈금 그리기
//                val lineColor = if (index in 80..100) Color.Red else if (isMajorGradation) clockStyle.hourGradationColor else clockStyle.minuteGradationColor
                val currentLineColor = when {
                    index in 80..100 -> Color.Red
                    isMajorGradation -> lineColor
                    else -> Color.Gray
                }
                drawLine(
                    color = currentLineColor,
                    start = start,
                    end = end,
                    strokeWidth = if (isMajorGradation) clockStyle.hourGradationWidth.toPx() else clockStyle.minuteGradationWidth.toPx()
                )
            }
        }

        //text
        val gradationCount = 101
        repeat(gradationCount) { index ->
            if (index % 10 == 0) {
                val angleInDegree =
                    startAngle + (index * sweepAngle / (gradationCount - 1).toDouble())
                val angleInRadian = Math.toRadians(angleInDegree)

                val textRadius = radius - clockStyle.textSize.toPx() * 1.7f
                val x = textRadius * cos(angleInRadian) + centerX
                val y = textRadius * sin(angleInRadian) + centerY + clockStyle.textSize.toPx() / 3f

                drawContext.canvas.nativeCanvas.drawText(
                    "${index}",
                    x.toFloat(),
                    y.toFloat(),
                    Paint().apply {
                        color = textColor.toArgb()
                        textSize = (clockStyle.textSize.toPx() * 0.8).toFloat()
                        textAlign = Paint.Align.CENTER
                    }
                )
            }
        }

        // 중심점 그리기
        drawCircle(
            radius = clockStyle.centerCircleSize.toPx(),
            color = clockStyle.centerCircleColor,
            center = Offset(centerX, centerY)
        )

        // 분침 그리기

        val decibelAngleIncrement = sweepAngle / 100.0 // 분침을 0~100으로 나누어 범위 설정
        val decibelHandInDegree = startAngle + decibel * decibelAngleIncrement
        val decibelHandInRadian = Math.toRadians(decibelHandInDegree)
        val decibelLineEnd = Offset(
            x = (centerX + clockStyle.minuteHandLength.toPx() * cos(decibelHandInRadian)).toFloat(),
            y = (centerY + clockStyle.minuteHandLength.toPx() * sin(decibelHandInRadian)).toFloat()
        )

        drawLine(
            color = minuteHandColor,
            start = Offset(centerX, centerY),
            end = decibelLineEnd,
            strokeWidth = clockStyle.minuteHandWidth.toPx(),
            cap = StrokeCap.Round
        )

        val rectWidth = 300f
        val rectHeight = 150f
        val rectTop = centerY + 100f
        val cornerRadius = 20f

        drawRoundRect(
            color = backgroundColor,
            topLeft = Offset(centerX - rectWidth / 2, rectTop),
            size = Size(rectWidth, rectHeight),
            cornerRadius = CornerRadius(cornerRadius, cornerRadius)
        )
        val text = "${decibel.toInt()} dB"

        drawContext.canvas.nativeCanvas.drawText(
            text,
            centerX,
            rectTop + rectHeight / 2 + 30f,
            Paint().apply {
                color = textColor.toArgb()
                textSize = 100f
                textAlign = Paint.Align.CENTER
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MeterPreview() {
    Meter(
        modifier = Modifier.size(300.dp),
        clockStyle = ClockStyle(),
    )
}