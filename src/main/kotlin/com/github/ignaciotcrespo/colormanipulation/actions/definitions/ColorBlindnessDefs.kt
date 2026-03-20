package com.github.ignaciotcrespo.colormanipulation.actions.definitions

import com.github.ignaciotcrespo.colormanipulation.actions.ActionEntry
import com.github.ignaciotcrespo.colormanipulation.actions.MenuDefinition
import com.github.ignaciotcrespo.colormanipulation.model.ColorTransforms

val ColorBlindnessMenu = MenuDefinition("Color Blindness Simulation...", listOf(
    ActionEntry.Transform("Protanopia (red-blind)") { c, _ -> ColorTransforms.simulateProtanopia(c) },
    ActionEntry.Transform("Deuteranopia (green-blind)") { c, _ -> ColorTransforms.simulateDeuteranopia(c) },
    ActionEntry.Transform("Tritanopia (blue-blind)") { c, _ -> ColorTransforms.simulateTritanopia(c) },
))
