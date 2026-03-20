package com.github.ignaciotcrespo.colormanipulation.actions

import com.github.ignaciotcrespo.colormanipulation.util.EditorUtil
import com.intellij.openapi.actionSystem.*
import javax.swing.Icon

class DynamicParentGroup(
    text: String,
    private val menus: List<MenuDefinition>,
    private val categoryIcon: Icon? = null
) : DefaultActionGroup(text, true) {

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        return menus.map { DynamicColorGroup(it) }.toTypedArray()
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        if (editor == null || !EditorUtil.hasValidColorSelection(editor)) {
            e.presentation.isEnabled = false
            e.presentation.icon = null
            return
        }
        e.presentation.isEnabled = true
        e.presentation.icon = categoryIcon
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
