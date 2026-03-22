package dev.krgm4d.shiroguessr.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.krgm4d.shiroguessr.R
import dev.krgm4d.shiroguessr.ui.theme.CanvasElevated
import dev.krgm4d.shiroguessr.ui.theme.ShiroGuessrAndroidTheme

/**
 * Displays the round dot indicator and cumulative score.
 *
 * Corresponds to the iOS version's `ScoreBoard.swift`.
 * Shows a dot-based round indicator on the left and "Score" on the right,
 * separated by a vertical divider.
 *
 * The round indicator uses dot/step indicators per the Shiro Gallery guideline:
 * - Completed rounds: filled AccentPrimary
 * - Current round: AccentPrimary ring + pulse animation
 * - Upcoming rounds: TextMuted outline ring
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

            // Score display
            Column {
                Text(
                    text = stringResource(R.string.round_result_score),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$currentScore",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
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
