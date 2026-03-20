package com.github.ignaciotcrespo.colormanipulation.actions.convert

import com.github.ignaciotcrespo.colormanipulation.actions.LastActionTracker
import com.github.ignaciotcrespo.colormanipulation.model.ColorConverter
import com.github.ignaciotcrespo.colormanipulation.model.ColorFormat
import com.github.ignaciotcrespo.colormanipulation.model.UnifiedColor
import com.github.ignaciotcrespo.colormanipulation.ui.ColorCircleIcon
import com.github.ignaciotcrespo.colormanipulation.util.EditorUtil
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

internal abstract class BaseToggleAction(text: String) : AnAction(text) {

    abstract fun transform(color: UnifiedColor, format: ColorFormat): String?

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val project = e.project ?: return
        val fn = { color: UnifiedColor, format: ColorFormat -> transform(color, format) }
        LastActionTracker.record(templatePresentation.text, fn)
        EditorUtil.replaceSelections(editor, project, fn)
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        if (editor == null || !EditorUtil.hasValidColorSelection(editor)) {
            e.presentation.isEnabled = false
            e.presentation.icon = null
            return
        }
        e.presentation.isEnabled = true

        val colors = EditorUtil.getSelectedColors(editor)
        if (colors.isNotEmpty()) {
            e.presentation.icon = ColorCircleIcon(colors.first().color.toAwtColor())
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}

internal class ToggleHashAction : BaseToggleAction("Toggle #") {
    override fun transform(color: UnifiedColor, format: ColorFormat): String? {
        val toggled = when (format) {
            ColorFormat.HEX3 -> ColorFormat.HEX3_NO_HASH
            ColorFormat.HEX6 -> ColorFormat.HEX6_NO_HASH
            ColorFormat.HEX8 -> ColorFormat.HEX8_NO_HASH
            ColorFormat.ARGB8 -> ColorFormat.ARGB8_NO_HASH
            ColorFormat.HEX3_NO_HASH -> ColorFormat.HEX3
            ColorFormat.HEX6_NO_HASH -> ColorFormat.HEX6
            ColorFormat.HEX8_NO_HASH -> ColorFormat.HEX8
            ColorFormat.ARGB8_NO_HASH -> ColorFormat.ARGB8
            else -> return null
        }
        return ColorConverter.format(color, toggled)
    }
}

internal class Toggle0xAction : BaseToggleAction("Toggle 0x") {
    override fun transform(color: UnifiedColor, format: ColorFormat): String? {
        val toggled = when (format) {
            ColorFormat.HEX_0X -> ColorFormat.HEX6_NO_HASH
            ColorFormat.HEX6_NO_HASH -> ColorFormat.HEX_0X
            else -> return null
        }
        return ColorConverter.format(color, toggled)
    }
}

internal class SwapByteOrderAction : BaseToggleAction("Swap RRGGBBAA \u2194 AARRGGBB") {
    override fun transform(color: UnifiedColor, format: ColorFormat): String? {
        val swapped = when (format) {
            ColorFormat.HEX8 -> ColorFormat.ARGB8
            ColorFormat.HEX8_NO_HASH -> ColorFormat.ARGB8_NO_HASH
            ColorFormat.ARGB8 -> ColorFormat.HEX8
            ColorFormat.ARGB8_NO_HASH -> ColorFormat.HEX8_NO_HASH
            else -> return null
        }
        return ColorConverter.format(color, swapped)
    }
}
