package com.github.ignaciotcrespo.colormanipulation.model

object ColorTransforms {

    fun lighten(color: UnifiedColor, percent: Double): UnifiedColor {
        val hsl = color.toHsl()
        val newL = (hsl.l + percent).coerceIn(0.0, 100.0)
        return HslColor(hsl.h, hsl.s, newL, hsl.a).toUnifiedColor()
    }

    fun darken(color: UnifiedColor, percent: Double): UnifiedColor {
        val hsl = color.toHsl()
        val newL = (hsl.l - percent).coerceIn(0.0, 100.0)
        return HslColor(hsl.h, hsl.s, newL, hsl.a).toUnifiedColor()
    }

    fun saturate(color: UnifiedColor, percent: Double): UnifiedColor {
        val hsl = color.toHsl()
        val newS = (hsl.s + percent).coerceIn(0.0, 100.0)
        return HslColor(hsl.h, newS, hsl.l, hsl.a).toUnifiedColor()
    }

    fun desaturate(color: UnifiedColor, percent: Double): UnifiedColor {
        val hsl = color.toHsl()
        val newS = (hsl.s - percent).coerceIn(0.0, 100.0)
        return HslColor(hsl.h, newS, hsl.l, hsl.a).toUnifiedColor()
    }

    fun adjustAlpha(color: UnifiedColor, newAlpha: Double): UnifiedColor {
        return color.copy(a = newAlpha.coerceIn(0.0, 1.0))
    }

    fun hueRotate(color: UnifiedColor, degrees: Double): UnifiedColor {
        val hsl = color.toHsl()
        val newH = ((hsl.h + degrees) % 360.0).let { if (it < 0) it + 360.0 else it }
        return HslColor(newH, hsl.s, hsl.l, hsl.a).toUnifiedColor()
    }

    fun invert(color: UnifiedColor): UnifiedColor {
        return UnifiedColor(255.0 - color.r, 255.0 - color.g, 255.0 - color.b, color.a)
    }

    fun grayscale(color: UnifiedColor): UnifiedColor {
        val gray = 0.299 * color.r + 0.587 * color.g + 0.114 * color.b
        return UnifiedColor(gray, gray, gray, color.a)
    }

    fun warmer(color: UnifiedColor, amount: Double): UnifiedColor {
        val factor = amount / 100.0
        return UnifiedColor(
            (color.r + (255.0 - color.r) * factor * 0.3).coerceIn(0.0, 255.0),
            color.g,
            (color.b - color.b * factor * 0.3).coerceIn(0.0, 255.0),
            color.a
        )
    }

    fun cooler(color: UnifiedColor, amount: Double): UnifiedColor {
        val factor = amount / 100.0
        return UnifiedColor(
            (color.r - color.r * factor * 0.3).coerceIn(0.0, 255.0),
            color.g,
            (color.b + (255.0 - color.b) * factor * 0.3).coerceIn(0.0, 255.0),
            color.a
        )
    }

    fun mix(color1: UnifiedColor, color2: UnifiedColor, weight: Double): UnifiedColor {
        val w = weight.coerceIn(0.0, 1.0)
        return UnifiedColor(
            color1.r * (1.0 - w) + color2.r * w,
            color1.g * (1.0 - w) + color2.g * w,
            color1.b * (1.0 - w) + color2.b * w,
            color1.a * (1.0 - w) + color2.a * w
        )
    }

    fun tint(color: UnifiedColor, percent: Double): UnifiedColor {
        val white = UnifiedColor(255.0, 255.0, 255.0, color.a)
        return mix(color, white, percent / 100.0)
    }

    fun shade(color: UnifiedColor, percent: Double): UnifiedColor {
        val black = UnifiedColor(0.0, 0.0, 0.0, color.a)
        return mix(color, black, percent / 100.0)
    }

    fun simulateProtanopia(color: UnifiedColor): UnifiedColor {
        val r = color.r / 255.0; val g = color.g / 255.0; val b = color.b / 255.0
        return UnifiedColor(
            (0.56667 * r + 0.43333 * g) * 255.0,
            (0.55833 * g + 0.44167 * b) * 255.0,
            (0.24167 * g + 0.75833 * b) * 255.0,
            color.a
        )
    }

    fun simulateDeuteranopia(color: UnifiedColor): UnifiedColor {
        val r = color.r / 255.0; val g = color.g / 255.0; val b = color.b / 255.0
        return UnifiedColor(
            (0.625 * r + 0.375 * g) * 255.0,
            (0.7 * g + 0.3 * b) * 255.0,
            (0.3 * g + 0.7 * b) * 255.0,
            color.a
        )
    }

    fun simulateTritanopia(color: UnifiedColor): UnifiedColor {
        val r = color.r / 255.0; val g = color.g / 255.0; val b = color.b / 255.0
        return UnifiedColor(
            (0.95 * r + 0.05 * g) * 255.0,
            (0.43333 * g + 0.56667 * b) * 255.0,
            (0.475 * g + 0.525 * b) * 255.0,
            color.a
        )
    }

    fun randomColor(): UnifiedColor {
        return UnifiedColor(
            (Math.random() * 255.0),
            (Math.random() * 255.0),
            (Math.random() * 255.0),
            1.0
        )
    }

    fun relativeLuminance(color: UnifiedColor): Double {
        fun linearize(v: Double): Double {
            val s = v / 255.0
            return if (s <= 0.04045) s / 12.92 else Math.pow((s + 0.055) / 1.055, 2.4)
        }
        return 0.2126 * linearize(color.r) + 0.7152 * linearize(color.g) + 0.0722 * linearize(color.b)
    }

    fun contrastRatio(color1: UnifiedColor, color2: UnifiedColor): Double {
        val l1 = relativeLuminance(color1)
        val l2 = relativeLuminance(color2)
        val lighter = maxOf(l1, l2)
        val darker = minOf(l1, l2)
        return (lighter + 0.05) / (darker + 0.05)
    }

    fun adjustForContrast(color: UnifiedColor, background: UnifiedColor, targetRatio: Double): UnifiedColor {
        val hsl = color.toHsl()
        val bgLum = relativeLuminance(background)

        // Try both directions (lighter and darker), pick the one closer to original
        var bestLight: Double? = null
        var bestDark: Double? = null

        // Search lighter
        for (l in hsl.l.toInt()..100) {
            val candidate = HslColor(hsl.h, hsl.s, l.toDouble(), hsl.a).toUnifiedColor()
            if (contrastRatio(candidate, background) >= targetRatio) {
                bestLight = l.toDouble()
                break
            }
        }

        // Search darker
        for (l in hsl.l.toInt() downTo 0) {
            val candidate = HslColor(hsl.h, hsl.s, l.toDouble(), hsl.a).toUnifiedColor()
            if (contrastRatio(candidate, background) >= targetRatio) {
                bestDark = l.toDouble()
                break
            }
        }

        val targetL = when {
            bestLight != null && bestDark != null -> {
                if (Math.abs(bestLight - hsl.l) <= Math.abs(bestDark - hsl.l)) bestLight else bestDark
            }
            bestLight != null -> bestLight
            bestDark != null -> bestDark
            else -> if (bgLum > 0.5) 0.0 else 100.0 // fallback to black or white
        }
        return HslColor(hsl.h, hsl.s, targetL, hsl.a).toUnifiedColor()
    }
}
