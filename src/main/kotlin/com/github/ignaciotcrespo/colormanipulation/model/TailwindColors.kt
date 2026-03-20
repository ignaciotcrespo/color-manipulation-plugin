package com.github.ignaciotcrespo.colormanipulation.model

object TailwindColors {

    private val colors: Map<String, Long> = mapOf(
        // Slate
        "slate-50" to 0xf8fafc, "slate-100" to 0xf1f5f9, "slate-200" to 0xe2e8f0,
        "slate-300" to 0xcbd5e1, "slate-400" to 0x94a3b8, "slate-500" to 0x64748b,
        "slate-600" to 0x475569, "slate-700" to 0x334155, "slate-800" to 0x1e293b, "slate-900" to 0x0f172a, "slate-950" to 0x020617,
        // Gray
        "gray-50" to 0xf9fafb, "gray-100" to 0xf3f4f6, "gray-200" to 0xe5e7eb,
        "gray-300" to 0xd1d5db, "gray-400" to 0x9ca3af, "gray-500" to 0x6b7280,
        "gray-600" to 0x4b5563, "gray-700" to 0x374151, "gray-800" to 0x1f2937, "gray-900" to 0x111827, "gray-950" to 0x030712,
        // Zinc
        "zinc-50" to 0xfafafa, "zinc-100" to 0xf4f4f5, "zinc-200" to 0xe4e4e7,
        "zinc-300" to 0xd4d4d8, "zinc-400" to 0xa1a1aa, "zinc-500" to 0x71717a,
        "zinc-600" to 0x52525b, "zinc-700" to 0x3f3f46, "zinc-800" to 0x27272a, "zinc-900" to 0x18181b, "zinc-950" to 0x09090b,
        // Neutral
        "neutral-50" to 0xfafafa, "neutral-100" to 0xf5f5f5, "neutral-200" to 0xe5e5e5,
        "neutral-300" to 0xd4d4d4, "neutral-400" to 0xa3a3a3, "neutral-500" to 0x737373,
        "neutral-600" to 0x525252, "neutral-700" to 0x404040, "neutral-800" to 0x262626, "neutral-900" to 0x171717, "neutral-950" to 0x0a0a0a,
        // Stone
        "stone-50" to 0xfafaf9, "stone-100" to 0xf5f5f4, "stone-200" to 0xe7e5e4,
        "stone-300" to 0xd6d3d1, "stone-400" to 0xa8a29e, "stone-500" to 0x78716c,
        "stone-600" to 0x57534e, "stone-700" to 0x44403c, "stone-800" to 0x292524, "stone-900" to 0x1c1917, "stone-950" to 0x0c0a09,
        // Red
        "red-50" to 0xfef2f2, "red-100" to 0xfee2e2, "red-200" to 0xfecaca,
        "red-300" to 0xfca5a5, "red-400" to 0xf87171, "red-500" to 0xef4444,
        "red-600" to 0xdc2626, "red-700" to 0xb91c1c, "red-800" to 0x991b1b, "red-900" to 0x7f1d1d, "red-950" to 0x450a0a,
        // Orange
        "orange-50" to 0xfff7ed, "orange-100" to 0xffedd5, "orange-200" to 0xfed7aa,
        "orange-300" to 0xfdba74, "orange-400" to 0xfb923c, "orange-500" to 0xf97316,
        "orange-600" to 0xea580c, "orange-700" to 0xc2410c, "orange-800" to 0x9a3412, "orange-900" to 0x7c2d12, "orange-950" to 0x431407,
        // Amber
        "amber-50" to 0xfffbeb, "amber-100" to 0xfef3c7, "amber-200" to 0xfde68a,
        "amber-300" to 0xfcd34d, "amber-400" to 0xfbbf24, "amber-500" to 0xf59e0b,
        "amber-600" to 0xd97706, "amber-700" to 0xb45309, "amber-800" to 0x92400e, "amber-900" to 0x78350f, "amber-950" to 0x451a03,
        // Yellow
        "yellow-50" to 0xfefce8, "yellow-100" to 0xfef9c3, "yellow-200" to 0xfef08a,
        "yellow-300" to 0xfde047, "yellow-400" to 0xfacc15, "yellow-500" to 0xeab308,
        "yellow-600" to 0xca8a04, "yellow-700" to 0xa16207, "yellow-800" to 0x854d0e, "yellow-900" to 0x713f12, "yellow-950" to 0x422006,
        // Lime
        "lime-50" to 0xf7fee7, "lime-100" to 0xecfccb, "lime-200" to 0xd9f99d,
        "lime-300" to 0xbef264, "lime-400" to 0xa3e635, "lime-500" to 0x84cc16,
        "lime-600" to 0x65a30d, "lime-700" to 0x4d7c0f, "lime-800" to 0x3f6212, "lime-900" to 0x365314, "lime-950" to 0x1a2e05,
        // Green
        "green-50" to 0xf0fdf4, "green-100" to 0xdcfce7, "green-200" to 0xbbf7d0,
        "green-300" to 0x86efac, "green-400" to 0x4ade80, "green-500" to 0x22c55e,
        "green-600" to 0x16a34a, "green-700" to 0x15803d, "green-800" to 0x166534, "green-900" to 0x14532d, "green-950" to 0x052e16,
        // Emerald
        "emerald-50" to 0xecfdf5, "emerald-100" to 0xd1fae5, "emerald-200" to 0xa7f3d0,
        "emerald-300" to 0x6ee7b7, "emerald-400" to 0x34d399, "emerald-500" to 0x10b981,
        "emerald-600" to 0x059669, "emerald-700" to 0x047857, "emerald-800" to 0x065f46, "emerald-900" to 0x064e3b, "emerald-950" to 0x022c22,
        // Teal
        "teal-50" to 0xf0fdfa, "teal-100" to 0xccfbf1, "teal-200" to 0x99f6e4,
        "teal-300" to 0x5eead4, "teal-400" to 0x2dd4bf, "teal-500" to 0x14b8a6,
        "teal-600" to 0x0d9488, "teal-700" to 0x0f766e, "teal-800" to 0x115e59, "teal-900" to 0x134e4a, "teal-950" to 0x042f2e,
        // Cyan
        "cyan-50" to 0xecfeff, "cyan-100" to 0xcffafe, "cyan-200" to 0xa5f3fc,
        "cyan-300" to 0x67e8f9, "cyan-400" to 0x22d3ee, "cyan-500" to 0x06b6d4,
        "cyan-600" to 0x0891b2, "cyan-700" to 0x0e7490, "cyan-800" to 0x155e75, "cyan-900" to 0x164e63, "cyan-950" to 0x083344,
        // Sky
        "sky-50" to 0xf0f9ff, "sky-100" to 0xe0f2fe, "sky-200" to 0xbae6fd,
        "sky-300" to 0x7dd3fc, "sky-400" to 0x38bdf8, "sky-500" to 0x0ea5e9,
        "sky-600" to 0x0284c7, "sky-700" to 0x0369a1, "sky-800" to 0x075985, "sky-900" to 0x0c4a6e, "sky-950" to 0x082f49,
        // Blue
        "blue-50" to 0xeff6ff, "blue-100" to 0xdbeafe, "blue-200" to 0xbfdbfe,
        "blue-300" to 0x93c5fd, "blue-400" to 0x60a5fa, "blue-500" to 0x3b82f6,
        "blue-600" to 0x2563eb, "blue-700" to 0x1d4ed8, "blue-800" to 0x1e40af, "blue-900" to 0x1e3a8a, "blue-950" to 0x172554,
        // Indigo
        "indigo-50" to 0xeef2ff, "indigo-100" to 0xe0e7ff, "indigo-200" to 0xc7d2fe,
        "indigo-300" to 0xa5b4fc, "indigo-400" to 0x818cf8, "indigo-500" to 0x6366f1,
        "indigo-600" to 0x4f46e5, "indigo-700" to 0x4338ca, "indigo-800" to 0x3730a3, "indigo-900" to 0x312e81, "indigo-950" to 0x1e1b4b,
        // Violet
        "violet-50" to 0xf5f3ff, "violet-100" to 0xede9fe, "violet-200" to 0xddd6fe,
        "violet-300" to 0xc4b5fd, "violet-400" to 0xa78bfa, "violet-500" to 0x8b5cf6,
        "violet-600" to 0x7c3aed, "violet-700" to 0x6d28d9, "violet-800" to 0x5b21b6, "violet-900" to 0x4c1d95, "violet-950" to 0x2e1065,
        // Purple
        "purple-50" to 0xfaf5ff, "purple-100" to 0xf3e8ff, "purple-200" to 0xe9d5ff,
        "purple-300" to 0xd8b4fe, "purple-400" to 0xc084fc, "purple-500" to 0xa855f7,
        "purple-600" to 0x9333ea, "purple-700" to 0x7e22ce, "purple-800" to 0x6b21a8, "purple-900" to 0x581c87, "purple-950" to 0x3b0764,
        // Fuchsia
        "fuchsia-50" to 0xfdf4ff, "fuchsia-100" to 0xfae8ff, "fuchsia-200" to 0xf5d0fe,
        "fuchsia-300" to 0xf0abfc, "fuchsia-400" to 0xe879f9, "fuchsia-500" to 0xd946ef,
        "fuchsia-600" to 0xc026d3, "fuchsia-700" to 0xa21caf, "fuchsia-800" to 0x86198f, "fuchsia-900" to 0x701a75, "fuchsia-950" to 0x4a044e,
        // Pink
        "pink-50" to 0xfdf2f8, "pink-100" to 0xfce7f3, "pink-200" to 0xfbcfe8,
        "pink-300" to 0xf9a8d4, "pink-400" to 0xf472b6, "pink-500" to 0xec4899,
        "pink-600" to 0xdb2777, "pink-700" to 0xbe185d, "pink-800" to 0x9d174d, "pink-900" to 0x831843, "pink-950" to 0x500724,
        // Rose
        "rose-50" to 0xfff1f2, "rose-100" to 0xffe4e6, "rose-200" to 0xfecdd3,
        "rose-300" to 0xfda4af, "rose-400" to 0xfb7185, "rose-500" to 0xf43f5e,
        "rose-600" to 0xe11d48, "rose-700" to 0xbe123c, "rose-800" to 0x9f1239, "rose-900" to 0x881337, "rose-950" to 0x4c0519,
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
