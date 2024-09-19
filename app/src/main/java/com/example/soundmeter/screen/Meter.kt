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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import kotlin.io.path.Path
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
        // 설정된 각도
        val startAngle = 160f
        val sweepAngle = 220f

        // 중심점과 반지름 설정
        val radius: Float = size.minDimension / 2.0f
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
                drawLine(
                    color = if (isMajorGradation) clockStyle.hourGradationColor else clockStyle.minuteGradationColor,
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
                val angleInDegree = startAngle + (index * sweepAngle / (gradationCount - 1).toDouble())
                val angleInRadian = Math.toRadians(angleInDegree)

                val textRadius = radius - clockStyle.textSize.toPx() * 1.7f
                val x = textRadius * cos(angleInRadian) + centerX
                val y = textRadius * sin(angleInRadian) + centerY + clockStyle.textSize.toPx() / 3f

                drawContext.canvas.nativeCanvas.drawText(
                    "${index}",
                    x.toFloat(),
                    y.toFloat(),
                    Paint().apply {
                        color = clockStyle.textColor.toArgb()
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

            val minuteAngleIncrement = sweepAngle / 100.0 // 분침을 0~100으로 나누어 범위 설정
            val minuteHandInDegree = startAngle + minute * minuteAngleIncrement
            val minuteHandInRadian = Math.toRadians(minuteHandInDegree)
            val minuteLineEnd = Offset(
                x = (centerX + clockStyle.minuteHandLength.toPx() * cos(minuteHandInRadian)).toFloat(),
                y = (centerY + clockStyle.minuteHandLength.toPx() * sin(minuteHandInRadian)).toFloat()
            )

            drawLine(
                color = clockStyle.minuteHandColor,
                start = Offset(centerX, centerY),
                end = minuteLineEnd,
                strokeWidth = clockStyle.minuteHandWidth.toPx(),
                cap = StrokeCap.Round
            )
        }
    }

@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
 fun ClockPreview() {
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