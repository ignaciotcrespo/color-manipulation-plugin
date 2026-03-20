package com.github.ignaciotcrespo.colormanipulation.actions.definitions

import com.github.ignaciotcrespo.colormanipulation.actions.ActionEntry
import com.github.ignaciotcrespo.colormanipulation.actions.MenuDefinition
import com.github.ignaciotcrespo.colormanipulation.model.ColorConverter
import com.github.ignaciotcrespo.colormanipulation.model.ColorTransforms

val TemperatureMenu = MenuDefinition("Temperature...", listOf(
    ActionEntry.Transform("Warmer 10%") { c, _ -> ColorTransforms.warmer(c, 10.0) },
    ActionEntry.Transform("Warmer 20%") { c, _ -> ColorTransforms.warmer(c, 20.0) },
    ActionEntry.Transform("Warmer 40%") { c, _ -> ColorTransforms.warmer(c, 40.0) },
    ActionEntry.Sep(),
    ActionEntry.Transform("Cooler 10%") { c, _ -> ColorTransforms.cooler(c, 10.0) },
    ActionEntry.Transform("Cooler 20%") { c, _ -> ColorTransforms.cooler(c, 20.0) },
    ActionEntry.Transform("Cooler 40%") { c, _ -> ColorTransforms.cooler(c, 40.0) },
    ActionEntry.Sep(),
    ActionEntry.CustomDialog(
        "Custom...", "Temperature",
        "Amount (positive = warmer, negative = cooler):",
        -100.0, 100.0, 20.0
    ) { color, format, amount ->
        val transformed = if (amount >= 0) {
            ColorTransforms.warmer(color, amount)
        } else {
            ColorTransforms.cooler(color, -amount)
        }
        ColorConverter.format(transformed, format)
    }
))
