package com.github.ignaciotcrespo.colormanipulation.actions

import com.github.ignaciotcrespo.colormanipulation.model.ColorConverter
import com.github.ignaciotcrespo.colormanipulation.model.UnifiedColor
import com.github.ignaciotcrespo.colormanipulation.ui.PaletteSwatchIcon
import com.github.ignaciotcrespo.colormanipulation.util.EditorUtil
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction

class DynamicPaletteAction(
    text: String,
    private val generateFn: (UnifiedColor) -> List<UnifiedColor>
) : AnAction(text) {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val project = e.project ?: return
        val caretColors = EditorUtil.getSelectedColors(editor)
        if (caretColors.isEmpty()) return

        val cc = caretColors.first()
        val palette = generateFn(cc.color)
        val paletteText = palette.joinToString(", ") { ColorConverter.format(it, cc.format) }

        WriteCommandAction.runWriteCommandAction(project, "Color Manipulation - Palette", null, {
            editor.document.replaceString(
                cc.caret.selectionStart,
                cc.caret.selectionEnd,
                paletteText
            )
        })
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        if (editor == null || !EditorUtil.hasValidColorSelection(editor)) {
            e.presentation.isEnabled = false
            e.presentation.icon = null
            return
        }
        e.presentation.isEnabled = true

        val colors = EditorUtil.getSelectedColors(editor)
        if (colors.isNotEmpty()) {
            val src = colors.first().color
            val palette = generateFn(src)
            e.presentation.icon = PaletteSwatchIcon(palette.map { it.toAwtColor() }, src.toAwtColor())
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
