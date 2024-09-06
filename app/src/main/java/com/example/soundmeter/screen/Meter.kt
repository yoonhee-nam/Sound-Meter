package com.example.soundmeter.screen

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun Meter (
    modifier: Modifier = Modifier,
    clockStyle: ClockStyle = ClockStyle(),
    hour: Int,
    minute: Int,
    second: Int
) {
    Canvas(modifier = modifier) {
        // 시계 배경
        val radius: Float = size.minDimension / 2.0f // 원의 반지름
        drawContext.canvas.nativeCanvas.apply {
            this.drawCircle(
                center.x,
                center.y,
                radius,
                Paint().apply {
                    color = android.graphics.Color.WHITE
                    setShadowLayer(
                        clockStyle.shadowRadius.toPx(),
                        0f,
                        0f,
                        clockStyle.shadowColor.toArgb()
                    )
                }
            )
        }

        // 중심점
        drawCircle(
            radius = clockStyle.centerCircleSize.toPx(),
            color = clockStyle.centerCircleColor
        )

        // 시계 눈금
        val gradationCount = 100 // 시계눈금 갯수
        repeat(gradationCount) { index ->
            val angleInDegree = (index * 360 / gradationCount).toDouble()
            val angleInRadian = Math.toRadians(angleInDegree)

            val isHourGradation = index % 5 == 0
            val length = if (isHourGradation) {
                clockStyle.hourGradationLength.toPx()
            } else {
                clockStyle.minuteGradationLength.toPx()
            }

            val start = Offset(
                x = (center.x + (radius - length) * cos(angleInRadian)).toFloat(),
                y = (center.y + (radius - length) * sin(angleInRadian)).toFloat()
            )
            val end = Offset(
                x = (center.x + radius * cos(angleInRadian)).toFloat(),
                y = (center.y + radius * sin(angleInRadian)).toFloat()
            )
            val gradationColor: Color = if (isHourGradation) {
                clockStyle.hourGradationColor
            } else {
                clockStyle.minuteGradationColor
            }

            val gradationWidth: Dp = if (isHourGradation) {
                clockStyle.hourGradationWidth
            } else {
                clockStyle.minuteGradationWidth
            }

            //시,분 눈금 그리기
            drawLine(
                color = gradationColor,
                start = start,
                end = end,
                strokeWidth = gradationWidth.toPx()
            )

            // 1~12시 텍스트 그리기
            drawContext.canvas.nativeCanvas.apply {
                if (index % 5 == 0) {
                    var hourText = (index / 5 + 3) % 12
                    if (hourText == 0) {
                        hourText = 12
                    }
                    val textSize = clockStyle.textSize.toPx()
                    val textRadius = radius - length - textSize
                    val x = textRadius * cos(angleInRadian) + center.x
                    val y = textRadius * sin(angleInRadian) + center.y + textSize / 2f
                    drawText(
                        "$hourText",
                        x.toFloat(),
                        y.toFloat(),
                        Paint().apply {
                            this.color = clockStyle.textColor.toArgb()
                            this.textSize = textSize
                            this.textAlign = Paint.Align.CENTER
                        }
                    )
                }
            }
        }

        // 시침 그리기(Hour hand)
        val hourAngleIncrement = 360.0/12.0
        val hourHandInDegree = (
                -90 // 12시부터 시작하도록 90도 꺾음
                        + hour * hourAngleIncrement
                        + hourAngleIncrement * minute.toDouble() / TimeUnit.HOURS.toMinutes(1)
                        + hourAngleIncrement * second.toDouble() / TimeUnit.HOURS.toSeconds(1)
                )
        val hourHandInRadian = Math.toRadians(hourHandInDegree)
        val hourHandStart = Offset(
            x = center.x,
            y = center.y
        )
        val hourHandEnd = Offset(
            x = (center.x + clockStyle.hourHandLength.toPx() * cos(hourHandInRadian)).toFloat(),
            y = (center.y + clockStyle.hourHandLength.toPx() * sin(hourHandInRadian)).toFloat()
        )
        drawLine(
            color = clockStyle.hourHandColor,
            start = hourHandStart,
            end = hourHandEnd,
            strokeWidth = clockStyle.hourHandWidth.toPx(),
            cap = StrokeCap.Round
        )

        // 분침 그리기(Minute hand)
        val minuteAngleIncrement = 360.0/60.0
        val minuteHandInDegree = (
                -90 // 90도 꺾음
                        + minuteAngleIncrement * minute.toDouble()
                        + minuteAngleIncrement * second.toDouble() / TimeUnit.MINUTES.toSeconds(1)
                )

        val minuteHandInRadian = Math.toRadians(minuteHandInDegree)

        val minuteLineStart = Offset(
            x = center.x,
            y = center.y
        )
        val minuteLineEnd = Offset(
            x = (center.x + clockStyle.minuteHandLength.toPx() * cos(minuteHandInRadian)).toFloat(),
            y = (center.y + clockStyle.minuteHandLength.toPx() * sin(minuteHandInRadian)).toFloat()
        )

        drawLine(
            color = clockStyle.minuteHandColor,
            start = minuteLineStart,
            end = minuteLineEnd,
            strokeWidth = clockStyle.minuteHandWidth.toPx(),
            cap = StrokeCap.Round
        )

        // 초침 그리기 (Second hand)
        val secondAngleIncrement = 360.0/60.0
        val secondInDegree = -90 + secondAngleIncrement * second.toDouble()
        val secondInRadian = Math.toRadians(secondInDegree)
        val secondLineStart = Offset(
            x = center.x,
            y = center.y
        )
        val secondLineEnd = Offset(
            x = (center.x + clockStyle.secondHandLength.toPx() * cos(secondInRadian)).toFloat(),
            y = (center.y + clockStyle.secondHandLength.toPx() * sin(secondInRadian)).toFloat()
        )

        drawLine(
            color = clockStyle.secondHandColor,
            start = secondLineStart,
            end = secondLineEnd,
            strokeWidth = clockStyle.secondHandWidth.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
private fun ClockPreview() {
    var currentTime by remember { mutableStateOf(LocalTime.now()) }
    val coroutineScope = rememberCoroutineScope()
    coroutineScope.launch(Dispatchers.IO) {
        while (true){
            delay(500)
            currentTime = LocalTime.now()
        }
    }
    Surface(color = Color.White) {
        Meter(
            modifier = Modifier.size(300.dp),
            clockStyle = ClockStyle(),
            hour = currentTime.hour,
            minute = currentTime.minute,
            second = currentTime.second
        )
    }

}