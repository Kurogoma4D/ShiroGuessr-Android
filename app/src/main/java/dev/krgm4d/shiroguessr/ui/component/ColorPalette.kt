package dev.krgm4d.shiroguessr.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.krgm4d.shiroguessr.model.PaletteColor
import dev.krgm4d.shiroguessr.model.RGBColor

/**
 * A 5x5 grid of selectable color cells.
 *
 * Corresponds to the iOS version's `ColorPalette.swift`.
 * Displays palette colors in a grid layout where each cell can be tapped
 * to select a color. The selected cell shows a checkmark overlay.
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
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        modifier = modifier,
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            itemsIndexed(colors) { _, paletteColor ->
                ColorCell(
                    color = paletteColor.color,
                    isSelected = selectedColor == paletteColor.color,
                    isEnabled = isEnabled,
                    onClick = { onColorSelected(paletteColor.color) },
                )
            }
        }
    }
}

/**
 * Individual color cell in the palette grid.
 *
 * Shows the color as a rounded square. When selected, displays a primary-colored
 * border and a checkmark icon overlay.
 */
@Composable
private fun ColorCell(
    color: RGBColor,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outlineVariant
        },
        animationSpec = tween(durationMillis = 200),
        label = "borderColor",
    )

    val borderWidth = if (isSelected) 3.dp else 1.dp

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(color.toComposeColor())
            .border(borderWidth, borderColor, RoundedCornerShape(8.dp))
            .alpha(if (isEnabled) 1f else 0.5f)
            .clickable(enabled = isEnabled, onClick = onClick),
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .background(Color.White, CircleShape)
                    .padding(1.dp),
            )
        }
    }
}
