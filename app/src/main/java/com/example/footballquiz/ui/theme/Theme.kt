package com.example.footballquiz.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PitchGreen,
    secondary = SkyBlue,
    tertiary = GoalGold,
    background = NightBlue,
    surface = SurfaceDark,
    onPrimary = Cream,
    onSecondary = Cream,
    onTertiary = NightBlue,
    onBackground = Cream,
    onSurface = Cream,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = PitchGreen,
    secondary = NightBlue,
    tertiary = GoalGold,
    background = Cream,
    surface = SurfaceLight,
    onPrimary = Cream,
    onSecondary = Cream,
    onTertiary = NightBlue,
    onBackground = NightBlue,
    onSurface = NightBlue,
    error = ErrorRed
)

@Composable
fun FootballQuizTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (darkTheme) DarkColorScheme else LightColorScheme
    } else {
        if (darkTheme) DarkColorScheme else LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
