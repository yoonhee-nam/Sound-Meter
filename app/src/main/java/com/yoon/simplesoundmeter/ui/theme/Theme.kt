package com.yoon.simplesoundmeter.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,

    background = Color.Black,         // 전체 배경 색상
    surface = Color.Black,            // 표면(카드 등) 배경 색상
    onPrimary = Color.White,          // primary 색상 위의 텍스트 색상
    onSecondary = Color.White,        // secondary 색상 위의 텍스트 색상
    onTertiary = Color.White,         // tertiary 색상 위의 텍스트 색상
    onBackground = Color.White,       // 배경 색상 위의 텍스트 색상
    onSurface = Color.White           // 표면 색상 위의 텍스트 색상
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,

    background = Color.White,         // 전체 배경 색상
    surface = Color.White,            // 표면(카드 등) 배경 색상
    onPrimary = Color.Black,          // primary 색상 위의 텍스트 색상
    onSecondary = Color.Black,        // secondary 색상 위의 텍스트 색상
    onTertiary = Color.Black,         // tertiary 색상 위의 텍스트 색상
    onBackground = Color.Black,       // 배경 색상 위의 텍스트 색상
    onSurface = Color.Black           // 표면 색상 위의 텍스트 색상

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun SoundMeterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}