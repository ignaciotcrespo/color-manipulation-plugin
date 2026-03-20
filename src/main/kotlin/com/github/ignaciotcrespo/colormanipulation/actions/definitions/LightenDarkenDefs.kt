package com.github.ignaciotcrespo.colormanipulation.actions.definitions

import com.github.ignaciotcrespo.colormanipulation.actions.ActionEntry
import com.github.ignaciotcrespo.colormanipulation.actions.MenuDefinition
import com.github.ignaciotcrespo.colormanipulation.model.ColorConverter
import com.github.ignaciotcrespo.colormanipulation.model.ColorTransforms

val LightenDarkenMenu = MenuDefinition("Lighten/Darken...", listOf(
    ActionEntry.Transform("Lighten 5%") { c, _ -> ColorTransforms.lighten(c, 5.0) },
    ActionEntry.Transform("Lighten 10%") { c, _ -> ColorTransforms.lighten(c, 10.0) },
    ActionEntry.Transform("Lighten 20%") { c, _ -> ColorTransforms.lighten(c, 20.0) },
    ActionEntry.Sep(),
    ActionEntry.Transform("Darken 5%") { c, _ -> ColorTransforms.darken(c, 5.0) },
    ActionEntry.Transform("Darken 10%") { c, _ -> ColorTransforms.darken(c, 10.0) },
    ActionEntry.Transform("Darken 20%") { c, _ -> ColorTransforms.darken(c, 20.0) },
    ActionEntry.Sep(),
    ActionEntry.CustomDialog(
        "Custom...", "Lighten/Darken",
        "Amount (positive = lighten, negative = darken):",
        -100.0, 100.0, 10.0
    ) { color, format, amount ->
        val transformed = if (amount >= 0) {
            ColorTransforms.lighten(color, amount)
        } else {
            ColorTransforms.darken(color, -amount)
        }
        ColorConverter.format(transformed, format)
    }
))
