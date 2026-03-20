package com.github.ignaciotcrespo.colormanipulation.ui

import java.awt.*
import java.awt.geom.Ellipse2D
import java.awt.geom.RoundRectangle2D
import javax.swing.Icon

class PaletteSwatchIcon(
    private val colors: List<Color>,
    private val sourceColor: Color,
    private val height: Int = 14
) : Icon {

    private val circleSize = 12
    private val gap = 4
    private val swatchWidth = 6
    private val swatchGap = 1
    private val swatchTotalWidth: Int
        get() = if (colors.isEmpty()) 0 else colors.size * swatchWidth + (colors.size - 1) * swatchGap

    override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
        val g2 = g.create() as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // Draw source color circle
        val cy = y + (height - circleSize) / 2
        g2.color = sourceColor
        g2.fill(Ellipse2D.Float(x.toFloat(), cy.toFloat(), circleSize.toFloat(), circleSize.toFloat()))
        g2.color = Color(128, 128, 128, 128)
        g2.stroke = BasicStroke(1f)
        g2.draw(Ellipse2D.Float(x + 0.5f, cy + 0.5f, circleSize - 1f, circleSize - 1f))

        // Draw arrow
        val arrowX = x + circleSize + gap / 2
        g2.color = Color(160, 160, 160)
        val arrowY = y + height / 2
        g2.drawLine(arrowX, arrowY, arrowX + 3, arrowY)
        g2.drawLine(arrowX + 1, arrowY - 1, arrowX + 3, arrowY)
        g2.drawLine(arrowX + 1, arrowY + 1, arrowX + 3, arrowY)

        // Draw palette swatch
        if (colors.isNotEmpty()) {
            val sx = x + circleSize + gap + 4
            val clip = RoundRectangle2D.Float(
                sx.toFloat(), y.toFloat(),
                swatchTotalWidth.toFloat(), height.toFloat(),
                4f, 4f
            )
            g2.clip(clip)

            var cx = sx
            for (color in colors) {
                g2.color = color
                g2.fillRect(cx, y, swatchWidth, height)
                cx += swatchWidth + swatchGap
            }

            g2.clip = null

            g2.color = Color(128, 128, 128, 100)
            g2.stroke = BasicStroke(1f)
            g2.draw(RoundRectangle2D.Float(
                sx + 0.5f, y + 0.5f,
                swatchTotalWidth - 1f, height - 1f,
                4f, 4f
            ))
        }

        g2.dispose()
    }

    override fun getIconWidth(): Int = circleSize + gap + 4 + swatchTotalWidth
    override fun getIconHeight(): Int = height
}
