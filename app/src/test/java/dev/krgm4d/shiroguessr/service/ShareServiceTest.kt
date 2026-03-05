package dev.krgm4d.shiroguessr.service

import dev.krgm4d.shiroguessr.model.GameRound
import dev.krgm4d.shiroguessr.model.GameState
import dev.krgm4d.shiroguessr.model.RGBColor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ShareServiceTest {

    private val shareService = ShareService()
    private val dummyColor = RGBColor(r = 250, g = 250, b = 250)

    // --- generateStarRating tests ---

    @Test
    fun `generateStarRating returns 5 stars for distance 0`() {
        assertEquals("\u2B50\u2B50\u2B50\u2B50\u2B50", shareService.generateStarRating(0))
    }

    @Test
    fun `generateStarRating returns 5 stars for distance 5`() {
        assertEquals("\u2B50\u2B50\u2B50\u2B50\u2B50", shareService.generateStarRating(5))
    }

    @Test
    fun `generateStarRating returns 4 stars for distance 6`() {
        assertEquals("\u2B50\u2B50\u2B50\u2B50", shareService.generateStarRating(6))
    }

    @Test
    fun `generateStarRating returns 4 stars for distance 10`() {
        assertEquals("\u2B50\u2B50\u2B50\u2B50", shareService.generateStarRating(10))
    }

    @Test
    fun `generateStarRating returns 3 stars for distance 11`() {
        assertEquals("\u2B50\u2B50\u2B50", shareService.generateStarRating(11))
    }

    @Test
    fun `generateStarRating returns 3 stars for distance 20`() {
        assertEquals("\u2B50\u2B50\u2B50", shareService.generateStarRating(20))
    }

    @Test
    fun `generateStarRating returns 2 stars for distance 21`() {
        assertEquals("\u2B50\u2B50", shareService.generateStarRating(21))
    }

    @Test
    fun `generateStarRating returns 2 stars for distance 40`() {
        assertEquals("\u2B50\u2B50", shareService.generateStarRating(40))
    }

    @Test
    fun `generateStarRating returns 1 star for distance 41`() {
        assertEquals("\u2B50", shareService.generateStarRating(41))
    }

    @Test
    fun `generateStarRating returns 1 star for distance 100`() {
        assertEquals("\u2B50", shareService.generateStarRating(100))
    }

    @Test
    fun `generateStarRating returns 1 star for null distance`() {
        assertEquals("\u2B50", shareService.generateStarRating(null))
    }

    // --- formatScore tests ---

    @Test
    fun `formatScore formats zero correctly`() {
        assertEquals("0", shareService.formatScore(0))
    }

    @Test
    fun `formatScore formats hundreds without comma`() {
        assertEquals("500", shareService.formatScore(500))
    }

    @Test
    fun `formatScore formats thousands with comma`() {
        assertEquals("4,100", shareService.formatScore(4100))
    }

    @Test
    fun `formatScore formats 5000 correctly`() {
        assertEquals("5,000", shareService.formatScore(5000))
    }

    @Test
    fun `formatScore formats 1000 correctly`() {
        assertEquals("1,000", shareService.formatScore(1000))
    }

    // --- generateShareText tests (English) ---

    @Test
    fun `generateShareText returns empty for incomplete game`() {
        val incompleteState = GameState(
            rounds = emptyList(),
            currentRoundIndex = 0,
            isCompleted = false,
            totalScore = 0,
        )
        assertEquals(
            "",
            shareService.generateShareText(
                gameState = incompleteState,
                scoreLabel = "Score:",
                roundLabelFormat = "Round %d:",
                distanceLabel = "Distance:",
            ),
        )
    }

    @Test
    fun `generateShareText matches iOS format for English`() {
        val gameState = GameState(
            rounds = listOf(
                GameRound(roundNumber = 1, targetColor = dummyColor, distance = 2, score = 900),
                GameRound(roundNumber = 2, targetColor = dummyColor, distance = 8, score = 700),
                GameRound(roundNumber = 3, targetColor = dummyColor, distance = 0, score = 1000),
                GameRound(roundNumber = 4, targetColor = dummyColor, distance = 15, score = 600),
                GameRound(roundNumber = 5, targetColor = dummyColor, distance = 5, score = 900),
            ),
            currentRoundIndex = 5,
            isCompleted = true,
            totalScore = 4100,
        )

        val expected = """
            |白Guessr 🎨
            |Score: 4,100 / 5,000
            |
            |Round 1: ⭐⭐⭐⭐⭐ (Distance: 2)
            |Round 2: ⭐⭐⭐⭐ (Distance: 8)
            |Round 3: ⭐⭐⭐⭐⭐ (Distance: 0)
            |Round 4: ⭐⭐⭐ (Distance: 15)
            |Round 5: ⭐⭐⭐⭐⭐ (Distance: 5)
            |
            |https://shiro-guessr.pages.dev/app
            |
            |#白Guessr
        """.trimMargin()

        val actual = shareService.generateShareText(
            gameState = gameState,
            scoreLabel = "Score:",
            roundLabelFormat = "Round %d:",
            distanceLabel = "Distance:",
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `generateShareText matches iOS format for Japanese`() {
        val gameState = GameState(
            rounds = listOf(
                GameRound(roundNumber = 1, targetColor = dummyColor, distance = 2, score = 900),
                GameRound(roundNumber = 2, targetColor = dummyColor, distance = 8, score = 700),
                GameRound(roundNumber = 3, targetColor = dummyColor, distance = 0, score = 1000),
                GameRound(roundNumber = 4, targetColor = dummyColor, distance = 15, score = 600),
                GameRound(roundNumber = 5, targetColor = dummyColor, distance = 5, score = 900),
            ),
            currentRoundIndex = 5,
            isCompleted = true,
            totalScore = 4100,
        )

        val expected = """
            |白Guessr 🎨
            |スコア: 4,100 / 5,000
            |
            |ラウンド1: ⭐⭐⭐⭐⭐ (距離: 2)
            |ラウンド2: ⭐⭐⭐⭐ (距離: 8)
            |ラウンド3: ⭐⭐⭐⭐⭐ (距離: 0)
            |ラウンド4: ⭐⭐⭐ (距離: 15)
            |ラウンド5: ⭐⭐⭐⭐⭐ (距離: 5)
            |
            |https://shiro-guessr.pages.dev/app
            |
            |#白Guessr
        """.trimMargin()

        val actual = shareService.generateShareText(
            gameState = gameState,
            scoreLabel = "スコア:",
            roundLabelFormat = "ラウンド%d:",
            distanceLabel = "距離:",
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `generateShareText handles null distance in rounds`() {
        val gameState = GameState(
            rounds = listOf(
                GameRound(roundNumber = 1, targetColor = dummyColor, distance = null, score = 0),
            ),
            currentRoundIndex = 1,
            isCompleted = true,
            totalScore = 0,
        )

        val actual = shareService.generateShareText(
            gameState = gameState,
            scoreLabel = "Score:",
            roundLabelFormat = "Round %d:",
            distanceLabel = "Distance:",
        )

        // Null distance should show as 0, star rating should be 1
        assertTrue(actual.contains("(Distance: 0)"))
        assertTrue(actual.contains("\u2B50 "))  // Single star followed by space before parenthesis
    }

    @Test
    fun `generateShareText includes correct URL and hashtag`() {
        val gameState = GameState(
            rounds = listOf(
                GameRound(roundNumber = 1, targetColor = dummyColor, distance = 0, score = 1000),
            ),
            currentRoundIndex = 1,
            isCompleted = true,
            totalScore = 1000,
        )

        val actual = shareService.generateShareText(
            gameState = gameState,
            scoreLabel = "Score:",
            roundLabelFormat = "Round %d:",
            distanceLabel = "Distance:",
        )

        assertTrue(actual.contains("https://shiro-guessr.pages.dev/app"))
        assertTrue(actual.endsWith("#白Guessr"))
    }
}
