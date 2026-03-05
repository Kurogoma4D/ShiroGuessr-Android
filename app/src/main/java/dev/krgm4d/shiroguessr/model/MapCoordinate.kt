package dev.krgm4d.shiroguessr.model

/**
 * Map coordinate with normalized values (0.0-1.0).
 *
 * @property x Normalized x coordinate (0.0-1.0)
 * @property y Normalized y coordinate (0.0-1.0)
 */
data class MapCoordinate(
    val x: Double,
    val y: Double,
)
