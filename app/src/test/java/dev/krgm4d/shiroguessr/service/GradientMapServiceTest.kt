package dev.krgm4d.shiroguessr.service

import dev.krgm4d.shiroguessr.model.GradientMap
import dev.krgm4d.shiroguessr.model.MapCoordinate
import dev.krgm4d.shiroguessr.model.RGBColor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [GradientMapService].
 *
 * Tests cover gradient map generation, bilinear interpolation at corners,
 * edges, and arbitrary coordinates, as well as best-match coordinate search.
 */
class GradientMapServiceTest {

    private lateinit var service: GradientMapService

    @Before
    fun setUp() {
        service = GradientMapService()
    }

    @Test
    fun `generateGradientMap creates map with correct dimensions`() {
        val map = service.generateGradientMap()
        assertEquals(50, map.width)
        assertEquals(50, map.height)
    }

    @Test
    fun `generateGradientMap creates map with 4 corner colors`() {
        val map = service.generateGradientMap()
        assertEquals(4, map.cornerColors.size)
    }

    @Test
    fun `generateGradientMap has correct corner colors`() {
        val map = service.generateGradientMap()
        assertEquals(RGBColor(r = 245, g = 245, b = 245), map.cornerColors[0]) // topLeft
        assertEquals(RGBColor(r = 255, g = 245, b = 255), map.cornerColors[1]) // topRight
        assertEquals(RGBColor(r = 245, g = 255, b = 255), map.cornerColors[2]) // bottomLeft
        assertEquals(RGBColor(r = 255, g = 255, b = 245), map.cornerColors[3]) // bottomRight
    }

    @Test
    fun `generateGradientMap supports custom dimensions`() {
        val map = service.generateGradientMap(width = 100, height = 100)
        assertEquals(100, map.width)
        assertEquals(100, map.height)
    }

    @Test
    fun `getColorAtCoordinate returns top-left corner color at (0, 0)`() {
        val map = service.generateGradientMap()
        val color = service.getColorAtCoordinate(map, MapCoordinate(x = 0.0, y = 0.0))
        assertEquals(RGBColor(r = 245, g = 245, b = 245), color)
    }

    @Test
    fun `getColorAtCoordinate returns top-right corner color at (1, 0)`() {
        val map = service.generateGradientMap()
        val color = service.getColorAtCoordinate(map, MapCoordinate(x = 1.0, y = 0.0))
        assertEquals(RGBColor(r = 255, g = 245, b = 255), color)
    }

    @Test
    fun `getColorAtCoordinate returns bottom-left corner color at (0, 1)`() {
        val map = service.generateGradientMap()
        val color = service.getColorAtCoordinate(map, MapCoordinate(x = 0.0, y = 1.0))
        assertEquals(RGBColor(r = 245, g = 255, b = 255), color)
    }

    @Test
    fun `getColorAtCoordinate returns bottom-right corner color at (1, 1)`() {
        val map = service.generateGradientMap()
        val color = service.getColorAtCoordinate(map, MapCoordinate(x = 1.0, y = 1.0))
        assertEquals(RGBColor(r = 255, g = 255, b = 245), color)
    }

    @Test
    fun `getColorAtCoordinate returns center color at (0_5, 0_5)`() {
        val map = service.generateGradientMap()
        val color = service.getColorAtCoordinate(map, MapCoordinate(x = 0.5, y = 0.5))
        // Center should be the average of all four corners
        // R: (245+255+245+255)/4 = 250, G: (245+245+255+255)/4 = 250, B: (245+255+255+245)/4 = 250
        assertEquals(250, color.r)
        assertEquals(250, color.g)
        assertEquals(250, color.b)
    }

    @Test
    fun `getColorAtCoordinate returns valid white color for any coordinate`() {
        val map = service.generateGradientMap()
        val coordinates = listOf(
            MapCoordinate(x = 0.0, y = 0.0),
            MapCoordinate(x = 0.25, y = 0.25),
            MapCoordinate(x = 0.5, y = 0.5),
            MapCoordinate(x = 0.75, y = 0.75),
            MapCoordinate(x = 1.0, y = 1.0),
        )
        for (coord in coordinates) {
            val color = service.getColorAtCoordinate(map, coord)
            assertTrue("R=${color.r} should be in 245..255", color.r in 245..255)
            assertTrue("G=${color.g} should be in 245..255", color.g in 245..255)
            assertTrue("B=${color.b} should be in 245..255", color.b in 245..255)
        }
    }

    @Test
    fun `findBestMatchCoordinate returns exact corner for corner color`() {
        val map = service.generateGradientMap()
        val topLeftColor = RGBColor(r = 245, g = 245, b = 245)

        val result = service.findBestMatchCoordinate(map, topLeftColor)
        // Should find coordinate near (0, 0)
        assertTrue("x should be near 0.0, was ${result.x}", result.x <= 0.05)
        assertTrue("y should be near 0.0, was ${result.y}", result.y <= 0.05)
    }

    @Test
    fun `findBestMatchCoordinate returns center-ish coordinate for center color`() {
        val map = service.generateGradientMap()
        val centerColor = RGBColor(r = 250, g = 250, b = 250)

        val result = service.findBestMatchCoordinate(map, centerColor)
        // The center color (250, 250, 250) should map to approximately (0.5, 0.5)
        assertTrue("x should be near 0.5, was ${result.x}", result.x in 0.3..0.7)
        assertTrue("y should be near 0.5, was ${result.y}", result.y in 0.3..0.7)
    }

    @Test
    fun `bilinear interpolation is symmetric`() {
        val map = service.generateGradientMap()
        // Top edge midpoint
        val topMid = service.getColorAtCoordinate(map, MapCoordinate(x = 0.5, y = 0.0))
        // Expected: R=(245+255)/2=250, G=(245+245)/2=245, B=(245+255)/2=250
        assertEquals(250, topMid.r)
        assertEquals(245, topMid.g)
        assertEquals(250, topMid.b)
    }
}
