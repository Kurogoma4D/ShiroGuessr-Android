package dev.krgm4d.shiroguessr.service

import dev.krgm4d.shiroguessr.model.GameRound
import dev.krgm4d.shiroguessr.model.RGBColor
import org.junit.Assert.assertEquals
import org.junit.Test

class ScoreServiceTest {

    private val scoreService = ScoreService()

    @Test
    fun `calculateScore returns 1000 for distance 0`() {
        assertEquals(1000, scoreService.calculateScore(0))
    }

    @Test
    fun `calculateScore returns 500 for distance 15`() {
        assertEquals(500, scoreService.calculateScore(15))
    }

    @Test
    fun `calculateScore returns 0 for distance 30`() {
        assertEquals(0, scoreService.calculateScore(30))
    }

    @Test
    fun `calculateScore clamps distance above 30`() {
        assertEquals(0, scoreService.calculateScore(50))
    }

    @Test
    fun `calculateScore clamps negative distance`() {
        assertEquals(1000, scoreService.calculateScore(-5))
    }

    @Test
    fun `calculateTotalScore sums all round scores`() {
        val dummyColor = RGBColor(r = 250, g = 250, b = 250)
        val rounds = listOf(
            GameRound(roundNumber = 1, targetColor = dummyColor, score = 800),
            GameRound(roundNumber = 2, targetColor = dummyColor, score = 600),
            GameRound(roundNumber = 3, targetColor = dummyColor, score = 1000),
        )
        assertEquals(2400, scoreService.calculateTotalScore(rounds))
    }

    @Test
    fun `calculateTotalScore ignores rounds with null score`() {
        val dummyColor = RGBColor(r = 250, g = 250, b = 250)
        val rounds = listOf(
            GameRound(roundNumber = 1, targetColor = dummyColor, score = 800),
            GameRound(roundNumber = 2, targetColor = dummyColor, score = null),
            GameRound(roundNumber = 3, targetColor = dummyColor, score = 500),
        )
        assertEquals(1300, scoreService.calculateTotalScore(rounds))
    }

    @Test
    fun `calculateTotalScore returns 0 for empty rounds`() {
        assertEquals(0, scoreService.calculateTotalScore(emptyList()))
    }

    @Test
    fun `getStarRating returns 5 for distance 0`() {
        assertEquals(5, scoreService.getStarRating(0))
    }

    @Test
    fun `getStarRating returns 5 for distance 2`() {
        assertEquals(5, scoreService.getStarRating(2))
    }

    @Test
    fun `getStarRating returns 4 for distance 3`() {
        assertEquals(4, scoreService.getStarRating(3))
    }

    @Test
    fun `getStarRating returns 4 for distance 6`() {
        assertEquals(4, scoreService.getStarRating(6))
    }

    @Test
    fun `getStarRating returns 3 for distance 7`() {
        assertEquals(3, scoreService.getStarRating(7))
    }

    @Test
    fun `getStarRating returns 3 for distance 12`() {
        assertEquals(3, scoreService.getStarRating(12))
    }

    @Test
    fun `getStarRating returns 2 for distance 13`() {
        assertEquals(2, scoreService.getStarRating(13))
    }

    @Test
    fun `getStarRating returns 2 for distance 20`() {
        assertEquals(2, scoreService.getStarRating(20))
    }

    @Test
    fun `getStarRating returns 1 for distance 21`() {
        assertEquals(1, scoreService.getStarRating(21))
    }

    @Test
    fun `getStarRating returns 1 for distance 30`() {
        assertEquals(1, scoreService.getStarRating(30))
    }
}
