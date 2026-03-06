package dev.krgm4d.shiroguessr.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.krgm4d.shiroguessr.R
import dev.krgm4d.shiroguessr.model.GameRound
import dev.krgm4d.shiroguessr.model.PaletteColor
import dev.krgm4d.shiroguessr.model.RGBColor
import dev.krgm4d.shiroguessr.ui.theme.ShiroGuessrAndroidTheme

/**
 * Bottom sheet dialog displaying the result of a single round.
 *
 * Corresponds to the iOS version's `RoundResultDialog.swift`.
 * Shows a comparison between the target color and the selected color,
 * along with the Manhattan distance and score.
 *
 * @param round The completed game round with results
 * @param onNext Callback to proceed to the next round
 * @param onDismiss Callback when the sheet is dismissed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundResultDialog(
    round: GameRound,
    onNext: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        RoundResultContent(
            round = round,
            onNext = onNext,
        )
    }
}

/**
 * Content of the round result dialog.
 *
 * Extracted as a separate composable for preview support.
 */
@Composable
private fun RoundResultContent(
    round: GameRound,
    onNext: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 8.dp, bottom = 24.dp),
    ) {
        // Header
        Text(
            text = stringResource(R.string.round_result_title, round.roundNumber),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Color comparison
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Target color
            ColorComparisonItem(
                label = stringResource(R.string.round_result_target),
                color = round.targetColor,
            )

            // Arrow between colors
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            // Selected color
            if (round.selectedColor != null) {
                ColorComparisonItem(
                    label = stringResource(R.string.round_result_your_guess),
                    color = round.selectedColor,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Stats section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .padding(vertical = 16.dp),
        ) {
            // Distance
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.round_result_distance),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${round.distance ?: 0}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
            )

            // Score
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.round_result_score),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${round.score ?: 0}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Next button
        MdFilledButton(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.controls_continue))
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ArrowCircleRight,
                contentDescription = null,
            )
        }
    }
}

/**
 * A single color item in the comparison view, showing label, color swatch, and CSS value.
 */
@Composable
private fun ColorComparisonItem(
    label: String,
    color: RGBColor,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.toComposeColor())
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(12.dp),
                ),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = color.toCSSString(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = FontFamily.Monospace,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RoundResultContentPreview() {
    ShiroGuessrAndroidTheme {
        RoundResultContent(
            round = GameRound(
                roundNumber = 1,
                targetColor = RGBColor(r = 250, g = 248, b = 252),
                selectedColor = RGBColor(r = 248, g = 250, b = 250),
                distance = 6,
                score = 800,
                paletteColors = emptyList(),
            ),
            onNext = {},
        )
    }
}
