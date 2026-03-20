package com.github.ignaciotcrespo.colormanipulation.actions.info

import com.github.ignaciotcrespo.colormanipulation.model.*
import com.github.ignaciotcrespo.colormanipulation.util.EditorUtil
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.awt.RelativePoint
import java.awt.*
import javax.swing.*

class ShowColorInfoAction : AnAction("Show Color Info") {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val caretColors = EditorUtil.getSelectedColors(editor)
        if (caretColors.isEmpty()) return

        val cc = caretColors.first()
        val color = cc.color
        val awtColor = color.toAwtColor()
        val hsl = color.toHsl()

        val panel = JPanel(BorderLayout(10, 10))
        panel.border = BorderFactory.createEmptyBorder(12, 12, 12, 12)

        val swatch = object : JPanel() {
            override fun getPreferredSize() = Dimension(60, 60)
            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)
                g.color = awtColor
                g.fillRect(0, 0, width, height)
                g.color = Color.GRAY
                g.drawRect(0, 0, width - 1, height - 1)
            }
        }
        panel.add(swatch, BorderLayout.WEST)

        val info = buildString {
            appendLine("All Formats:")
            appendLine()
            for (fmt in ColorFormat.entries) {
                val formatted = ColorConverter.format(color, fmt)
                appendLine("  ${fmt.displayName}: $formatted")
            }
            appendLine()
            appendLine("HSL: h=${String.format("%.1f", hsl.h)}, s=${String.format("%.1f", hsl.s)}%, l=${String.format("%.1f", hsl.l)}%")
            appendLine("Alpha: ${String.format("%.2f", color.a)}")
            appendLine()
            appendLine("Closest Match:")
            appendLine()
            val (cssName, cssColor) = NamedCssColors.findClosest(color)
            appendLine("  CSS:       $cssName (${ColorConverter.format(cssColor, ColorFormat.HEX6)})")
            val (twName, twColor) = TailwindColors.findClosest(color)
            appendLine("  Tailwind:  $twName (${ColorConverter.format(twColor, ColorFormat.HEX6)})")
            val (bsName, bsColor) = BootstrapColors.findClosest(color)
            appendLine("  Bootstrap: $bsName (${ColorConverter.format(bsColor, ColorFormat.HEX6)})")
            val (mdName, mdColor) = MaterialColors.findClosest(color)
            appendLine("  Material:  $mdName (${ColorConverter.format(mdColor, ColorFormat.HEX6)})")
            val (iosName, iosColor) = IOSSystemColors.findClosest(color)
            appendLine("  iOS:       $iosName (${ColorConverter.format(iosColor, ColorFormat.HEX6)})")
        }

        val textArea = JTextArea(info).apply {
            isEditable = false
            font = Font(Font.MONOSPACED, Font.PLAIN, 12)
            background = panel.background
        }
        panel.add(JScrollPane(textArea), BorderLayout.CENTER)

        val popup = JBPopupFactory.getInstance()
            .createComponentPopupBuilder(panel, null)
            .setTitle("Color Info")
            .setMovable(true)
            .setResizable(true)
            .setRequestFocus(true)
            .createPopup()

        popup.showInBestPositionFor(editor)
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        val hasColor = editor != null && EditorUtil.getSelectedColors(editor).isNotEmpty()
        e.presentation.isEnabled = hasColor
        e.presentation.icon = if (hasColor) com.github.ignaciotcrespo.colormanipulation.ui.InfoIcon() else null
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
