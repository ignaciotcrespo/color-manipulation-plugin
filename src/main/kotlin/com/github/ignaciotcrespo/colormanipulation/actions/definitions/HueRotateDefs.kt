package com.github.ignaciotcrespo.colormanipulation.actions.definitions

import com.github.ignaciotcrespo.colormanipulation.actions.ActionEntry
import com.github.ignaciotcrespo.colormanipulation.actions.MenuDefinition
import com.github.ignaciotcrespo.colormanipulation.model.ColorConverter
import com.github.ignaciotcrespo.colormanipulation.model.ColorTransforms

val HueRotateMenu = MenuDefinition("Hue Rotate...", listOf(
    ActionEntry.Transform("Rotate +30\u00B0") { c, _ -> ColorTransforms.hueRotate(c, 30.0) },
    ActionEntry.Transform("Rotate +60\u00B0") { c, _ -> ColorTransforms.hueRotate(c, 60.0) },
    ActionEntry.Transform("Rotate +90\u00B0") { c, _ -> ColorTransforms.hueRotate(c, 90.0) },
    ActionEntry.Transform("Rotate +180\u00B0 (complement)") { c, _ -> ColorTransforms.hueRotate(c, 180.0) },
    ActionEntry.Sep(),
    ActionEntry.Transform("Invert") { c, _ -> ColorTransforms.invert(c) },
    ActionEntry.CustomDialog(
        "Custom...", "Hue Rotate",
        "Degrees (-360 to 360):",
        -360.0, 360.0, 30.0
    ) { color, format, degrees ->
        ColorConverter.format(ColorTransforms.hueRotate(color, degrees), format)
    }
))
