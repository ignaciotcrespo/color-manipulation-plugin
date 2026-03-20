package com.github.ignaciotcrespo.colormanipulation.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ColorTransformsTest {

    private val red = UnifiedColor(255.0, 0.0, 0.0)
    private val white = UnifiedColor(255.0, 255.0, 255.0)
    private val black = UnifiedColor(0.0, 0.0, 0.0)

    @Test
    fun `lighten red`() {
        val result = ColorTransforms.lighten(red, 10.0)
        val hsl = result.toHsl()
        assertEquals(60.0, hsl.l, 1.0) // was 50%, now ~60%
    }

    @Test
    fun `darken red`() {
        val result = ColorTransforms.darken(red, 10.0)
        val hsl = result.toHsl()
        assertEquals(40.0, hsl.l, 1.0) // was 50%, now ~40%
    }

    @Test
    fun `lighten white stays white`() {
        val result = ColorTransforms.lighten(white, 50.0)
        assertEquals(255.0, result.r, 1.0)
        assertEquals(255.0, result.g, 1.0)
        assertEquals(255.0, result.b, 1.0)
    }

    @Test
    fun `darken black stays black`() {
        val result = ColorTransforms.darken(black, 50.0)
        assertEquals(0.0, result.r, 1.0)
        assertEquals(0.0, result.g, 1.0)
        assertEquals(0.0, result.b, 1.0)
    }

    @Test
    fun `saturate`() {
        val muted = UnifiedColor.fromHsl(0.0, 50.0, 50.0)
        val result = ColorTransforms.saturate(muted, 20.0)
        val hsl = result.toHsl()
        assertEquals(70.0, hsl.s, 1.0)
    }

    @Test
    fun `desaturate to grayscale`() {
        val result = ColorTransforms.desaturate(red, 100.0)
        val hsl = result.toHsl()
        assertEquals(0.0, hsl.s, 1.0)
    }

    @Test
    fun `adjust alpha`() {
        val result = ColorTransforms.adjustAlpha(red, 0.5)
        assertEquals(0.5, result.a)
        assertEquals(255.0, result.r)
    }

    @Test
    fun `alpha clamped`() {
        val result = ColorTransforms.adjustAlpha(red, 1.5)
        assertEquals(1.0, result.a)
    }

    @Test
    fun `hue rotate red to green`() {
        val result = ColorTransforms.hueRotate(red, 120.0)
        // Should be approximately green
        assertEquals(0.0, result.r, 2.0)
        assertEquals(255.0, result.g, 2.0)
        assertEquals(0.0, result.b, 2.0)
    }

    @Test
    fun `hue rotate 360 returns same`() {
        val result = ColorTransforms.hueRotate(red, 360.0)
        assertEquals(red.r, result.r, 1.0)
        assertEquals(red.g, result.g, 1.0)
        assertEquals(red.b, result.b, 1.0)
    }

    @Test
    fun `invert red to cyan`() {
        val result = ColorTransforms.invert(red)
        assertEquals(0.0, result.r)
        assertEquals(255.0, result.g)
        assertEquals(255.0, result.b)
    }

    @Test
    fun `grayscale`() {
        val result = ColorTransforms.grayscale(red)
        // Luminance of red: 0.299 * 255 ≈ 76
        assertEquals(result.r, result.g, 0.01)
        assertEquals(result.g, result.b, 0.01)
        assertEquals(76.245, result.r, 1.0)
    }
}
