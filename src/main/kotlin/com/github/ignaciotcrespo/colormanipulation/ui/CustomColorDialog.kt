package com.github.ignaciotcrespo.colormanipulation.ui

import com.github.ignaciotcrespo.colormanipulation.model.ColorConverter
import com.github.ignaciotcrespo.colormanipulation.model.ColorFormat
import com.github.ignaciotcrespo.colormanipulation.model.UnifiedColor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import java.awt.*
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

/**
 * Dialog that lets users type a color in any supported format (hex, rgb, hsl, named CSS, etc.)
 * with a live preview swatch and real-time validation.
 */
class CustomColorDialog(
    project: Project,
    private val currentColor: UnifiedColor? = null
) : DialogWrapper(project) {

    private val textField = JTextField(24)
    private val previewPanel = ColorPreviewPanel()
    private val hintLabel = JLabel(" ").apply {
        font = font.deriveFont(Font.ITALIC, 11f)
        foreground = Color.GRAY
    }

    /** The parsed color result, available after OK. */
    var resultColor: UnifiedColor? = null
        private set

    init {
        title = "Replace with Color"
        init()

        // Pre-fill with current color in hex if available
        if (currentColor != null) {
            val hex = ColorConverter.format(currentColor, ColorFormat.HEX6)
            textField.text = hex
        }

        // Live preview as user types
        textField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) = updatePreview()
            override fun removeUpdate(e: DocumentEvent?) = updatePreview()
            override fun changedUpdate(e: DocumentEvent?) = updatePreview()
        })
        updatePreview()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(BorderLayout(8, 8)).apply {
            preferredSize = Dimension(380, 100)
        }

        // Top: label + hint
        val topPanel = JPanel(BorderLayout()).apply {
            add(JLabel("Enter a color (any format):"), BorderLayout.NORTH)
            add(JLabel("<html><span style='color: gray; font-size: 10px;'>" +
                "e.g. #FF5733, rgb(255, 87, 51), hsl(11, 100%, 60%), red</span></html>"),
                BorderLayout.SOUTH)
        }
        panel.add(topPanel, BorderLayout.NORTH)

        // Center: text field + preview swatch
        val inputRow = JPanel(BorderLayout(8, 0))
        inputRow.add(textField, BorderLayout.CENTER)
        inputRow.add(previewPanel, BorderLayout.EAST)
        panel.add(inputRow, BorderLayout.CENTER)

        // Bottom: validation hint
        panel.add(hintLabel, BorderLayout.SOUTH)

        return panel
    }

    private fun updatePreview() {
        val text = textField.text.trim()
        if (text.isEmpty()) {
            previewPanel.previewColor = null
            hintLabel.text = " "
            return
        }
        val parsed = ColorConverter.parse(text)
        if (parsed != null) {
            val (color, format) = parsed
            previewPanel.previewColor = color.toAwtColor()
            hintLabel.text = "Detected: ${format.displayName}"
            hintLabel.foreground = Color(46, 160, 67)
        } else {
            previewPanel.previewColor = null
            hintLabel.text = "Not a recognized color format"
            hintLabel.foreground = Color(207, 34, 46)
        }
    }

    override fun doValidate(): ValidationInfo? {
        val text = textField.text.trim()
        if (text.isEmpty()) {
            return ValidationInfo("Please enter a color value", textField)
        }
        val parsed = ColorConverter.parse(text)
        if (parsed == null) {
            return ValidationInfo("Not a recognized color format", textField)
        }
        return null
    }

    override fun doOKAction() {
        val parsed = ColorConverter.parse(textField.text.trim())
        resultColor = parsed?.first
        super.doOKAction()
    }

    override fun getPreferredFocusedComponent(): JComponent = textField

    /**
     * Small square panel that shows a color swatch or a crossed-out empty state.
     */
    private class ColorPreviewPanel : JPanel() {
        var previewColor: Color? = null
            set(value) {
                field = value
                repaint()
            }

        override fun getPreferredSize() = Dimension(48, 32)
        override fun getMinimumSize() = preferredSize

        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            val g2 = g as Graphics2D
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

            val c = previewColor
            if (c != null) {
                // Draw checkerboard for alpha
                val sq = 6
                for (row in 0 until height / sq + 1) {
                    for (col in 0 until width / sq + 1) {
                        g2.color = if ((row + col) % 2 == 0) Color.WHITE else Color.LIGHT_GRAY
                        g2.fillRect(col * sq, row * sq, sq, sq)
                    }
                }
                g2.color = c
                g2.fillRect(0, 0, width, height)
                g2.color = Color.GRAY
                g2.drawRect(0, 0, width - 1, height - 1)
            } else {
                // Empty state: light gray with diagonal line
                g2.color = Color(240, 240, 240)
                g2.fillRect(0, 0, width, height)
                g2.color = Color.GRAY
                g2.drawRect(0, 0, width - 1, height - 1)
                g2.color = Color(200, 200, 200)
                g2.drawLine(0, 0, width - 1, height - 1)
            }
        }
    }
}
