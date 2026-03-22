package dev.krgm4d.shiroguessr.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import dev.krgm4d.shiroguessr.ui.theme.AccentContainer
import dev.krgm4d.shiroguessr.ui.theme.AccentPrimary
import dev.krgm4d.shiroguessr.ui.theme.AccentSecondary
import dev.krgm4d.shiroguessr.ui.theme.CanvasDeep
import dev.krgm4d.shiroguessr.ui.theme.TextPrimary
import dev.krgm4d.shiroguessr.ui.theme.TextSecondary

/**
 * Shiro Gallery Button Components for ShiroGuessr.
 *
 * Corresponds to the iOS version's MaterialButtonStyles.swift.
 * All buttons use 24dp corner radius and include a press animation
 * (scale down to 0.97) following the Shiro Gallery design guideline.
 *
 * Three button variants are provided:
 * - Filled (Primary CTA): Gold background, dark text, shadow
 * - Outlined (Secondary): Gold border, gold text, accent container on press
 * - Text (Tertiary): Muted text, underline on press
 */

// Press animation constants matching Shiro Gallery guideline (scale 0.97)
private const val PRESSED_SCALE = 0.97f
private const val DEFAULT_SCALE = 1.0f

// Shared button shape: 24dp corner radius per Shiro Gallery spec
private val ButtonShape = RoundedCornerShape(24.dp)

/**
 * Creates a scale modifier that animates on press following
 * the Shiro Gallery spring: stiffness 300, dampingRatio 0.7.
 */
@Composable
private fun Modifier.pressScale(interactionSource: MutableInteractionSource): Modifier {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) PRESSED_SCALE else DEFAULT_SCALE,
        animationSpec = spring(
            dampingRatio = 0.7f,
            stiffness = 300f
        ),
        label = "pressScale"
    )
    return this.scale(scale)
}

/**
 * Filled button for primary CTA actions (Shiro Gallery "Filled" style).
 *
 * - Background: AccentPrimary (#C9A96E)
 * - Text: CanvasDeep (#0D0D12)
 * - Corner radius: 24dp
 * - Press: scale(0.97) + darken 10%
 * - Shadow: 0 2dp 8dp rgba(0,0,0,0.3)
 */
@Composable
fun MdFilledButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Button(
        onClick = onClick,
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = ButtonShape,
                ambientColor = Color.Black.copy(alpha = 0.3f),
                spotColor = Color.Black.copy(alpha = 0.3f),
            )
            .pressScale(interactionSource),
        enabled = enabled,
        shape = ButtonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = AccentPrimary,
            contentColor = CanvasDeep,
            disabledContainerColor = AccentPrimary.copy(alpha = 0.38f),
            disabledContentColor = CanvasDeep.copy(alpha = 0.38f),
        ),
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content
    )
}

/**
 * Outlined button for secondary actions (Shiro Gallery "Outlined" style).
 *
 * - Border: 1.5dp AccentSecondary (#8B7A5E)
 * - Text: AccentPrimary
 * - Corner radius: 24dp
 * - Press: background AccentContainer + scale(0.97)
 */
@Composable
fun MdOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.pressScale(interactionSource),
        enabled = enabled,
        shape = ButtonShape,
        border = BorderStroke(
            width = 1.5.dp,
            color = if (enabled) AccentSecondary else AccentSecondary.copy(alpha = 0.38f),
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isPressed) AccentContainer else Color.Transparent,
            contentColor = AccentPrimary,
            disabledContentColor = AccentPrimary.copy(alpha = 0.38f),
        ),
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content
    )
}

/**
 * Text button for tertiary / lowest-emphasis actions (Shiro Gallery "Text" style).
 *
 * - Text: TextSecondary (#9995A0)
 * - Press: TextPrimary + underline
 */
@Composable
fun MdTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    TextButton(
        onClick = onClick,
        modifier = modifier.pressScale(interactionSource),
        enabled = enabled,
        shape = ButtonShape,
        colors = ButtonDefaults.textButtonColors(
            contentColor = if (isPressed) TextPrimary else TextSecondary,
            disabledContentColor = TextSecondary.copy(alpha = 0.38f),
        ),
        contentPadding = contentPadding,
        interactionSource = interactionSource,
    ) {
        // Apply underline text decoration when pressed
        val textStyle = if (isPressed) {
            TextStyle(textDecoration = TextDecoration.Underline)
        } else {
            TextStyle.Default
        }
        CompositionLocalProvider(
            LocalTextStyle provides LocalTextStyle.current.merge(textStyle)
        ) {
            content()
        }
    }
}
