package com.github.ignaciotcrespo.colormanipulation.palette

import com.github.ignaciotcrespo.colormanipulation.model.ColorConverter
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor

/**
 * Scans all project source files for color occurrences.
 */
object ProjectColorScanner {

    private val TEXT_EXTENSIONS = setOf(
        // Web
        "css", "scss", "sass", "less", "styl",
        "html", "htm", "svg", "vue", "svelte",
        "js", "jsx", "ts", "tsx", "mjs", "cjs",
        // JVM
        "kt", "kts", "java", "groovy", "scala",
        // Mobile
        "swift", "m", "mm",
        // Config / Data
        "xml", "json", "yaml", "yml", "toml", "properties",
        // Other
        "py", "rb", "php", "go", "rs", "dart", "c", "cpp", "h", "hpp",
    )

    private val EXCLUDED_DIRS = setOf(
        "build", "out", "node_modules", "dist", ".gradle", ".idea",
        "__pycache__", "target", "Pods", "DerivedData", ".build",
        "vendor", ".dart_tool", ".pub-cache", "gen", "generated",
        "test", "tests", "__tests__", "__test__", "spec", "specs",
        "androidTest", "testFixtures",
    )

    /** Filename patterns that indicate test files. */
    private val TEST_FILE_REGEX = Regex(
        """(?i)(^test[_.]|[_.]test\.|[_.]spec\.|\.spec\.|Tests?\.|_test\.|_spec\.""" +
        """|Test\.kt$|Test\.java$|Tests\.kt$|Tests\.java$|Spec\.kt$|Spec\.java$""" +
        """|\.test\.(js|jsx|ts|tsx|mjs|cjs)$|\.spec\.(js|jsx|ts|tsx|mjs|cjs)$""" +
        """|_test\.(go|py|rb|dart|rs)$|test_[^/]*\.py$)"""
    )

    private const val MAX_FILE_SIZE = 512 * 1024 // 512 KB

    /**
     * @param extensionFilter if non-empty, only scan files with these extensions.
     *                        Empty set means use the default TEXT_EXTENSIONS.
     */
    fun scan(project: Project, indicator: ProgressIndicator, extensionFilter: Set<String> = emptySet()): List<ProjectColor> {
        val results = mutableListOf<ProjectColor>()
        val contentRoots = ProjectRootManager.getInstance(project).contentRoots
        val allowedExtensions = if (extensionFilter.isEmpty()) TEXT_EXTENSIONS else extensionFilter

        // Collect files first
        val files = mutableListOf<VirtualFile>()
        for (root in contentRoots) {
            VfsUtilCore.visitChildrenRecursively(root, object : VirtualFileVisitor<Unit>() {
                override fun visitFile(file: VirtualFile): Boolean {
                    if (indicator.isCanceled) return false
                    if (file.isDirectory) {
                        // Skip build output, hidden dirs, node_modules, etc.
                        val name = file.name
                        if (name.startsWith(".") || name in EXCLUDED_DIRS) {
                            return false
                        }
                        return true
                    }
                    val ext = file.extension?.lowercase()
                    if (ext != null && ext in allowedExtensions
                        && file.length < MAX_FILE_SIZE
                        && !isTestFile(file)
                    ) {
                        files.add(file)
                    }
                    return true
                }
            })
        }

        indicator.isIndeterminate = false
        for ((index, file) in files.withIndex()) {
            if (indicator.isCanceled) break
            indicator.fraction = index.toDouble() / files.size
            indicator.text2 = file.name

            try {
                // Prefer in-memory Document (may have unsaved changes) over disk content
                val document = FileDocumentManager.getInstance().getCachedDocument(file)
                val text = document?.text ?: String(file.contentsToByteArray(), Charsets.UTF_8)
                val matches = ColorConverter.findAll(text)
                for (match in matches) {
                    val lineNumber = text.substring(0, match.range.first).count { it == '\n' } + 1
                    val lastNewline = text.lastIndexOf('\n', match.range.first - 1)
                    val column = match.range.first - lastNewline
                    results.add(
                        ProjectColor(
                            color = match.color,
                            format = match.format,
                            file = file,
                            line = lineNumber,
                            column = column,
                            matchText = match.text
                        )
                    )
                }
            } catch (_: Exception) {
                // Skip unreadable files
            }
        }

        return results
    }

    private fun isTestFile(file: VirtualFile): Boolean {
        return TEST_FILE_REGEX.containsMatchIn(file.name)
    }
}
