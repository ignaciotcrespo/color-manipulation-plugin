package com.github.ignaciotcrespo.colormanipulation.palette

import com.github.ignaciotcrespo.colormanipulation.ui.ColorWheelIcon
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.wm.ToolWindowManager

/**
 * Action accessible from the right-click editor menu under "Color Manipulation".
 * Opens the Color Palette tool window and triggers a project scan.
 */
class AnalyzeProjectAction : AnAction("Analyze Project Colors") {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val toolWindow = ToolWindowManager.getInstance(project)
            .getToolWindow(ColorPaletteToolWindowFactory.TOOL_WINDOW_ID)

        if (toolWindow != null) {
            toolWindow.show {
                // Get or wait for the panel to be created
                val panel = project.getUserData(ColorPaletteToolWindowFactory.PALETTE_PANEL_KEY)
                panel?.runScan()
            }
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
        e.presentation.icon = ColorWheelIcon(14)
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
