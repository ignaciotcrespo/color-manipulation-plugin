package com.github.ignaciotcrespo.colormanipulation.actions

import com.github.ignaciotcrespo.colormanipulation.model.ColorConverter
import com.github.ignaciotcrespo.colormanipulation.model.ColorFormat
import com.github.ignaciotcrespo.colormanipulation.model.UnifiedColor
import com.github.ignaciotcrespo.colormanipulation.ui.CustomColorDialog
import com.github.ignaciotcrespo.colormanipulation.util.EditorUtil
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

/**
 * Editor action that opens a dialog to replace the selected color(s) with a
 * user-specified color value. Each selection keeps its original format.
 */
class ReplaceWithColorAction : AnAction("Replace with Color...") {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val project = e.project ?: return

        // Use the first selected color as the pre-fill hint
        val currentColors = EditorUtil.getSelectedColors(editor)
        val previewColor = currentColors.firstOrNull()?.color

        val dialog = CustomColorDialog(project, previewColor)
        if (!dialog.showAndGet()) return
        val newColor = dialog.resultColor ?: return

        val transform = { _: UnifiedColor, format: ColorFormat ->
            ColorConverter.format(newColor, format)
        }
        val hexLabel = ColorConverter.format(newColor, ColorFormat.HEX6)
        LastActionTracker.record("Replace with $hexLabel", transform)
        EditorUtil.replaceSelections(editor, project, transform)
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabled = editor != null && EditorUtil.hasValidColorSelection(editor)
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
