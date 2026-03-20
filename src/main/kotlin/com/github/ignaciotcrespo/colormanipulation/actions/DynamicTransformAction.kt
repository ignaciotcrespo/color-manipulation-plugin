package com.github.ignaciotcrespo.colormanipulation.actions

import com.github.ignaciotcrespo.colormanipulation.model.ColorConverter
import com.github.ignaciotcrespo.colormanipulation.model.ColorFormat
import com.github.ignaciotcrespo.colormanipulation.model.UnifiedColor
import com.github.ignaciotcrespo.colormanipulation.ui.ColorCircleIcon
import com.github.ignaciotcrespo.colormanipulation.util.EditorUtil
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

class DynamicTransformAction(
    text: String,
    private val transformFn: (UnifiedColor, ColorFormat) -> UnifiedColor?
) : AnAction(text) {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val project = e.project ?: return
        val transform: (UnifiedColor, ColorFormat) -> String? = { color, format ->
            val result = transformFn(color, format)
            if (result != null) {
                val outputFormat = if (result.a < 1.0 && !format.supportsAlpha) {
                    format.alphaVariant()
                } else {
                    format
                }
                ColorConverter.format(result, outputFormat)
            } else null
        }
        LastActionTracker.record(templatePresentation.text, transform)
        EditorUtil.replaceSelections(editor, project, transform)
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
            val cc = colors.first()
            val preview = transformFn(cc.color, cc.format)
            if (preview != null) {
                e.presentation.icon = ColorCircleIcon(preview.toAwtColor())
            }
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
