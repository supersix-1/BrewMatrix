package com.brewmatrix.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Custom colors not covered by Material 3 color scheme
@Immutable
data class BrewMatrixColors(
    val gradientStart: Color,
    val gradientEnd: Color,
    val secondaryText: Color,
    val subtleBorder: Color,
)

val LocalBrewMatrixColors = staticCompositionLocalOf {
    BrewMatrixColors(
        gradientStart = Color.Unspecified,
        gradientEnd = Color.Unspecified,
        secondaryText = Color.Unspecified,
        subtleBorder = Color.Unspecified,
    )
}

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    error = LightError,
    onError = LightOnError,
    surfaceVariant = LightSurface,
    onSurfaceVariant = LightSecondaryText,
    outline = LightSubtleBorder,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    error = DarkError,
    onError = DarkOnError,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = DarkSecondaryText,
    outline = DarkSubtleBorder,
)

private val LightBrewMatrixColors = BrewMatrixColors(
    gradientStart = LightGradientStart,
    gradientEnd = LightGradientEnd,
    secondaryText = LightSecondaryText,
    subtleBorder = LightSubtleBorder,
)

private val DarkBrewMatrixColors = BrewMatrixColors(
    gradientStart = DarkGradientStart,
    gradientEnd = DarkGradientEnd,
    secondaryText = DarkSecondaryText,
    subtleBorder = DarkSubtleBorder,
)

@Composable
fun BrewMatrixTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val brewMatrixColors = if (darkTheme) DarkBrewMatrixColors else LightBrewMatrixColors

    CompositionLocalProvider(LocalBrewMatrixColors provides brewMatrixColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = BrewMatrixTypography,
            content = content,
        )
    }
}

// Convenience accessor
object BrewMatrixTheme {
    val extraColors: BrewMatrixColors
        @Composable
        get() = LocalBrewMatrixColors.current
}
