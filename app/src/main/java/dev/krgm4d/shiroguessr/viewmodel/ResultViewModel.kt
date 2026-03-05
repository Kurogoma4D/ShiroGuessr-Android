package dev.krgm4d.shiroguessr.viewmodel

import androidx.lifecycle.ViewModel
import dev.krgm4d.shiroguessr.model.GameState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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

    /**
     * Sets the completed game state for display on the result screen.
     *
     * @param state The completed game state
     */
    fun setGameState(state: GameState) {
        _gameState.value = state
    }

    /**
     * Clears the game state when navigating away.
     */
    fun clearGameState() {
        _gameState.value = null
    }
}
