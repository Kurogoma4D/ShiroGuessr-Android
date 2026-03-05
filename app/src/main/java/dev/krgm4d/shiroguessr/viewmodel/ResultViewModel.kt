package dev.krgm4d.shiroguessr.viewmodel

import androidx.lifecycle.ViewModel
import dev.krgm4d.shiroguessr.model.GameState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * The game mode that produced the result, used to navigate
 * back to the correct screen on "Play Again".
 */
enum class GameMode {
    Classic,
    Map,
}

/**
 * ViewModel for the result screen.
 *
 * Holds the completed game state so the ResultScreen can display
 * round-by-round results and total score. The state is set by the
 * game screen upon completion and consumed by the result screen.
 */
class ResultViewModel : ViewModel() {

    private val _gameState = MutableStateFlow<GameState?>(null)

    /** The completed game state with all round results. */
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()

    private val _gameMode = MutableStateFlow(GameMode.Map)

    /** The game mode that produced the current result. */
    val gameMode: StateFlow<GameMode> = _gameMode.asStateFlow()

    /**
     * Sets the completed game state for display on the result screen.
     *
     * @param state The completed game state
     * @param mode The game mode that produced this result
     */
    fun setGameState(state: GameState, mode: GameMode = GameMode.Map) {
        _gameState.value = state
        _gameMode.value = mode
    }

    /**
     * Clears the game state when navigating away.
     */
    fun clearGameState() {
        _gameState.value = null
    }
}
