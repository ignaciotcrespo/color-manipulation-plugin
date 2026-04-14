package com.github.ignaciotcrespo.colormanipulation.model

import kotlin.math.roundToInt

object ColorConverter {

    // ── Parse regexes (anchored, for single-color selection) ──

    private val HEX8_RE = Regex("""^#([0-9a-fA-F]{8})$""")
    private val HEX6_RE = Regex("""^#([0-9a-fA-F]{6})$""")
    private val HEX3_RE = Regex("""^#([0-9a-fA-F]{3})$""")
    private val HEX_0X_RE = Regex("""^0x([0-9a-fA-F]{6,8})$""")
    private val HEX3_BARE_RE = Regex("""^([0-9a-fA-F]{3})$""")
    private val HEX6_BARE_RE = Regex("""^([0-9a-fA-F]{6})$""")
    private val HEX8_BARE_RE = Regex("""^([0-9a-fA-F]{8})$""")

    private val RGBA_RE = Regex("""^rgba\(\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*([\d.]+)\s*\)$""")
    private val RGB_RE = Regex("""^rgb\(\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*(\d{1,3})\s*\)$""")
    private val RGB_SPACE_ALPHA_RE = Regex("""^rgb\(\s*(\d{1,3})\s+(\d{1,3})\s+(\d{1,3})\s*/\s*([\d.]+)\s*\)$""")
    private val RGB_SPACE_RE = Regex("""^rgb\(\s*(\d{1,3})\s+(\d{1,3})\s+(\d{1,3})\s*\)$""")
    private val RGB_PERCENT_RE = Regex("""^rgb\(\s*([\d.]+)%\s*,\s*([\d.]+)%\s*,\s*([\d.]+)%\s*\)$""")

    private val HSLA_RE = Regex("""^hsla\(\s*([\d.]+)\s*,\s*([\d.]+)%\s*,\s*([\d.]+)%\s*,\s*([\d.]+)\s*\)$""")
    private val HSL_RE = Regex("""^hsl\(\s*([\d.]+)\s*,\s*([\d.]+)%\s*,\s*([\d.]+)%\s*\)$""")
    private val HSL_SPACE_ALPHA_RE = Regex("""^hsl\(\s*([\d.]+)\s+([\d.]+)%\s+([\d.]+)%\s*/\s*([\d.]+)\s*\)$""")
    private val HSL_SPACE_RE = Regex("""^hsl\(\s*([\d.]+)\s+([\d.]+)%\s+([\d.]+)%\s*\)$""")
    private val HWB_RE = Regex("""^hwb\(\s*([\d.]+)\s+([\d.]+)%\s+([\d.]+)%\s*\)$""")

    private val COMPOSE_RE = Regex("""^Color\(\s*0x([0-9a-fA-F]{8})\s*\)$""")
    private val ANDROID_ARGB_RE = Regex("""^Color\.argb\(\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*(\d{1,3})\s*\)$""")
    private val ANDROID_RGB_RE = Regex("""^Color\.rgb\(\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*(\d{1,3})\s*\)$""")

    private val UICOLOR_RE = Regex("""^UIColor\(\s*red:\s*([\d.]+)\s*,\s*green:\s*([\d.]+)\s*,\s*blue:\s*([\d.]+)\s*,\s*alpha:\s*([\d.]+)\s*\)$""")
    private val UICOLOR_OBJC_RE = Regex("""^\[UIColor\s+colorWithRed:\s*([\d.]+)\s+green:\s*([\d.]+)\s+blue:\s*([\d.]+)\s+alpha:\s*([\d.]+)\s*]$""")
    private val SWIFTUI_RE = Regex("""^Color\(\s*red:\s*([\d.]+)\s*,\s*green:\s*([\d.]+)\s*,\s*blue:\s*([\d.]+)\s*\)$""")

    private val JAVA_COLOR_ALPHA_RE = Regex("""^new Color\(\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*(\d{1,3})\s*\)$""")
    private val JAVA_COLOR_RE = Regex("""^new Color\(\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*(\d{1,3})\s*\)$""")

    private val FLOAT_RGB_RE = Regex("""^\(\s*([\d.]+)\s*,\s*([\d.]+)\s*,\s*([\d.]+)\s*\)$""")
    private val FLOAT_RGBA_RE = Regex("""^\(\s*([\d.]+)\s*,\s*([\d.]+)\s*,\s*([\d.]+)\s*,\s*([\d.]+)\s*\)$""")
    private val FLOAT_RGB_NP_RE = Regex("""^([\d.]+)\s*,\s*([\d.]+)\s*,\s*([\d.]+)$""")
    private val FLOAT_RGBA_NP_RE = Regex("""^([\d.]+)\s*,\s*([\d.]+)\s*,\s*([\d.]+)\s*,\s*([\d.]+)$""")

    // ── Parse (single color) ──

    fun parse(text: String): Pair<UnifiedColor, ColorFormat>? {
        val t = text.trim()
        return parseHex8(t) ?: parseHex6(t) ?: parseHex3(t)
            ?: parseRgba(t) ?: parseRgbSpaceAlpha(t) ?: parseRgbSpace(t) ?: parseRgb(t)
            ?: parseRgbPercent(t)
            ?: parseHsla(t) ?: parseHslSpaceAlpha(t) ?: parseHslSpace(t) ?: parseHsl(t)
            ?: parseHwb(t)
            ?: parseHex0x(t)
            ?: parseCompose(t) ?: parseAndroidArgb(t) ?: parseAndroidRgb(t)
            ?: parseUIColor(t) ?: parseUIColorObjc(t) ?: parseSwiftUI(t)
            ?: parseJavaColorAlpha(t) ?: parseJavaColor(t)
            ?: parseFloatRgba(t) ?: parseFloatRgb(t)
            ?: parseFloatRgbaNoParen(t) ?: parseFloatRgbNoParen(t)
            ?: parseNamed(t)
            ?: parseHex8Bare(t) ?: parseHex6Bare(t) ?: parseHex3Bare(t)
    }

    // ── Format ──

    fun format(color: UnifiedColor, fmt: ColorFormat): String {
        val ri = Math.round(color.r).toInt().coerceIn(0, 255)
        val gi = Math.round(color.g).toInt().coerceIn(0, 255)
        val bi = Math.round(color.b).toInt().coerceIn(0, 255)
        val ai = Math.round(color.a * 255).toInt().coerceIn(0, 255)

        return when (fmt) {
            ColorFormat.HEX3 -> {
                "#%X%X%X".format((ri + 8) / 17, (gi + 8) / 17, (bi + 8) / 17)
            }
            ColorFormat.HEX6 -> "#%02X%02X%02X".format(ri, gi, bi)
            ColorFormat.HEX8 -> "#%02X%02X%02X%02X".format(ri, gi, bi, ai)
            ColorFormat.ARGB8 -> "#%02X%02X%02X%02X".format(ai, ri, gi, bi)

            ColorFormat.RGB_FUNC -> "rgb($ri, $gi, $bi)"
            ColorFormat.RGBA_FUNC -> "rgba($ri, $gi, $bi, ${fmtAlpha(color.a)})"
            ColorFormat.RGB_SPACE -> "rgb($ri $gi $bi)"
            ColorFormat.RGB_SPACE_ALPHA -> "rgb($ri $gi $bi / ${fmtAlpha(color.a)})"
            ColorFormat.RGB_PERCENT -> {
                "rgb(${fmtPct(color.r / 255.0 * 100)}, ${fmtPct(color.g / 255.0 * 100)}, ${fmtPct(color.b / 255.0 * 100)})"
            }

            ColorFormat.HSL_FUNC -> {
                val h = color.toHsl(); "hsl(${h.h.roundToInt()}, ${h.s.roundToInt()}%, ${h.l.roundToInt()}%)"
            }
            ColorFormat.HSLA_FUNC -> {
                val h = color.toHsl(); "hsla(${h.h.roundToInt()}, ${h.s.roundToInt()}%, ${h.l.roundToInt()}%, ${fmtAlpha(h.a)})"
            }
            ColorFormat.HSL_SPACE -> {
                val h = color.toHsl(); "hsl(${h.h.roundToInt()} ${h.s.roundToInt()}% ${h.l.roundToInt()}%)"
            }
            ColorFormat.HSL_SPACE_ALPHA -> {
                val h = color.toHsl(); "hsl(${h.h.roundToInt()} ${h.s.roundToInt()}% ${h.l.roundToInt()}% / ${fmtAlpha(h.a)})"
            }
            ColorFormat.HWB -> {
                val h = color.toHsl()
                val w = minOf(color.r, color.g, color.b) / 255.0 * 100.0
                val b = (1.0 - maxOf(color.r, color.g, color.b) / 255.0) * 100.0
                "hwb(${h.h.roundToInt()} ${w.roundToInt()}% ${b.roundToInt()}%)"
            }

            ColorFormat.NAMED_CSS -> NamedCssColors.getName(color) ?: format(color, ColorFormat.HEX6)

            ColorFormat.HEX_0X -> "0x%02X%02X%02X".format(ri, gi, bi)
            ColorFormat.COMPOSE_COLOR -> "Color(0x%02X%02X%02X%02X)".format(ai, ri, gi, bi)
            ColorFormat.ANDROID_RGB -> "Color.rgb($ri, $gi, $bi)"
            ColorFormat.ANDROID_ARGB -> "Color.argb($ai, $ri, $gi, $bi)"

            ColorFormat.UICOLOR -> "UIColor(red: ${fmtFloat(color.r / 255.0)}, green: ${fmtFloat(color.g / 255.0)}, blue: ${fmtFloat(color.b / 255.0)}, alpha: ${fmtFloat(color.a)})"
            ColorFormat.UICOLOR_OBJC -> "[UIColor colorWithRed:${fmtFloat(color.r / 255.0)} green:${fmtFloat(color.g / 255.0)} blue:${fmtFloat(color.b / 255.0)} alpha:${fmtFloat(color.a)}]"
            ColorFormat.SWIFTUI_COLOR -> "Color(red: ${fmtFloat(color.r / 255.0)}, green: ${fmtFloat(color.g / 255.0)}, blue: ${fmtFloat(color.b / 255.0)})"

            ColorFormat.JAVA_COLOR -> "new Color($ri, $gi, $bi)"
            ColorFormat.JAVA_COLOR_ALPHA -> "new Color($ri, $gi, $bi, $ai)"

            ColorFormat.HEX3_NO_HASH -> "%X%X%X".format((ri + 8) / 17, (gi + 8) / 17, (bi + 8) / 17)
            ColorFormat.HEX6_NO_HASH -> "%02X%02X%02X".format(ri, gi, bi)
            ColorFormat.HEX8_NO_HASH -> "%02X%02X%02X%02X".format(ri, gi, bi, ai)
            ColorFormat.ARGB8_NO_HASH -> "%02X%02X%02X%02X".format(ai, ri, gi, bi)
            ColorFormat.FLOAT_RGB -> "(${fmtFloat(color.r / 255.0)}, ${fmtFloat(color.g / 255.0)}, ${fmtFloat(color.b / 255.0)})"
            ColorFormat.FLOAT_RGBA -> "(${fmtFloat(color.r / 255.0)}, ${fmtFloat(color.g / 255.0)}, ${fmtFloat(color.b / 255.0)}, ${fmtFloat(color.a)})"
            ColorFormat.FLOAT_RGB_NO_PAREN -> "${fmtFloat(color.r / 255.0)}, ${fmtFloat(color.g / 255.0)}, ${fmtFloat(color.b / 255.0)}"
            ColorFormat.FLOAT_RGBA_NO_PAREN -> "${fmtFloat(color.r / 255.0)}, ${fmtFloat(color.g / 255.0)}, ${fmtFloat(color.b / 255.0)}, ${fmtFloat(color.a)}"
        }
    }

    // ── findAll (multi-color scan) ──

    data class ColorMatch(
        val color: UnifiedColor,
        val format: ColorFormat,
        val range: IntRange,
        val text: String
    )

    private val SCAN_PATTERNS: List<Pair<Regex, (MatchResult) -> Pair<UnifiedColor, ColorFormat>?>> by lazy {
        listOf(
            // Language-specific constructors (most specific first)
            Regex("""Color\(\s*0x([0-9a-fA-F]{8})\s*\)""") to { m: MatchResult ->
                parseArgbHex(m.groupValues[1])?.let { it to ColorFormat.COMPOSE_COLOR }
            },
            Regex("""Color\.argb\(\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*(\d{1,3})\s*\)""") to { m: MatchResult ->
                val a = m.groupValues[1].toIntOrNull()?.takeIf { it in 0..255 }
                val r = m.groupValues[2].toIntOrNull()?.takeIf { it in 0..255 }
                val g = m.groupValues[3].toIntOrNull()?.takeIf { it in 0..255 }
                val b = m.groupValues[4].toIntOrNull()?.takeIf { it in 0..255 }
                if (a != null && r != null && g != null && b != null)
                    UnifiedColor(r.toDouble(), g.toDouble(), b.toDouble(), a / 255.0) to ColorFormat.ANDROID_ARGB
                else null
            },
            Regex("""Color\.rgb\(\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*(\d{1,3})\s*\)""") to { m: MatchResult ->
                parseRgbTriple(m)?.let { it to ColorFormat.ANDROID_RGB }
            },
            Regex("""UIColor\(\s*red:\s*([\d.]+)\s*,\s*green:\s*([\d.]+)\s*,\s*blue:\s*([\d.]+)\s*,\s*alpha:\s*([\d.]+)\s*\)""") to { m: MatchResult ->
                parseFloatRgba(m)?.let { it to ColorFormat.UICOLOR }
            },
            Regex("""\[UIColor\s+colorWithRed:\s*([\d.]+)\s+green:\s*([\d.]+)\s+blue:\s*([\d.]+)\s+alpha:\s*([\d.]+)\s*]""") to { m: MatchResult ->
                parseFloatRgba(m)?.let { it to ColorFormat.UICOLOR_OBJC }
            },
            Regex("""Color\(\s*red:\s*([\d.]+)\s*,\s*green:\s*([\d.]+)\s*,\s*blue:\s*([\d.]+)\s*\)""") to { m: MatchResult ->
                parseFloatRgbTriple(m)?.let { it to ColorFormat.SWIFTUI_COLOR }
            },
            Regex("""new Color\(\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*(\d{1,3})\s*\)""") to { m: MatchResult ->
                val r = m.groupValues[1].toIntOrNull()?.takeIf { it in 0..255 }
                val g = m.groupValues[2].toIntOrNull()?.takeIf { it in 0..255 }
                val b = m.groupValues[3].toIntOrNull()?.takeIf { it in 0..255 }
                val a = m.groupValues[4].toIntOrNull()?.takeIf { it in 0..255 }
                if (r != null && g != null && b != null && a != null)
                    UnifiedColor(r.toDouble(), g.toDouble(), b.toDouble(), a / 255.0) to ColorFormat.JAVA_COLOR_ALPHA
                else null
            },
            Regex("""new Color\(\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*(\d{1,3})\s*\)""") to { m: MatchResult ->
                parseRgbTriple(m)?.let { it to ColorFormat.JAVA_COLOR }
            },

            // CSS functional (comma variants first, then space)
            Regex("""rgba\(\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*([\d.]+)\s*\)""") to { m: MatchResult ->
                parseRgbTriple(m)?.let { c ->
                    val a = m.groupValues[4].toDoubleOrNull()?.takeIf { it in 0.0..1.0 } ?: return@let null
                    c.copy(a = a) to ColorFormat.RGBA_FUNC
                }
            },
            Regex("""rgb\(\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*(\d{1,3})\s*\)""") to { m: MatchResult ->
                parseRgbTriple(m)?.let { it to ColorFormat.RGB_FUNC }
            },
            Regex("""rgb\(\s*(\d{1,3})\s+(\d{1,3})\s+(\d{1,3})\s*/\s*([\d.]+)\s*\)""") to { m: MatchResult ->
                parseRgbTriple(m)?.let { c ->
                    val a = m.groupValues[4].toDoubleOrNull()?.takeIf { it in 0.0..1.0 } ?: return@let null
                    c.copy(a = a) to ColorFormat.RGB_SPACE_ALPHA
                }
            },
            Regex("""rgb\(\s*(\d{1,3})\s+(\d{1,3})\s+(\d{1,3})\s*\)""") to { m: MatchResult ->
                parseRgbTriple(m)?.let { it to ColorFormat.RGB_SPACE }
            },
            Regex("""hsla\(\s*([\d.]+)\s*,\s*([\d.]+)%\s*,\s*([\d.]+)%\s*,\s*([\d.]+)\s*\)""") to { m: MatchResult ->
                parseHslTriple(m)?.let { (h, s, l) ->
                    val a = m.groupValues[4].toDoubleOrNull()?.takeIf { it in 0.0..1.0 } ?: return@let null
                    UnifiedColor.fromHsl(h, s, l, a) to ColorFormat.HSLA_FUNC
                }
            },
            Regex("""hsl\(\s*([\d.]+)\s*,\s*([\d.]+)%\s*,\s*([\d.]+)%\s*\)""") to { m: MatchResult ->
                parseHslTriple(m)?.let { (h, s, l) -> UnifiedColor.fromHsl(h, s, l) to ColorFormat.HSL_FUNC }
            },
            Regex("""hsl\(\s*([\d.]+)\s+([\d.]+)%\s+([\d.]+)%\s*/\s*([\d.]+)\s*\)""") to { m: MatchResult ->
                parseHslTriple(m)?.let { (h, s, l) ->
                    val a = m.groupValues[4].toDoubleOrNull()?.takeIf { it in 0.0..1.0 } ?: return@let null
                    UnifiedColor.fromHsl(h, s, l, a) to ColorFormat.HSL_SPACE_ALPHA
                }
            },
            Regex("""hsl\(\s*([\d.]+)\s+([\d.]+)%\s+([\d.]+)%\s*\)""") to { m: MatchResult ->
                parseHslTriple(m)?.let { (h, s, l) -> UnifiedColor.fromHsl(h, s, l) to ColorFormat.HSL_SPACE }
            },
            Regex("""hwb\(\s*([\d.]+)\s+([\d.]+)%\s+([\d.]+)%\s*\)""") to { m: MatchResult ->
                parseHwbTriple(m)
            },

            // 0x prefix
            Regex("""0x([0-9a-fA-F]{8})\b""") to { m: MatchResult ->
                parseArgbHex(m.groupValues[1])?.let { it to ColorFormat.HEX_0X }
            },
            Regex("""0x([0-9a-fA-F]{6})\b""") to { m: MatchResult ->
                parseRgbHex(m.groupValues[1])?.let { it to ColorFormat.HEX_0X }
            },

            // # prefixed hex
            Regex("""#([0-9a-fA-F]{8})\b""") to { m: MatchResult ->
                parseRgbaHex(m.groupValues[1])?.let { it to ColorFormat.HEX8 }
            },
            Regex("""#([0-9a-fA-F]{6})\b""") to { m: MatchResult ->
                parseRgbHex(m.groupValues[1])?.let { it to ColorFormat.HEX6 }
            },
            Regex("""#([0-9a-fA-F]{3})\b""") to { m: MatchResult ->
                parseShortHex(m.groupValues[1])?.let { it to ColorFormat.HEX3 }
            },

            // Bare hex in quotes
            Regex("""(?<=['":,\s(])([0-9a-fA-F]{8})(?=['":,\s)])""") to { m: MatchResult ->
                parseRgbaHex(m.groupValues[1])?.let { it to ColorFormat.HEX8_NO_HASH }
            },
            Regex("""(?<=['":,\s(])([0-9a-fA-F]{6})(?=['":,\s)])""") to { m: MatchResult ->
                parseRgbHex(m.groupValues[1])?.let { it to ColorFormat.HEX6_NO_HASH }
            },
        )
    }

    fun findAll(text: String): List<ColorMatch> {
        val results = mutableListOf<ColorMatch>()
        val occupied = BooleanArray(text.length)
        for ((regex, parser) in SCAN_PATTERNS) {
            for (match in regex.findAll(text)) {
                if (match.range.any { occupied[it] }) continue
                val parsed = parser(match) ?: continue
                results.add(ColorMatch(parsed.first, parsed.second, match.range, match.value))
                match.range.forEach { occupied[it] = true }
            }
        }
        return results.sortedBy { it.range.first }
    }

    fun availableConversions(currentFormat: ColorFormat): List<ColorFormat> {
        return ColorFormat.entries.filter { it != currentFormat }
    }

    // ── Helpers ──

    private fun fmtAlpha(a: Double): String =
        if (a == 1.0) "1" else "%.2f".format(a).trimEnd('0').trimEnd('.')

    private fun fmtFloat(v: Double): String =
        "%.2f".format(v).trimEnd('0').trimEnd('.')

    private fun fmtPct(v: Double): String = "${fmtFloat(v)}%"

    // ── Shared parse helpers ──

    private fun parseRgbTriple(m: MatchResult): UnifiedColor? {
        val r = m.groupValues[1].toIntOrNull()?.takeIf { it in 0..255 } ?: return null
        val g = m.groupValues[2].toIntOrNull()?.takeIf { it in 0..255 } ?: return null
        val b = m.groupValues[3].toIntOrNull()?.takeIf { it in 0..255 } ?: return null
        return UnifiedColor(r.toDouble(), g.toDouble(), b.toDouble())
    }

    private fun parseHslTriple(m: MatchResult): Triple<Double, Double, Double>? {
        val h = m.groupValues[1].toDoubleOrNull()?.takeIf { it in 0.0..360.0 } ?: return null
        val s = m.groupValues[2].toDoubleOrNull()?.takeIf { it in 0.0..100.0 } ?: return null
        val l = m.groupValues[3].toDoubleOrNull()?.takeIf { it in 0.0..100.0 } ?: return null
        return Triple(h, s, l)
    }

    private fun parseFloatRgba(m: MatchResult): UnifiedColor? {
        val r = m.groupValues[1].toDoubleOrNull()?.takeIf { it in 0.0..1.0 } ?: return null
        val g = m.groupValues[2].toDoubleOrNull()?.takeIf { it in 0.0..1.0 } ?: return null
        val b = m.groupValues[3].toDoubleOrNull()?.takeIf { it in 0.0..1.0 } ?: return null
        val a = m.groupValues[4].toDoubleOrNull()?.takeIf { it in 0.0..1.0 } ?: return null
        return UnifiedColor(r * 255.0, g * 255.0, b * 255.0, a)
    }

    private fun parseFloatRgbTriple(m: MatchResult): UnifiedColor? {
        val r = m.groupValues[1].toDoubleOrNull()?.takeIf { it in 0.0..1.0 } ?: return null
        val g = m.groupValues[2].toDoubleOrNull()?.takeIf { it in 0.0..1.0 } ?: return null
        val b = m.groupValues[3].toDoubleOrNull()?.takeIf { it in 0.0..1.0 } ?: return null
        return UnifiedColor(r * 255.0, g * 255.0, b * 255.0)
    }

    private fun parseRgbHex(hex: String): UnifiedColor? {
        if (hex.length != 6) return null
        val r = hex.substring(0, 2).toInt(16)
        val g = hex.substring(2, 4).toInt(16)
        val b = hex.substring(4, 6).toInt(16)
        return UnifiedColor(r.toDouble(), g.toDouble(), b.toDouble())
    }

    private fun parseRgbaHex(hex: String): UnifiedColor? {
        if (hex.length != 8) return null
        val r = hex.substring(0, 2).toInt(16)
        val g = hex.substring(2, 4).toInt(16)
        val b = hex.substring(4, 6).toInt(16)
        val a = hex.substring(6, 8).toInt(16)
        return UnifiedColor(r.toDouble(), g.toDouble(), b.toDouble(), a / 255.0)
    }

    private fun parseArgbHex(hex: String): UnifiedColor? {
        if (hex.length != 8) return null
        val a = hex.substring(0, 2).toInt(16)
        val r = hex.substring(2, 4).toInt(16)
        val g = hex.substring(4, 6).toInt(16)
        val b = hex.substring(6, 8).toInt(16)
        return UnifiedColor(r.toDouble(), g.toDouble(), b.toDouble(), a / 255.0)
    }

    private fun parseShortHex(hex: String): UnifiedColor? {
        if (hex.length != 3) return null
        val r = hex[0].digitToInt(16) * 17
        val g = hex[1].digitToInt(16) * 17
        val b = hex[2].digitToInt(16) * 17
        return UnifiedColor(r.toDouble(), g.toDouble(), b.toDouble())
    }

    private fun parseHwbTriple(m: MatchResult): Pair<UnifiedColor, ColorFormat>? {
        val h = m.groupValues[1].toDoubleOrNull()?.takeIf { it in 0.0..360.0 } ?: return null
        val w = m.groupValues[2].toDoubleOrNull()?.takeIf { it in 0.0..100.0 } ?: return null
        val b = m.groupValues[3].toDoubleOrNull()?.takeIf { it in 0.0..100.0 } ?: return null
        // HWB to RGB conversion
        val wNorm = w / 100.0
        val bNorm = b / 100.0
        if (wNorm + bNorm >= 1.0) {
            val gray = wNorm / (wNorm + bNorm) * 255.0
            return UnifiedColor(gray, gray, gray) to ColorFormat.HWB
        }
        val hsl = HslColor(h, 100.0, 50.0).toUnifiedColor()
        val factor = 1.0 - wNorm - bNorm
        val r = hsl.r / 255.0 * factor + wNorm
        val g = hsl.g / 255.0 * factor + wNorm
        val bl = hsl.b / 255.0 * factor + wNorm
        return UnifiedColor(r * 255.0, g * 255.0, bl * 255.0) to ColorFormat.HWB
    }

    // ── Individual parse methods (for single-selection `parse()`) ──

    private fun parseHex3(t: String) = HEX3_RE.matchEntire(t)?.let { parseShortHex(it.groupValues[1])?.let { c -> c to ColorFormat.HEX3 } }
    private fun parseHex6(t: String) = HEX6_RE.matchEntire(t)?.let { parseRgbHex(it.groupValues[1])?.let { c -> c to ColorFormat.HEX6 } }
    private fun parseHex8(t: String) = HEX8_RE.matchEntire(t)?.let { parseRgbaHex(it.groupValues[1])?.let { c -> c to ColorFormat.HEX8 } }

    private fun parseHex0x(t: String): Pair<UnifiedColor, ColorFormat>? {
        val m = HEX_0X_RE.matchEntire(t) ?: return null
        val hex = m.groupValues[1]
        val c = if (hex.length == 8) parseArgbHex(hex) else parseRgbHex(hex)
        return c?.let { it to ColorFormat.HEX_0X }
    }

    private fun parseRgb(t: String) = RGB_RE.matchEntire(t)?.let { parseRgbTriple(it)?.let { c -> c to ColorFormat.RGB_FUNC } }
    private fun parseRgba(t: String): Pair<UnifiedColor, ColorFormat>? {
        val m = RGBA_RE.matchEntire(t) ?: return null
        val c = parseRgbTriple(m) ?: return null
        val a = m.groupValues[4].toDoubleOrNull()?.takeIf { it in 0.0..1.0 } ?: return null
        return c.copy(a = a) to ColorFormat.RGBA_FUNC
    }
    private fun parseRgbSpace(t: String) = RGB_SPACE_RE.matchEntire(t)?.let { parseRgbTriple(it)?.let { c -> c to ColorFormat.RGB_SPACE } }
    private fun parseRgbSpaceAlpha(t: String): Pair<UnifiedColor, ColorFormat>? {
        val m = RGB_SPACE_ALPHA_RE.matchEntire(t) ?: return null
        val c = parseRgbTriple(m) ?: return null
        val a = m.groupValues[4].toDoubleOrNull()?.takeIf { it in 0.0..1.0 } ?: return null
        return c.copy(a = a) to ColorFormat.RGB_SPACE_ALPHA
    }
    private fun parseRgbPercent(t: String): Pair<UnifiedColor, ColorFormat>? {
        val m = RGB_PERCENT_RE.matchEntire(t) ?: return null
        val r = m.groupValues[1].toDoubleOrNull()?.takeIf { it in 0.0..100.0 } ?: return null
        val g = m.groupValues[2].toDoubleOrNull()?.takeIf { it in 0.0..100.0 } ?: return null
        val b = m.groupValues[3].toDoubleOrNull()?.takeIf { it in 0.0..100.0 } ?: return null
        return UnifiedColor(r / 100.0 * 255.0, g / 100.0 * 255.0, b / 100.0 * 255.0) to ColorFormat.RGB_PERCENT
    }

    private fun parseHsl(t: String) = HSL_RE.matchEntire(t)?.let { parseHslTriple(it)?.let { (h, s, l) -> UnifiedColor.fromHsl(h, s, l) to ColorFormat.HSL_FUNC } }
    private fun parseHsla(t: String): Pair<UnifiedColor, ColorFormat>? {
        val m = HSLA_RE.matchEntire(t) ?: return null
        val (h, s, l) = parseHslTriple(m) ?: return null
        val a = m.groupValues[4].toDoubleOrNull()?.takeIf { it in 0.0..1.0 } ?: return null
        return UnifiedColor.fromHsl(h, s, l, a) to ColorFormat.HSLA_FUNC
    }
    private fun parseHslSpace(t: String) = HSL_SPACE_RE.matchEntire(t)?.let { parseHslTriple(it)?.let { (h, s, l) -> UnifiedColor.fromHsl(h, s, l) to ColorFormat.HSL_SPACE } }
    private fun parseHslSpaceAlpha(t: String): Pair<UnifiedColor, ColorFormat>? {
        val m = HSL_SPACE_ALPHA_RE.matchEntire(t) ?: return null
        val (h, s, l) = parseHslTriple(m) ?: return null
        val a = m.groupValues[4].toDoubleOrNull()?.takeIf { it in 0.0..1.0 } ?: return null
        return UnifiedColor.fromHsl(h, s, l, a) to ColorFormat.HSL_SPACE_ALPHA
    }
    private fun parseHwb(t: String) = HWB_RE.matchEntire(t)?.let { parseHwbTriple(it) }

    private fun parseCompose(t: String): Pair<UnifiedColor, ColorFormat>? {
        val m = COMPOSE_RE.matchEntire(t) ?: return null
        return parseArgbHex(m.groupValues[1])?.let { it to ColorFormat.COMPOSE_COLOR }
    }
    private fun parseAndroidArgb(t: String): Pair<UnifiedColor, ColorFormat>? {
        val m = ANDROID_ARGB_RE.matchEntire(t) ?: return null
        val a = m.groupValues[1].toIntOrNull()?.takeIf { it in 0..255 } ?: return null
        val r = m.groupValues[2].toIntOrNull()?.takeIf { it in 0..255 } ?: return null
        val g = m.groupValues[3].toIntOrNull()?.takeIf { it in 0..255 } ?: return null
        val b = m.groupValues[4].toIntOrNull()?.takeIf { it in 0..255 } ?: return null
        return UnifiedColor(r.toDouble(), g.toDouble(), b.toDouble(), a / 255.0) to ColorFormat.ANDROID_ARGB
    }
    private fun parseAndroidRgb(t: String) = ANDROID_RGB_RE.matchEntire(t)?.let { parseRgbTriple(it)?.let { c -> c to ColorFormat.ANDROID_RGB } }
    private fun parseUIColor(t: String) = UICOLOR_RE.matchEntire(t)?.let { parseFloatRgba(it)?.let { c -> c to ColorFormat.UICOLOR } }
    private fun parseUIColorObjc(t: String) = UICOLOR_OBJC_RE.matchEntire(t)?.let { parseFloatRgba(it)?.let { c -> c to ColorFormat.UICOLOR_OBJC } }
    private fun parseSwiftUI(t: String) = SWIFTUI_RE.matchEntire(t)?.let { parseFloatRgbTriple(it)?.let { c -> c to ColorFormat.SWIFTUI_COLOR } }
    private fun parseJavaColor(t: String) = JAVA_COLOR_RE.matchEntire(t)?.let { parseRgbTriple(it)?.let { c -> c to ColorFormat.JAVA_COLOR } }
    private fun parseJavaColorAlpha(t: String): Pair<UnifiedColor, ColorFormat>? {
        val m = JAVA_COLOR_ALPHA_RE.matchEntire(t) ?: return null
        val r = m.groupValues[1].toIntOrNull()?.takeIf { it in 0..255 } ?: return null
        val g = m.groupValues[2].toIntOrNull()?.takeIf { it in 0..255 } ?: return null
        val b = m.groupValues[3].toIntOrNull()?.takeIf { it in 0..255 } ?: return null
        val a = m.groupValues[4].toIntOrNull()?.takeIf { it in 0..255 } ?: return null
        return UnifiedColor(r.toDouble(), g.toDouble(), b.toDouble(), a / 255.0) to ColorFormat.JAVA_COLOR_ALPHA
    }
    private fun parseFloatRgb(t: String) = FLOAT_RGB_RE.matchEntire(t)?.let { parseFloatRgbTriple(it)?.let { c -> c to ColorFormat.FLOAT_RGB } }
    private fun parseFloatRgba(t: String) = FLOAT_RGBA_RE.matchEntire(t)?.let { parseFloatRgba(it)?.let { c -> c to ColorFormat.FLOAT_RGBA } }
    private fun parseFloatRgbNoParen(t: String) = FLOAT_RGB_NP_RE.matchEntire(t)?.let { parseFloatRgbTriple(it)?.let { c -> c to ColorFormat.FLOAT_RGB_NO_PAREN } }
    private fun parseFloatRgbaNoParen(t: String) = FLOAT_RGBA_NP_RE.matchEntire(t)?.let { parseFloatRgba(it)?.let { c -> c to ColorFormat.FLOAT_RGBA_NO_PAREN } }

    private fun parseNamed(t: String) = NamedCssColors.getColor(t)?.let { it to ColorFormat.NAMED_CSS }

    private fun parseHex3Bare(t: String) = HEX3_BARE_RE.matchEntire(t)?.let { parseShortHex(it.groupValues[1])?.let { c -> c to ColorFormat.HEX3_NO_HASH } }
    private fun parseHex6Bare(t: String) = HEX6_BARE_RE.matchEntire(t)?.let { parseRgbHex(it.groupValues[1])?.let { c -> c to ColorFormat.HEX6_NO_HASH } }
    private fun parseHex8Bare(t: String) = HEX8_BARE_RE.matchEntire(t)?.let { parseRgbaHex(it.groupValues[1])?.let { c -> c to ColorFormat.HEX8_NO_HASH } }
}
