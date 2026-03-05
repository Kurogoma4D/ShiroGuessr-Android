package dev.krgm4d.shiroguessr.viewmodel

import androidx.lifecycle.ViewModel
import dev.krgm4d.shiroguessr.model.GameRound
import dev.krgm4d.shiroguessr.model.GameState
import dev.krgm4d.shiroguessr.model.RGBColor
import dev.krgm4d.shiroguessr.service.ColorService
import dev.krgm4d.shiroguessr.service.ScoreService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Game phase representing the current stage of the classic game.
 */
sealed interface ClassicGamePhase {
    /** Initial state before the game starts. */
    data object NotStarted : ClassicGamePhase

    /** Active gameplay with rounds in progress. */
    data object Playing : ClassicGamePhase

    /** Showing the result of the current round. */
    data object RoundResult : ClassicGamePhase

    /** All rounds completed. */
    data object Completed : ClassicGamePhase
}

/**
 * UI state for the classic game screen.
 *
 * @property phase Current game phase
 * @property gameState Overall game state including rounds and scores
 * @property selectedColor Currently selected color from the palette
 */
data class ClassicGameUiState(
    val phase: ClassicGamePhase = ClassicGamePhase.NotStarted,
    val gameState: GameState? = null,
    val selectedColor: RGBColor? = null,
) {
    /** The current round based on the game state index. */
    val currentRound: GameRound?
        get() {
            val state = gameState ?: return null
            if (state.currentRoundIndex >= state.rounds.size) return null
            return state.rounds[state.currentRoundIndex]
        }

    /** Whether a color has been selected from the palette. */
    val hasSelectedColor: Boolean
        get() = selectedColor != null

    /** Whether the current round answer has been submitted. */
    val isRoundSubmitted: Boolean
        get() = currentRound?.selectedColor != null
}

/**
 * ViewModel for managing classic game state and logic.
 *
 * Corresponds to the iOS version's `GameViewModel.swift`.
 * Manages a 5-round game where each round presents a target white color
 * and a 5x5 palette of white colors to choose from.
 *
 * @param colorService Service for generating colors and calculating distances
 * @param scoreService Service for calculating scores
 */
class ClassicGameViewModel(
    private val colorService: ColorService = ColorService(),
    private val scoreService: ScoreService = ScoreService(),
) : ViewModel() {

    private val totalRounds = 5

    private val _uiState = MutableStateFlow(ClassicGameUiState())

    /** Observable UI state for the classic game. */
    val uiState: StateFlow<ClassicGameUiState> = _uiState.asStateFlow()

    /**
     * Starts a new game with fresh rounds.
     *
     * Generates [totalRounds] rounds, each with a random target color
     * and a palette of 25 random white colors.
     */
    fun startNewGame() {
        val rounds = (1..totalRounds).map { roundNumber ->
            GameRound(
                roundNumber = roundNumber,
                targetColor = colorService.generateRandomWhiteColor(),
                paletteColors = colorService.generatePaletteColors(count = 25),
            )
        }

        val gameState = GameState(
            rounds = rounds,
            currentRoundIndex = 0,
            isCompleted = false,
            totalScore = 0,
        )

        _uiState.value = ClassicGameUiState(
            phase = ClassicGamePhase.Playing,
            gameState = gameState,
            selectedColor = null,
        )
    }

    /**
     * Selects a color from the palette.
     *
     * Only allowed when the game is active and the current round
     * has not been submitted yet.
     *
     * @param color The color to select
     */
    fun selectColor(color: RGBColor) {
        val state = _uiState.value
        if (state.phase != ClassicGamePhase.Playing) return
        if (state.isRoundSubmitted) return

        _uiState.value = state.copy(selectedColor = color)
    }

    /**
     * Submits the selected color as the answer for the current round.
     *
     * Calculates the Manhattan distance and score, updates the round,
     * and transitions to the round result phase.
     */
    fun submitAnswer() {
        val state = _uiState.value
        val gameState = state.gameState ?: return
        val selectedColor = state.selectedColor ?: return
        val currentRound = state.currentRound ?: return
        if (state.isRoundSubmitted) return

        val distance = colorService.calculateDistance(
            currentRound.targetColor,
            selectedColor,
        )
        val score = scoreService.calculateScore(distance)

        val updatedRound = currentRound.copy(
            selectedColor = selectedColor,
            distance = distance,
            score = score,
        )

        val updatedRounds = gameState.rounds.toMutableList().apply {
            set(gameState.currentRoundIndex, updatedRound)
        }

        val totalScore = scoreService.calculateTotalScore(updatedRounds)

        val updatedGameState = gameState.copy(
            rounds = updatedRounds,
            totalScore = totalScore,
        )

        _uiState.value = state.copy(
            phase = ClassicGamePhase.RoundResult,
            gameState = updatedGameState,
        )
    }

    /**
     * Proceeds to the next round or completes the game.
     *
     * If all rounds are completed, transitions to the Completed phase.
     * Otherwise, advances to the next round and resets the selection.
     */
    fun nextRound() {
        val state = _uiState.value
        val gameState = state.gameState ?: return

        val nextIndex = gameState.currentRoundIndex + 1
        val isCompleted = nextIndex >= gameState.rounds.size

        val updatedGameState = gameState.copy(
            currentRoundIndex = nextIndex,
            isCompleted = isCompleted,
        )

        _uiState.value = ClassicGameUiState(
            phase = if (isCompleted) ClassicGamePhase.Completed else ClassicGamePhase.Playing,
            gameState = updatedGameState,
            selectedColor = null,
        )
    }

    /**
     * Resets the game to start fresh.
     */
    fun resetGame() {
        startNewGame()
    }
}
