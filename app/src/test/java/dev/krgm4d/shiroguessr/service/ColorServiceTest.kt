package dev.krgm4d.shiroguessr.service

import dev.krgm4d.shiroguessr.model.RGBColor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ColorServiceTest {

    private val colorService = ColorService()

    @Test
    fun `generateRandomWhiteColor returns color within white range`() {
        repeat(100) {
            val color = colorService.generateRandomWhiteColor()
            assertTrue("Red value ${color.r} should be in 245-255", color.r in 245..255)
            assertTrue("Green value ${color.g} should be in 245-255", color.g in 245..255)
            assertTrue("Blue value ${color.b} should be in 245-255", color.b in 245..255)
        }
    }

    @Test
    fun `generateAllWhiteColors returns 1331 colors`() {
        val colors = colorService.generateAllWhiteColors()
        assertEquals(1331, colors.size)
    }

    @Test
    fun `generateAllWhiteColors contains only valid white colors`() {
        val colors = colorService.generateAllWhiteColors()
        for (color in colors) {
            assertTrue("Red value ${color.r} should be in 245-255", color.r in 245..255)
            assertTrue("Green value ${color.g} should be in 245-255", color.g in 245..255)
            assertTrue("Blue value ${color.b} should be in 245-255", color.b in 245..255)
        }
    }

    @Test
    fun `generateAllWhiteColors contains no duplicates`() {
        val colors = colorService.generateAllWhiteColors()
        assertEquals(colors.size, colors.toSet().size)
    }

    @Test
    fun `generatePaletteColors returns requested count`() {
        val palette = colorService.generatePaletteColors(10)
        assertEquals(10, palette.size)
    }

    @Test
    fun `generatePaletteColors default count is 25`() {
        val palette = colorService.generatePaletteColors()
        assertEquals(25, palette.size)
    }

    @Test
    fun `calculateDistance returns 0 for identical colors`() {
        val color = RGBColor(r = 250, g = 250, b = 250)
        assertEquals(0, colorService.calculateDistance(color, color))
    }

    @Test
    fun `calculateDistance returns correct Manhattan distance`() {
        val color1 = RGBColor(r = 245, g = 245, b = 245)
        val color2 = RGBColor(r = 255, g = 255, b = 255)
        assertEquals(30, colorService.calculateDistance(color1, color2))
    }

    @Test
    fun `calculateDistance returns correct distance for partial difference`() {
        val color1 = RGBColor(r = 245, g = 250, b = 255)
        val color2 = RGBColor(r = 250, g = 250, b = 250)
        // |245-250| + |250-250| + |255-250| = 5 + 0 + 5 = 10
        assertEquals(10, colorService.calculateDistance(color1, color2))
    }

    @Test
    fun `interpolateColor at t=0 returns color1`() {
        val color1 = RGBColor(r = 245, g = 245, b = 245)
        val color2 = RGBColor(r = 255, g = 255, b = 255)
        val result = colorService.interpolateColor(color1, color2, 0.0)
        assertEquals(color1, result)
    }

    @Test
    fun `interpolateColor at t=1 returns color2`() {
        val color1 = RGBColor(r = 245, g = 245, b = 245)
        val color2 = RGBColor(r = 255, g = 255, b = 255)
        val result = colorService.interpolateColor(color1, color2, 1.0)
        assertEquals(color2, result)
    }

    @Test
    fun `interpolateColor at t=0_5 returns midpoint`() {
        val color1 = RGBColor(r = 240, g = 240, b = 240)
        val color2 = RGBColor(r = 250, g = 250, b = 250)
        val result = colorService.interpolateColor(color1, color2, 0.5)
        assertEquals(RGBColor(r = 245, g = 245, b = 245), result)
    }

    @Test
    fun `interpolateColor clamps t below 0`() {
        val color1 = RGBColor(r = 245, g = 245, b = 245)
        val color2 = RGBColor(r = 255, g = 255, b = 255)
        val result = colorService.interpolateColor(color1, color2, -0.5)
        assertEquals(color1, result)
    }

    @Test
    fun `interpolateColor clamps t above 1`() {
        val color1 = RGBColor(r = 245, g = 245, b = 245)
        val color2 = RGBColor(r = 255, g = 255, b = 255)
        val result = colorService.interpolateColor(color1, color2, 1.5)
        assertEquals(color2, result)
    }
}
