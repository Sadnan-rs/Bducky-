package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = CyanNeon,
  onPrimary = DarkBg,
  primaryContainer = DarkCard,
  onPrimaryContainer = CyanNeon,
  secondary = BlueAccent,
  onSecondary = DarkBg,
  secondaryContainer = DarkBorder,
  onSecondaryContainer = TextWhite,
  tertiary = BlueSecondary,
  onTertiary = Color.White,
  background = DarkBg,
  onBackground = TextWhite,
  surface = DarkSurface,
  onSurface = TextWhite,
  surfaceVariant = DarkCard,
  onSurfaceVariant = TextSlate,
  outline = DarkBorder,
  outlineVariant = DarkBorderSubtle,
  error = StatusRed,
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default is DARK theme as requested
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = Typography,
    content = content
  )
}
