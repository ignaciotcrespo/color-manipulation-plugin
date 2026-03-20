package com.github.ignaciotcrespo.colormanipulation.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PaletteGeneratorTest {

    private val red = UnifiedColor(255.0, 0.0, 0.0)

    @Test
    fun `complementary returns 2 colors`() {
        val palette = PaletteGenerator.complementary(red)
        assertEquals(2, palette.size)
        // First is original
        assertEquals(red, palette[0])
        // Second is complement (cyan)
        assertEquals(0.0, palette[1].r, 2.0)
        assertEquals(255.0, palette[1].g, 2.0)
        assertEquals(255.0, palette[1].b, 2.0)
    }

    @Test
    fun `analogous returns 3 colors`() {
        val palette = PaletteGenerator.analogous(red)
        assertEquals(3, palette.size)
    }

    @Test
    fun `triadic returns 3 colors`() {
        val palette = PaletteGenerator.triadic(red)
        assertEquals(3, palette.size)
    }

    @Test
    fun `shades returns 9 colors by default`() {
        val palette = PaletteGenerator.shades(red)
        assertEquals(9, palette.size)
        // First shade should be lightest
        assertTrue(palette.first().toHsl().l > palette.last().toHsl().l)
    }
}
