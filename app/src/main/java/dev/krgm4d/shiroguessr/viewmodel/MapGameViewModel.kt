package dev.krgm4d.shiroguessr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.krgm4d.shiroguessr.model.GameRound
import dev.krgm4d.shiroguessr.model.GameState
import dev.krgm4d.shiroguessr.model.GradientMap
import dev.krgm4d.shiroguessr.model.MapCoordinate
import dev.krgm4d.shiroguessr.service.GradientMapService
import dev.krgm4d.shiroguessr.service.MapGameService
import dev.krgm4d.shiroguessr.service.TimerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Game phase representing the current stage of the map game.
 */
sealed interface MapGamePhase {
    /** Initial state before the game starts. */
    data object NotStarted : MapGamePhase

    /** Active gameplay with rounds in progress. */
    data object Playing : MapGamePhase

    /** Showing the animated result sequence after submission. */
    data object AnimatingResult : MapGamePhase

    /** Showing the result dialog for the current round. */
    data object RoundResult : MapGamePhase

    /** All rounds completed. */
    data object Completed : MapGamePhase
}

/**
 * UI state for the map game screen.
 *
 * @property phase Current game phase
 * @property gameState Overall game state including rounds and scores
 * @property currentGradientMap The gradient map for the current round
 * @property showTargetPin Whether the target pin should be shown (after submission)
 * @property lineDrawProgress Progress of the dashed line animation (0.0-1.0)
 * @property totalRounds Total number of rounds in the game
 */
data class MapGameUiState(
    val phase: MapGamePhase = MapGamePhase.NotStarted,
    val gameState: GameState? = null,
    val currentGradientMap: GradientMap? = null,
    val showTargetPin: Boolean = false,
    val lineDrawProgress: Float = 0f,
    val totalRounds: Int = 5,
    val timeRemaining: Int = 60,
) {
    /** The current round based on the game state index. */
    val currentRound: GameRound?
        get() {
            val state = gameState ?: return null
            if (state.currentRoundIndex >= state.rounds.size) return null
            return state.rounds[state.currentRoundIndex]
        }

    /** Whether the current round answer has been submitted. */
    val isRoundSubmitted: Boolean
        get() = currentRound?.selectedColor != null

    /** Whether a pin has been placed on the map. */
    val hasPinPlaced: Boolean
        get() = currentRound?.pin != null

    /** Whether the game is active (not completed). */
    val isGameActive: Boolean
        get() {
            val state = gameState ?: return false
            return !state.isCompleted
        }

    /** Whether the result animation is currently playing. */
    val isAnimatingResult: Boolean
        get() = phase == MapGamePhase.AnimatingResult
}

/**
 * ViewModel for managing map game state and logic.
 *
 * Corresponds to the iOS version's `MapGameViewModel.swift`.
 * Manages a 5-round map game where each round presents a gradient map
 * and a target color. The player must place a pin on the map location
 * that best matches the target color within 60 seconds.
 *
 * @param mapGameService Service for map game logic
 * @param gradientMapService Service for gradient map operations
 * @param timerServiceFactory Factory to create a [TimerService]. Receives the
 *   [CoroutineScope] (typically viewModelScope) so tests can supply their own.
 */
class MapGameViewModel(
    private val mapGameService: MapGameService = MapGameService(),
    private val gradientMapService: GradientMapService = GradientMapService(),
    timerServiceFactory: ((CoroutineScope) -> TimerService)? = null,
) : ViewModel() {

    private val totalRounds = 5
    private val timeLimit = 60

    private val timerService: TimerService =
        (timerServiceFactory ?: { scope -> TimerService(scope) }).invoke(viewModelScope)
    private var animationJob: Job? = null

    private val _uiState = MutableStateFlow(MapGameUiState())

    /** Observable UI state for the map game. */
    val uiState: StateFlow<MapGameUiState> = _uiState.asStateFlow()

    init {
        // Collect timer state
        viewModelScope.launch {
            timerService.timeRemaining.collect { time ->
                _uiState.value = _uiState.value.copy(timeRemaining = time)
            }
        }
    }

    /**
     * Starts a new map game.
     *
     * Generates a gradient map, creates 5 rounds, sets the target color
     * for the first round, and starts the countdown timer.
     */
    fun startNewGame() {
        // Stop any existing timer and animation
        timerService.stop()
        animationJob?.cancel()
        animationJob = null

        // Create new game state
        var gameState = mapGameService.createNewGame(
            totalRounds = totalRounds,
            timeLimit = timeLimit,
        )

        // Generate gradient map for first round
        val gradientMap = gradientMapService.generateGradientMap()

        // Set target color for first round
        gameState = mapGameService.startRound(gameState, gradientMap)

        _uiState.value = MapGameUiState(
            phase = MapGamePhase.Playing,
            gameState = gameState,
            currentGradientMap = gradientMap,
            totalRounds = totalRounds,
            timeRemaining = timeLimit,
        )

        // Start timer
        startRoundTimer()
    }

    /**
     * Places a pin at the specified coordinate on the gradient map.
     *
     * Only allowed when the game is active, not submitted, and not animating.
     *
     * @param coordinate The coordinate where to place the pin
     */
    fun placePin(coordinate: MapCoordinate) {
        val state = _uiState.value
        val gameState = state.gameState ?: return
        val gradientMap = state.currentGradientMap ?: return
        if (state.isRoundSubmitted || state.isAnimatingResult) return

        val updatedGameState = mapGameService.placePin(gameState, coordinate, gradientMap)

        _uiState.value = state.copy(gameState = updatedGameState)
    }

    /**
     * Submits the current guess.
     *
     * Stops the timer, calculates the score based on Manhattan distance,
     * and starts the result animation sequence.
     */
    fun submitGuess() {
        val state = _uiState.value
        val gameState = state.gameState ?: return
        if (!state.hasPinPlaced || state.isRoundSubmitted || state.isAnimatingResult) return

        // Stop the timer
        timerService.stop()

        // Submit with current time remaining
        val updatedGameState = mapGameService.submitGuess(
            gameState,
            timeRemaining = _uiState.value.timeRemaining,
        )

        _uiState.value = state.copy(
            phase = MapGamePhase.AnimatingResult,
            gameState = updatedGameState,
        )

        // Start result animation sequence
        startResultAnimation()
    }

    /**
     * Proceeds to the next round or completes the game.
     */
    fun nextRound() {
        val state = _uiState.value
        val gameState = state.gameState ?: return

        // Cancel any running animation
        animationJob?.cancel()
        animationJob = null

        // Advance to next round
        val updatedGameState = mapGameService.nextRound(gameState)

        if (!updatedGameState.isCompleted) {
            // Generate new gradient map for next round
            val gradientMap = gradientMapService.generateGradientMap()
            val stateWithTarget = mapGameService.startRound(updatedGameState, gradientMap)

            _uiState.value = MapGameUiState(
                phase = MapGamePhase.Playing,
                gameState = stateWithTarget,
                currentGradientMap = gradientMap,
                totalRounds = totalRounds,
                timeRemaining = timeLimit,
            )

            // Start timer for next round
            startRoundTimer()
        } else {
            // Game completed
            timerService.stop()
            _uiState.value = MapGameUiState(
                phase = MapGamePhase.Completed,
                gameState = updatedGameState,
                totalRounds = totalRounds,
            )
        }
    }

    /**
     * Pauses the game timer.
     */
    fun pauseTimer() {
        timerService.pause()
    }

    /**
     * Resumes the game timer.
     */
    fun resumeTimer() {
        timerService.resume()
    }

    /**
     * Resets the game phase to NotStarted without starting a new game.
     */
    fun resetToNotStarted() {
        timerService.stop()
        animationJob?.cancel()
        animationJob = null
        _uiState.value = MapGameUiState()
    }

    private fun startRoundTimer() {
        timerService.start(seconds = timeLimit) {
            handleTimeout()
        }
    }

    /**
     * Handles timeout by automatically placing a pin at the center (0.5, 0.5).
     */
    private fun handleTimeout() {
        val state = _uiState.value
        val gameState = state.gameState ?: return
        val gradientMap = state.currentGradientMap ?: return
        if (state.isRoundSubmitted) return

        val updatedGameState = mapGameService.handleTimeout(gameState, gradientMap)

        _uiState.value = state.copy(
            phase = MapGamePhase.AnimatingResult,
            gameState = updatedGameState,
            timeRemaining = 0,
        )

        startResultAnimation()
    }

    /**
     * Starts the result animation sequence:
     * 1. Show target pin with pop-in animation (0ms)
     * 2. Draw dashed line (180ms delay)
     * 3. Show result dialog (700ms from start)
     */
    private fun startResultAnimation() {
        animationJob = viewModelScope.launch {
            // Phase 1: Show target pin with spring animation
            _uiState.value = _uiState.value.copy(showTargetPin = true)

            // Phase 2: Draw dashed line (180ms delay)
            delay(180L)
            _uiState.value = _uiState.value.copy(lineDrawProgress = 1f)

            // Phase 3: Show result dialog (520ms more = 700ms from start)
            delay(520L)
            _uiState.value = _uiState.value.copy(phase = MapGamePhase.RoundResult)
        }
    }
}
