package dev.krgm4d.shiroguessr.service

import dev.krgm4d.shiroguessr.model.GradientMap
import dev.krgm4d.shiroguessr.model.MapCoordinate
import dev.krgm4d.shiroguessr.model.RGBColor

/**
 * Service for generating and manipulating gradient maps.
 *
 * Corresponds to the iOS version's `GradientMapService.swift`.
 * Provides gradient map generation with fixed corner colors,
 * bilinear interpolation for color lookup, and exhaustive search
 * for the best-matching coordinate.
 *
 * @param colorService Service for color interpolation and distance calculations
 */
class GradientMapService(
    private val colorService: ColorService = ColorService(),
) {

    /**
     * Generates a gradient map with fixed white corner colors for consistent map appearance.
     *
     * Corner colors:
     * - TopLeft: (245, 245, 245) -- most neutral/darkest white
     * - TopRight: (255, 245, 255) -- pinkish
     * - BottomLeft: (245, 255, 255) -- cyanish
     * - BottomRight: (255, 255, 245) -- yellowish
     *
     * @param width Width of the map in logical pixels (default: 50)
     * @param height Height of the map in logical pixels (default: 50)
     * @return A [GradientMap] with bilinear interpolation corner colors
     */
    fun generateGradientMap(width: Int = 50, height: Int = 50): GradientMap {
        val topLeft = RGBColor(r = 245, g = 245, b = 245)
        val topRight = RGBColor(r = 255, g = 245, b = 255)
        val bottomLeft = RGBColor(r = 245, g = 255, b = 255)
        val bottomRight = RGBColor(r = 255, g = 255, b = 245)

        return GradientMap(
            width = width,
            height = height,
            cornerColors = listOf(topLeft, topRight, bottomLeft, bottomRight),
        )
    }

    /**
     * Gets the interpolated color at a specific coordinate using bilinear interpolation.
     *
     * Performs two-pass linear interpolation:
     * 1. Interpolate along the top edge (topLeft -> topRight) and bottom edge (bottomLeft -> bottomRight) using x
     * 2. Interpolate between the top and bottom results using y
     *
     * @param map The gradient map
     * @param coordinate Normalized coordinate (0.0-1.0 range)
     * @return The interpolated RGB color at the coordinate
     */
    fun getColorAtCoordinate(map: GradientMap, coordinate: MapCoordinate): RGBColor {
        val x = coordinate.x.coerceIn(0.0, 1.0)
        val y = coordinate.y.coerceIn(0.0, 1.0)

        val topLeft = map.cornerColors[0]
        val topRight = map.cornerColors[1]
        val bottomLeft = map.cornerColors[2]
        val bottomRight = map.cornerColors[3]

        // Bilinear interpolation
        val topColor = colorService.interpolateColor(topLeft, topRight, x)
        val bottomColor = colorService.interpolateColor(bottomLeft, bottomRight, x)
        return colorService.interpolateColor(topColor, bottomColor, y)
    }

    /**
     * Finds the coordinate on the map that most closely matches the target color.
     *
     * Uses exhaustive search with a step size of 0.02 (51 steps per axis)
     * for good accuracy on a 50x50 logical map.
     *
     * @param map The gradient map to search
     * @param targetColor The target color to find
     * @return The coordinate with the closest matching color
     */
    fun findBestMatchCoordinate(map: GradientMap, targetColor: RGBColor): MapCoordinate {
        var bestCoordinate = MapCoordinate(x = 0.5, y = 0.5)
        var bestDistance = Int.MAX_VALUE

        val step = 0.02
        var y = 0.0
        while (y <= 1.0) {
            var x = 0.0
            while (x <= 1.0) {
                val coordinate = MapCoordinate(x = x, y = y)
                val mapColor = getColorAtCoordinate(map, coordinate)
                val distance = colorService.calculateDistance(mapColor, targetColor)

                if (distance < bestDistance) {
                    bestDistance = distance
                    bestCoordinate = coordinate
                }

                x += step
            }
            y += step
        }

        return bestCoordinate
    }
}
