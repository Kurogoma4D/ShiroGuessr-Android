package dev.krgm4d.shiroguessr.model

/**
 * A color in the palette with its RGB values.
 */
data class PaletteColor(
    val color: RGBColor,
)

/**
 * A single round in the game.
 *
 * @property roundNumber The round number (1-based)
 * @property targetColor The target color to guess
 * @property selectedColor The color selected by the player
 * @property distance Manhattan distance between target and selected color
 * @property score Score for this round (0-1000)
 * @property paletteColors Available colors in the palette for this round (classic mode)
 * @property pin Pin placed on the map (map mode)
 * @property targetPin Target pin location on the map (map mode)
 * @property timeRemaining Time remaining when the guess was made (map mode)
 */
data class GameRound(
    val roundNumber: Int,
    val targetColor: RGBColor,
    val selectedColor: RGBColor? = null,
    val distance: Int? = null,
    val score: Int? = null,
    val paletteColors: List<PaletteColor> = emptyList(),
    val pin: Pin? = null,
    val targetPin: Pin? = null,
    val timeRemaining: Int? = null,
)
