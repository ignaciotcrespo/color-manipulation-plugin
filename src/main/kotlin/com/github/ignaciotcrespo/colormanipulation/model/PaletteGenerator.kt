package com.github.ignaciotcrespo.colormanipulation.model

object PaletteGenerator {

    fun complementary(color: UnifiedColor): List<UnifiedColor> {
        return listOf(color, ColorTransforms.hueRotate(color, 180.0))
    }

    fun analogous(color: UnifiedColor): List<UnifiedColor> {
        return listOf(
            ColorTransforms.hueRotate(color, -30.0),
            color,
            ColorTransforms.hueRotate(color, 30.0)
        )
    }

    fun triadic(color: UnifiedColor): List<UnifiedColor> {
        return listOf(
            color,
            ColorTransforms.hueRotate(color, 120.0),
            ColorTransforms.hueRotate(color, 240.0)
        )
    }

    fun shades(color: UnifiedColor, steps: Int = 9): List<UnifiedColor> {
        val hsl = color.toHsl()
        val result = mutableListOf<UnifiedColor>()
        for (i in 0 until steps) {
            val l = 95.0 - (i * 90.0 / (steps - 1))
            result.add(HslColor(hsl.h, hsl.s, l.coerceIn(0.0, 100.0), hsl.a).toUnifiedColor())
        }
        return result
    }

    fun tints(color: UnifiedColor, count: Int = 5): List<UnifiedColor> {
        return (1..count).map { i ->
            val factor = i.toDouble() / (count + 1)
            UnifiedColor(
                color.r + (255.0 - color.r) * factor,
                color.g + (255.0 - color.g) * factor,
                color.b + (255.0 - color.b) * factor,
                color.a
            )
        }
    }
}
