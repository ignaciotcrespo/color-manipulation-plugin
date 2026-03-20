package com.github.ignaciotcrespo.colormanipulation.actions.definitions

import com.github.ignaciotcrespo.colormanipulation.actions.ActionEntry
import com.github.ignaciotcrespo.colormanipulation.actions.MenuDefinition
import com.github.ignaciotcrespo.colormanipulation.model.ColorConverter
import com.github.ignaciotcrespo.colormanipulation.model.ColorTransforms

val ColorMixingMenu = MenuDefinition("Color Mixing...", listOf(
    ActionEntry.Sep("Tint (mix with white)"),
    ActionEntry.Transform("Tint 10%") { c, _ -> ColorTransforms.tint(c, 10.0) },
    ActionEntry.Transform("Tint 25%") { c, _ -> ColorTransforms.tint(c, 25.0) },
    ActionEntry.Transform("Tint 50%") { c, _ -> ColorTransforms.tint(c, 50.0) },
    ActionEntry.Sep("Shade (mix with black)"),
    ActionEntry.Transform("Shade 10%") { c, _ -> ColorTransforms.shade(c, 10.0) },
    ActionEntry.Transform("Shade 25%") { c, _ -> ColorTransforms.shade(c, 25.0) },
    ActionEntry.Transform("Shade 50%") { c, _ -> ColorTransforms.shade(c, 50.0) },
    ActionEntry.Sep(),
    ActionEntry.CustomDialog(
        "Custom Tint/Shade...", "Tint/Shade",
        "Amount (positive = tint, negative = shade):",
        -100.0, 100.0, 25.0
    ) { color, format, amount ->
        val transformed = if (amount >= 0) {
            ColorTransforms.tint(color, amount)
        } else {
            ColorTransforms.shade(color, -amount)
        }
        ColorConverter.format(transformed, format)
    }
))
