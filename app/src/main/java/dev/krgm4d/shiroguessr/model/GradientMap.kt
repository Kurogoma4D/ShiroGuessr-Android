package dev.krgm4d.shiroguessr.model

/**
 * Gradient map with bilinear interpolation.
 *
 * @property width Width of the map in pixels
 * @property height Height of the map in pixels
 * @property cornerColors Colors at the four corners: [topLeft, topRight, bottomLeft, bottomRight]
 */
data class GradientMap(
    val width: Int,
    val height: Int,
    val cornerColors: List<RGBColor>,
) {
    init {
        require(cornerColors.size == 4) {
            "GradientMap requires exactly 4 corner colors, but got ${cornerColors.size}"
        }
    }
}
