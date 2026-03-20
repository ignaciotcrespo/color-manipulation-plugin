package com.github.ignaciotcrespo.colormanipulation.actions

import com.github.ignaciotcrespo.colormanipulation.actions.convert.ConvertFormatGroup
import com.github.ignaciotcrespo.colormanipulation.actions.definitions.*
import com.github.ignaciotcrespo.colormanipulation.actions.info.ShowColorInfoAction
import com.github.ignaciotcrespo.colormanipulation.ui.*
import com.github.ignaciotcrespo.colormanipulation.util.EditorUtil
import com.intellij.openapi.actionSystem.*

class ColorManipulationGroup : DefaultActionGroup() {

    override fun getChildren(e: AnActionEvent?): Array<AnAction> = buildList<AnAction> {
        if (LastActionTracker.lastTransform != null) {
            add(RepeatLastAction())
            add(Separator.getInstance())
        }
        add(ConvertFormatGroup())
        add(DynamicColorGroup(ConvertFromNameMenu, ConvertIcon(), requiresColor = false))
        add(Separator.getInstance())
        add(DynamicParentGroup("Adjustments...", listOf(
            LightenDarkenMenu, SaturateMenu, AlphaMenu, HueRotateMenu, TemperatureMenu
        ), AdjustmentsIcon()))
        add(DynamicParentGroup("Mixing & Palette...", listOf(
            ColorMixingMenu, PaletteMenu
        ), MixingIcon()))
        add(Separator.getInstance())
        add(DynamicColorGroup(AccessibilityMenu, AccessibilityIcon()))
        add(DynamicColorGroup(ColorBlindnessMenu, ColorBlindIcon()))
        add(DynamicColorGroup(ClosestMatchMenu, ClosestMatchIcon()))
        add(Separator.getInstance())
        add(DynamicColorGroup(PracticalMenu, UtilsIcon()))
        add(ShowColorInfoAction())
    }.toTypedArray()

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        if (editor == null || !editor.selectionModel.hasSelection()) {
            e.presentation.isEnabledAndVisible = false
            return
        }
        e.presentation.isEnabledAndVisible = true
        e.presentation.icon = ColorWheelIcon(14)
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
