package dev.krgm4d.shiroguessr.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * ShiroGuessr dark color scheme based on the Shiro Gallery design guideline.
 *
 * Dynamic color is intentionally disabled because this game requires
 * fixed colors for the white-color guessing mechanic.
 * Only a dark theme is used to maximize visibility of white color samples.
 */
private val ShiroGuessrColorScheme = darkColorScheme(
    primary = AccentPrimary,
    onPrimary = CanvasDeep,
    primaryContainer = AccentContainer,
    onPrimaryContainer = AccentPrimary,
    secondary = AccentSecondary,
    onSecondary = CanvasDeep,
    secondaryContainer = CanvasSubtle,
    onSecondaryContainer = TextPrimary,
    tertiary = AccentPrimary,
    onTertiary = CanvasDeep,
    tertiaryContainer = AccentContainer,
    onTertiaryContainer = AccentPrimary,
    error = ScoreLow,
    onError = CanvasDeep,
    errorContainer = ErrorContainerDark,
    onErrorContainer = ScoreLow,
    background = CanvasDeep,
    onBackground = TextPrimary,
    surface = CanvasDeep,
    onSurface = TextPrimary,
    surfaceVariant = CanvasElevated,
    onSurfaceVariant = TextSecondary,
    outline = SampleBorder,
    outlineVariant = CanvasSubtle
)

/**
 * Application theme for ShiroGuessr.
 *
 * Always uses the dark theme with a fixed color scheme.
 * Dynamic color is disabled to ensure consistent color presentation
 * across all devices, which is essential for a color-guessing game.
 *
 * Custom game-specific colors are provided via [LocalShiroGuessrColors].
 */
@Composable
fun ShiroGuessrAndroidTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalShiroGuessrColors provides ShiroGuessrColors()
    ) {
        MaterialTheme(
            colorScheme = ShiroGuessrColorScheme,
            typography = Typography,
            content = content
        )
    }
}

/**
 * Convenience accessor for ShiroGuessr custom colors within a Composable scope.
 */
object ShiroGuessrTheme {
    val colors: ShiroGuessrColors
        @Composable
        get() = LocalShiroGuessrColors.current
}
