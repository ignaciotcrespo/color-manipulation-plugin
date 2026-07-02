package com.github.ignaciotcrespo.colormanipulation.palette

import com.github.ignaciotcrespo.colormanipulation.ui.ColorCircleIcon
import com.github.ignaciotcrespo.colormanipulation.util.EditorUtil
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.wm.ToolWindowManager

/**
 * Editor right-click action: opens the Color Palette tool window and filters every section
 * of the tree (Frequency, Similar Colors, Format Inconsistencies) by the selected color,
 * matching by RGB regardless of the source format.
 */
class InspectInPaletteAction : AnAction("Inspect in Color Palette") {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val color = EditorUtil.getSelectedColors(editor).firstOrNull()?.color ?: return

        val toolWindow = ToolWindowManager.getInstance(project)
            .getToolWindow(ColorPaletteToolWindowFactory.TOOL_WINDOW_ID) ?: return

        toolWindow.show {
            val panel = project.getUserData(ColorPaletteToolWindowFactory.PALETTE_PANEL_KEY)
            panel?.inspectColor(color)
        }
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        val valid = e.project != null && editor != null && EditorUtil.hasValidColorSelection(editor)
        e.presentation.isEnabledAndVisible = valid
        if (valid) {
            val color = EditorUtil.getSelectedColors(editor!!).firstOrNull()?.color
            if (color != null) {
                e.presentation.icon = ColorCircleIcon(color.toAwtColor())
            }
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
