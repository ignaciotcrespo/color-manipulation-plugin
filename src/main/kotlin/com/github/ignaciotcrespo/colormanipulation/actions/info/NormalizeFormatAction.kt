package com.github.ignaciotcrespo.colormanipulation.actions.info

import com.github.ignaciotcrespo.colormanipulation.model.ColorConverter
import com.github.ignaciotcrespo.colormanipulation.util.EditorUtil
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction

class NormalizeFormatAction : AnAction("Normalize to Same Format") {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val project = e.project ?: return
        val allColors = EditorUtil.findAllColors(editor)
        if (allColors.size < 2) return

        val targetFormat = allColors.first().format
        val sorted = allColors.sortedByDescending { it.absoluteStart }

        WriteCommandAction.runWriteCommandAction(project, "Color Manipulation - Normalize", null, {
            for (ec in sorted) {
                val formatted = ColorConverter.format(ec.color, targetFormat)
                editor.document.replaceString(ec.absoluteStart, ec.absoluteEnd, formatted)
            }
        })
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        if (editor == null || !EditorUtil.hasValidColorSelection(editor)) {
            e.presentation.isEnabled = false
            return
        }
        e.presentation.isEnabled = EditorUtil.findAllColors(editor).size >= 2
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
