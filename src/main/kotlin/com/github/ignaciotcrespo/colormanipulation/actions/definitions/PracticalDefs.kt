package com.github.ignaciotcrespo.colormanipulation.actions.definitions

import com.github.ignaciotcrespo.colormanipulation.actions.ActionEntry
import com.github.ignaciotcrespo.colormanipulation.actions.MenuDefinition
import com.github.ignaciotcrespo.colormanipulation.actions.info.NormalizeFormatAction
import com.github.ignaciotcrespo.colormanipulation.actions.info.SortColorsAction
import com.github.ignaciotcrespo.colormanipulation.actions.info.SortLinesAction
import com.github.ignaciotcrespo.colormanipulation.actions.info.SortMode
import com.github.ignaciotcrespo.colormanipulation.model.ColorTransforms

val PracticalMenu = MenuDefinition("Practical Utils...", listOf(
    ActionEntry.Transform("Random Color") { _, _ -> ColorTransforms.randomColor() },
    ActionEntry.Sep("Sort Lines (by first color)"),
    ActionEntry.Raw(SortLinesAction(SortMode.HUE)),
    ActionEntry.Raw(SortLinesAction(SortMode.LIGHTNESS)),
    ActionEntry.Raw(SortLinesAction(SortMode.SATURATION)),
    ActionEntry.Sep("Sort Colors (swap values)"),
    ActionEntry.Raw(SortColorsAction(SortMode.HUE)),
    ActionEntry.Raw(SortColorsAction(SortMode.LIGHTNESS)),
    ActionEntry.Raw(SortColorsAction(SortMode.SATURATION)),
    ActionEntry.Sep(),
    ActionEntry.Raw(NormalizeFormatAction()),
))
