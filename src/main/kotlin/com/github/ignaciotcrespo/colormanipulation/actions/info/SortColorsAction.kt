package com.github.ignaciotcrespo.colormanipulation.actions.info

import com.github.ignaciotcrespo.colormanipulation.model.ColorConverter
import com.github.ignaciotcrespo.colormanipulation.util.EditorUtil
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction

enum class SortMode(val label: String) {
    HUE("Sort by Hue"),
    LIGHTNESS("Sort by Lightness"),
    SATURATION("Sort by Saturation"),
}

class SortColorsAction(private val mode: SortMode) : AnAction(mode.label) {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val project = e.project ?: return
        val allColors = EditorUtil.findAllColors(editor)
        if (allColors.size < 2) return

        fun bucketHue(h: Double) = (Math.round(h / 30.0) * 30.0)

        val sorted = when (mode) {
            SortMode.HUE -> allColors.sortedWith(compareBy({ bucketHue(it.color.toHsl().h) }, { it.color.toHsl().l }))
            SortMode.LIGHTNESS -> allColors.sortedWith(compareBy({ it.color.toHsl().l }, { bucketHue(it.color.toHsl().h) }))
            SortMode.SATURATION -> allColors.sortedWith(compareBy({ it.color.toHsl().s }, { bucketHue(it.color.toHsl().h) }))
        }

        val formattedSorted = sorted.map { ColorConverter.format(it.color, it.format) }
        val originalPositions = allColors.sortedByDescending { it.absoluteStart }

        WriteCommandAction.runWriteCommandAction(project, "Color Manipulation - Sort", null, {
            for ((i, ec) in originalPositions.withIndex()) {
                val sortedIndex = originalPositions.size - 1 - i
                editor.document.replaceString(ec.absoluteStart, ec.absoluteEnd, formattedSorted[sortedIndex])
            }
        })
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        if (editor == null || !EditorUtil.hasValidColorSelection(editor)) {
            e.presentation.isEnabled = false
            return
        }
        e.presentation.isEnabled = EditorUtil.findAllColors(editor).size >= 2
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
