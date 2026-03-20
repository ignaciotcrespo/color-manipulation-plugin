package com.github.ignaciotcrespo.colormanipulation.actions.info

import com.github.ignaciotcrespo.colormanipulation.actions.ActionUtils
import com.github.ignaciotcrespo.colormanipulation.actions.LastActionTracker
import com.github.ignaciotcrespo.colormanipulation.model.*
import com.github.ignaciotcrespo.colormanipulation.util.EditorUtil
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

enum class MatchSystem(val label: String) {
    CSS("CSS Color"),
    TAILWIND("Tailwind"),
    BOOTSTRAP("Bootstrap"),
    MATERIAL("Material Design"),
    IOS("iOS System"),
}

class ClosestMatchAction(
    private val system: MatchSystem,
    private val useName: Boolean
) : AnAction(if (useName) "${system.label} → Name" else "${system.label} → Value") {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val project = e.project ?: return
        val label = templatePresentation.text
        val transform = { color: UnifiedColor, format: ColorFormat ->
            val (name, matchColor) = when (system) {
                MatchSystem.CSS -> NamedCssColors.findClosest(color)
                MatchSystem.TAILWIND -> TailwindColors.findClosest(color)
                MatchSystem.BOOTSTRAP -> BootstrapColors.findClosest(color)
                MatchSystem.MATERIAL -> MaterialColors.findClosest(color)
                MatchSystem.IOS -> IOSSystemColors.findClosest(color)
            }
            if (useName) name else ColorConverter.format(matchColor, format)
        }
        LastActionTracker.record(label, transform)
        EditorUtil.replaceSelections(editor, project, transform)
    }

    override fun update(e: AnActionEvent) = ActionUtils.updateWithCurrentColorIcon(e)

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
