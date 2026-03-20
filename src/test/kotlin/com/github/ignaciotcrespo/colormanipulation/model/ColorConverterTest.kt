package com.github.ignaciotcrespo.colormanipulation.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ColorConverterTest {

    @Test
    fun `parse hex6`() {
        val (color, format) = ColorConverter.parse("#FF0000")!!
        assertEquals(ColorFormat.HEX6, format)
        assertEquals(255.0, color.r)
        assertEquals(0.0, color.g)
        assertEquals(0.0, color.b)
        assertEquals(1.0, color.a)
    }

    @Test
    fun `parse hex3`() {
        val (color, format) = ColorConverter.parse("#F00")!!
        assertEquals(ColorFormat.HEX3, format)
        assertEquals(255.0, color.r)
        assertEquals(0.0, color.g)
        assertEquals(0.0, color.b)
    }

    @Test
    fun `parse hex8`() {
        val (color, format) = ColorConverter.parse("#FF000080")!!
        assertEquals(ColorFormat.HEX8, format)
        assertEquals(255.0, color.r)
        assertEquals(0.0, color.g)
        assertEquals(0.0, color.b)
        assertEquals(128.0 / 255.0, color.a, 0.01)
    }

    @Test
    fun `parse rgb`() {
        val (color, format) = ColorConverter.parse("rgb(100, 200, 50)")!!
        assertEquals(ColorFormat.RGB_FUNC, format)
        assertEquals(100.0, color.r)
        assertEquals(200.0, color.g)
        assertEquals(50.0, color.b)
    }

    @Test
    fun `parse rgba`() {
        val (color, format) = ColorConverter.parse("rgba(100, 200, 50, 0.5)")!!
        assertEquals(ColorFormat.RGBA_FUNC, format)
        assertEquals(100.0, color.r)
        assertEquals(200.0, color.g)
        assertEquals(50.0, color.b)
        assertEquals(0.5, color.a)
    }

    @Test
    fun `parse hsl`() {
        val (color, format) = ColorConverter.parse("hsl(120, 100%, 50%)")!!
        assertEquals(ColorFormat.HSL_FUNC, format)
        // Pure green
        assertEquals(0.0, color.r, 1.0)
        assertEquals(255.0, color.g, 1.0)
        assertEquals(0.0, color.b, 1.0)
    }

    @Test
    fun `parse hsla`() {
        val (color, format) = ColorConverter.parse("hsla(120, 100%, 50%, 0.8)")!!
        assertEquals(ColorFormat.HSLA_FUNC, format)
        assertEquals(0.8, color.a)
    }

    @Test
    fun `parse named css color`() {
        val (color, format) = ColorConverter.parse("red")!!
        assertEquals(ColorFormat.NAMED_CSS, format)
        assertEquals(255.0, color.r)
        assertEquals(0.0, color.g)
        assertEquals(0.0, color.b)
    }

    @Test
    fun `parse 0x hex`() {
        val (color, format) = ColorConverter.parse("0xFF0000")!!
        assertEquals(ColorFormat.HEX_0X, format)
        assertEquals(255.0, color.r)
        assertEquals(0.0, color.g)
        assertEquals(0.0, color.b)
    }

    @Test
    fun `parse invalid returns null`() {
        assertNull(ColorConverter.parse("not a color"))
        assertNull(ColorConverter.parse("#GGG"))
        assertNull(ColorConverter.parse("rgb(300, 0, 0)"))
        assertNull(ColorConverter.parse(""))
    }

    @Test
    fun `format hex6`() {
        val color = UnifiedColor(255.0, 128.0, 0.0)
        assertEquals("#FF8000", ColorConverter.format(color, ColorFormat.HEX6))
    }

    @Test
    fun `format rgb`() {
        val color = UnifiedColor(255.0, 128.0, 0.0)
        assertEquals("rgb(255, 128, 0)", ColorConverter.format(color, ColorFormat.RGB_FUNC))
    }

    @Test
    fun `format rgba`() {
        val color = UnifiedColor(255.0, 128.0, 0.0, 0.5)
        assertEquals("rgba(255, 128, 0, 0.5)", ColorConverter.format(color, ColorFormat.RGBA_FUNC))
    }

    @Test
    fun `format hsl`() {
        val color = UnifiedColor(255.0, 0.0, 0.0)  // pure red
        val result = ColorConverter.format(color, ColorFormat.HSL_FUNC)
        assertEquals("hsl(0, 100%, 50%)", result)
    }

    @Test
    fun `format named css color`() {
        val color = UnifiedColor(255.0, 0.0, 0.0)
        assertEquals("red", ColorConverter.format(color, ColorFormat.NAMED_CSS))
    }

    @Test
    fun `format named css color falls back to hex`() {
        val color = UnifiedColor(255.0, 128.0, 1.0)
        val result = ColorConverter.format(color, ColorFormat.NAMED_CSS)
        assertTrue(result.startsWith("#"))  // no named color, falls back to hex
    }

    @Test
    fun `round trip hex6`() {
        val original = "#3498DB"
        val (color, _) = ColorConverter.parse(original)!!
        val result = ColorConverter.format(color, ColorFormat.HEX6)
        assertEquals(original, result)
    }

    @Test
    fun `round trip rgb`() {
        val original = "rgb(52, 152, 219)"
        val (color, _) = ColorConverter.parse(original)!!
        val result = ColorConverter.format(color, ColorFormat.RGB_FUNC)
        assertEquals(original, result)
    }

    @Test
    fun `convert hex to rgb`() {
        val (color, _) = ColorConverter.parse("#FF8000")!!
        assertEquals("rgb(255, 128, 0)", ColorConverter.format(color, ColorFormat.RGB_FUNC))
    }

    @Test
    fun `convert rgb to hex`() {
        val (color, _) = ColorConverter.parse("rgb(255, 128, 0)")!!
        assertEquals("#FF8000", ColorConverter.format(color, ColorFormat.HEX6))
    }
}
