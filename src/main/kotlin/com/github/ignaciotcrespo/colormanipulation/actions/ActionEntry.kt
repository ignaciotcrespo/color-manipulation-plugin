package com.github.ignaciotcrespo.colormanipulation.actions

import com.github.ignaciotcrespo.colormanipulation.model.ColorFormat
import com.github.ignaciotcrespo.colormanipulation.model.UnifiedColor
import com.intellij.openapi.actionSystem.AnAction

sealed interface ActionEntry {
    data class Raw(val action: AnAction) : ActionEntry
    data class Transform(
        val text: String,
        val transform: (UnifiedColor, ColorFormat) -> UnifiedColor?
    ) : ActionEntry

    data class Palette(
        val text: String,
        val generate: (UnifiedColor) -> List<UnifiedColor>
    ) : ActionEntry

    data class CustomDialog(
        val text: String,
        val dialogTitle: String,
        val dialogLabel: String,
        val min: Double,
        val max: Double,
        val default: Double,
        val transform: (UnifiedColor, ColorFormat, Double) -> String?
    ) : ActionEntry

    data class Sep(val label: String? = null) : ActionEntry
}

data class MenuDefinition(val text: String, val entries: List<ActionEntry>)
