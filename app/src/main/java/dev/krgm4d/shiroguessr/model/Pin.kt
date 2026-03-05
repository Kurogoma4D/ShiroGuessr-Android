package dev.krgm4d.shiroguessr.model

/**
 * Pin placed on the gradient map.
 *
 * @property coordinate The coordinate where the pin is placed
 * @property color The color at the pin location
 */
data class Pin(
    val coordinate: MapCoordinate,
    val color: RGBColor,
)
