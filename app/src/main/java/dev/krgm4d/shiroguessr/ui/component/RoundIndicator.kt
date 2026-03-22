package dev.krgm4d.shiroguessr.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.krgm4d.shiroguessr.ui.theme.AccentPrimary
import dev.krgm4d.shiroguessr.ui.theme.ShiroGuessrAndroidTheme
import dev.krgm4d.shiroguessr.ui.theme.TextMuted

/**
 * Round indicator displaying 5 dots representing game progress.
 *
 * Follows the Shiro Gallery design guideline:
 * - Size: 10dp per dot
 * - Completed rounds: filled AccentPrimary (#C9A96E)
 * - Current round: ring AccentPrimary + pulse animation
 * - Upcoming rounds: ring TextMuted (#5C5866)
 * - Spacing: 12dp between dots
 *
 * @param currentRound Current round number (1-based)
 * @param totalRounds Total number of rounds in the game
 * @param modifier Optional modifier for the root layout
 */
@Composable
fun RoundIndicator(
    currentRound: Int,
    totalRounds: Int,
    modifier: Modifier = Modifier,
) {
    val clampedCurrentRound = currentRound.coerceIn(1..totalRounds)

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        for (round in 1..totalRounds) {
            when {
                round < clampedCurrentRound -> CompletedDot()
                round == clampedCurrentRound -> CurrentDot()
                else -> UpcomingDot()
            }
        }
    }
}

/**
 * Filled dot for completed rounds.
 */
@Composable
private fun CompletedDot() {
    Box(
        modifier = Modifier
            .size(10.dp)
            .background(AccentPrimary, CircleShape),
    )
}

/**
 * Ring dot with pulse animation for the current round.
 */
@Composable
private fun CurrentDot() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseScale",
    )

    Box(
        modifier = Modifier
            .size(10.dp)
            .scale(pulseScale)
            .border(
                width = 2.dp,
                color = AccentPrimary,
                shape = CircleShape,
            ),
    )
}

/**
 * Outlined dot for upcoming rounds.
 */
@Composable
private fun UpcomingDot() {
    Box(
        modifier = Modifier
            .size(10.dp)
            .border(
                width = 1.5.dp,
                color = TextMuted,
                shape = CircleShape,
            ),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0D0D12)
@Composable
private fun RoundIndicatorPreview() {
    ShiroGuessrAndroidTheme {
        RoundIndicator(currentRound = 3, totalRounds = 5)
    }
}
