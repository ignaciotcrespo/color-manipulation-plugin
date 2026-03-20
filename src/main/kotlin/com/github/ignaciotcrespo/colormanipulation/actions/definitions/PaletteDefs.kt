package com.github.ignaciotcrespo.colormanipulation.actions.definitions

import com.github.ignaciotcrespo.colormanipulation.actions.ActionEntry
import com.github.ignaciotcrespo.colormanipulation.actions.MenuDefinition
import com.github.ignaciotcrespo.colormanipulation.model.PaletteGenerator

val PaletteMenu = MenuDefinition("Palette Generate...", listOf(
    ActionEntry.Palette("Complementary") { c -> PaletteGenerator.complementary(c) },
    ActionEntry.Palette("Analogous") { c -> PaletteGenerator.analogous(c) },
    ActionEntry.Palette("Triadic") { c -> PaletteGenerator.triadic(c) },
    ActionEntry.Palette("Shades (100-900)") { c -> PaletteGenerator.shades(c) },
))
