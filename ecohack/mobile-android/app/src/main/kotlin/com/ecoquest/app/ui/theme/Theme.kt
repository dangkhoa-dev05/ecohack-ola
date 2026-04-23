package com.ecoquest.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EcoLeaf,
    onPrimary = EcoTextDark,
    secondary = GreenGrey80,
    tertiary = Teal80,
    background = Color(0xFF0F1A16),
    surface = Color(0xFF16231E),
    surfaceVariant = Color(0xFF22342C)
)

private val LightColorScheme = lightColorScheme(
    primary = EcoForest,
    onPrimary = Color.White,
    secondary = EcoMint,
    tertiary = Teal40,
    background = EcoSand,
    surface = EcoSurface,
    surfaceVariant = EcoSky,
    onSurface = EcoTextDark,
    onSurfaceVariant = Color(0xFF4A5E55)
)

@Composable
fun EcoQuestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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
