package dev.krgm4d.shiroguessr.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.krgm4d.shiroguessr.R
import dev.krgm4d.shiroguessr.ui.theme.AccentPrimary
import dev.krgm4d.shiroguessr.ui.theme.CanvasElevated
import dev.krgm4d.shiroguessr.ui.theme.ShiroGuessrAndroidTheme
import dev.krgm4d.shiroguessr.ui.theme.TextSecondary

/**
 * Displays the round dot indicator and cumulative score.
 *
 * Corresponds to the iOS version's `ScoreBoard.swift`.
 * Shows a dot-based round indicator on the left and the score on the right,
 * separated by a vertical divider.
 *
 * Score display follows the Shiro Gallery guideline (Phase 2-5):
 * - Score rendered in gold color (AccentPrimary #C9A96E)
 * - Display Large size font (DM Serif Display) as a hero element
 * - Tabular Figures (monospaced numerals) to prevent digit shifting
 * - Bounce animation (scale spring) on point gain
 * - "Score" label in TextSecondary for subdued contrast
 *
 * Shiro Gallery Card/Panel style:
 * - Background: CanvasElevated (#1A1A22)
 * - Corner radius: 16dp
 * - Border: 1dp #2A2A35
 * - Shadow: 0 4dp 16dp rgba(0,0,0,0.4)
 *
 * @param currentRound Current round number (1-based)
 * @param totalRounds Total number of rounds in the game
 * @param currentScore Cumulative score so far
 * @param modifier Optional modifier for the root layout
 */
@Composable
fun ScoreBoard(
    currentRound: Int,
    totalRounds: Int,
    currentScore: Int,
    modifier: Modifier = Modifier,
) {
    val panelShape = RoundedCornerShape(16.dp)

    // Bounce animation: track previous score and animate scale on change
    val scoreScale = remember { Animatable(1f) }
    var previousScore by remember { mutableIntStateOf(currentScore) }

    LaunchedEffect(currentScore) {
        if (currentScore != previousScore && currentScore > previousScore) {
            // Score increased — trigger bounce animation
            scoreScale.animateTo(
                targetValue = 1.15f,
                animationSpec = spring(
                    dampingRatio = 0.4f,
                    stiffness = 300f,
                ),
            )
            scoreScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = 0.7f,
                    stiffness = 300f,
                ),
            )
        }
        previousScore = currentScore
    }

    Surface(
        shape = panelShape,
        color = CanvasElevated,
        shadowElevation = 16.dp,
        border = BorderStroke(1.dp, Color(0xFF2A2A35)),
        modifier = modifier
            .padding(horizontal = 16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .padding(16.dp),
        ) {
            // Round dot indicator
            RoundIndicator(
                currentRound = currentRound,
                totalRounds = totalRounds,
            )

            // Vertical divider
            VerticalDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 24.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
            )

            // Score display — hero element per Shiro Gallery guideline
            Column {
                Text(
                    text = stringResource(R.string.round_result_score),
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$currentScore",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontFeatureSettings = "tnum",
                    ),
                    color = AccentPrimary,
                    modifier = Modifier.scale(scoreScale.value),
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D0D12)
@Composable
private fun ScoreBoardPreview() {
    ShiroGuessrAndroidTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ScoreBoard(currentRound = 1, totalRounds = 5, currentScore = 0)
            ScoreBoard(currentRound = 3, totalRounds = 5, currentScore = 2450)
            ScoreBoard(currentRound = 5, totalRounds = 5, currentScore = 4800)
        }
    }
}
