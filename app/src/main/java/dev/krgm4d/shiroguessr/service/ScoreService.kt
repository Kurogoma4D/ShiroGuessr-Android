package dev.krgm4d.shiroguessr.service

import dev.krgm4d.shiroguessr.model.GameRound
import kotlin.math.round

/**
 * Service for calculating game scores.
 */
class ScoreService {

    /** Maximum possible score for a single round. */
    private val maxRoundScore = 1000

    /**
     * Maximum possible Manhattan distance for white colors (245-255 range).
     *
     * Distance = |255-245| + |255-245| + |255-245| = 30
     */
    private val maxDistance = 30

    /**
     * Calculates the score for a single round based on Manhattan distance.
     *
     * Formula: 1000 * (1 - distance / 30)
     * - Distance 0 (perfect match) = 1000 points
     * - Distance 30 (maximum) = 0 points
     * - Score decreases linearly with distance
     *
     * @param distance Manhattan distance between selected and target color
     * @return Score for the round (0-1000)
     */
    fun calculateScore(distance: Int): Int {
        val clampedDistance = distance.coerceIn(0, maxDistance)
        val score = maxRoundScore.toDouble() * (1.0 - clampedDistance.toDouble() / maxDistance.toDouble())
        return round(score).toInt()
    }

    /**
     * Calculates the total score across all completed rounds.
     *
     * @param rounds List of game rounds
     * @return Total score (sum of all round scores)
     */
    fun calculateTotalScore(rounds: List<GameRound>): Int {
        return rounds.sumOf { it.score ?: 0 }
    }

    /**
     * Returns a star rating (1-5) based on the Manhattan distance.
     *
     * - 5 stars: distance 0-2 (nearly perfect)
     * - 4 stars: distance 3-6
     * - 3 stars: distance 7-12
     * - 2 stars: distance 13-20
     * - 1 star:  distance 21-30
     *
     * @param distance Manhattan distance between selected and target color
     * @return Star rating from 1 to 5
     */
    fun getStarRating(distance: Int): Int {
        val clampedDistance = distance.coerceAtLeast(0)
        return when {
            clampedDistance <= 2 -> 5
            clampedDistance <= 6 -> 4
            clampedDistance <= 12 -> 3
            clampedDistance <= 20 -> 2
            else -> 1
        }
    }
}
