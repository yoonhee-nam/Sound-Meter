package com.example.soundmeter.screen

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun Circle (
    size: Dp,
    color: Color = Color.White,
    borderWidth: Dp = 0.dp,
    borderColor: Color = Color.LightGray.copy(alpha = 0.0f)
){
    var modifier = Modifier

    Box(
        modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
            .border(borderWidth, borderColor)
    )
}


@Composable
fun PulsatingCircles() {
    var value by remember { mutableStateOf(0.0) }
//    val viewModel: RecodingViewModel = hiltViewModel()

//    LaunchedEffect(Unit) {
//        viewModel.startRecording()
//    }

    LaunchedEffect(Unit) {
//        viewModel.decibelFlow.collect { db ->
//            value = db
//        }
    }

    DisposableEffect(Unit) {
        onDispose {
//            viewModel.stopRecording()
        }
    }


    val infiniteTransition = rememberInfiniteTransition()
    val size by infiniteTransition.animateValue(
        initialValue = 200.dp,
        targetValue = 190.dp,
        Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(
                500, easing =
                FastOutLinearInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val smallCircle by infiniteTransition.animateValue(
        initialValue = 150.dp,
        targetValue = 160.dp,
        Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(
                500, easing =
                FastOutLinearInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val circleColors = listOf(
        when (value.toInt()) {
            in 0..40 -> Color(0xFF9BEC00)
            in 41..70 -> Color(0xFFF6FB7A)
            in 71 .. 89 -> Color(0xFFFF6500)
                     in 91 .. 120 -> Color(0xFFC40C0C)
            else -> Color.Green
        },
        when (value.toInt()) {
            in 0..40 -> Color(0xFF06D001)
            in 41..70 -> Color.Yellow
            in 71 .. 89 -> Color(0xFFFC4100)
            in 91 .. 120 -> Color(0xFFE4003A)
            else -> Color.Green
        },
        when (value.toInt()) {
            in 0..40 -> Color(0xFF059212)
            in 41..70 -> Color(0xFFFFDB5C)
            in 71 .. 89 -> Color(0xFFFF8F00)
            in 91 .. 120 -> Color.Red
            else -> Color.Green
        }
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .wrapContentHeight()
            .padding(top = 120.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .align(Alignment.Center)
        ) {
            Circle(
                size = size,
                color = circleColors[0]
            )
        }
        Box(
            modifier = Modifier
                .size(smallCircle)
                .align(Alignment.Center)
        ) {
            Circle(
                size = smallCircle,
                color = circleColors[1]
            )
        }
        Circle(
            size = 130.dp,
            color = circleColors[2]
        )
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value.toInt().toString() + "Db",
                    style = TextStyle().copy(color = MaterialTheme.colorScheme.primary),
                    fontSize = 30.sp
                )
            }
        }
    }
}
