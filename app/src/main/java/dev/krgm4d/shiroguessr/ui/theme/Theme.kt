package dev.krgm4d.shiroguessr.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * ShiroGuessr custom light color scheme.
 *
 * Dynamic color is intentionally disabled because this game requires
 * fixed colors for the white-color guessing mechanic.
 * Only a light theme is used, matching the iOS version's
 * `.preferredColorScheme(.light)` enforcement.
 */
private val ShiroGuessrColorScheme = lightColorScheme(
    primary = MdPrimary,
    onPrimary = MdOnPrimary,
    primaryContainer = MdPrimaryContainer,
    onPrimaryContainer = MdOnPrimaryContainer,
    secondary = MdSecondary,
    onSecondary = MdOnSecondary,
    secondaryContainer = MdSecondaryContainer,
    onSecondaryContainer = MdOnSecondaryContainer,
    tertiary = MdTertiary,
    onTertiary = MdOnTertiary,
    tertiaryContainer = MdTertiaryContainer,
    onTertiaryContainer = MdOnTertiaryContainer,
    error = MdError,
    onError = MdOnError,
    errorContainer = MdErrorContainer,
    onErrorContainer = MdOnErrorContainer,
    background = MdBackground,
    onBackground = MdOnBackground,
    surface = MdSurface,
    onSurface = MdOnSurface,
    surfaceVariant = MdSurfaceVariant,
    onSurfaceVariant = MdOnSurfaceVariant,
    outline = MdOutline,
    outlineVariant = MdOutlineVariant
)

/**
 * Application theme for ShiroGuessr.
 *
 * Always uses the light theme with a fixed color scheme.
 * Dynamic color is disabled to ensure consistent color presentation
 * across all devices, which is essential for a color-guessing game.
 */
@Composable
fun ShiroGuessrAndroidTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ShiroGuessrColorScheme,
        typography = Typography,
        content = content
    )
}
