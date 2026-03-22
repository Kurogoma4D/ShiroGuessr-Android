package dev.krgm4d.shiroguessr.ui.component

/**
 * Calculates a 1-5 star rating based on Manhattan distance.
 *
 * Distance ranges (out of max 30):
 * - 0-2: 5 stars (near perfect)
 * - 3-6: 4 stars (great)
 * - 7-12: 3 stars (good)
 * - 13-20: 2 stars (fair)
 * - 21+: 1 star (needs improvement)
 */
internal fun calculateStarRating(distance: Int): Int {
    return when {
        distance <= 2 -> 5
        distance <= 6 -> 4
        distance <= 12 -> 3
        distance <= 20 -> 2
        else -> 1
    }
}
