package com.github.ignaciotcrespo.colormanipulation.model

object IOSSystemColors {

    // iOS 17 light mode system colors
    private val colors: Map<String, Long> = mapOf(
        // System colors
        "systemRed" to 0xFF3B30,
        "systemOrange" to 0xFF9500,
        "systemYellow" to 0xFFCC00,
        "systemGreen" to 0x34C759,
        "systemMint" to 0x00C7BE,
        "systemTeal" to 0x30B0C7,
        "systemCyan" to 0x32ADE6,
        "systemBlue" to 0x007AFF,
        "systemIndigo" to 0x5856D6,
        "systemPurple" to 0xAF52DE,
        "systemPink" to 0xFF2D55,
        "systemBrown" to 0xA2845E,
        // System grays
        "systemGray" to 0x8E8E93,
        "systemGray2" to 0xAEAEB2,
        "systemGray3" to 0xC7C7CC,
        "systemGray4" to 0xD1D1D6,
        "systemGray5" to 0xE5E5EA,
        "systemGray6" to 0xF2F2F7,
        // Semantic colors
        "label" to 0x000000,
        "secondaryLabel" to 0x3C3C43,
        "tertiaryLabel" to 0x3C3C43,
        "quaternaryLabel" to 0x3C3C43,
        "separator" to 0x3C3C43,
        "link" to 0x007AFF,
        // Backgrounds
        "systemBackground" to 0xFFFFFF,
        "secondarySystemBackground" to 0xF2F2F7,
        "tertiarySystemBackground" to 0xFFFFFF,
    )

    private val parsed: List<Pair<String, UnifiedColor>> by lazy {
        colors.map { (name, hex) ->
            name to UnifiedColor.fromRgbInt(hex.toInt())
        }
    }

    fun allNames(): Set<String> = colors.keys

    fun getByName(name: String): UnifiedColor? {
        val key = name.trim()
        val hex = colors[key] ?: colors.entries.find { it.key.equals(key, ignoreCase = true) }?.value ?: return null
        return UnifiedColor.fromRgbInt(hex.toInt())
    }

    fun findClosest(color: UnifiedColor): Pair<String, UnifiedColor> {
        return parsed.minBy { (_, c) -> colorDistance(color, c) }
    }

    private fun colorDistance(a: UnifiedColor, b: UnifiedColor): Double {
        val dr = a.r - b.r; val dg = a.g - b.g; val db = a.b - b.b
        return dr * dr + dg * dg + db * db
    }
}
