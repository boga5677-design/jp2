package com.petlingo.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightScheme = lightColorScheme(
    primary = Color(0xFF5B4296),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEAE1FF),
    onPrimaryContainer = Color(0xFF2D1E50),
    secondary = Color(0xFF5E7E4C),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE4F2DA),
    onSecondaryContainer = Color(0xFF21351A),
    tertiary = Color(0xFF9A6337),
    onTertiary = Color.White,
    background = Color(0xFFFFFBF3),
    onBackground = Color(0xFF211A17),
    surface = Color(0xFFFFFBF3),
    onSurface = Color(0xFF211A17),
    surfaceVariant = Color(0xFFF7F0E8),
    onSurfaceVariant = Color(0xFF554B45),
    outline = Color(0xFF85756C),
    error = Color(0xFFD84B40),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD5),
    onErrorContainer = Color(0xFF410002)
)

private val DarkScheme = darkColorScheme(
    primary = Color(0xFFD9C7FF),
    onPrimary = Color(0xFF2C1658),
    primaryContainer = Color(0xFF4B3577),
    onPrimaryContainer = Color(0xFFF0E8FF),
    secondary = Color(0xFFC1DDAF),
    onSecondary = Color(0xFF17320F),
    secondaryContainer = Color(0xFF354F2B),
    onSecondaryContainer = Color(0xFFDCEFD0),
    tertiary = Color(0xFFFFC48F),
    onTertiary = Color(0xFF4C2600),
    background = Color(0xFF17131C),
    onBackground = Color(0xFFF1EAF4),
    surface = Color(0xFF1E1923),
    onSurface = Color(0xFFF1EAF4),
    surfaceVariant = Color(0xFF37303D),
    onSurfaceVariant = Color(0xFFE0D7E4),
    outline = Color(0xFF9D919F),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

@Composable
fun PetLingoTheme(themeMode: String = "淺色", content: @Composable () -> Unit) {
    val useDark = when (themeMode) {
        "深色" -> true
        "淺色" -> false
        else -> isSystemInDarkTheme()
    }
    MaterialTheme(
        colorScheme = if (useDark) DarkScheme else LightScheme,
        content = content
    )
}
