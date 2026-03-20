package com.github.ignaciotcrespo.colormanipulation.model

enum class ColorFormat(
    val displayName: String,
    val category: Category,
    val supportsAlpha: Boolean = false
) {
    // Web/CSS
    HEX3("#RGB", Category.CSS),
    HEX6("#RRGGBB", Category.CSS),
    HEX8("#RRGGBBAA", Category.CSS, supportsAlpha = true),
    ARGB8("#AARRGGBB", Category.CSS, supportsAlpha = true),
    RGB_FUNC("rgb(r, g, b)", Category.CSS),
    RGBA_FUNC("rgba(r, g, b, a)", Category.CSS, supportsAlpha = true),
    RGB_SPACE("rgb(r g b)", Category.CSS),
    RGB_SPACE_ALPHA("rgb(r g b / a)", Category.CSS, supportsAlpha = true),
    RGB_PERCENT("rgb(r%, g%, b%)", Category.CSS),
    HSL_FUNC("hsl(h, s%, l%)", Category.CSS),
    HSLA_FUNC("hsla(h, s%, l%, a)", Category.CSS, supportsAlpha = true),
    HSL_SPACE("hsl(h s% l%)", Category.CSS),
    HSL_SPACE_ALPHA("hsl(h s% l% / a)", Category.CSS, supportsAlpha = true),
    HWB("hwb(h w% b%)", Category.CSS),
    NAMED_CSS("Named CSS Color", Category.CSS),

    // Android / Kotlin
    HEX_0X("0xRRGGBB", Category.ANDROID),
    COMPOSE_COLOR("Color(0xAARRGGBB)", Category.ANDROID, supportsAlpha = true),
    ANDROID_RGB("Color.rgb(r, g, b)", Category.ANDROID),
    ANDROID_ARGB("Color.argb(a, r, g, b)", Category.ANDROID, supportsAlpha = true),

    // iOS / Swift
    UICOLOR("UIColor(red:green:blue:alpha:)", Category.SWIFT, supportsAlpha = true),
    SWIFTUI_COLOR("Color(red:green:blue:)", Category.SWIFT),

    // Java
    JAVA_COLOR("new Color(r, g, b)", Category.JAVA),
    JAVA_COLOR_ALPHA("new Color(r, g, b, a)", Category.JAVA, supportsAlpha = true),

    // Generic (no prefix)
    HEX3_NO_HASH("RGB", Category.GENERIC),
    HEX6_NO_HASH("RRGGBB", Category.GENERIC),
    HEX8_NO_HASH("RRGGBBAA", Category.GENERIC, supportsAlpha = true),
    ARGB8_NO_HASH("AARRGGBB", Category.GENERIC, supportsAlpha = true),
    FLOAT_RGB("(r, g, b) float", Category.GENERIC),
    FLOAT_RGBA("(r, g, b, a) float", Category.GENERIC, supportsAlpha = true),
    FLOAT_RGB_NO_PAREN("r, g, b float", Category.GENERIC),
    FLOAT_RGBA_NO_PAREN("r, g, b, a float", Category.GENERIC, supportsAlpha = true),
    ;

    val isNoHash: Boolean get() = this == HEX3_NO_HASH || this == HEX6_NO_HASH || this == HEX8_NO_HASH || this == ARGB8_NO_HASH

    fun alphaVariant(): ColorFormat = when (this) {
        HEX3, HEX6 -> HEX8
        HEX3_NO_HASH, HEX6_NO_HASH -> HEX8_NO_HASH
        RGB_FUNC -> RGBA_FUNC
        RGB_SPACE -> RGB_SPACE_ALPHA
        HSL_FUNC -> HSLA_FUNC
        HSL_SPACE -> HSL_SPACE_ALPHA
        HEX_0X -> HEX8_NO_HASH
        NAMED_CSS -> RGBA_FUNC
        JAVA_COLOR -> JAVA_COLOR_ALPHA
        FLOAT_RGB -> FLOAT_RGBA
        FLOAT_RGB_NO_PAREN -> FLOAT_RGBA_NO_PAREN
        else -> this
    }

    fun noHashVariant(): ColorFormat = when (this) {
        HEX3 -> HEX3_NO_HASH
        HEX6 -> HEX6_NO_HASH
        HEX8 -> HEX8_NO_HASH
        ARGB8 -> ARGB8_NO_HASH
        else -> this
    }

    fun hashVariant(): ColorFormat = when (this) {
        HEX3_NO_HASH -> HEX3
        HEX6_NO_HASH -> HEX6
        HEX8_NO_HASH -> HEX8
        ARGB8_NO_HASH -> ARGB8
        else -> this
    }

    enum class Category(val label: String) {
        CSS("Web / CSS"),
        ANDROID("Android / Kotlin"),
        SWIFT("iOS / Swift"),
        JAVA("Java"),
        GENERIC("Generic"),
    }
}
