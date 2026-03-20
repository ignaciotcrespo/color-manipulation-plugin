package com.github.ignaciotcrespo.colormanipulation.actions.info

import com.github.ignaciotcrespo.colormanipulation.actions.ActionUtils
import com.github.ignaciotcrespo.colormanipulation.model.ColorTransforms
import com.github.ignaciotcrespo.colormanipulation.model.UnifiedColor
import com.github.ignaciotcrespo.colormanipulation.util.EditorUtil
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.popup.JBPopupFactory
import java.awt.*
import javax.swing.*

class ContrastCheckAction : AnAction("WCAG Contrast Check") {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val caretColors = EditorUtil.getSelectedColors(editor)
        if (caretColors.isEmpty()) return

        val color = caretColors.first().color
        val white = UnifiedColor(255.0, 255.0, 255.0, 1.0)
        val black = UnifiedColor(0.0, 0.0, 0.0, 1.0)

        val ratioOnWhite = ColorTransforms.contrastRatio(color, white)
        val ratioOnBlack = ColorTransforms.contrastRatio(color, black)

        val panel = JPanel(BorderLayout(10, 10))
        panel.border = BorderFactory.createEmptyBorder(12, 12, 12, 12)

        val swatchPanel = JPanel(GridLayout(1, 2, 8, 0))
        swatchPanel.add(createSwatch(color.toAwtColor(), Color.WHITE, "On White"))
        swatchPanel.add(createSwatch(color.toAwtColor(), Color.BLACK, "On Black"))
        panel.add(swatchPanel, BorderLayout.NORTH)

        val info = buildString {
            appendLine("Contrast Ratios")
            appendLine("════════════════════════════════")
            appendLine()
            appendLine("On White:  ${String.format("%.2f", ratioOnWhite)}:1")
            appendLine("  AA Normal (4.5:1):  ${passFail(ratioOnWhite >= 4.5)}")
            appendLine("  AA Large  (3.0:1):  ${passFail(ratioOnWhite >= 3.0)}")
            appendLine("  AAA Normal(7.0:1):  ${passFail(ratioOnWhite >= 7.0)}")
            appendLine("  AAA Large (4.5:1):  ${passFail(ratioOnWhite >= 4.5)}")
            appendLine()
            appendLine("On Black:  ${String.format("%.2f", ratioOnBlack)}:1")
            appendLine("  AA Normal (4.5:1):  ${passFail(ratioOnBlack >= 4.5)}")
            appendLine("  AA Large  (3.0:1):  ${passFail(ratioOnBlack >= 3.0)}")
            appendLine("  AAA Normal(7.0:1):  ${passFail(ratioOnBlack >= 7.0)}")
            appendLine("  AAA Large (4.5:1):  ${passFail(ratioOnBlack >= 4.5)}")
        }

        val textArea = JTextArea(info).apply {
            isEditable = false
            font = Font(Font.MONOSPACED, Font.PLAIN, 12)
            background = panel.background
        }
        panel.add(JScrollPane(textArea), BorderLayout.CENTER)

        val popup = JBPopupFactory.getInstance()
            .createComponentPopupBuilder(panel, null)
            .setTitle("WCAG Contrast Check")
            .setMovable(true)
            .setResizable(true)
            .setRequestFocus(true)
            .createPopup()

        popup.showInBestPositionFor(editor)
    }

    private fun passFail(pass: Boolean): String = if (pass) "PASS" else "FAIL"

    private fun createSwatch(fg: Color, bg: Color, label: String): JPanel {
        return object : JPanel() {
            init { preferredSize = Dimension(120, 50) }
            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)
                g.color = bg
                g.fillRect(0, 0, width, height)
                g.color = fg
                (g as Graphics2D).setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON
                )
                g.font = Font(Font.SANS_SERIF, Font.BOLD, 14)
                g.drawString("Aa Text", 10, 30)
                g.font = Font(Font.SANS_SERIF, Font.PLAIN, 10)
                g.drawString(label, 10, 44)
            }
        }
    }

    override fun update(e: AnActionEvent) = ActionUtils.updateWithCurrentColorIcon(e)

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
