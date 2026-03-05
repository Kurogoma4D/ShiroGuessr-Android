package dev.krgm4d.shiroguessr.viewmodel

import dev.krgm4d.shiroguessr.model.GameRound
import dev.krgm4d.shiroguessr.model.GameState
import dev.krgm4d.shiroguessr.model.RGBColor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ResultViewModelTest {

    private fun createSampleGameState(): GameState {
        val rounds = (1..5).map { roundNumber ->
            GameRound(
                roundNumber = roundNumber,
                targetColor = RGBColor(250, 248, 252),
                selectedColor = RGBColor(248, 250, 250),
                distance = roundNumber * 2,
                score = 1000 - roundNumber * 67,
            )
        }
        return GameState(
            rounds = rounds,
            currentRoundIndex = 5,
            isCompleted = true,
            totalScore = rounds.sumOf { it.score ?: 0 },
        )
    }

    @Test
    fun `initial state is null`() {
        val viewModel = ResultViewModel()
        assertNull(viewModel.gameState.value)
    }

    @Test
    fun `setGameState updates state`() {
        val viewModel = ResultViewModel()
        val gameState = createSampleGameState()

        viewModel.setGameState(gameState)

        assertNotNull(viewModel.gameState.value)
        assertEquals(gameState.totalScore, viewModel.gameState.value?.totalScore)
        assertEquals(5, viewModel.gameState.value?.rounds?.size)
    }

    @Test
    fun `clearGameState resets state to null`() {
        val viewModel = ResultViewModel()
        val gameState = createSampleGameState()

        viewModel.setGameState(gameState)
        assertNotNull(viewModel.gameState.value)

        viewModel.clearGameState()
        assertNull(viewModel.gameState.value)
    }

    @Test
    fun `setGameState preserves round details`() {
        val viewModel = ResultViewModel()
        val gameState = createSampleGameState()

        viewModel.setGameState(gameState)

        val state = viewModel.gameState.value!!
        assertEquals(true, state.isCompleted)
        state.rounds.forEachIndexed { index, round ->
            assertEquals(index + 1, round.roundNumber)
            assertNotNull(round.selectedColor)
            assertNotNull(round.distance)
            assertNotNull(round.score)
        }
    }
}
