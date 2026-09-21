package com.equipo.pixelplay.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = NeonViolet,
    secondary = CyberCyan,
    tertiary = NeonMagenta,
    background = AlmostBlack,
    surface = SurfaceDark,
    onPrimary = OnDark,
    onSecondary = AlmostBlack,
    onTertiary = OnDark,
    onBackground = OnDark,
    onSurface = OnDark
)

private val LightColorScheme = lightColorScheme(
    primary = DeepViolet,
    secondary = DeepCyan,
    tertiary = DeepMagenta,
    background = AlmostWhite,
    surface = SurfaceLight,
    onPrimary = AlmostWhite,
    onSecondary = AlmostWhite,
    onTertiary = AlmostWhite,
    onBackground = OnLight,
    onSurface = OnLight
)

/**
 * Tema PixelPlay. `dynamicColor` por defecto en false para conservar la
 * identidad de la app (con true, Android 12+ la reemplaza por el wallpaper).
 */
@Composable
fun PixelPlayTheme(
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
        shapes = Shapes,
        content = content
    )
}
