package com.github.ignaciotcrespo.colormanipulation.actions

import com.github.ignaciotcrespo.colormanipulation.util.EditorUtil
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

class RepeatLastAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val project = e.project ?: return
        val transform = LastActionTracker.lastTransform ?: return
        EditorUtil.replaceSelections(editor, project, transform)
    }

    override fun update(e: AnActionEvent) {
        val label = LastActionTracker.lastLabel
        if (label == null || LastActionTracker.lastTransform == null) {
            e.presentation.isEnabledAndVisible = false
            return
        }
        e.presentation.text = "Repeat - $label"

        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabledAndVisible = editor != null && EditorUtil.hasValidColorSelection(editor)
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
