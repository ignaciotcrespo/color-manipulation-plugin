package com.github.ignaciotcrespo.colormanipulation.util

import com.github.ignaciotcrespo.colormanipulation.model.ColorConverter
import com.github.ignaciotcrespo.colormanipulation.model.ColorFormat
import com.github.ignaciotcrespo.colormanipulation.model.UnifiedColor
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project

object EditorUtil {

    data class CaretColor(
        val caret: Caret,
        val color: UnifiedColor,
        val format: ColorFormat,
        val selectedText: String
    )

    fun getSelectedColors(editor: Editor): List<CaretColor> {
        return editor.caretModel.allCarets.mapNotNull { caret ->
            val text = caret.selectedText ?: return@mapNotNull null
            val (color, format) = ColorConverter.parse(text) ?: return@mapNotNull null
            CaretColor(caret, color, format, text)
        }
    }

    data class EmbeddedColor(
        val color: UnifiedColor,
        val format: ColorFormat,
        val absoluteStart: Int,
        val absoluteEnd: Int
    )

    fun findAllColors(editor: Editor): List<EmbeddedColor> {
        val caretColors = getSelectedColors(editor)
        if (caretColors.isNotEmpty()) {
            return caretColors.map {
                EmbeddedColor(it.color, it.format, it.caret.selectionStart, it.caret.selectionEnd)
            }
        }
        // Scan for embedded colors within each selection
        val result = mutableListOf<EmbeddedColor>()
        for (caret in editor.caretModel.allCarets) {
            val text = caret.selectedText ?: continue
            val offset = caret.selectionStart
            for (match in ColorConverter.findAll(text)) {
                result.add(EmbeddedColor(match.color, match.format, offset + match.range.first, offset + match.range.last + 1))
            }
        }
        return result
    }

    fun hasValidColorSelection(editor: Editor): Boolean {
        if (!editor.selectionModel.hasSelection()) return false
        return editor.caretModel.allCarets.any { caret ->
            val text = caret.selectedText ?: return@any false
            // Either the whole selection is a color, or it contains embedded colors
            ColorConverter.parse(text) != null || ColorConverter.findAll(text).isNotEmpty()
        }
    }

    fun replaceSelections(
        editor: Editor,
        project: Project,
        transform: (UnifiedColor, ColorFormat) -> String?
    ) {
        // First try: each caret selection is a single color
        val caretColors = getSelectedColors(editor)
        if (caretColors.isNotEmpty()) {
            WriteCommandAction.runWriteCommandAction(project, "Color Manipulation", null, {
                caretColors.sortedByDescending { it.caret.selectionStart }.forEach { cc ->
                    val newText = transform(cc.color, cc.format) ?: return@forEach
                    editor.document.replaceString(
                        cc.caret.selectionStart,
                        cc.caret.selectionEnd,
                        newText
                    )
                }
            })
            return
        }

        // Fallback: scan for embedded colors within each selection
        WriteCommandAction.runWriteCommandAction(project, "Color Manipulation", null, {
            editor.caretModel.allCarets.sortedByDescending { it.selectionStart }.forEach { caret ->
                val text = caret.selectedText ?: return@forEach
                val matches = ColorConverter.findAll(text)
                if (matches.isEmpty()) return@forEach

                // Build new text by replacing each color match in reverse order
                val sb = StringBuilder(text)
                for (match in matches.reversed()) {
                    val newColor = transform(match.color, match.format) ?: continue
                    sb.replace(match.range.first, match.range.last + 1, newColor)
                }

                editor.document.replaceString(
                    caret.selectionStart,
                    caret.selectionEnd,
                    sb.toString()
                )
            }
        })
    }
}
