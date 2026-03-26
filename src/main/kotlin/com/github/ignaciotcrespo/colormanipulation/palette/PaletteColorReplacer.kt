package com.github.ignaciotcrespo.colormanipulation.palette

import com.github.ignaciotcrespo.colormanipulation.model.ColorConverter
import com.github.ignaciotcrespo.colormanipulation.model.ColorFormat
import com.github.ignaciotcrespo.colormanipulation.model.UnifiedColor
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project

/**
 * Replaces color occurrences found by the palette scanner directly in their source documents.
 * Works independently of editor selections — uses stored file/line/column positions.
 */
object PaletteColorReplacer {

    /**
     * Apply a color transform to a list of [ProjectColor] occurrences.
     *
     * @param project the current project
     * @param occurrences the colors to transform (from tree selection)
     * @param transform function that takes (color, format) and returns the replacement text, or null to skip
     * @return the number of replacements made
     */
    fun replaceAll(
        project: Project,
        occurrences: List<ProjectColor>,
        transform: (UnifiedColor, ColorFormat) -> String?
    ): Int {
        if (occurrences.isEmpty()) return 0

        // Group by file so we can batch replacements per document
        val byFile = occurrences.groupBy { it.file }
        var totalReplaced = 0

        WriteCommandAction.runWriteCommandAction(project, "Color Manipulation - Palette", null, {
            for ((vFile, occs) in byFile) {
                val document = FileDocumentManager.getInstance().getDocument(vFile) ?: continue
                val text = document.text

                // Build a list of (startOffset, endOffset, newText) sorted descending by offset
                // so replacements don't shift positions of earlier ones
                data class Replacement(val start: Int, val end: Int, val newText: String)

                val replacements = mutableListOf<Replacement>()
                for (occ in occs) {
                    // Convert line/column to offset (line is 1-based)
                    if (occ.line < 1 || occ.line > document.lineCount) continue
                    val lineStartOffset = document.getLineStartOffset(occ.line - 1)
                    val startOffset = lineStartOffset + occ.column - 1

                    // Verify the text at this position still matches what we scanned
                    val matchLen = occ.matchText.length
                    val endOffset = startOffset + matchLen
                    if (endOffset > text.length) continue
                    val currentText = text.substring(startOffset, endOffset)
                    if (currentText != occ.matchText) continue

                    val newText = transform(occ.color, occ.format) ?: continue
                    if (newText == currentText) continue // no change
                    replacements.add(Replacement(startOffset, endOffset, newText))
                }

                // Apply in reverse order to preserve offsets
                for (r in replacements.sortedByDescending { it.start }) {
                    document.replaceString(r.start, r.end, r.newText)
                    totalReplaced++
                }
            }
        })

        return totalReplaced
    }
}
