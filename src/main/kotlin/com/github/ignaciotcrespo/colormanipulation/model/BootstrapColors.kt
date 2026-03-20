package com.github.ignaciotcrespo.colormanipulation.model

object BootstrapColors {

    // Bootstrap 5.3 colors
    private val colors: Map<String, Long> = mapOf(
        // Theme colors
        "primary" to 0x0D6EFD,
        "secondary" to 0x6C757D,
        "success" to 0x198754,
        "info" to 0x0DCAF0,
        "warning" to 0xFFC107,
        "danger" to 0xDC3545,
        "light" to 0xF8F9FA,
        "dark" to 0x212529,
        // Blue
        "blue-100" to 0xCFE2FF, "blue-200" to 0x9EC5FE, "blue-300" to 0x6EA8FE,
        "blue-400" to 0x3D8BFD, "blue-500" to 0x0D6EFD, "blue-600" to 0x0A58CA,
        "blue-700" to 0x084298, "blue-800" to 0x052C65, "blue-900" to 0x031633,
        // Indigo
        "indigo-100" to 0xE0CFFC, "indigo-200" to 0xC29FFA, "indigo-300" to 0xA370F7,
        "indigo-400" to 0x8540F5, "indigo-500" to 0x6610F2, "indigo-600" to 0x520DC2,
        "indigo-700" to 0x3D0A91, "indigo-800" to 0x290661, "indigo-900" to 0x140330,
        // Purple
        "purple-100" to 0xE2D9F3, "purple-200" to 0xC5B3E6, "purple-300" to 0xA98EDA,
        "purple-400" to 0x8C68CD, "purple-500" to 0x6F42C1, "purple-600" to 0x59359A,
        "purple-700" to 0x432874, "purple-800" to 0x2C1A4D, "purple-900" to 0x160D27,
        // Pink
        "pink-100" to 0xF7D6E6, "pink-200" to 0xEFADCE, "pink-300" to 0xE685B5,
        "pink-400" to 0xDE5C9D, "pink-500" to 0xD63384, "pink-600" to 0xAB296A,
        "pink-700" to 0x801F4F, "pink-800" to 0x561435, "pink-900" to 0x2B0A1A,
        // Red
        "red-100" to 0xF8D7DA, "red-200" to 0xF1AEB5, "red-300" to 0xEA868F,
        "red-400" to 0xE35D6A, "red-500" to 0xDC3545, "red-600" to 0xB02A37,
        "red-700" to 0x842029, "red-800" to 0x58151C, "red-900" to 0x2C0B0E,
        // Orange
        "orange-100" to 0xFFE5D0, "orange-200" to 0xFECBA1, "orange-300" to 0xFEB272,
        "orange-400" to 0xFD9843, "orange-500" to 0xFD7E14, "orange-600" to 0xCA6510,
        "orange-700" to 0x984C0C, "orange-800" to 0x653208, "orange-900" to 0x331904,
        // Yellow
        "yellow-100" to 0xFFF3CD, "yellow-200" to 0xFFE69C, "yellow-300" to 0xFFDA6A,
        "yellow-400" to 0xFFCD39, "yellow-500" to 0xFFC107, "yellow-600" to 0xCC9A06,
        "yellow-700" to 0x997404, "yellow-800" to 0x664D03, "yellow-900" to 0x332701,
        // Green
        "green-100" to 0xD1E7DD, "green-200" to 0xA3CFBB, "green-300" to 0x75B798,
        "green-400" to 0x479F76, "green-500" to 0x198754, "green-600" to 0x146C43,
        "green-700" to 0x0F5132, "green-800" to 0x0A3622, "green-900" to 0x051B11,
        // Teal
        "teal-100" to 0xD2F4EA, "teal-200" to 0xA6E9D5, "teal-300" to 0x79DFC1,
        "teal-400" to 0x4DD4AC, "teal-500" to 0x20C997, "teal-600" to 0x1AA179,
        "teal-700" to 0x13795B, "teal-800" to 0x0D503C, "teal-900" to 0x06281E,
        // Cyan
        "cyan-100" to 0xCFF4FC, "cyan-200" to 0x9EEAF9, "cyan-300" to 0x6EDFF6,
        "cyan-400" to 0x3DD5F3, "cyan-500" to 0x0DCAF0, "cyan-600" to 0x0AA2C0,
        "cyan-700" to 0x087990, "cyan-800" to 0x055160, "cyan-900" to 0x032830,
        // Gray
        "gray-100" to 0xF8F9FA, "gray-200" to 0xE9ECEF, "gray-300" to 0xDEE2E6,
        "gray-400" to 0xCED4DA, "gray-500" to 0xADB5BD, "gray-600" to 0x6C757D,
        "gray-700" to 0x495057, "gray-800" to 0x343A40, "gray-900" to 0x212529,
    )

    private val parsed: List<Pair<String, UnifiedColor>> by lazy {
        colors.map { (name, hex) ->
            name to UnifiedColor.fromRgbInt(hex.toInt())
        }
    }

    fun allNames(): Set<String> = colors.keys

    fun getByName(name: String): UnifiedColor? {
        val hex = colors[name.lowercase().trim()] ?: return null
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
