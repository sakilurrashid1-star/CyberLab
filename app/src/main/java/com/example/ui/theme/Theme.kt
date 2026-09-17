package com.example.ui.theme

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

private val DarkColorScheme =
  darkColorScheme(
    primary = CyberCyan,
    onPrimary = Color(0xFF00382E),
    primaryContainer = Color(0xFF005144),
    onPrimaryContainer = CyberCyan,
    secondary = CyberGreen,
    onSecondary = Color(0xFF00391A),
    secondaryContainer = CyberGreenContainer,
    onSecondaryContainer = CyberGreen,
    tertiary = CyberAccentBlue,
    background = CyberNavyDark,
    surface = CyberNavySurface,
    surfaceVariant = CyberNavyCard,
    onBackground = CyberTextPrimary,
    onSurface = CyberTextPrimary,
    onSurfaceVariant = CyberTextSecondary,
    outline = CyberNavyBorder,
    error = CyberThreatRed,
    errorContainer = CyberThreatRedContainer,
    onError = Color.White
  )

private val LightColorScheme =
  lightColorScheme(
    primary = CyberLightPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF73F8DF),
    onPrimaryContainer = Color(0xFF00201B),
    secondary = Color(0xFF006D37),
    onSecondary = Color.White,
    tertiary = Color(0xFF006688),
    background = CyberLightBackground,
    surface = CyberLightSurface,
    surfaceVariant = CyberLightCard,
    onBackground = CyberLightText,
    onSurface = CyberLightText,
    onSurfaceVariant = Color(0xFF475569),
    outline = CyberLightBorder,
    error = Color(0xFFBA1A1A),
    onError = Color.White
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our handcrafted cyber color scheme by default
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> DarkColorScheme // Cyber theme shines brightest in dark mode
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

