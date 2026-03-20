package com.github.ignaciotcrespo.colormanipulation.actions.definitions

import com.github.ignaciotcrespo.colormanipulation.actions.ActionEntry
import com.github.ignaciotcrespo.colormanipulation.actions.MenuDefinition
import com.github.ignaciotcrespo.colormanipulation.model.ColorConverter
import com.github.ignaciotcrespo.colormanipulation.model.ColorTransforms

val SaturateMenu = MenuDefinition("Saturate/Desaturate...", listOf(
    ActionEntry.Transform("Saturate 10%") { c, _ -> ColorTransforms.saturate(c, 10.0) },
    ActionEntry.Transform("Saturate 20%") { c, _ -> ColorTransforms.saturate(c, 20.0) },
    ActionEntry.Sep(),
    ActionEntry.Transform("Desaturate 10%") { c, _ -> ColorTransforms.desaturate(c, 10.0) },
    ActionEntry.Transform("Desaturate 20%") { c, _ -> ColorTransforms.desaturate(c, 20.0) },
    ActionEntry.Sep(),
    ActionEntry.Transform("Grayscale") { c, _ -> ColorTransforms.grayscale(c) },
    ActionEntry.CustomDialog(
        "Custom...", "Saturate/Desaturate",
        "Amount (positive = saturate, negative = desaturate):",
        -100.0, 100.0, 10.0
    ) { color, format, amount ->
        val transformed = if (amount >= 0) {
            ColorTransforms.saturate(color, amount)
        } else {
            ColorTransforms.desaturate(color, -amount)
        }
        ColorConverter.format(transformed, format)
    }
))
