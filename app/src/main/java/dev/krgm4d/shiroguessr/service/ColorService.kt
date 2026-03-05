package dev.krgm4d.shiroguessr.service

import dev.krgm4d.shiroguessr.model.PaletteColor
import dev.krgm4d.shiroguessr.model.RGBColor
import kotlin.math.abs
import kotlin.math.round

/**
 * Service for generating and manipulating colors.
 */
class ColorService {

    /** Minimum RGB value for white colors (inclusive). */
    private val minWhiteValue = 245

    /** Maximum RGB value for white colors (inclusive). */
    private val maxWhiteValue = 255

    /**
     * Generates a random white color with RGB values between 245-255.
     *
     * @return A random white [RGBColor]
     */
    fun generateRandomWhiteColor(): RGBColor {
        return RGBColor(
            r = (minWhiteValue..maxWhiteValue).random(),
            g = (minWhiteValue..maxWhiteValue).random(),
            b = (minWhiteValue..maxWhiteValue).random(),
        )
    }

    /**
     * Generates all possible white colors (RGB 245-255).
     *
     * Total: 11 x 11 x 11 = 1,331 colors.
     *
     * @return List of all possible white colors
     */
    fun generateAllWhiteColors(): List<RGBColor> {
        val colors = mutableListOf<RGBColor>()
        for (r in minWhiteValue..maxWhiteValue) {
            for (g in minWhiteValue..maxWhiteValue) {
                for (b in minWhiteValue..maxWhiteValue) {
                    colors.add(RGBColor(r = r, g = g, b = b))
                }
            }
        }
        return colors
    }

    /**
     * Gets a random sample of palette colors from all possible white colors.
     *
     * @param count Number of colors to sample (default: 25)
     * @return List of random palette colors
     */
    fun generatePaletteColors(count: Int = 25): List<PaletteColor> {
        return generateAllWhiteColors()
            .shuffled()
            .take(count)
            .map { PaletteColor(color = it) }
    }

    /**
     * Calculates the Manhattan distance between two RGB colors.
     *
     * Formula: |r1 - r2| + |g1 - g2| + |b1 - b2|
     *
     * @param color1 First color
     * @param color2 Second color
     * @return Manhattan distance (0-30 for white colors)
     */
    fun calculateDistance(color1: RGBColor, color2: RGBColor): Int {
        return abs(color1.r - color2.r) + abs(color1.g - color2.g) + abs(color1.b - color2.b)
    }

    /**
     * Interpolates between two colors using linear interpolation.
     *
     * @param color1 First color
     * @param color2 Second color
     * @param t Interpolation factor (0.0-1.0). 0 returns color1, 1 returns color2
     * @return Interpolated color
     */
    fun interpolateColor(color1: RGBColor, color2: RGBColor, t: Double): RGBColor {
        val clampedT = t.coerceIn(0.0, 1.0)
        return RGBColor(
            r = round(color1.r + (color2.r - color1.r) * clampedT).toInt(),
            g = round(color1.g + (color2.g - color1.g) * clampedT).toInt(),
            b = round(color1.b + (color2.b - color1.b) * clampedT).toInt(),
        )
    }
}
