package com.github.ignaciotcrespo.colormanipulation.actions

import com.github.ignaciotcrespo.colormanipulation.ui.CustomPercentageDialog
import com.github.ignaciotcrespo.colormanipulation.util.EditorUtil
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

class DynamicCustomAction(
    private val entry: ActionEntry.CustomDialog
) : AnAction(entry.text) {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val project = e.project ?: return

        val dialog = CustomPercentageDialog(
            project, entry.dialogTitle, entry.dialogLabel,
            entry.min, entry.max, entry.default
        )
        if (!dialog.showAndGet()) return

        val value = dialog.result
        val transform = { color: com.github.ignaciotcrespo.colormanipulation.model.UnifiedColor, format: com.github.ignaciotcrespo.colormanipulation.model.ColorFormat ->
            entry.transform(color, format, value)
        }
        LastActionTracker.record("${entry.dialogTitle} ($value)", transform)
        EditorUtil.replaceSelections(editor, project, transform)
    }

    override fun update(e: AnActionEvent) = ActionUtils.updateWithCurrentColorIcon(e)

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
