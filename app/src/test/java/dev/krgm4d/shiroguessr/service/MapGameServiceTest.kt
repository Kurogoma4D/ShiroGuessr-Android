package dev.krgm4d.shiroguessr.service

import dev.krgm4d.shiroguessr.model.MapCoordinate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

/**
 * Unit tests for [MapGameService].
 *
 * Tests cover game creation, round management, pin placement,
 * score submission, timeout handling, and round advancement.
 */
class MapGameServiceTest {

    private lateinit var service: MapGameService
    private lateinit var gradientMapService: GradientMapService

    @Before
    fun setUp() {
        gradientMapService = GradientMapService()
        service = MapGameService(
            gradientMapService = gradientMapService,
        )
    }

    @Test
    fun `createNewGame creates game with 5 rounds`() {
        val gameState = service.createNewGame()
        assertEquals(5, gameState.rounds.size)
    }

    @Test
    fun `createNewGame starts at round index 0`() {
        val gameState = service.createNewGame()
        assertEquals(0, gameState.currentRoundIndex)
        assertFalse(gameState.isCompleted)
        assertEquals(0, gameState.totalScore)
    }

    @Test
    fun `createNewGame sets time limit`() {
        val gameState = service.createNewGame(timeLimit = 60)
        assertEquals(60, gameState.timeLimit)
    }

    @Test
    fun `createNewGame initializes round numbers correctly`() {
        val gameState = service.createNewGame()
        for (i in gameState.rounds.indices) {
            assertEquals(i + 1, gameState.rounds[i].roundNumber)
        }
    }

    @Test
    fun `startRound sets target color and target pin`() {
        val gameState = service.createNewGame()
        val gradientMap = gradientMapService.generateGradientMap()
        val seededRandom = Random(42)

        val updatedState = service.startRound(gameState, gradientMap, seededRandom)

        val currentRound = updatedState.rounds[0]
        assertNotNull(currentRound.targetPin)
        // Target color should be in white range
        assertTrue(currentRound.targetColor.r in 245..255)
        assertTrue(currentRound.targetColor.g in 245..255)
        assertTrue(currentRound.targetColor.b in 245..255)
    }

    @Test
    fun `placePin sets pin on current round`() {
        var gameState = service.createNewGame()
        val gradientMap = gradientMapService.generateGradientMap()
        gameState = service.startRound(gameState, gradientMap)

        val coordinate = MapCoordinate(x = 0.3, y = 0.7)
        val updatedState = service.placePin(gameState, coordinate, gradientMap)

        val currentRound = updatedState.rounds[0]
        assertNotNull(currentRound.pin)
        assertEquals(0.3, currentRound.pin!!.coordinate.x, 0.001)
        assertEquals(0.7, currentRound.pin!!.coordinate.y, 0.001)
    }

    @Test
    fun `placePin calculates color at pin location`() {
        var gameState = service.createNewGame()
        val gradientMap = gradientMapService.generateGradientMap()
        gameState = service.startRound(gameState, gradientMap)

        val coordinate = MapCoordinate(x = 0.5, y = 0.5)
        val updatedState = service.placePin(gameState, coordinate, gradientMap)

        val pin = updatedState.rounds[0].pin!!
        // Center color should be (250, 250, 250)
        assertEquals(250, pin.color.r)
        assertEquals(250, pin.color.g)
        assertEquals(250, pin.color.b)
    }

    @Test
    fun `submitGuess calculates distance and score`() {
        var gameState = service.createNewGame()
        val gradientMap = gradientMapService.generateGradientMap()
        gameState = service.startRound(gameState, gradientMap)

        val coordinate = MapCoordinate(x = 0.5, y = 0.5)
        gameState = service.placePin(gameState, coordinate, gradientMap)
        gameState = service.submitGuess(gameState, timeRemaining = 30)

        val currentRound = gameState.rounds[0]
        assertNotNull(currentRound.selectedColor)
        assertNotNull(currentRound.distance)
        assertNotNull(currentRound.score)
        assertEquals(30, currentRound.timeRemaining)
    }

    @Test
    fun `submitGuess sets selectedColor to pin color`() {
        var gameState = service.createNewGame()
        val gradientMap = gradientMapService.generateGradientMap()
        gameState = service.startRound(gameState, gradientMap)

        val coordinate = MapCoordinate(x = 0.5, y = 0.5)
        gameState = service.placePin(gameState, coordinate, gradientMap)
        gameState = service.submitGuess(gameState, timeRemaining = 30)

        val currentRound = gameState.rounds[0]
        assertEquals(currentRound.pin!!.color, currentRound.selectedColor)
    }

    @Test
    fun `submitGuess without pin does nothing`() {
        var gameState = service.createNewGame()
        val gradientMap = gradientMapService.generateGradientMap()
        gameState = service.startRound(gameState, gradientMap)

        val result = service.submitGuess(gameState, timeRemaining = 30)
        assertNull(result.rounds[0].selectedColor)
    }

    @Test
    fun `submitGuess updates total score`() {
        var gameState = service.createNewGame()
        val gradientMap = gradientMapService.generateGradientMap()
        gameState = service.startRound(gameState, gradientMap)

        val coordinate = MapCoordinate(x = 0.5, y = 0.5)
        gameState = service.placePin(gameState, coordinate, gradientMap)
        gameState = service.submitGuess(gameState, timeRemaining = 30)

        assertTrue(gameState.totalScore >= 0)
    }

    @Test
    fun `handleTimeout places pin at center`() {
        var gameState = service.createNewGame()
        val gradientMap = gradientMapService.generateGradientMap()
        gameState = service.startRound(gameState, gradientMap)

        gameState = service.handleTimeout(gameState, gradientMap)

        val currentRound = gameState.rounds[0]
        assertNotNull(currentRound.pin)
        assertEquals(0.5, currentRound.pin!!.coordinate.x, 0.001)
        assertEquals(0.5, currentRound.pin!!.coordinate.y, 0.001)
        assertEquals(0, currentRound.timeRemaining)
    }

    @Test
    fun `handleTimeout calculates score`() {
        var gameState = service.createNewGame()
        val gradientMap = gradientMapService.generateGradientMap()
        gameState = service.startRound(gameState, gradientMap)

        gameState = service.handleTimeout(gameState, gradientMap)

        val currentRound = gameState.rounds[0]
        assertNotNull(currentRound.score)
        assertTrue(currentRound.score!! >= 0)
    }

    @Test
    fun `nextRound advances to next round`() {
        var gameState = service.createNewGame()
        gameState = service.nextRound(gameState)

        assertEquals(1, gameState.currentRoundIndex)
        assertFalse(gameState.isCompleted)
    }

    @Test
    fun `nextRound marks game completed after 5 rounds`() {
        var gameState = service.createNewGame()

        repeat(5) {
            gameState = service.nextRound(gameState)
        }

        assertTrue(gameState.isCompleted)
    }

    @Test
    fun `score is within valid range 0-1000 per round`() {
        var gameState = service.createNewGame()
        val gradientMap = gradientMapService.generateGradientMap()
        gameState = service.startRound(gameState, gradientMap)

        val coordinate = MapCoordinate(x = 0.3, y = 0.7)
        gameState = service.placePin(gameState, coordinate, gradientMap)
        gameState = service.submitGuess(gameState, timeRemaining = 30)

        val score = gameState.rounds[0].score!!
        assertTrue("Score $score should be >= 0", score >= 0)
        assertTrue("Score $score should be <= 1000", score <= 1000)
    }

    @Test
    fun `distance is within valid range 0-30`() {
        var gameState = service.createNewGame()
        val gradientMap = gradientMapService.generateGradientMap()
        gameState = service.startRound(gameState, gradientMap)

        val coordinate = MapCoordinate(x = 0.3, y = 0.7)
        gameState = service.placePin(gameState, coordinate, gradientMap)
        gameState = service.submitGuess(gameState, timeRemaining = 30)

        val distance = gameState.rounds[0].distance!!
        assertTrue("Distance $distance should be >= 0", distance >= 0)
        assertTrue("Distance $distance should be <= 30", distance <= 30)
    }
}
