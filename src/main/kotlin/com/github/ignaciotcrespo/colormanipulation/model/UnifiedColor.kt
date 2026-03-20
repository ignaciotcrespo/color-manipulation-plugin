package com.github.ignaciotcrespo.colormanipulation.model

import java.awt.Color

/**
 * Internal color representation: RGBA with doubles.
 * r, g, b in [0.0, 255.0], a in [0.0, 1.0].
 */
data class UnifiedColor(
    val r: Double,
    val g: Double,
    val b: Double,
    val a: Double = 1.0
) {
    fun toHsl(): HslColor {
        val rNorm = r / 255.0
        val gNorm = g / 255.0
        val bNorm = b / 255.0

        val max = maxOf(rNorm, gNorm, bNorm)
        val min = minOf(rNorm, gNorm, bNorm)
        val delta = max - min

        val l = (max + min) / 2.0

        if (delta == 0.0) {
            return HslColor(0.0, 0.0, l * 100.0, a)
        }

        val s = if (l <= 0.5) delta / (max + min) else delta / (2.0 - max - min)

        val h = when (max) {
            rNorm -> ((gNorm - bNorm) / delta).let { if (it < 0) it + 6.0 else it } * 60.0
            gNorm -> ((bNorm - rNorm) / delta + 2.0) * 60.0
            else -> ((rNorm - gNorm) / delta + 4.0) * 60.0
        }

        return HslColor(h % 360.0, s * 100.0, l * 100.0, a)
    }

    fun toAwtColor(): Color = Color(
        r.toInt().coerceIn(0, 255),
        g.toInt().coerceIn(0, 255),
        b.toInt().coerceIn(0, 255),
        (a * 255).toInt().coerceIn(0, 255)
    )

    fun toArgbInt(): Long {
        val ai = (a * 255).toInt().coerceIn(0, 255)
        val ri = r.toInt().coerceIn(0, 255)
        val gi = g.toInt().coerceIn(0, 255)
        val bi = b.toInt().coerceIn(0, 255)
        return ((ai.toLong() shl 24) or (ri.toLong() shl 16) or (gi.toLong() shl 8) or bi.toLong())
    }

    companion object {
        fun fromHsl(h: Double, s: Double, l: Double, a: Double = 1.0): UnifiedColor {
            return HslColor(h, s, l, a).toUnifiedColor()
        }

        fun fromAwtColor(color: Color): UnifiedColor = UnifiedColor(
            color.red.toDouble(),
            color.green.toDouble(),
            color.blue.toDouble(),
            color.alpha / 255.0
        )

        fun fromRgbInt(rgb: Int): UnifiedColor = UnifiedColor(
            ((rgb shr 16) and 0xFF).toDouble(),
            ((rgb shr 8) and 0xFF).toDouble(),
            (rgb and 0xFF).toDouble()
        )

        fun fromArgbLong(argb: Long): UnifiedColor = UnifiedColor(
            ((argb shr 16) and 0xFF).toDouble(),
            ((argb shr 8) and 0xFF).toDouble(),
            (argb and 0xFF).toDouble(),
            ((argb shr 24) and 0xFF) / 255.0
        )
    }
}
