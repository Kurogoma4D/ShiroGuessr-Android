package dev.krgm4d.shiroguessr.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.krgm4d.shiroguessr.R
import dev.krgm4d.shiroguessr.model.PaletteColor
import dev.krgm4d.shiroguessr.model.RGBColor
import dev.krgm4d.shiroguessr.ui.theme.AccentPrimary
import dev.krgm4d.shiroguessr.ui.theme.CanvasElevated
import dev.krgm4d.shiroguessr.ui.theme.SampleBorder

/**
 * A 5x5 grid of selectable color cells following the Shiro Gallery design.
 *
 * Corresponds to the iOS version's `ColorPalette.swift`.
 * Displays palette colors in a grid layout where each cell can be tapped
 * to select a color. The selected cell shows a gold ring animation with
 * a subtle scale-up effect (1.05x). Each cell also has a gentle press
 * sink effect (0.95x scale) on tap.
 *
 * Shiro Gallery Card/Panel style:
 * - Background: CanvasElevated (#1A1A22)
 * - Corner radius: 16dp
 * - Border: 1dp #2A2A35
 * - Shadow: 0 4dp 16dp rgba(0,0,0,0.4)
 *
 * @param colors List of palette colors to display (typically 25)
 * @param selectedColor Currently selected color, or null if none
 * @param onColorSelected Callback when a color cell is tapped
 * @param isEnabled Whether color selection is allowed
 * @param modifier Optional modifier for the root layout
 */
@Composable
fun ColorPalette(
    colors: List<PaletteColor>,
    selectedColor: RGBColor?,
    onColorSelected: (RGBColor) -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val columnsPerRow = 5
    val panelShape = RoundedCornerShape(16.dp)

    Surface(
        shape = panelShape,
        color = CanvasElevated,
        shadowElevation = 16.dp,
        border = BorderStroke(1.dp, Color(0xFF2A2A35)),
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            colors.chunked(columnsPerRow).forEach { rowColors ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    rowColors.forEach { paletteColor ->
                        ColorCell(
                            color = paletteColor.color,
                            isSelected = selectedColor == paletteColor.color,
                            isEnabled = isEnabled,
                            onClick = { onColorSelected(paletteColor.color) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    // Fill remaining cells in the last row if needed
                    repeat(columnsPerRow - rowColors.size) {
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

/**
 * Individual color cell in the palette grid (Shiro Gallery style).
 *
 * Shows the color as a rounded square with 16dp corner radius.
 * When selected, displays a gold ring border (2.5dp AccentPrimary) with
 * a glow effect and a scale-up animation (1.05x).
 * On tap, applies a gentle sink effect (0.95x scale) that springs back.
 *
 * Shiro Gallery Color Sample Cell style:
 * - Corner radius: 16dp
 * - Border default: 1.5dp SampleBorder (#3A3A45)
 * - Border selected: 2.5dp AccentPrimary (#C9A96E) with glow
 * - Shadow: 0 2dp 6dp rgba(0,0,0,0.3)
 * - Selected scale: 1.05
 * - Press scale: 0.95 (spring animation)
 */
@Composable
private fun ColorCell(
    color: RGBColor,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cellShape = RoundedCornerShape(16.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) AccentPrimary else SampleBorder,
        animationSpec = tween(durationMillis = 200),
        label = "borderColor",
    )

    val borderWidth by animateDpAsState(
        targetValue = if (isSelected) 2.5.dp else 1.5.dp,
        animationSpec = tween(durationMillis = 200),
        label = "borderWidth",
    )

    // Selection scale: 1.05x when selected
    val selectionScale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1.0f,
        animationSpec = spring(
            dampingRatio = 0.7f,
            stiffness = 300f,
        ),
        label = "selectionScale",
    )

    // Press sink scale: 0.95x on tap, springs back to 1.0x
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        animationSpec = spring(
            dampingRatio = 0.7f,
            stiffness = 300f,
        ),
        label = "pressScale",
    )

    // Combine selection and press scales
    val combinedScale = selectionScale * pressScale

    // Glow shadow elevation when selected
    val glowElevation by animateDpAsState(
        targetValue = if (isSelected) 8.dp else 2.dp,
        animationSpec = tween(durationMillis = 200),
        label = "glowElevation",
    )

    val colorCellDescription = stringResource(R.string.cd_color_cell)

    Box(
        modifier = modifier
            .semantics {
                contentDescription = colorCellDescription
                selected = isSelected
            }
            .scale(combinedScale)
            .aspectRatio(1f)
            .shadow(
                elevation = glowElevation,
                shape = cellShape,
                ambientColor = if (isSelected) {
                    AccentPrimary.copy(alpha = 0.4f)
                } else {
                    Color.Black.copy(alpha = 0.3f)
                },
                spotColor = if (isSelected) {
                    AccentPrimary.copy(alpha = 0.4f)
                } else {
                    Color.Black.copy(alpha = 0.3f)
                },
            )
            .clip(cellShape)
            .background(color.toComposeColor())
            .border(borderWidth, borderColor, cellShape)
            .alpha(if (isEnabled) 1f else 0.5f)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = isEnabled,
                onClick = onClick,
            ),
    )
}
