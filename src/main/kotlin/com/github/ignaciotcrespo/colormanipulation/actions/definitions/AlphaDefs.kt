package com.github.ignaciotcrespo.colormanipulation.actions.definitions

import com.github.ignaciotcrespo.colormanipulation.actions.ActionEntry
import com.github.ignaciotcrespo.colormanipulation.actions.MenuDefinition
import com.github.ignaciotcrespo.colormanipulation.model.ColorConverter
import com.github.ignaciotcrespo.colormanipulation.model.ColorTransforms

val AlphaMenu = MenuDefinition("Adjust Alpha...", listOf(
    ActionEntry.Transform("Set to 100% (opaque)") { c, _ -> ColorTransforms.adjustAlpha(c, 1.0) },
    ActionEntry.Transform("Set to 75%") { c, _ -> ColorTransforms.adjustAlpha(c, 0.75) },
    ActionEntry.Transform("Set to 50%") { c, _ -> ColorTransforms.adjustAlpha(c, 0.5) },
    ActionEntry.Transform("Set to 25%") { c, _ -> ColorTransforms.adjustAlpha(c, 0.25) },
    ActionEntry.Transform("Set to 0% (transparent)") { c, _ -> ColorTransforms.adjustAlpha(c, 0.0) },
    ActionEntry.Sep(),
    ActionEntry.CustomDialog(
        "Custom...", "Adjust Alpha",
        "Alpha (0-100%):",
        0.0, 100.0, 50.0
    ) { color, format, value ->
        ColorConverter.format(ColorTransforms.adjustAlpha(color, value / 100.0), format)
    }
))
