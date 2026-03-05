package dev.krgm4d.shiroguessr.model

/**
 * Overall game state.
 *
 * @property rounds All rounds in the game
 * @property currentRoundIndex Current round index (0-based)
 * @property isCompleted Whether the game has been completed
 * @property totalScore Total score across all rounds
 * @property timeLimit Time limit per round in seconds (for map mode)
 */
data class GameState(
    val rounds: List<GameRound>,
    val currentRoundIndex: Int,
    val isCompleted: Boolean,
    val totalScore: Int,
    val timeLimit: Int? = null,
)
