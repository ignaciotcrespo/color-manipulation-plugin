package com.github.ignaciotcrespo.colormanipulation.model

object MaterialColors {

    private val colors: Map<String, Long> = mapOf(
        // Red
        "Red 50" to 0xFFEBEE, "Red 100" to 0xFFCDD2, "Red 200" to 0xEF9A9A,
        "Red 300" to 0xE57373, "Red 400" to 0xEF5350, "Red 500" to 0xF44336,
        "Red 600" to 0xE53935, "Red 700" to 0xD32F2F, "Red 800" to 0xC62828, "Red 900" to 0xB71C1C,
        // Pink
        "Pink 50" to 0xFCE4EC, "Pink 100" to 0xF8BBD0, "Pink 200" to 0xF48FB1,
        "Pink 300" to 0xF06292, "Pink 400" to 0xEC407A, "Pink 500" to 0xE91E63,
        "Pink 600" to 0xD81B60, "Pink 700" to 0xC2185B, "Pink 800" to 0xAD1457, "Pink 900" to 0x880E4F,
        // Purple
        "Purple 50" to 0xF3E5F5, "Purple 100" to 0xE1BEE7, "Purple 200" to 0xCE93D8,
        "Purple 300" to 0xBA68C8, "Purple 400" to 0xAB47BC, "Purple 500" to 0x9C27B0,
        "Purple 600" to 0x8E24AA, "Purple 700" to 0x7B1FA2, "Purple 800" to 0x6A1B9A, "Purple 900" to 0x4A148C,
        // Deep Purple
        "Deep Purple 50" to 0xEDE7F6, "Deep Purple 100" to 0xD1C4E9, "Deep Purple 200" to 0xB39DDB,
        "Deep Purple 300" to 0x9575CD, "Deep Purple 400" to 0x7E57C2, "Deep Purple 500" to 0x673AB7,
        "Deep Purple 600" to 0x5E35B1, "Deep Purple 700" to 0x512DA8, "Deep Purple 800" to 0x4527A0, "Deep Purple 900" to 0x311B92,
        // Indigo
        "Indigo 50" to 0xE8EAF6, "Indigo 100" to 0xC5CAE9, "Indigo 200" to 0x9FA8DA,
        "Indigo 300" to 0x7986CB, "Indigo 400" to 0x5C6BC0, "Indigo 500" to 0x3F51B5,
        "Indigo 600" to 0x3949AB, "Indigo 700" to 0x303F9F, "Indigo 800" to 0x283593, "Indigo 900" to 0x1A237E,
        // Blue
        "Blue 50" to 0xE3F2FD, "Blue 100" to 0xBBDEFB, "Blue 200" to 0x90CAF9,
        "Blue 300" to 0x64B5F6, "Blue 400" to 0x42A5F5, "Blue 500" to 0x2196F3,
        "Blue 600" to 0x1E88E5, "Blue 700" to 0x1976D2, "Blue 800" to 0x1565C0, "Blue 900" to 0x0D47A1,
        // Light Blue
        "Light Blue 50" to 0xE1F5FE, "Light Blue 100" to 0xB3E5FC, "Light Blue 200" to 0x81D4FA,
        "Light Blue 300" to 0x4FC3F7, "Light Blue 400" to 0x29B6F6, "Light Blue 500" to 0x03A9F4,
        "Light Blue 600" to 0x039BE5, "Light Blue 700" to 0x0288D1, "Light Blue 800" to 0x0277BD, "Light Blue 900" to 0x01579B,
        // Cyan
        "Cyan 50" to 0xE0F7FA, "Cyan 100" to 0xB2EBF2, "Cyan 200" to 0x80DEEA,
        "Cyan 300" to 0x4DD0E1, "Cyan 400" to 0x26C6DA, "Cyan 500" to 0x00BCD4,
        "Cyan 600" to 0x00ACC1, "Cyan 700" to 0x0097A7, "Cyan 800" to 0x00838F, "Cyan 900" to 0x006064,
        // Teal
        "Teal 50" to 0xE0F2F1, "Teal 100" to 0xB2DFDB, "Teal 200" to 0x80CBC4,
        "Teal 300" to 0x4DB6AC, "Teal 400" to 0x26A69A, "Teal 500" to 0x009688,
        "Teal 600" to 0x00897B, "Teal 700" to 0x00796B, "Teal 800" to 0x00695C, "Teal 900" to 0x004D40,
        // Green
        "Green 50" to 0xE8F5E9, "Green 100" to 0xC8E6C9, "Green 200" to 0xA5D6A7,
        "Green 300" to 0x81C784, "Green 400" to 0x66BB6A, "Green 500" to 0x4CAF50,
        "Green 600" to 0x43A047, "Green 700" to 0x388E3C, "Green 800" to 0x2E7D32, "Green 900" to 0x1B5E20,
        // Light Green
        "Light Green 50" to 0xF1F8E9, "Light Green 100" to 0xDCEDC8, "Light Green 200" to 0xC5E1A5,
        "Light Green 300" to 0xAED581, "Light Green 400" to 0x9CCC65, "Light Green 500" to 0x8BC34A,
        "Light Green 600" to 0x7CB342, "Light Green 700" to 0x689F38, "Light Green 800" to 0x558B2F, "Light Green 900" to 0x33691E,
        // Lime
        "Lime 50" to 0xF9FBE7, "Lime 100" to 0xF0F4C3, "Lime 200" to 0xE6EE9C,
        "Lime 300" to 0xDCE775, "Lime 400" to 0xD4E157, "Lime 500" to 0xCDDC39,
        "Lime 600" to 0xC0CA33, "Lime 700" to 0xAFB42B, "Lime 800" to 0x9E9D24, "Lime 900" to 0x827717,
        // Yellow
        "Yellow 50" to 0xFFFDE7, "Yellow 100" to 0xFFF9C4, "Yellow 200" to 0xFFF59D,
        "Yellow 300" to 0xFFF176, "Yellow 400" to 0xFFEE58, "Yellow 500" to 0xFFEB3B,
        "Yellow 600" to 0xFDD835, "Yellow 700" to 0xFBC02D, "Yellow 800" to 0xF9A825, "Yellow 900" to 0xF57F17,
        // Amber
        "Amber 50" to 0xFFF8E1, "Amber 100" to 0xFFECB3, "Amber 200" to 0xFFE082,
        "Amber 300" to 0xFFD54F, "Amber 400" to 0xFFCA28, "Amber 500" to 0xFFC107,
        "Amber 600" to 0xFFB300, "Amber 700" to 0xFFA000, "Amber 800" to 0xFF8F00, "Amber 900" to 0xFF6F00,
        // Orange
        "Orange 50" to 0xFFF3E0, "Orange 100" to 0xFFE0B2, "Orange 200" to 0xFFCC80,
        "Orange 300" to 0xFFB74D, "Orange 400" to 0xFFA726, "Orange 500" to 0xFF9800,
        "Orange 600" to 0xFB8C00, "Orange 700" to 0xF57C00, "Orange 800" to 0xEF6C00, "Orange 900" to 0xE65100,
        // Deep Orange
        "Deep Orange 50" to 0xFBE9E7, "Deep Orange 100" to 0xFFCCBC, "Deep Orange 200" to 0xFFAB91,
        "Deep Orange 300" to 0xFF8A65, "Deep Orange 400" to 0xFF7043, "Deep Orange 500" to 0xFF5722,
        "Deep Orange 600" to 0xF4511E, "Deep Orange 700" to 0xE64A19, "Deep Orange 800" to 0xD84315, "Deep Orange 900" to 0xBF360C,
        // Brown
        "Brown 50" to 0xEFEBE9, "Brown 100" to 0xD7CCC8, "Brown 200" to 0xBCAAA4,
        "Brown 300" to 0xA1887F, "Brown 400" to 0x8D6E63, "Brown 500" to 0x795548,
        "Brown 600" to 0x6D4C41, "Brown 700" to 0x5D4037, "Brown 800" to 0x4E342E, "Brown 900" to 0x3E2723,
        // Grey
        "Grey 50" to 0xFAFAFA, "Grey 100" to 0xF5F5F5, "Grey 200" to 0xEEEEEE,
        "Grey 300" to 0xE0E0E0, "Grey 400" to 0xBDBDBD, "Grey 500" to 0x9E9E9E,
        "Grey 600" to 0x757575, "Grey 700" to 0x616161, "Grey 800" to 0x424242, "Grey 900" to 0x212121,
        // Blue Grey
        "Blue Grey 50" to 0xECEFF1, "Blue Grey 100" to 0xCFD8DC, "Blue Grey 200" to 0xB0BEC5,
        "Blue Grey 300" to 0x90A4AE, "Blue Grey 400" to 0x78909C, "Blue Grey 500" to 0x607D8B,
        "Blue Grey 600" to 0x546E7A, "Blue Grey 700" to 0x455A64, "Blue Grey 800" to 0x37474F, "Blue Grey 900" to 0x263238,
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
