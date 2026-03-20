package com.github.ignaciotcrespo.colormanipulation.model

/**
 * HSL color representation.
 * h in [0.0, 360.0), s in [0.0, 100.0], l in [0.0, 100.0], a in [0.0, 1.0].
 */
data class HslColor(
    val h: Double,
    val s: Double,
    val l: Double,
    val a: Double = 1.0
) {
    fun toUnifiedColor(): UnifiedColor {
        val sNorm = s / 100.0
        val lNorm = l / 100.0

        if (sNorm == 0.0) {
            val v = (lNorm * 255.0)
            return UnifiedColor(v, v, v, a)
        }

        val q = if (lNorm < 0.5) lNorm * (1.0 + sNorm) else lNorm + sNorm - lNorm * sNorm
        val p = 2.0 * lNorm - q
        val hNorm = h / 360.0

        fun hueToRgb(t: Double): Double {
            val tc = when {
                t < 0 -> t + 1.0
                t > 1 -> t - 1.0
                else -> t
            }
            return when {
                tc < 1.0 / 6.0 -> p + (q - p) * 6.0 * tc
                tc < 1.0 / 2.0 -> q
                tc < 2.0 / 3.0 -> p + (q - p) * (2.0 / 3.0 - tc) * 6.0
                else -> p
            }
        }

        return UnifiedColor(
            r = hueToRgb(hNorm + 1.0 / 3.0) * 255.0,
            g = hueToRgb(hNorm) * 255.0,
            b = hueToRgb(hNorm - 1.0 / 3.0) * 255.0,
            a = a
        )
    }
}
