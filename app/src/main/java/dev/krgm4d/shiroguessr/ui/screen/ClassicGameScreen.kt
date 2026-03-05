package dev.krgm4d.shiroguessr.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.krgm4d.shiroguessr.model.RGBColor
import dev.krgm4d.shiroguessr.ui.component.ColorPalette
import dev.krgm4d.shiroguessr.ui.component.GameControls
import dev.krgm4d.shiroguessr.ui.component.MdFilledButton
import dev.krgm4d.shiroguessr.ui.component.RoundResultDialog
import dev.krgm4d.shiroguessr.ui.component.ScoreBoard
import dev.krgm4d.shiroguessr.ui.theme.ShiroGuessrAndroidTheme
import dev.krgm4d.shiroguessr.viewmodel.ClassicGamePhase
import dev.krgm4d.shiroguessr.viewmodel.ClassicGameViewModel

/**
 * Main screen for the classic color-guessing game mode.
 *
 * Corresponds to the iOS version's `ClassicGameScreen.swift`.
 * Displays three phases:
 * 1. Start screen with title and start button
 * 2. Active game with score board, target color, color palette, and controls
 * 3. Completed screen with total score after all rounds
 *
 * @param modifier Optional modifier for the root layout
 * @param viewModel ViewModel managing the game state
 */
@Composable
fun ClassicGameScreen(
    modifier: Modifier = Modifier,
    viewModel: ClassicGameViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        when (uiState.phase) {
            ClassicGamePhase.NotStarted -> {
                StartScreen(
                    onStartGame = { viewModel.startNewGame() },
                )
            }

            ClassicGamePhase.Playing, ClassicGamePhase.RoundResult -> {
                val currentRound = uiState.currentRound
                val gameState = uiState.gameState

                if (currentRound != null && gameState != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))

                        // Score board
                        ScoreBoard(
                            currentRound = currentRound.roundNumber,
                            totalRounds = 5,
                            currentScore = gameState.totalScore,
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Target color display
                        TargetColorDisplay(
                            targetColor = currentRound.targetColor,
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Color palette
                        ColorPalette(
                            colors = currentRound.paletteColors,
                            selectedColor = uiState.selectedColor,
                            onColorSelected = { color -> viewModel.selectColor(color) },
                            isEnabled = !uiState.isRoundSubmitted,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Game controls
                        GameControls(
                            canSubmit = uiState.hasSelectedColor && !uiState.isRoundSubmitted,
                            canProceed = uiState.isRoundSubmitted,
                            onSubmit = { viewModel.submitAnswer() },
                            onNext = { viewModel.nextRound() },
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // Round result dialog
                    if (uiState.phase == ClassicGamePhase.RoundResult) {
                        RoundResultDialog(
                            round = currentRound,
                            onNext = { viewModel.nextRound() },
                            onDismiss = { /* Keep dialog open until next is pressed */ },
                        )
                    }
                }
            }

            ClassicGamePhase.Completed -> {
                CompletedScreen(
                    totalScore = uiState.gameState?.totalScore ?: 0,
                    onReplay = { viewModel.resetGame() },
                )
            }
        }
    }
}

/**
 * Start screen shown before the game begins.
 *
 * Displays a palette icon, app title, tagline, and a "Start" button.
 */
@Composable
private fun StartScreen(
    onStartGame: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.Default.Palette,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(80.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "ShiroGuessr",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Can you find the right white?",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.weight(1f))

        MdFilledButton(
            onClick = onStartGame,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
            )
            Text(text = "Start Game")
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

/**
 * Displays the target color that the player needs to match.
 *
 * Shows a label, a large colored rectangle, and the CSS color value.
 */
@Composable
private fun TargetColorDisplay(
    targetColor: RGBColor,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        Text(
            text = "Find this color:",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(targetColor.toComposeColor())
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(16.dp),
                ),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = targetColor.toCSSString(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * Screen shown when all 5 rounds are completed.
 *
 * Displays the total score and a replay button.
 * Will be replaced with a full ResultScreen in a future issue.
 */
@Composable
private fun CompletedScreen(
    totalScore: Int,
    onReplay: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Game Complete!",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Total Score",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Text(
            text = "$totalScore",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.weight(1f))

        MdFilledButton(
            onClick = onReplay,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Play Again")
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun ClassicGameScreenPreview() {
    ShiroGuessrAndroidTheme {
        ClassicGameScreen()
    }
}
