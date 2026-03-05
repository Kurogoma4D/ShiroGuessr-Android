package dev.krgm4d.shiroguessr.model

import androidx.compose.ui.graphics.Color

/**
 * RGB color representation with values from 0-255.
 *
 * @property r Red component (0-255)
 * @property g Green component (0-255)
 * @property b Blue component (0-255)
 */
data class RGBColor(
    val r: Int,
    val g: Int,
    val b: Int,
) {
    init {
        require(r in 0..255) { "Red value must be in 0..255, but was $r" }
        require(g in 0..255) { "Green value must be in 0..255, but was $g" }
        require(b in 0..255) { "Blue value must be in 0..255, but was $b" }
    }

    /**
     * Converts the RGB color to Jetpack Compose [Color].
     */
    fun toComposeColor(): Color {
        return Color(
            red = r / 255f,
            green = g / 255f,
            blue = b / 255f,
        )
    }

    /**
     * Converts the RGB color to CSS color string.
     *
     * @return CSS color string in format "rgb(r, g, b)"
     */
    fun toCSSString(): String {
        return "rgb($r, $g, $b)"
    }
}
