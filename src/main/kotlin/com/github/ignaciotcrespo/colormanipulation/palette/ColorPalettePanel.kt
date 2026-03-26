package com.github.ignaciotcrespo.colormanipulation.palette

import com.github.ignaciotcrespo.colormanipulation.model.ColorConverter
import com.github.ignaciotcrespo.colormanipulation.model.ColorFormat
import com.github.ignaciotcrespo.colormanipulation.model.ColorTransforms
import com.github.ignaciotcrespo.colormanipulation.model.NamedCssColors
import com.github.ignaciotcrespo.colormanipulation.model.TailwindColors
import com.github.ignaciotcrespo.colormanipulation.model.UnifiedColor
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.ui.ColoredTreeCellRenderer
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.ui.JBUI
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.geom.Ellipse2D
import javax.swing.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath

/**
 * The main panel displayed in the Color Palette tool window.
 * Shows a summary header and a tree with analysis results.
 */
class ColorPalettePanel(private val project: Project) : JPanel(BorderLayout()) {

    private val rootNode = DefaultMutableTreeNode("Project Colors")
    private val treeModel = DefaultTreeModel(rootNode)
    private val tree = Tree(treeModel)
    private val summaryLabel = JBLabel("Click 'Analyze Project' or right-click \u2192 Color Manipulation \u2192 Analyze Project Colors")
    private val filterField = JBTextField(18).apply {
        emptyText.setText("All files  (e.g. *.ts, *.tsx, *.css)")
    }
    private val sortCombo = JComboBox(arrayOf("Frequency", "Hue")).apply {
        selectedIndex = 0
    }
    private val displayFormats = arrayOf(
        DisplayFormat("#RRGGBB", ColorFormat.HEX6, ColorFormat.HEX8),
        DisplayFormat("rgb()", ColorFormat.RGB_FUNC, ColorFormat.RGBA_FUNC),
        DisplayFormat("hsl()", ColorFormat.HSL_FUNC, ColorFormat.HSLA_FUNC),
        DisplayFormat("0xRRGGBB", ColorFormat.HEX_0X, ColorFormat.COMPOSE_COLOR),
        DisplayFormat("Color()", ColorFormat.COMPOSE_COLOR, ColorFormat.COMPOSE_COLOR),
        DisplayFormat("UIColor()", ColorFormat.UICOLOR, ColorFormat.UICOLOR),
    )
    private val formatCombo = JComboBox(displayFormats.map { it.label }.toTypedArray()).apply {
        selectedIndex = 0
    }
    private val scanButton = JButton("Analyze Project")

    private var lastAnalysis: ProjectColorAnalysis? = null

    private val optionsPanel = JPanel(GridBagLayout())
    private var optionsVisible = false

    init {
        val headerPanel = JPanel(GridBagLayout())
        headerPanel.border = JBUI.Borders.empty(8, 8, 4, 8)
        val gbc = GridBagConstraints().apply {
            gridx = 0
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
            insets = Insets(0, 0, JBUI.scale(4), 0)
        }

        // Row 1: Analyze button + toggle options (always visible)
        val topRow = JPanel(BorderLayout(JBUI.scale(6), 0))
        val toggleLink = JBLabel("<html><a href=''>Options \u25BC</a></html>")
        toggleLink.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        toggleLink.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                optionsVisible = !optionsVisible
                optionsPanel.isVisible = optionsVisible
                toggleLink.text = if (optionsVisible) "<html><a href=''>Options \u25B2</a></html>"
                    else "<html><a href=''>Options \u25BC</a></html>"
                revalidate()
            }
        })
        topRow.add(toggleLink, BorderLayout.CENTER)
        topRow.add(scanButton, BorderLayout.EAST)
        gbc.gridy = 0
        headerPanel.add(topRow, gbc)

        // Row 2: collapsible options panel
        val ogbc = GridBagConstraints().apply {
            gridy = 0
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
            insets = Insets(JBUI.scale(2), 0, JBUI.scale(2), 0)
        }

        // Option: Files
        ogbc.gridx = 0; ogbc.weightx = 0.0
        optionsPanel.add(JBLabel("Files:"), ogbc)
        ogbc.gridx = 1; ogbc.weightx = 1.0; ogbc.insets = Insets(JBUI.scale(2), JBUI.scale(4), JBUI.scale(2), 0)
        optionsPanel.add(filterField, ogbc)

        // Option: Sort
        ogbc.gridy = 1; ogbc.gridx = 0; ogbc.weightx = 0.0; ogbc.insets = Insets(JBUI.scale(2), 0, JBUI.scale(2), 0)
        optionsPanel.add(JBLabel("Sort:"), ogbc)
        ogbc.gridx = 1; ogbc.weightx = 1.0; ogbc.insets = Insets(JBUI.scale(2), JBUI.scale(4), JBUI.scale(2), 0)
        optionsPanel.add(sortCombo, ogbc)

        // Option: Format
        ogbc.gridy = 2; ogbc.gridx = 0; ogbc.weightx = 0.0; ogbc.insets = Insets(JBUI.scale(2), 0, JBUI.scale(2), 0)
        optionsPanel.add(JBLabel("Format:"), ogbc)
        ogbc.gridx = 1; ogbc.weightx = 1.0; ogbc.insets = Insets(JBUI.scale(2), JBUI.scale(4), JBUI.scale(2), 0)
        optionsPanel.add(formatCombo, ogbc)

        optionsPanel.isVisible = false
        gbc.gridy = 1
        headerPanel.add(optionsPanel, gbc)

        // Row 3: summary
        gbc.gridy = 2
        gbc.insets = Insets(0, 0, 0, 0)
        headerPanel.add(summaryLabel, gbc)

        add(headerPanel, BorderLayout.NORTH)

        scanButton.addActionListener { runScan() }
        sortCombo.addActionListener {
            lastAnalysis?.let { rebuildTree(it) }
        }
        formatCombo.addActionListener {
            lastAnalysis?.let { rebuildTree(it) }
        }

        // Tree
        tree.isRootVisible = false
        tree.showsRootHandles = true
        tree.cellRenderer = ColorTreeCellRenderer()
        ToolTipManager.sharedInstance().registerComponent(tree)
        tree.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 2) {
                    navigateToSelectedColor()
                }
            }

            override fun mousePressed(e: MouseEvent) = handlePopup(e)
            override fun mouseReleased(e: MouseEvent) = handlePopup(e)

            private fun handlePopup(e: MouseEvent) {
                if (!e.isPopupTrigger) return

                // Ensure the right-clicked node is part of the selection
                val clickedPath = tree.getPathForLocation(e.x, e.y) ?: return
                if (!tree.isPathSelected(clickedPath)) {
                    tree.selectionPath = clickedPath
                }

                showColorPopupMenu(e)
            }
        })
        add(JBScrollPane(tree), BorderLayout.CENTER)
    }

    private fun parseFilePatterns(): Set<String> {
        val raw = filterField.text.trim()
        if (raw.isEmpty()) return emptySet() // empty = all supported files
        return raw.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { pattern ->
                // Extract extension from patterns like "*.ts" or ".ts" or "ts"
                pattern.removePrefix("*").removePrefix(".")
            }
            .filter { it.isNotEmpty() }
            .map { it.lowercase() }
            .toSet()
    }

    fun runScan() {
        scanButton.isEnabled = false
        summaryLabel.text = "Scanning project..."
        val extensionFilter = parseFilePatterns()

        object : Task.Backgroundable(project, "Analyzing Project Colors", true) {
            override fun run(indicator: ProgressIndicator) {
                indicator.text = "Scanning project files for colors..."
                val startTime = System.currentTimeMillis()
                val colors = ProjectColorScanner.scan(project, indicator, extensionFilter)
                val elapsed = System.currentTimeMillis() - startTime

                if (indicator.isCanceled) return

                indicator.text = "Analyzing colors..."
                indicator.isIndeterminate = true
                val analysis = ColorAnalyzer.analyze(colors, 0, elapsed)

                SwingUtilities.invokeLater {
                    lastAnalysis = analysis
                    rebuildTree(analysis)
                    scanButton.isEnabled = true
                }
            }

            override fun onCancel() {
                SwingUtilities.invokeLater {
                    summaryLabel.text = "Scan cancelled."
                    scanButton.isEnabled = true
                }
            }

            override fun onThrowable(error: Throwable) {
                SwingUtilities.invokeLater {
                    summaryLabel.text = "Error: ${error.message}"
                    scanButton.isEnabled = true
                }
            }
        }.queue()
    }

    private fun rebuildTree(analysis: ProjectColorAnalysis) {
        summaryLabel.text = buildString {
            append("${analysis.uniqueColors} unique colors, ")
            append("${analysis.totalOccurrences} occurrences in ${analysis.filesWithColors} files")
            append(" (${analysis.scanTimeMs}ms)")
        }

        // Save tree state before rebuilding
        val expandedKeys = saveExpandedKeys()
        val selectedKeys = saveSelectedKeys()
        val scrollPosition = (tree.parent as? JViewport)?.viewPosition

        rootNode.removeAllChildren()

        // Section: Colors sorted by selected order
        if (analysis.colorGroups.isNotEmpty()) {
            val sortByHue = sortCombo.selectedItem == "Hue"
            val sortedGroups = if (sortByHue) {
                analysis.colorGroups.sortedWith(compareBy(
                    { bucketHue(it.color.toHsl().h) },
                    { it.color.toHsl().l }
                ))
            } else {
                analysis.colorGroups // already sorted by frequency from analyzer
            }
            val sortLabel = if (sortByHue) "By Hue" else "By Frequency"
            val freqNode = DefaultMutableTreeNode(SectionNode("$sortLabel (${analysis.uniqueColors} unique)"))
            for (group in sortedGroups) {
                val hex = formatColor(group.color)
                val dsName = findClosestDesignSystemName(group.color)
                val suffix = if (dsName != null) "  [$dsName]" else ""
                if (group.count == 1) {
                    // Single use: show occurrence directly
                    val occ = group.occurrences.first()
                    freqNode.add(DefaultMutableTreeNode(
                        OccurrenceEntry(occ, "$hex  ${occ.file.name}:${occ.line}", "${occ.matchText}$suffix")
                    ))
                } else {
                    val uses = "${group.count} uses"
                    val files = "in ${group.fileCount} ${plural(group.fileCount, "file")}"
                    val groupNode = DefaultMutableTreeNode(
                        ColorGroupEntry(group, hex, "$uses $files$suffix")
                    )
                    for (occ in group.occurrences) {
                        groupNode.add(DefaultMutableTreeNode(
                            OccurrenceEntry(occ, "${occ.file.name}:${occ.line}", occ.matchText)
                        ))
                    }
                    freqNode.add(groupNode)
                }
            }
            rootNode.add(freqNode)
        }

        // Section: Similar colors (sorted by hue of representative color)
        if (analysis.similarClusters.isNotEmpty()) {
            val sortedClusters = analysis.similarClusters.sortedWith(compareBy(
                { bucketHue(it.colors.first().color.toHsl().h) },
                { it.colors.first().color.toHsl().l }
            ))
            val simNode = DefaultMutableTreeNode(
                SectionNode("Similar Colors (${sortedClusters.size} ${plural(sortedClusters.size, "cluster")})")
            )
            for (cluster in sortedClusters) {
                val totalUses = cluster.colors.sumOf { it.count }
                val hexes = cluster.colors.take(3).joinToString(", ") {
                    formatColor(it.color)
                }
                val representativeColor = cluster.colors.first().color
                val clusterNode = DefaultMutableTreeNode(
                    ClusterEntry(cluster, representativeColor, hexes, "dist ${"%.1f".format(cluster.maxDistance)}, $totalUses uses")
                )
                for (group in cluster.colors) {
                    val hex = formatColor(group.color)
                    if (group.count == 1) {
                        val occ = group.occurrences.first()
                        clusterNode.add(DefaultMutableTreeNode(
                            OccurrenceEntry(occ, "$hex  ${occ.file.name}:${occ.line}", occ.matchText)
                        ))
                    } else {
                        val groupNode = DefaultMutableTreeNode(
                            ColorGroupEntry(group, hex, "${group.count} uses")
                        )
                        for (occ in group.occurrences) {
                            groupNode.add(DefaultMutableTreeNode(
                                OccurrenceEntry(occ, "${occ.file.name}:${occ.line}", occ.matchText)
                            ))
                        }
                        clusterNode.add(groupNode)
                    }
                }
                simNode.add(clusterNode)
            }
            rootNode.add(simNode)
        }

        // Section: Format inconsistencies (sorted by hue)
        if (analysis.formatInconsistencies.isNotEmpty()) {
            val sortedInconsistencies = analysis.formatInconsistencies.sortedWith(compareBy(
                { bucketHue(it.color.toHsl().h) },
                { it.color.toHsl().l }
            ))
            val fmtNode = DefaultMutableTreeNode(
                SectionNode("Format Inconsistencies (${sortedInconsistencies.size})")
            )
            for (group in sortedInconsistencies) {
                val hex = formatColor(group.color)
                val formats = group.formats.joinToString(", ") { it.displayName }
                val groupNode = DefaultMutableTreeNode(
                    ColorGroupEntry(group, hex, "appears as: $formats")
                )
                // Sub-group occurrences by format
                val byFormat = group.occurrences.groupBy { it.format }
                for ((fmt, occs) in byFormat) {
                    val fmtSubNode = DefaultMutableTreeNode(
                        FormatSubgroupEntry(fmt, "${fmt.displayName} (${occs.size})")
                    )
                    for (occ in occs) {
                        fmtSubNode.add(DefaultMutableTreeNode(
                            OccurrenceEntry(occ, "${occ.file.name}:${occ.line}", occ.matchText)
                        ))
                    }
                    groupNode.add(fmtSubNode)
                }
                fmtNode.add(groupNode)
            }
            rootNode.add(fmtNode)
        }

        treeModel.reload()

        // Restore tree state: expand previously expanded nodes, or default first-level sections
        if (expandedKeys.isNotEmpty()) {
            restoreExpandedKeys(expandedKeys)
            restoreSelectedKeys(selectedKeys)
            // Restore scroll position after layout
            if (scrollPosition != null) {
                SwingUtilities.invokeLater {
                    (tree.parent as? JViewport)?.viewPosition = scrollPosition
                }
            }
        } else {
            // First time: expand first-level sections
            for (i in 0 until tree.rowCount.coerceAtMost(3)) {
                tree.expandRow(i)
            }
        }
    }

    // ── Tree state save/restore ──

    /** Returns a stable string key for a tree node that survives rebuild. */
    private fun nodeKey(node: DefaultMutableTreeNode): String? {
        return when (val obj = node.userObject) {
            is SectionNode -> "section:${obj.title.substringBefore(" (")}"
            is ColorGroupEntry -> "group:${obj.group.hexKey}"
            is ClusterEntry -> "cluster:${obj.hexes}"
            is FormatSubgroupEntry -> "fmt:${obj.format.name}"
            is OccurrenceEntry -> "occ:${obj.occurrence.file.path}:${obj.occurrence.line}:${obj.occurrence.column}"
            else -> null
        }
    }

    /** Builds a path key by joining node keys from root to the given node. */
    private fun pathKey(node: DefaultMutableTreeNode): String {
        val parts = mutableListOf<String>()
        var current: DefaultMutableTreeNode? = node
        while (current != null && current !== rootNode) {
            nodeKey(current)?.let { parts.add(0, it) }
            current = current.parent as? DefaultMutableTreeNode
        }
        return parts.joinToString("/")
    }

    private fun saveExpandedKeys(): Set<String> {
        val keys = mutableSetOf<String>()
        for (i in 0 until tree.rowCount) {
            val path = tree.getPathForRow(i) ?: continue
            if (tree.isExpanded(path)) {
                val node = path.lastPathComponent as? DefaultMutableTreeNode ?: continue
                keys.add(pathKey(node))
            }
        }
        return keys
    }

    private fun saveSelectedKeys(): List<String> {
        val paths = tree.selectionPaths ?: return emptyList()
        return paths.mapNotNull { path ->
            val node = path.lastPathComponent as? DefaultMutableTreeNode ?: return@mapNotNull null
            pathKey(node)
        }
    }

    private fun restoreExpandedKeys(expandedKeys: Set<String>) {
        fun visit(node: DefaultMutableTreeNode) {
            val key = pathKey(node)
            if (key in expandedKeys) {
                val path = TreePath(node.path)
                tree.expandPath(path)
            }
            for (i in 0 until node.childCount) {
                visit(node.getChildAt(i) as DefaultMutableTreeNode)
            }
        }
        visit(rootNode)
    }

    private fun restoreSelectedKeys(selectedKeys: List<String>) {
        if (selectedKeys.isEmpty()) return
        val keySet = selectedKeys.toSet()
        val paths = mutableListOf<TreePath>()
        fun visit(node: DefaultMutableTreeNode) {
            if (pathKey(node) in keySet) {
                paths.add(TreePath(node.path))
            }
            for (i in 0 until node.childCount) {
                visit(node.getChildAt(i) as DefaultMutableTreeNode)
            }
        }
        visit(rootNode)
        if (paths.isNotEmpty()) {
            tree.selectionPaths = paths.toTypedArray()
        }
    }

    private fun findClosestDesignSystemName(color: UnifiedColor): String? {
        val exactName = NamedCssColors.getName(color)
        if (exactName != null) return "CSS: $exactName"

        val (twName, twColor) = TailwindColors.findClosest(color)
        val dr = color.r - twColor.r
        val dg = color.g - twColor.g
        val db = color.b - twColor.b
        val dist = dr * dr + dg * dg + db * db
        return if (dist < 100) "\u2248 Tailwind $twName" else null
    }

    private fun navigateToSelectedColor() {
        val node = tree.lastSelectedPathComponent as? DefaultMutableTreeNode ?: return
        val userObj = node.userObject
        if (userObj is OccurrenceEntry) {
            val occ = userObj.occurrence
            OpenFileDescriptor(project, occ.file, occ.line - 1, occ.column - 1).navigate(true)
        }
    }

    private fun showColorPopupMenu(e: MouseEvent) {
        val occurrences = collectOccurrencesFromSelection()
        if (occurrences.isEmpty()) return

        val previewColor = occurrences.first().color
        val popup = PalettePopupMenuBuilder.build(project, occurrences, previewColor) { count ->
            // After transform: re-scan to refresh the tree with updated colors
            if (count > 0) {
                runScan()
            }
        }
        popup.show(tree, e.x, e.y)
    }

    /**
     * Collects all [ProjectColor] occurrences from the currently selected tree nodes.
     * Flattens groups, clusters, and format subgroups into their leaf occurrences.
     */
    private fun collectOccurrencesFromSelection(): List<ProjectColor> {
        val paths = tree.selectionPaths ?: return emptyList()
        val result = mutableListOf<ProjectColor>()
        val visited = mutableSetOf<DefaultMutableTreeNode>()

        for (path in paths) {
            val node = path.lastPathComponent as? DefaultMutableTreeNode ?: continue
            collectFromNode(node, result, visited)
        }
        return result
    }

    private fun collectFromNode(
        node: DefaultMutableTreeNode,
        result: MutableList<ProjectColor>,
        visited: MutableSet<DefaultMutableTreeNode>
    ) {
        if (!visited.add(node)) return
        when (node.userObject) {
            is OccurrenceEntry -> {
                result.add((node.userObject as OccurrenceEntry).occurrence)
            }
            is ColorGroupEntry, is ClusterEntry, is FormatSubgroupEntry, is SectionNode -> {
                // Recurse into children to collect all leaf occurrences
                for (i in 0 until node.childCount) {
                    val child = node.getChildAt(i) as? DefaultMutableTreeNode ?: continue
                    collectFromNode(child, result, visited)
                }
            }
        }
    }

    private fun plural(count: Int, word: String) = if (count == 1) word else "${word}s"

    private fun bucketHue(h: Double) = (Math.round(h / 30.0) * 30.0)

    /** Format a color using the selected display format, auto-switching to alpha variant when needed. */
    private fun formatColor(color: UnifiedColor): String {
        val df = displayFormats[formatCombo.selectedIndex]
        val hasAlpha = color.a < 0.99
        val fmt = if (hasAlpha) df.alphaFormat else df.format
        return ColorConverter.format(color, fmt)
    }

    private data class DisplayFormat(val label: String, val format: ColorFormat, val alphaFormat: ColorFormat)

    // ── Tree node data types ──

    data class SectionNode(val title: String)
    data class ColorGroupEntry(val group: ColorGroup, val hex: String, val detail: String)
    data class ClusterEntry(val cluster: SimilarColorCluster, val color: UnifiedColor, val hexes: String, val detail: String)
    data class FormatSubgroupEntry(val format: ColorFormat, val label: String)
    data class OccurrenceEntry(val occurrence: ProjectColor, val location: String, val detail: String)

    // ── Cell renderer using IntelliJ's ColoredTreeCellRenderer (stable fonts) ──

    private class ColorTreeCellRenderer : ColoredTreeCellRenderer() {

        private val white = UnifiedColor(255.0, 255.0, 255.0, 1.0)
        private val black = UnifiedColor(0.0, 0.0, 0.0, 1.0)

        override fun customizeCellRenderer(
            tree: JTree, value: Any?, selected: Boolean, expanded: Boolean,
            leaf: Boolean, row: Int, hasFocus: Boolean
        ) {
            val node = value as? DefaultMutableTreeNode ?: return
            when (val entry = node.userObject) {
                is SectionNode -> {
                    icon = null
                    append(entry.title, SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES)
                }
                is ColorGroupEntry -> {
                    icon = ColorSwatchIcon(entry.group.color.toAwtColor(), JBUI.scale(12))
                    append(entry.hex, SimpleTextAttributes.REGULAR_ATTRIBUTES)
                    append("  ${entry.detail}", SimpleTextAttributes.GRAYED_ATTRIBUTES)
                    appendContrastBadge(entry.group.color)
                    toolTipText = buildColorTooltip(entry.group.color)
                }
                is ClusterEntry -> {
                    icon = ColorSwatchIcon(entry.color.toAwtColor(), JBUI.scale(12))
                    append(entry.hexes, SimpleTextAttributes.REGULAR_ATTRIBUTES)
                    append("  ${entry.detail}", SimpleTextAttributes.GRAYED_ATTRIBUTES)
                    toolTipText = buildColorTooltip(entry.color)
                }
                is FormatSubgroupEntry -> {
                    icon = null
                    append(entry.label, SimpleTextAttributes.REGULAR_ATTRIBUTES)
                }
                is OccurrenceEntry -> {
                    icon = ColorSwatchIcon(entry.occurrence.color.toAwtColor(), JBUI.scale(10))
                    append(entry.location, SimpleTextAttributes.GRAYED_ATTRIBUTES)
                    append("  ${entry.detail}", SimpleTextAttributes.GRAYED_SMALL_ATTRIBUTES)
                    appendContrastBadge(entry.occurrence.color)
                    toolTipText = buildColorTooltip(entry.occurrence.color)
                }
            }
        }

        private fun appendContrastBadge(color: UnifiedColor) {
            val ratioOnWhite = ColorTransforms.contrastRatio(color, white)
            val ratioOnBlack = ColorTransforms.contrastRatio(color, black)
            val bestRatio = maxOf(ratioOnWhite, ratioOnBlack)
            val bg = if (ratioOnWhite >= ratioOnBlack) "W" else "B"

            val badge: String
            val attrs: SimpleTextAttributes
            when {
                bestRatio >= 7.0 -> {
                    badge = "  AAA/$bg"
                    attrs = SimpleTextAttributes(SimpleTextAttributes.STYLE_SMALLER, Color(60, 160, 60))
                }
                bestRatio >= 4.5 -> {
                    badge = "  AA/$bg"
                    attrs = SimpleTextAttributes(SimpleTextAttributes.STYLE_SMALLER, Color(60, 160, 60))
                }
                bestRatio >= 3.0 -> {
                    badge = "  AA-lg/$bg"
                    attrs = SimpleTextAttributes(SimpleTextAttributes.STYLE_SMALLER, Color(200, 150, 0))
                }
                else -> {
                    badge = "  Fail"
                    attrs = SimpleTextAttributes(SimpleTextAttributes.STYLE_SMALLER, Color(200, 60, 60))
                }
            }
            append(badge, attrs)
        }

        private fun buildColorTooltip(color: UnifiedColor): String {
            val hsl = color.toHsl()
            val ratioOnWhite = ColorTransforms.contrastRatio(color, white)
            val ratioOnBlack = ColorTransforms.contrastRatio(color, black)

            return buildString {
                append("<html><body style='font-family: monospace; padding: 4px;'>")
                append("<table cellpadding='2'>")
                row("HEX", ColorConverter.format(color, ColorFormat.HEX6))
                if (color.a < 0.99) row("HEX+A", ColorConverter.format(color, ColorFormat.HEX8))
                row("RGB", ColorConverter.format(color, ColorFormat.RGB_FUNC))
                row("HSL", "hsl(${f1(hsl.h)}, ${f1(hsl.s)}%, ${f1(hsl.l)}%)")
                if (color.a < 0.99) row("Alpha", f2(color.a))
                append("</table>")
                append("<hr>")
                append("<table cellpadding='2'>")
                row("On White", "${f2(ratioOnWhite)}:1 ${wcagLabel(ratioOnWhite)}")
                row("On Black", "${f2(ratioOnBlack)}:1 ${wcagLabel(ratioOnBlack)}")
                append("</table>")
                append("</body></html>")
            }
        }

        private fun StringBuilder.row(label: String, value: String) {
            append("<tr><td><b>$label</b></td><td>$value</td></tr>")
        }

        private fun wcagLabel(ratio: Double): String = when {
            ratio >= 7.0 -> "<span style='color: green;'>AAA</span>"
            ratio >= 4.5 -> "<span style='color: green;'>AA</span>"
            ratio >= 3.0 -> "<span style='color: #c89600;'>AA-large</span>"
            else -> "<span style='color: red;'>Fail</span>"
        }

        private fun f1(v: Double) = String.format("%.1f", v)
        private fun f2(v: Double) = String.format("%.2f", v)
    }

    // ── Icons ──

    private class ColorSwatchIcon(private val color: Color, private val size: Int) : Icon {
        override fun getIconWidth() = size
        override fun getIconHeight() = size
        override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
            val g2 = g.create() as Graphics2D
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2.color = color
            g2.fill(Ellipse2D.Float(x.toFloat(), y.toFloat(), size.toFloat(), size.toFloat()))
            g2.color = Color(128, 128, 128, 100)
            g2.stroke = BasicStroke(1f)
            g2.draw(Ellipse2D.Float(x + 0.5f, y + 0.5f, size - 1f, size - 1f))
            g2.dispose()
        }
    }

}
