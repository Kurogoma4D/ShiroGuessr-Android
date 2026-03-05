package dev.krgm4d.shiroguessr.service

import dev.krgm4d.shiroguessr.model.GameRound
import dev.krgm4d.shiroguessr.model.GameState
import dev.krgm4d.shiroguessr.model.GradientMap
import dev.krgm4d.shiroguessr.model.MapCoordinate
import dev.krgm4d.shiroguessr.model.Pin
import dev.krgm4d.shiroguessr.model.RGBColor
import kotlin.random.Random

/**
 * Service for managing map mode game logic.
 *
 * Corresponds to the iOS version's `MapGameService.swift`.
 * Handles game creation, round management, pin placement,
 * score calculation, and timeout handling for the map game mode.
 *
 * @param colorService Service for color distance calculations
 * @param scoreService Service for score calculations
 * @param gradientMapService Service for gradient map operations
 */
class MapGameService(
    private val colorService: ColorService = ColorService(),
    private val scoreService: ScoreService = ScoreService(),
    private val gradientMapService: GradientMapService = GradientMapService(),
) {

    /**
     * Creates a new map game state with the specified number of rounds and time limit.
     *
     * Rounds are initialized with placeholder target colors; actual target colors
     * are set when each round starts via [startRound].
     *
     * @param totalRounds Number of rounds to play (default: 5)
     * @param timeLimit Time limit per round in seconds (default: 60)
     * @return A new [GameState] configured for map mode
     */
    fun createNewGame(totalRounds: Int = 5, timeLimit: Int = 60): GameState {
        val rounds = (1..totalRounds).map { roundNumber ->
            GameRound(
                roundNumber = roundNumber,
                targetColor = RGBColor(r = 245, g = 245, b = 245),
            )
        }

        return GameState(
            rounds = rounds,
            currentRoundIndex = 0,
            isCompleted = false,
            totalScore = 0,
            timeLimit = timeLimit,
        )
    }

    /**
     * Starts a round by picking a random target coordinate and calculating its color.
     *
     * @param gameState Current game state
     * @param gradientMap The gradient map for this round
     * @param random Random instance for testing (default: [Random.Default])
     * @return Updated game state with target color and target pin set
     */
    fun startRound(
        gameState: GameState,
        gradientMap: GradientMap,
        random: Random = Random.Default,
    ): GameState {
        if (gameState.currentRoundIndex >= gameState.rounds.size) return gameState

        val currentRound = gameState.rounds[gameState.currentRoundIndex]

        // Pick a random target coordinate
        val targetCoordinate = MapCoordinate(
            x = random.nextDouble(),
            y = random.nextDouble(),
        )

        // Get the target color at that coordinate
        val targetColor = gradientMapService.getColorAtCoordinate(gradientMap, targetCoordinate)

        // Create target pin
        val targetPin = Pin(coordinate = targetCoordinate, color = targetColor)

        // Update the current round
        val updatedRound = currentRound.copy(
            targetColor = targetColor,
            targetPin = targetPin,
        )

        val updatedRounds = gameState.rounds.toMutableList().apply {
            set(gameState.currentRoundIndex, updatedRound)
        }

        return gameState.copy(rounds = updatedRounds)
    }

    /**
     * Places a pin at the specified coordinate on the gradient map.
     *
     * The color at the pin location is calculated via bilinear interpolation.
     *
     * @param gameState Current game state
     * @param coordinate Coordinate where to place the pin
     * @param gradientMap The gradient map for the current round
     * @return Updated game state with the pin placed
     */
    fun placePin(
        gameState: GameState,
        coordinate: MapCoordinate,
        gradientMap: GradientMap,
    ): GameState {
        if (gameState.currentRoundIndex >= gameState.rounds.size) return gameState

        val currentRound = gameState.rounds[gameState.currentRoundIndex]
        val pinColor = gradientMapService.getColorAtCoordinate(gradientMap, coordinate)
        val pin = Pin(coordinate = coordinate, color = pinColor)

        val updatedRound = currentRound.copy(pin = pin)
        val updatedRounds = gameState.rounds.toMutableList().apply {
            set(gameState.currentRoundIndex, updatedRound)
        }

        return gameState.copy(rounds = updatedRounds)
    }

    /**
     * Submits the current guess and calculates the score.
     *
     * Uses Manhattan distance between the target color and the pin color
     * to compute the round score.
     *
     * @param gameState Current game state
     * @param timeRemaining Time remaining when guess was submitted
     * @return Updated game state with score calculated
     */
    fun submitGuess(gameState: GameState, timeRemaining: Int): GameState {
        if (gameState.currentRoundIndex >= gameState.rounds.size) return gameState

        val currentRound = gameState.rounds[gameState.currentRoundIndex]
        val pin = currentRound.pin ?: return gameState

        val distance = colorService.calculateDistance(currentRound.targetColor, pin.color)
        val score = scoreService.calculateScore(distance)

        val updatedRound = currentRound.copy(
            selectedColor = pin.color,
            distance = distance,
            score = score,
            timeRemaining = timeRemaining,
        )

        val updatedRounds = gameState.rounds.toMutableList().apply {
            set(gameState.currentRoundIndex, updatedRound)
        }

        val totalScore = scoreService.calculateTotalScore(updatedRounds)

        return gameState.copy(
            rounds = updatedRounds,
            totalScore = totalScore,
        )
    }

    /**
     * Handles timeout by placing a pin at the center of the map (0.5, 0.5)
     * and submitting with 0 time remaining.
     *
     * @param gameState Current game state
     * @param gradientMap The gradient map for the current round
     * @return Updated game state with automatic pin placement and score
     */
    fun handleTimeout(gameState: GameState, gradientMap: GradientMap): GameState {
        val centerCoordinate = MapCoordinate(x = 0.5, y = 0.5)
        val stateWithPin = placePin(gameState, centerCoordinate, gradientMap)
        return submitGuess(stateWithPin, timeRemaining = 0)
    }

    /**
     * Advances to the next round or marks the game as completed.
     *
     * @param gameState Current game state
     * @return Updated game state with next round active or game completed
     */
    fun nextRound(gameState: GameState): GameState {
        val nextIndex = gameState.currentRoundIndex + 1
        val isCompleted = nextIndex >= gameState.rounds.size

        return gameState.copy(
            currentRoundIndex = nextIndex,
            isCompleted = isCompleted,
        )
    }
}
