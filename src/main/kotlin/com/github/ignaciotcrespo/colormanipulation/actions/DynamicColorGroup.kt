package com.github.ignaciotcrespo.colormanipulation.actions

import com.github.ignaciotcrespo.colormanipulation.ui.ColorCircleIcon
import com.github.ignaciotcrespo.colormanipulation.util.EditorUtil
import com.intellij.openapi.actionSystem.*
import javax.swing.Icon

class DynamicColorGroup(
    private val menu: MenuDefinition,
    private val categoryIcon: Icon? = null,
    private val requiresColor: Boolean = true
) : DefaultActionGroup(menu.text, true) {

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        return menu.entries.map { entry ->
            when (entry) {
                is ActionEntry.Transform -> DynamicTransformAction(entry.text, entry.transform)
                is ActionEntry.Palette -> DynamicPaletteAction(entry.text, entry.generate)
                is ActionEntry.CustomDialog -> DynamicCustomAction(entry)
                is ActionEntry.Raw -> entry.action
                is ActionEntry.Sep -> if (entry.label != null) Separator.create(entry.label) else Separator.getInstance()
            }
        }.toTypedArray()
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        val valid = if (requiresColor) {
            editor != null && EditorUtil.hasValidColorSelection(editor)
        } else {
            editor != null && editor.selectionModel.hasSelection()
        }
        if (!valid) {
            e.presentation.isEnabled = false
            e.presentation.icon = null
            return
        }
        e.presentation.isEnabled = true
        if (categoryIcon != null) {
            e.presentation.icon = categoryIcon
        } else if (editor != null) {
            val colors = EditorUtil.getSelectedColors(editor)
            if (colors.isNotEmpty()) {
                e.presentation.icon = ColorCircleIcon(colors.first().color.toAwtColor())
            }
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
