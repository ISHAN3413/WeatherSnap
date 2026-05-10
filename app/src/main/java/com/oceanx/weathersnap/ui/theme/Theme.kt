package com.oceanx.weathersnap.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary           = AccentGreenYellow,
    onPrimary         = Color(0xFF1A1E0F),
    primaryContainer  = DarkOliveCard,
    onPrimaryContainer= AccentWhite,
    secondary         = AccentTeal,
    onSecondary       = Color(0xFF1A1E0F),
    secondaryContainer= DarkOliveSurface,
    onSecondaryContainer = AccentWhite,
    background        = DarkOliveBlack,
    onBackground      = AccentWhite,
    surface           = DarkOliveCard,
    onSurface         = AccentWhite,
    surfaceVariant    = DarkOliveSurface,
    onSurfaceVariant  = Color(0xFFAAAAAA),
    outline           = Color(0xFF4A5230),
    error             = Color(0xFFFF6B6B),
    onError           = Color.White,
    errorContainer    = Color(0xFF4A1A1A),
    onErrorContainer  = Color(0xFFFFDAD6),
)

@Composable
fun WeatherSnapTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,  // always dark
        typography = Typography,
        content = content
    )
}