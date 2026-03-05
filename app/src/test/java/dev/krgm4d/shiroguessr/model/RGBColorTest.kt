package dev.krgm4d.shiroguessr.model

import org.junit.Assert.assertEquals
import org.junit.Test

class RGBColorTest {

    @Test
    fun `valid RGB color is created successfully`() {
        val color = RGBColor(r = 0, g = 128, b = 255)
        assertEquals(0, color.r)
        assertEquals(128, color.g)
        assertEquals(255, color.b)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `red value below 0 throws IllegalArgumentException`() {
        RGBColor(r = -1, g = 0, b = 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `red value above 255 throws IllegalArgumentException`() {
        RGBColor(r = 256, g = 0, b = 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `green value below 0 throws IllegalArgumentException`() {
        RGBColor(r = 0, g = -1, b = 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `green value above 255 throws IllegalArgumentException`() {
        RGBColor(r = 0, g = 256, b = 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `blue value below 0 throws IllegalArgumentException`() {
        RGBColor(r = 0, g = 0, b = -1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `blue value above 255 throws IllegalArgumentException`() {
        RGBColor(r = 0, g = 0, b = 256)
    }

    @Test
    fun `boundary values 0 and 255 are valid`() {
        val color = RGBColor(r = 0, g = 0, b = 0)
        assertEquals(0, color.r)

        val white = RGBColor(r = 255, g = 255, b = 255)
        assertEquals(255, white.r)
    }

    @Test
    fun `toCSSString returns correct format`() {
        val color = RGBColor(r = 245, g = 250, b = 255)
        assertEquals("rgb(245, 250, 255)", color.toCSSString())
    }
}
