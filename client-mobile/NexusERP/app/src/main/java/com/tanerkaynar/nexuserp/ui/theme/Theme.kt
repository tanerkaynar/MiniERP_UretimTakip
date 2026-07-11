package com.tanerkaynar.nexuserp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AccentLight,
    onPrimary = CardWhite,
    secondary = DesktopBlue,
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    onBackground = TextLight,
    onSurface = TextLight,
    error = ErrorColor
)

private val LightColorScheme = lightColorScheme(
    primary = AccentLight,
    onPrimary = CardWhite,
    secondary = DesktopBlue,
    background = DesktopWhite,
    surface = CardWhite,
    onBackground = TextDark,
    onSurface = TextDark,
    error = ErrorColor,
    outlineVariant = BorderColor
)

@Composable
fun NexusERPTheme(
    themeMode: Int = 0, 
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        1 -> false
        2 -> true
        else -> isSystemInDarkTheme()
    }
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.secondary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}