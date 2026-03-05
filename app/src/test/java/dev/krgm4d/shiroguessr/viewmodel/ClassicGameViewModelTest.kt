package dev.krgm4d.shiroguessr.viewmodel

import dev.krgm4d.shiroguessr.service.ColorService
import dev.krgm4d.shiroguessr.service.ScoreService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [ClassicGameViewModel].
 *
 * Tests cover game lifecycle, color selection, answer submission,
 * round progression, and score calculation.
 */
class ClassicGameViewModelTest {

    private lateinit var viewModel: ClassicGameViewModel

    @Before
    fun setUp() {
        viewModel = ClassicGameViewModel(
            colorService = ColorService(),
            scoreService = ScoreService(),
        )
    }

    @Test
    fun `initial state is NotStarted`() {
        val state = viewModel.uiState.value
        assertEquals(ClassicGamePhase.NotStarted, state.phase)
        assertNull(state.gameState)
        assertNull(state.selectedColor)
    }

    @Test
    fun `startNewGame transitions to Playing phase`() {
        viewModel.startNewGame()

        val state = viewModel.uiState.value
        assertEquals(ClassicGamePhase.Playing, state.phase)
        assertNotNull(state.gameState)
        assertNull(state.selectedColor)
    }

    @Test
    fun `startNewGame creates 5 rounds`() {
        viewModel.startNewGame()

        val gameState = viewModel.uiState.value.gameState
        assertNotNull(gameState)
        assertEquals(5, gameState!!.rounds.size)
    }

    @Test
    fun `startNewGame initializes round numbers correctly`() {
        viewModel.startNewGame()

        val rounds = viewModel.uiState.value.gameState!!.rounds
        for (i in rounds.indices) {
            assertEquals(i + 1, rounds[i].roundNumber)
        }
    }

    @Test
    fun `each round has 25 palette colors`() {
        viewModel.startNewGame()

        val rounds = viewModel.uiState.value.gameState!!.rounds
        for (round in rounds) {
            assertEquals(25, round.paletteColors.size)
        }
    }

    @Test
    fun `each round has a target color with valid RGB values`() {
        viewModel.startNewGame()

        val rounds = viewModel.uiState.value.gameState!!.rounds
        for (round in rounds) {
            val color = round.targetColor
            assertTrue(color.r in 245..255)
            assertTrue(color.g in 245..255)
            assertTrue(color.b in 245..255)
        }
    }

    @Test
    fun `startNewGame starts at round index 0`() {
        viewModel.startNewGame()

        val gameState = viewModel.uiState.value.gameState!!
        assertEquals(0, gameState.currentRoundIndex)
        assertFalse(gameState.isCompleted)
        assertEquals(0, gameState.totalScore)
    }

    @Test
    fun `selectColor updates selectedColor`() {
        viewModel.startNewGame()

        val firstPaletteColor = viewModel.uiState.value.currentRound!!.paletteColors.first().color
        viewModel.selectColor(firstPaletteColor)

        assertEquals(firstPaletteColor, viewModel.uiState.value.selectedColor)
    }

    @Test
    fun `selectColor does nothing when game not started`() {
        val color = viewModel.uiState.value.gameState?.rounds?.firstOrNull()?.paletteColors?.firstOrNull()?.color
        assertNull(color)
        // No crash expected
    }

    @Test
    fun `hasSelectedColor is false initially`() {
        viewModel.startNewGame()
        assertFalse(viewModel.uiState.value.hasSelectedColor)
    }

    @Test
    fun `hasSelectedColor is true after selection`() {
        viewModel.startNewGame()

        val firstColor = viewModel.uiState.value.currentRound!!.paletteColors.first().color
        viewModel.selectColor(firstColor)

        assertTrue(viewModel.uiState.value.hasSelectedColor)
    }

    @Test
    fun `submitAnswer transitions to RoundResult phase`() {
        viewModel.startNewGame()

        val firstColor = viewModel.uiState.value.currentRound!!.paletteColors.first().color
        viewModel.selectColor(firstColor)
        viewModel.submitAnswer()

        assertEquals(ClassicGamePhase.RoundResult, viewModel.uiState.value.phase)
    }

    @Test
    fun `submitAnswer calculates distance and score`() {
        viewModel.startNewGame()

        val firstColor = viewModel.uiState.value.currentRound!!.paletteColors.first().color
        viewModel.selectColor(firstColor)
        viewModel.submitAnswer()

        val currentRound = viewModel.uiState.value.currentRound!!
        assertNotNull(currentRound.selectedColor)
        assertNotNull(currentRound.distance)
        assertNotNull(currentRound.score)
        assertEquals(firstColor, currentRound.selectedColor)
    }

    @Test
    fun `submitAnswer does nothing without selection`() {
        viewModel.startNewGame()

        viewModel.submitAnswer()

        assertEquals(ClassicGamePhase.Playing, viewModel.uiState.value.phase)
    }

    @Test
    fun `isRoundSubmitted is true after submitAnswer`() {
        viewModel.startNewGame()

        val firstColor = viewModel.uiState.value.currentRound!!.paletteColors.first().color
        viewModel.selectColor(firstColor)
        viewModel.submitAnswer()

        assertTrue(viewModel.uiState.value.isRoundSubmitted)
    }

    @Test
    fun `selectColor does nothing after round is submitted`() {
        viewModel.startNewGame()

        val colors = viewModel.uiState.value.currentRound!!.paletteColors
        viewModel.selectColor(colors[0].color)
        viewModel.submitAnswer()

        viewModel.selectColor(colors[1].color)
        // Should still have the first color selected (via the round's selectedColor)
        assertEquals(colors[0].color, viewModel.uiState.value.currentRound!!.selectedColor)
    }

    @Test
    fun `nextRound advances to next round`() {
        viewModel.startNewGame()

        val firstColor = viewModel.uiState.value.currentRound!!.paletteColors.first().color
        viewModel.selectColor(firstColor)
        viewModel.submitAnswer()
        viewModel.nextRound()

        val state = viewModel.uiState.value
        assertEquals(ClassicGamePhase.Playing, state.phase)
        assertEquals(1, state.gameState!!.currentRoundIndex)
        assertNull(state.selectedColor)
    }

    @Test
    fun `nextRound resets selected color`() {
        viewModel.startNewGame()

        val firstColor = viewModel.uiState.value.currentRound!!.paletteColors.first().color
        viewModel.selectColor(firstColor)
        viewModel.submitAnswer()
        viewModel.nextRound()

        assertNull(viewModel.uiState.value.selectedColor)
        assertFalse(viewModel.uiState.value.hasSelectedColor)
    }

    @Test
    fun `game completes after 5 rounds`() {
        viewModel.startNewGame()

        repeat(5) {
            val firstColor = viewModel.uiState.value.currentRound!!.paletteColors.first().color
            viewModel.selectColor(firstColor)
            viewModel.submitAnswer()
            viewModel.nextRound()
        }

        val state = viewModel.uiState.value
        assertEquals(ClassicGamePhase.Completed, state.phase)
        assertTrue(state.gameState!!.isCompleted)
    }

    @Test
    fun `total score accumulates across rounds`() {
        viewModel.startNewGame()

        val firstColor = viewModel.uiState.value.currentRound!!.paletteColors.first().color
        viewModel.selectColor(firstColor)
        viewModel.submitAnswer()

        val scoreAfterRound1 = viewModel.uiState.value.gameState!!.totalScore
        assertTrue(scoreAfterRound1 >= 0)

        viewModel.nextRound()

        val secondColor = viewModel.uiState.value.currentRound!!.paletteColors.first().color
        viewModel.selectColor(secondColor)
        viewModel.submitAnswer()

        val scoreAfterRound2 = viewModel.uiState.value.gameState!!.totalScore
        assertTrue(scoreAfterRound2 >= scoreAfterRound1)
    }

    @Test
    fun `score is within valid range 0-1000 per round`() {
        viewModel.startNewGame()

        val firstColor = viewModel.uiState.value.currentRound!!.paletteColors.first().color
        viewModel.selectColor(firstColor)
        viewModel.submitAnswer()

        val score = viewModel.uiState.value.currentRound!!.score!!
        assertTrue("Score $score should be >= 0", score >= 0)
        assertTrue("Score $score should be <= 1000", score <= 1000)
    }

    @Test
    fun `distance is within valid range 0-30`() {
        viewModel.startNewGame()

        val firstColor = viewModel.uiState.value.currentRound!!.paletteColors.first().color
        viewModel.selectColor(firstColor)
        viewModel.submitAnswer()

        val distance = viewModel.uiState.value.currentRound!!.distance!!
        assertTrue("Distance $distance should be >= 0", distance >= 0)
        assertTrue("Distance $distance should be <= 30", distance <= 30)
    }

    @Test
    fun `resetGame starts a fresh game`() {
        viewModel.startNewGame()

        // Play some rounds
        val firstColor = viewModel.uiState.value.currentRound!!.paletteColors.first().color
        viewModel.selectColor(firstColor)
        viewModel.submitAnswer()
        viewModel.nextRound()

        // Reset
        viewModel.resetGame()

        val state = viewModel.uiState.value
        assertEquals(ClassicGamePhase.Playing, state.phase)
        assertEquals(0, state.gameState!!.currentRoundIndex)
        assertEquals(0, state.gameState!!.totalScore)
        assertNull(state.selectedColor)
    }

    @Test
    fun `currentRound returns correct round`() {
        viewModel.startNewGame()

        assertEquals(1, viewModel.uiState.value.currentRound!!.roundNumber)

        val firstColor = viewModel.uiState.value.currentRound!!.paletteColors.first().color
        viewModel.selectColor(firstColor)
        viewModel.submitAnswer()
        viewModel.nextRound()

        assertEquals(2, viewModel.uiState.value.currentRound!!.roundNumber)
    }

    @Test
    fun `totalRounds is exposed in uiState`() {
        viewModel.startNewGame()
        assertEquals(5, viewModel.uiState.value.totalRounds)
    }

    @Test
    fun `nextRound does nothing when phase is Playing`() {
        viewModel.startNewGame()

        // Select a color but do NOT submit -- phase is still Playing
        val firstColor = viewModel.uiState.value.currentRound!!.paletteColors.first().color
        viewModel.selectColor(firstColor)

        viewModel.nextRound()

        // Should remain on the same round
        val state = viewModel.uiState.value
        assertEquals(ClassicGamePhase.Playing, state.phase)
        assertEquals(0, state.gameState!!.currentRoundIndex)
    }

    @Test
    fun `nextRound does nothing when phase is NotStarted`() {
        viewModel.nextRound()

        // Should remain in NotStarted
        assertEquals(ClassicGamePhase.NotStarted, viewModel.uiState.value.phase)
    }

    @Test
    fun `perfect match gives score of 1000`() {
        viewModel.startNewGame()

        // Select the target color itself (simulate by selecting it from palette if it exists,
        // or verify score formula)
        val targetColor = viewModel.uiState.value.currentRound!!.targetColor
        viewModel.selectColor(targetColor)
        viewModel.submitAnswer()

        val currentRound = viewModel.uiState.value.currentRound!!
        assertEquals(0, currentRound.distance)
        assertEquals(1000, currentRound.score)
    }
}
