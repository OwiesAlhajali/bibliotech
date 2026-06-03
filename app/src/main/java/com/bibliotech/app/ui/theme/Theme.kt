package com.bibliotech.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Gold,
    secondary = SoftGray,
    tertiary = White,
    background = Navy,
    surface = SurfaceDark,
    onPrimary = Navy,
    onSecondary = White,
    onTertiary = Navy,
    onBackground = White,
    onSurface = White
)

private val LightColorScheme = lightColorScheme(
    // Use splash screen palette for light theme
    primary = Gold,
    secondary = SoftGray,
    tertiary = Navy,
    background = BackgroundLight,
    surface = Color.White,
    onPrimary = Navy,
    onSecondary = Navy,
    onTertiary = White,
    onBackground = Navy,
    onSurface = Navy
)

@Composable
fun BibliotechTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}