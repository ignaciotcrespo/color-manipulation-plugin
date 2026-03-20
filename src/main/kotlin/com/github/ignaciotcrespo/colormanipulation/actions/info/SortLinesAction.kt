package com.github.ignaciotcrespo.colormanipulation.actions.info

import com.github.ignaciotcrespo.colormanipulation.model.ColorConverter
import com.github.ignaciotcrespo.colormanipulation.util.EditorUtil
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction

class SortLinesAction(private val mode: SortMode) : AnAction("Lines by ${mode.label.removePrefix("Sort by ")}") {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val project = e.project ?: return
        val doc = editor.document

        val startLine = doc.getLineNumber(editor.selectionModel.selectionStart)
        val endLine = doc.getLineNumber(editor.selectionModel.selectionEnd)
        if (startLine == endLine) return

        val lines = (startLine..endLine).map { lineNum ->
            val lineStart = doc.getLineStartOffset(lineNum)
            val lineEnd = doc.getLineEndOffset(lineNum)
            doc.getText(com.intellij.openapi.util.TextRange(lineStart, lineEnd))
        }

        // Separate lines with colors from lines without
        data class IndexedLine(val index: Int, val text: String, val hsl: com.github.ignaciotcrespo.colormanipulation.model.HslColor?)

        val indexedLines = lines.mapIndexed { i, line ->
            val hsl = ColorConverter.findAll(line).firstOrNull()?.color?.toHsl()
            IndexedLine(i, line, hsl)
        }

        val withColor = indexedLines.filter { it.hsl != null }
        val withoutColor = indexedLines.filter { it.hsl == null }

        if (withColor.size < 2) return

        // Bucket hue to nearest 10° so similar shades group together
        fun bucketHue(h: Double) = (Math.round(h / 30.0) * 30.0)

        val sortedColorLines = when (mode) {
            SortMode.HUE -> withColor.sortedWith(compareBy({ bucketHue(it.hsl!!.h) }, { it.hsl!!.l }))
            SortMode.LIGHTNESS -> withColor.sortedWith(compareBy({ it.hsl!!.l }, { bucketHue(it.hsl!!.h) }))
            SortMode.SATURATION -> withColor.sortedWith(compareBy({ it.hsl!!.s }, { bucketHue(it.hsl!!.h) }))
        }

        // Rebuild: color lines get sorted, non-color lines stay at their original positions
        val result = Array(lines.size) { "" }
        val colorSlots = withColor.map { it.index }
        for ((i, slot) in colorSlots.withIndex()) {
            result[slot] = sortedColorLines[i].text
        }
        for (entry in withoutColor) {
            result[entry.index] = entry.text
        }

        val selStart = doc.getLineStartOffset(startLine)
        val selEnd = doc.getLineEndOffset(endLine)
        val newText = result.joinToString("\n")

        WriteCommandAction.runWriteCommandAction(project, "Color Manipulation - Sort Lines", null, {
            doc.replaceString(selStart, selEnd, newText)
        })
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        if (editor == null || !editor.selectionModel.hasSelection()) {
            e.presentation.isEnabled = false
            return
        }
        val doc = editor.document
        val startLine = doc.getLineNumber(editor.selectionModel.selectionStart)
        val endLine = doc.getLineNumber(editor.selectionModel.selectionEnd)
        e.presentation.isEnabled = endLine > startLine
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
