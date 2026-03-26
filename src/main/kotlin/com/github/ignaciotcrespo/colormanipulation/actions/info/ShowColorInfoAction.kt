package com.github.ignaciotcrespo.colormanipulation.actions.info

import com.github.ignaciotcrespo.colormanipulation.util.EditorUtil
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.popup.JBPopupFactory

class ShowColorInfoAction : AnAction("Show Color Info") {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val caretColors = EditorUtil.getSelectedColors(editor)
        if (caretColors.isEmpty()) return

        val color = caretColors.first().color
        val panel = ColorInfoPanelBuilder.buildPanel(color)

        val popup = JBPopupFactory.getInstance()
            .createComponentPopupBuilder(panel, null)
            .setTitle("Color Info")
            .setMovable(true)
            .setResizable(true)
            .setRequestFocus(true)
            .createPopup()

        popup.showInBestPositionFor(editor)
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        val hasColor = editor != null && EditorUtil.getSelectedColors(editor).isNotEmpty()
        e.presentation.isEnabled = hasColor
        e.presentation.icon = if (hasColor) com.github.ignaciotcrespo.colormanipulation.ui.InfoIcon() else null
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
