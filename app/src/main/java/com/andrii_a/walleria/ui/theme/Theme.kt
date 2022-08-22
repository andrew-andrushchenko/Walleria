package com.andrii_a.walleria.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryVariant = PrimaryVariantDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryVariant = SecondaryVariantDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    error = ErrorDark,
    onError = OnErrorDark
)

private val LightColorPalette = lightColors(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryVariant = PrimaryVariantLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryVariant = SecondaryVariantLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    error = ErrorLight,
    onError = OnErrorLight
)

@Composable
fun WalleriaTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}