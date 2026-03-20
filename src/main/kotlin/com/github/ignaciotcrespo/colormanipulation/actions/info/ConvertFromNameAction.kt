package com.github.ignaciotcrespo.colormanipulation.actions.info

import com.github.ignaciotcrespo.colormanipulation.actions.LastActionTracker
import com.github.ignaciotcrespo.colormanipulation.model.*
import com.github.ignaciotcrespo.colormanipulation.util.EditorUtil
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction

enum class NameSystem(val label: String) {
    CSS("From CSS Name"),
    TAILWIND("From Tailwind Name"),
    BOOTSTRAP("From Bootstrap Name"),
    MATERIAL("From Material Name"),
    IOS("From iOS Name"),
}

class ConvertFromNameAction(private val system: NameSystem) : AnAction(system.label) {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val project = e.project ?: return

        LastActionTracker.record(system.label) { color, format ->
            ColorConverter.format(color, format)
        }

        WriteCommandAction.runWriteCommandAction(project, "Color Manipulation - Convert from Name", null, {
            editor.caretModel.allCarets.sortedByDescending { it.selectionStart }.forEach { caret ->
                val text = caret.selectedText ?: return@forEach
                // Try whole selection as a single name first
                val singleMatch = resolve(text.trim())
                if (singleMatch != null) {
                    val hex = ColorConverter.format(singleMatch, ColorFormat.HEX6)
                    editor.document.replaceString(caret.selectionStart, caret.selectionEnd, hex)
                    return@forEach
                }
                // Scan for known names within the text, replace in reverse order
                val matches = findNamesInText(text)
                if (matches.isEmpty()) return@forEach
                val sb = StringBuilder(text)
                for ((start, end, hex) in matches.sortedByDescending { it.first }) {
                    sb.replace(start, end, hex)
                }
                editor.document.replaceString(caret.selectionStart, caret.selectionEnd, sb.toString())
            }
        })
    }

    private fun resolve(name: String): UnifiedColor? {
        return when (system) {
            NameSystem.CSS -> NamedCssColors.getColor(name)
            NameSystem.TAILWIND -> TailwindColors.getByName(name)
            NameSystem.BOOTSTRAP -> BootstrapColors.getByName(name)
            NameSystem.MATERIAL -> MaterialColors.getByName(name)
            NameSystem.IOS -> IOSSystemColors.getByName(name)
        }
    }

    private fun findNamesInText(text: String): List<Triple<Int, Int, String>> {
        val names = when (system) {
            NameSystem.CSS -> NamedCssColors.allNames()
            NameSystem.TAILWIND -> TailwindColors.allNames()
            NameSystem.BOOTSTRAP -> BootstrapColors.allNames()
            NameSystem.MATERIAL -> MaterialColors.allNames()
            NameSystem.IOS -> IOSSystemColors.allNames()
        }
        // Sort by length descending to match longer names first (e.g. "light-blue-500" before "blue-500")
        val results = mutableListOf<Triple<Int, Int, String>>()
        val used = BooleanArray(text.length)
        for (name in names.sortedByDescending { it.length }) {
            var idx = 0
            while (true) {
                val found = text.indexOf(name, idx, ignoreCase = true)
                if (found == -1) break
                val end = found + name.length
                // Check word boundaries and not already matched
                val before = found == 0 || !text[found - 1].isLetterOrDigit()
                val after = end >= text.length || !text[end].isLetterOrDigit()
                val overlaps = (found until end).any { used[it] }
                if (before && after && !overlaps) {
                    val color = resolve(name) ?: break
                    val hex = ColorConverter.format(color, ColorFormat.HEX6)
                    results.add(Triple(found, end, hex))
                    for (i in found until end) used[i] = true
                }
                idx = end
            }
        }
        return results
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        if (editor == null || !editor.selectionModel.hasSelection()) {
            e.presentation.isEnabled = false
            return
        }
        e.presentation.isEnabled = true
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
