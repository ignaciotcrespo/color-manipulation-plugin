package com.github.ignaciotcrespo.colormanipulation.actions.info

import com.github.ignaciotcrespo.colormanipulation.model.*
import java.awt.*
import javax.swing.*

/**
 * Builds the Color Info panel used by both the editor action and the palette popup menu.
 */
object ColorInfoPanelBuilder {

    private val white = UnifiedColor(255.0, 255.0, 255.0, 1.0)
    private val black = UnifiedColor(0.0, 0.0, 0.0, 1.0)

    fun buildPanel(color: UnifiedColor): JPanel {
        val awtColor = color.toAwtColor()
        val hsl = color.toHsl()
        val ratioOnWhite = ColorTransforms.contrastRatio(color, white)
        val ratioOnBlack = ColorTransforms.contrastRatio(color, black)

        // Compute suggested AA color when WCAG fails on one background.
        // Math: ratioOnWhite * ratioOnBlack = 21, so a color can only fail on one side, never both.
        val suggestedColor = when {
            ratioOnWhite < 4.5 -> ColorTransforms.adjustForContrast(color, white, 4.5)
            ratioOnBlack < 4.5 -> ColorTransforms.adjustForContrast(color, black, 4.5)
            else -> null
        }

        val panel = JPanel(BorderLayout(10, 10))
        panel.border = BorderFactory.createEmptyBorder(12, 12, 12, 12)

        // Swatch with contrast previews: 3 base rows + 2 suggestion rows when WCAG fails
        val hasSuggestion = suggestedColor != null
        val rows = if (hasSuggestion) 5 else 3
        val swatchPanel = JPanel(GridLayout(rows, 1, 0, 2))

        // Original color swatch
        swatchPanel.add(object : JPanel() {
            override fun getPreferredSize() = Dimension(80, 32)
            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)
                g.color = awtColor
                g.fillRect(0, 0, width, height)
                g.color = Color.GRAY
                g.drawRect(0, 0, width - 1, height - 1)
            }
        })
        // Aa on white
        swatchPanel.add(object : JPanel() {
            override fun getPreferredSize() = Dimension(80, 24)
            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)
                val g2 = g as Graphics2D
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
                g2.color = Color.WHITE; g2.fillRect(0, 0, width, height)
                g2.color = awtColor; g2.font = Font(Font.SANS_SERIF, Font.BOLD, 12)
                g2.drawString("Aa Text", 6, 16)
                g2.color = Color.GRAY; g2.drawRect(0, 0, width - 1, height - 1)
            }
        })
        // Aa on black
        swatchPanel.add(object : JPanel() {
            override fun getPreferredSize() = Dimension(80, 24)
            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)
                val g2 = g as Graphics2D
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
                g2.color = Color.BLACK; g2.fillRect(0, 0, width, height)
                g2.color = awtColor; g2.font = Font(Font.SANS_SERIF, Font.BOLD, 12)
                g2.drawString("Aa Text", 6, 16)
                g2.color = Color.GRAY; g2.drawRect(0, 0, width - 1, height - 1)
            }
        })
        // Suggested AA color previews on both backgrounds
        if (suggestedColor != null) {
            val suggestedAwtColor = suggestedColor.toAwtColor()
            val greenBorder = Color(46, 160, 67)
            // Suggested on white
            swatchPanel.add(object : JPanel() {
                override fun getPreferredSize() = Dimension(80, 24)
                override fun paintComponent(g: Graphics) {
                    super.paintComponent(g)
                    val g2 = g as Graphics2D
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
                    g2.color = Color.WHITE; g2.fillRect(0, 0, width, height)
                    g2.color = suggestedAwtColor; g2.font = Font(Font.SANS_SERIF, Font.BOLD, 12)
                    g2.drawString("Aa Fix", 6, 16)
                    g2.color = greenBorder; g2.drawRect(0, 0, width - 1, height - 1)
                }
            })
            // Suggested on black
            swatchPanel.add(object : JPanel() {
                override fun getPreferredSize() = Dimension(80, 24)
                override fun paintComponent(g: Graphics) {
                    super.paintComponent(g)
                    val g2 = g as Graphics2D
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
                    g2.color = Color.BLACK; g2.fillRect(0, 0, width, height)
                    g2.color = suggestedAwtColor; g2.font = Font(Font.SANS_SERIF, Font.BOLD, 12)
                    g2.drawString("Aa Fix", 6, 16)
                    g2.color = greenBorder; g2.drawRect(0, 0, width - 1, height - 1)
                }
            })
        }
        panel.add(swatchPanel, BorderLayout.WEST)

        // Info text as HTML for colored WCAG badges
        val (cssName, cssColor) = NamedCssColors.findClosest(color)
        val (twName, twColor) = TailwindColors.findClosest(color)
        val (bsName, bsColor) = BootstrapColors.findClosest(color)
        val (mdName, mdColor) = MaterialColors.findClosest(color)
        val (iosName, iosColor) = IOSSystemColors.findClosest(color)

        val html = buildString {
            append("<html><body style='font-family: monospace; font-size: 12px; padding: 4px;'>")
            append("<b>All Formats:</b><br>")
            append("<table cellpadding='1' cellspacing='0'>")
            for (fmt in ColorFormat.entries) {
                val formatted = ColorConverter.format(color, fmt)
                append("<tr><td>${esc(fmt.displayName)}</td><td>&nbsp;&nbsp;</td><td>${esc(formatted)}</td></tr>")
            }
            append("</table><br>")
            append("<table cellpadding='1' cellspacing='0'>")
            append("<tr><td>HSL</td><td>&nbsp;&nbsp;</td><td>h=${f1(hsl.h)}, s=${f1(hsl.s)}%, l=${f1(hsl.l)}%</td></tr>")
            append("<tr><td>Alpha</td><td>&nbsp;&nbsp;</td><td>${f2(color.a)}</td></tr>")
            append("</table>")
            append("<br><b>WCAG Contrast:</b><br>")
            append("<table cellpadding='1' cellspacing='0'>")
            append("<tr><td>On White</td><td>&nbsp;&nbsp;</td><td>${f2(ratioOnWhite)}:1</td><td>&nbsp;&nbsp;</td><td>${wcagBadgeHtml(ratioOnWhite)}</td></tr>")
            append("<tr><td>On Black</td><td>&nbsp;&nbsp;</td><td>${f2(ratioOnBlack)}:1</td><td>&nbsp;&nbsp;</td><td>${wcagBadgeHtml(ratioOnBlack)}</td></tr>")
            if (suggestedColor != null) {
                val fixHex = ColorConverter.format(suggestedColor, ColorFormat.HEX6)
                val failedBg = if (ratioOnWhite < 4.5) "white" else "black"
                val fixBg = if (ratioOnWhite < 4.5) white else black
                val fixRatio = ColorTransforms.contrastRatio(suggestedColor, fixBg)
                append("<tr><td></td><td></td><td colspan='3'><span style='color: #888;'>suggested AA on $failedBg: </span>")
                append("<b style='color: #2ea043;'>${esc(fixHex)}</b>")
                append("<span style='color: #888;'> (${f2(fixRatio)}:1)</span></td></tr>")
            }
            append("</table>")
            append("<br><b>Closest Match:</b><br>")
            append("<table cellpadding='1' cellspacing='0'>")
            append("<tr><td>CSS</td><td>&nbsp;&nbsp;</td><td>${esc(cssName)} (${esc(ColorConverter.format(cssColor, ColorFormat.HEX6))})</td></tr>")
            append("<tr><td>Tailwind</td><td>&nbsp;&nbsp;</td><td>${esc(twName)} (${esc(ColorConverter.format(twColor, ColorFormat.HEX6))})</td></tr>")
            append("<tr><td>Bootstrap</td><td>&nbsp;&nbsp;</td><td>${esc(bsName)} (${esc(ColorConverter.format(bsColor, ColorFormat.HEX6))})</td></tr>")
            append("<tr><td>Material</td><td>&nbsp;&nbsp;</td><td>${esc(mdName)} (${esc(ColorConverter.format(mdColor, ColorFormat.HEX6))})</td></tr>")
            append("<tr><td>iOS</td><td>&nbsp;&nbsp;</td><td>${esc(iosName)} (${esc(ColorConverter.format(iosColor, ColorFormat.HEX6))})</td></tr>")
            append("</table>")
            append("</body></html>")
        }

        val editorPane = JEditorPane("text/html", html).apply {
            isEditable = false
            background = panel.background
            caretPosition = 0
        }
        panel.add(JScrollPane(editorPane), BorderLayout.CENTER)

        return panel
    }

    private fun wcagBadgeHtml(ratio: Double): String = when {
        ratio >= 7.0 -> "<b style='color: #2ea043;'>AAA</b>"
        ratio >= 4.5 -> "<b style='color: #2ea043;'>AA</b>"
        ratio >= 3.0 -> "<b style='color: #c89600;'>AA-large only</b>"
        else -> "<b style='color: #cf222e;'>FAIL</b>"
    }

    private fun f1(v: Double) = String.format("%.1f", v)
    private fun f2(v: Double) = String.format("%.2f", v)

    /** Escape HTML special characters. */
    private fun esc(s: String) = s
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
}
