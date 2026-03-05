package dev.krgm4d.shiroguessr.ui.screen

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.krgm4d.shiroguessr.model.GameRound
import dev.krgm4d.shiroguessr.model.GameState
import dev.krgm4d.shiroguessr.model.RGBColor
import dev.krgm4d.shiroguessr.ui.component.MdFilledButton
import dev.krgm4d.shiroguessr.ui.component.MdOutlinedButton
import dev.krgm4d.shiroguessr.ui.component.MdTextButton
import dev.krgm4d.shiroguessr.ui.theme.ShiroGuessrAndroidTheme
import dev.krgm4d.shiroguessr.viewmodel.ResultViewModel

/**
 * Result screen displayed after all game rounds are completed.
 *
 * Shows a trophy icon, animated total score counter, round-by-round
 * results with color swatches, and action buttons for replay and sharing.
 *
 * Corresponds to the iOS version's `ResultScreen.swift`.
 *
 * @param onPlayAgain Callback invoked when the player taps "Play Again"
 * @param onShareResults Callback for sharing results (placeholder)
 * @param onCopyToClipboard Callback for copying results to clipboard (placeholder)
 * @param modifier Optional modifier for the root layout
 * @param resultViewModel ViewModel holding the completed game state
 */
@Composable
fun ResultScreen(
    onPlayAgain: () -> Unit,
    onShareResults: () -> Unit = {},
    onCopyToClipboard: () -> Unit = {},
    modifier: Modifier = Modifier,
    resultViewModel: ResultViewModel,
) {
    val gameState by resultViewModel.gameState.collectAsState()

    if (gameState == null) {
        // Fallback if no game state is available
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.fillMaxSize(),
        ) {
            Text(
                text = "No results available",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        return
    }

    ResultScreenContent(
        gameState = gameState!!,
        onPlayAgain = onPlayAgain,
        onShareResults = onShareResults,
        onCopyToClipboard = onCopyToClipboard,
        modifier = modifier,
    )
}

/**
 * Content of the result screen displaying game results.
 *
 * @param gameState The completed game state
 * @param onPlayAgain Callback for replay
 * @param onShareResults Callback for sharing (placeholder)
 * @param onCopyToClipboard Callback for copying (placeholder)
 * @param modifier Optional modifier
 */
@Composable
private fun ResultScreenContent(
    gameState: GameState,
    onPlayAgain: () -> Unit,
    onShareResults: () -> Unit,
    onCopyToClipboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Trigger the count-up animation
    var animateScore by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animateScore = true
    }

    val animatedScore by animateIntAsState(
        targetValue = if (animateScore) gameState.totalScore else 0,
        animationSpec = tween(durationMillis = 1500),
        label = "scoreCountUp",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Trophy icon
        Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = "Trophy",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(80.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
            text = "Game Complete!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Total score label
        Text(
            text = "Total Score",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        // Animated total score
        Text(
            text = "$animatedScore",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Round results header
        Text(
            text = "Round Results",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Round-by-round results
        gameState.rounds.forEach { round ->
            RoundResultRow(round = round)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Action buttons
        MdFilledButton(
            onClick = onPlayAgain,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Default.Replay,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Play Again")
        }

        Spacer(modifier = Modifier.height(12.dp))

        MdOutlinedButton(
            onClick = onShareResults,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Share Results")
        }

        Spacer(modifier = Modifier.height(8.dp))

        MdTextButton(
            onClick = onCopyToClipboard,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Copy to Clipboard")
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

/**
 * A single row displaying the result for one round.
 *
 * Shows the round number, target color swatch, selected color swatch,
 * Manhattan distance, and the score earned.
 *
 * @param round The game round with results
 */
@Composable
private fun RoundResultRow(round: GameRound) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(12.dp),
    ) {
        // Round number
        Text(
            text = "R${round.roundNumber}",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(32.dp),
        )

        // Target color swatch
        ColorSwatch(
            color = round.targetColor,
            label = "Target",
        )

        // Arrow and selected color swatch
        if (round.selectedColor != null) {
            Text(
                text = "\u2192",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp),
            )
            ColorSwatch(
                color = round.selectedColor,
                label = "Selected",
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Distance and score
        Column(
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = if (round.distance != null) "d=${round.distance}" else "\u2014",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = if (round.score != null) "${round.score} pts" else "\u2014",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

/**
 * A small color swatch rectangle for displaying RGB colors.
 *
 * @param color The RGB color to display
 * @param label Content description for accessibility
 */
@Composable
private fun ColorSwatch(
    color: RGBColor,
    label: String,
) {
    Box(
        modifier = Modifier
            .size(width = 36.dp, height = 28.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(color.toComposeColor())
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(6.dp),
            ),
    )
}

@Preview(showBackground = true)
@Composable
private fun ResultScreenContentPreview() {
    val sampleState = GameState(
        rounds = (1..5).map { roundNumber ->
            GameRound(
                roundNumber = roundNumber,
                targetColor = RGBColor(250, 248, 252),
                selectedColor = RGBColor(248, 250, 250),
                distance = roundNumber * 2,
                score = 1000 - roundNumber * 67,
            )
        },
        currentRoundIndex = 5,
        isCompleted = true,
        totalScore = 4335,
    )

    ShiroGuessrAndroidTheme {
        ResultScreenContent(
            gameState = sampleState,
            onPlayAgain = {},
            onShareResults = {},
            onCopyToClipboard = {},
        )
    }
}
