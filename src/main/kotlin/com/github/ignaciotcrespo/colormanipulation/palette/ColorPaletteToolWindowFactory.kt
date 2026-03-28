package com.github.ignaciotcrespo.colormanipulation.palette

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

/**
 * Factory that creates the "Color Palette" tool window.
 * Registered in plugin.xml as an extension.
 */
class ColorPaletteToolWindowFactory : ToolWindowFactory, DumbAware {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = ColorPalettePanel(project)
        val content = ContentFactory.getInstance().createContent(panel, "", false)
        toolWindow.contentManager.addContent(content)

        // Store reference for access from the action
        project.putUserData(PALETTE_PANEL_KEY, panel)
    }

    companion object {
        val PALETTE_PANEL_KEY = com.intellij.openapi.util.Key.create<ColorPalettePanel>("ColorPalettePanel")
        const val TOOL_WINDOW_ID = "Color Palette"
    }
}
