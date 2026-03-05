package dev.krgm4d.shiroguessr.model

import org.junit.Assert.assertEquals
import org.junit.Test

class MapCoordinateTest {

    @Test
    fun `valid coordinate is created successfully`() {
        val coord = MapCoordinate(x = 0.5, y = 0.5)
        assertEquals(0.5, coord.x, 0.0)
        assertEquals(0.5, coord.y, 0.0)
    }

    @Test
    fun `boundary values 0 and 1 are valid`() {
        val origin = MapCoordinate(x = 0.0, y = 0.0)
        assertEquals(0.0, origin.x, 0.0)

        val corner = MapCoordinate(x = 1.0, y = 1.0)
        assertEquals(1.0, corner.x, 0.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `x below 0 throws IllegalArgumentException`() {
        MapCoordinate(x = -0.1, y = 0.5)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `x above 1 throws IllegalArgumentException`() {
        MapCoordinate(x = 1.1, y = 0.5)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `y below 0 throws IllegalArgumentException`() {
        MapCoordinate(x = 0.5, y = -0.1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `y above 1 throws IllegalArgumentException`() {
        MapCoordinate(x = 0.5, y = 1.1)
    }
}
