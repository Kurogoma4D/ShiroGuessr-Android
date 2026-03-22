package dev.krgm4d.shiroguessr.ui.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import dev.krgm4d.shiroguessr.R
import dev.krgm4d.shiroguessr.model.GameState
import dev.krgm4d.shiroguessr.ui.component.ColorPalette
import dev.krgm4d.shiroguessr.ui.component.GameControls
import dev.krgm4d.shiroguessr.ui.component.MdFilledButton
import dev.krgm4d.shiroguessr.ui.component.RoundResultDialog
import dev.krgm4d.shiroguessr.ui.component.ScoreBoard
import dev.krgm4d.shiroguessr.ui.component.TargetColorFrame
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
 * Round transitions include:
 * - Target color fade-in animation (300ms EaseInOut)
 * - Palette slide-in from below with spring physics (stiffness: 300, dampingRatio: 0.7)
 * - Submit button brief freeze (100ms) before showing result overlay
 *
 * @param onGameCompleted Callback invoked with the final game state when all rounds are done
 * @param modifier Optional modifier for the root layout
 * @param viewModel ViewModel managing the game state
 */
@Composable
fun ClassicGameScreen(
    onGameCompleted: (GameState) -> Unit = {},
    autoStart: Boolean = false,
    modifier: Modifier = Modifier,
    viewModel: ClassicGameViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Whether the submit button is in its brief freeze state (100ms).
    // During freeze, the button is disabled to convey a moment of suspense.
    var isSubmitFrozen by remember { mutableStateOf(false) }

    // Auto-start the game when navigating from Play Again
    LaunchedEffect(autoStart) {
        if (autoStart && uiState.phase == ClassicGamePhase.NotStarted) {
            viewModel.startNewGame()
        }
    }

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
                    // Round transition animations keyed on round number.
                    // Target color fades in; palette slides in from below.
                    val targetAlpha = remember(currentRound.roundNumber) { Animatable(0f) }
                    val paletteOffsetY = remember(currentRound.roundNumber) { Animatable(200f) }
                    val controlsOffsetY = remember(currentRound.roundNumber) { Animatable(120f) }
                    val controlsAlpha = remember(currentRound.roundNumber) { Animatable(0f) }

                    LaunchedEffect(currentRound.roundNumber) {
                        // Target color fade-in (300ms EaseInOut tween)
                        targetAlpha.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = EaseInOut,
                            ),
                        )
                    }

                    LaunchedEffect(currentRound.roundNumber) {
                        // Palette slide-in from below (spring: stiffness 300, dampingRatio 0.7)
                        paletteOffsetY.animateTo(
                            targetValue = 0f,
                            animationSpec = spring(
                                dampingRatio = 0.7f,
                                stiffness = 300f,
                            ),
                        )
                    }

                    LaunchedEffect(currentRound.roundNumber) {
                        // Controls slide-in slightly delayed for stagger effect
                        kotlinx.coroutines.delay(80L)
                        controlsAlpha.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = EaseInOut,
                            ),
                        )
                    }

                    LaunchedEffect(currentRound.roundNumber) {
                        kotlinx.coroutines.delay(80L)
                        controlsOffsetY.animateTo(
                            targetValue = 0f,
                            animationSpec = spring(
                                dampingRatio = 0.7f,
                                stiffness = 300f,
                            ),
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))

                        // Score board
                        ScoreBoard(
                            currentRound = currentRound.roundNumber,
                            totalRounds = uiState.totalRounds,
                            currentScore = gameState.totalScore,
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Target color display (gallery-frame style) with fade-in
                        TargetColorFrame(
                            targetColor = currentRound.targetColor,
                            showCSSValue = false,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .alpha(targetAlpha.value),
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Color palette with slide-in from below
                        ColorPalette(
                            colors = currentRound.paletteColors,
                            selectedColor = uiState.selectedColor,
                            onColorSelected = { color -> viewModel.selectColor(color) },
                            isEnabled = !uiState.isRoundSubmitted,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .offset {
                                    IntOffset(0, paletteOffsetY.value.dp.roundToPx())
                                },
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Game controls with staggered slide-in and submit freeze
                        GameControls(
                            canSubmit = uiState.hasSelectedColor && !uiState.isRoundSubmitted && !isSubmitFrozen,
                            canProceed = uiState.isRoundSubmitted,
                            onSubmit = {
                                // Brief freeze (100ms) before submitting to build suspense
                                coroutineScope.launch {
                                    isSubmitFrozen = true
                                    kotlinx.coroutines.delay(100L)
                                    viewModel.submitAnswer()
                                    isSubmitFrozen = false
                                }
                            },
                            onNext = { viewModel.nextRound() },
                            modifier = Modifier
                                .alpha(controlsAlpha.value)
                                .offset {
                                    IntOffset(0, controlsOffsetY.value.dp.roundToPx())
                                },
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // Round result dialog
                    if (uiState.phase == ClassicGamePhase.RoundResult) {
                        RoundResultDialog(
                            round = currentRound,
                            onNext = { viewModel.nextRound() },
                            onDismiss = { viewModel.dismissRoundResult() },
                        )
                    }
                }
            }

            ClassicGamePhase.Completed -> {
                LaunchedEffect(Unit) {
                    val completedState = uiState.gameState ?: return@LaunchedEffect
                    onGameCompleted(completedState)
                }
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
            text = stringResource(R.string.app_display_name),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.app_tagline),
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
            Text(text = stringResource(R.string.game_start))
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
