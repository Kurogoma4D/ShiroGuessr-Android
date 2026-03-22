package dev.krgm4d.shiroguessr.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import dev.krgm4d.shiroguessr.R
import dev.krgm4d.shiroguessr.model.GameState
import dev.krgm4d.shiroguessr.model.RGBColor
import dev.krgm4d.shiroguessr.ui.component.GameControls
import dev.krgm4d.shiroguessr.ui.component.GradientMapView
import dev.krgm4d.shiroguessr.ui.component.MdFilledButton
import dev.krgm4d.shiroguessr.ui.component.RoundResultDialog
import dev.krgm4d.shiroguessr.ui.component.ScoreBoard
import dev.krgm4d.shiroguessr.ui.component.TimerDisplay
import dev.krgm4d.shiroguessr.ui.component.rememberRoundTransitionAnimations
import dev.krgm4d.shiroguessr.ui.theme.ShiroGuessrAndroidTheme
import dev.krgm4d.shiroguessr.viewmodel.MapGamePhase
import dev.krgm4d.shiroguessr.viewmodel.MapGameViewModel

/**
 * Main screen for the map-based color-guessing game mode.
 *
 * Corresponds to the iOS version's `MapGameScreen.swift`.
 * Displays three phases:
 * 1. Start screen with map icon, title, and start button
 * 2. Active game with score board, timer, target color, gradient map, and controls
 * 3. Result animation sequence followed by RoundResultDialog
 *
 * Round transitions include:
 * - Target color fade-in animation (300ms EaseInOut)
 * - Gradient map slide-in from below with spring physics (stiffness: 300, dampingRatio: 0.7)
 * - Controls staggered slide-in (80ms delay)
 *
 * After 5 rounds, navigates to ResultScreen.
 *
 * @param onGameCompleted Callback invoked with the final game state when all rounds are done
 * @param modifier Optional modifier for the root layout
 * @param viewModel ViewModel managing the map game state
 */
@Composable
fun MapGameScreen(
    onGameCompleted: (GameState) -> Unit = {},
    autoStart: Boolean = false,
    modifier: Modifier = Modifier,
    viewModel: MapGameViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Whether the submit button is in its brief freeze state (100ms).
    var isSubmitFrozen by remember { mutableStateOf(false) }

    // Auto-start the game when navigating from Play Again
    LaunchedEffect(autoStart) {
        if (autoStart && uiState.phase == MapGamePhase.NotStarted) {
            viewModel.startNewGame()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        when (uiState.phase) {
            MapGamePhase.NotStarted -> {
                MapStartScreen(
                    onStartGame = { viewModel.startNewGame() },
                )
            }

            MapGamePhase.Playing,
            MapGamePhase.AnimatingResult,
            MapGamePhase.RoundResult,
            -> {
                val currentRound = uiState.currentRound
                val gameState = uiState.gameState
                val gradientMap = uiState.currentGradientMap

                if (currentRound != null && gameState != null && gradientMap != null) {
                    // Calculate map size based on screen width
                    val configuration = LocalConfiguration.current
                    val screenWidth = configuration.screenWidthDp.dp
                    val isTablet = configuration.screenWidthDp >= 600
                    // Map is the star of this mode -- use larger ratio to screen width.
                    // Account for gallery frame border (3dp each side = 6dp total).
                    val mapSize = if (isTablet) {
                        min(screenWidth - 48.dp, 500.dp)
                    } else {
                        min(screenWidth - 48.dp, 360.dp)
                    }

                    // Round transition animations keyed on round number.
                    // Target color fades in; map slides in from below.
                    val animations = rememberRoundTransitionAnimations(currentRound.roundNumber)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
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

                        Spacer(modifier = Modifier.height(8.dp))

                        // Timer display with progress bar and pulse effects
                        TimerDisplay(
                            timeRemaining = uiState.timeRemaining,
                            totalTime = uiState.timeLimit,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Target color display with fade-in
                        MapTargetColorDisplay(
                            targetColor = currentRound.targetColor,
                            modifier = Modifier.alpha(animations.targetAlpha.value),
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Gradient map view with slide-in from below
                        GradientMapView(
                            gradientMap = gradientMap,
                            userPin = currentRound.pin,
                            targetPin = if (uiState.isRoundSubmitted) currentRound.targetPin else null,
                            showTargetPinAnimated = uiState.showTargetPin,
                            lineDrawProgress = uiState.lineDrawProgress,
                            mapSize = mapSize,
                            isInteractionEnabled = !uiState.isRoundSubmitted && !uiState.isAnimatingResult,
                            onPinPlacement = { coordinate ->
                                viewModel.placePin(coordinate)
                            },
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .offset {
                                    IntOffset(0, animations.contentOffsetYDp.value.dp.roundToPx())
                                },
                        )

                        Spacer(modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Game controls with staggered slide-in and submit freeze
                        GameControls(
                            canSubmit = uiState.hasPinPlaced && !uiState.isAnimatingResult && !isSubmitFrozen,
                            canProceed = uiState.isRoundSubmitted && !uiState.isAnimatingResult,
                            onSubmit = {
                                // Brief freeze (100ms) before submitting to build suspense
                                coroutineScope.launch {
                                    isSubmitFrozen = true
                                    kotlinx.coroutines.delay(100L)
                                    viewModel.submitGuess()
                                    isSubmitFrozen = false
                                }
                            },
                            onNext = { viewModel.nextRound() },
                            modifier = Modifier
                                .alpha(animations.controlsAlpha.value)
                                .offset {
                                    IntOffset(0, animations.controlsOffsetYDp.value.dp.roundToPx())
                                },
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // Round result dialog
                    if (uiState.phase == MapGamePhase.RoundResult) {
                        RoundResultDialog(
                            round = currentRound,
                            onNext = { viewModel.nextRound() },
                            onDismiss = { viewModel.dismissRoundResult() },
                        )
                    }
                }
            }

            MapGamePhase.Completed -> {
                LaunchedEffect(Unit) {
                    val completedState = uiState.gameState ?: return@LaunchedEffect
                    onGameCompleted(completedState)
                }
            }
        }
    }
}

/**
 * Start screen for the map game mode.
 *
 * Displays a map icon, app title, subtitle, and a "Start" button.
 */
@Composable
private fun MapStartScreen(
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
            imageVector = Icons.Default.Map,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
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
            text = stringResource(R.string.game_map_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.game_map_tagline),
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

/**
 * Displays the target color that the player needs to find on the gradient map.
 *
 * Shows the color in a rounded rectangle with a label.
 *
 * @param targetColor The RGB color to display
 * @param modifier Optional modifier for the root layout
 */
@Composable
private fun MapTargetColorDisplay(
    targetColor: RGBColor,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp),
            )
            .padding(12.dp),
    ) {
        Text(
            text = stringResource(R.string.game_find_this_color),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(targetColor.toComposeColor())
                .border(
                    width = 1.5.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(8.dp),
                ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MapGameScreenPreview() {
    ShiroGuessrAndroidTheme {
        MapGameScreen()
    }
}
