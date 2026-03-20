package com.github.ignaciotcrespo.colormanipulation.ui

import java.awt.*
import java.awt.geom.Ellipse2D
import javax.swing.Icon

class ColorCircleIcon(
    private val color: Color,
    private val size: Int = 12
) : Icon {

    override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
        val g2 = g.create() as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // Draw checkerboard background for alpha colors
        if (color.alpha < 255) {
            val checkSize = size / 4
            g2.color = Color.WHITE
            g2.fill(Ellipse2D.Float(x.toFloat(), y.toFloat(), size.toFloat(), size.toFloat()))
            g2.clip = Ellipse2D.Float(x.toFloat(), y.toFloat(), size.toFloat(), size.toFloat())
            g2.color = Color(204, 204, 204)
            for (row in 0 until (size / checkSize + 1)) {
                for (col in 0 until (size / checkSize + 1)) {
                    if ((row + col) % 2 == 0) {
                        g2.fillRect(x + col * checkSize, y + row * checkSize, checkSize, checkSize)
                    }
                }
            }
            g2.clip = null
        }

        // Draw filled circle
        g2.color = color
        g2.fill(Ellipse2D.Float(x.toFloat(), y.toFloat(), size.toFloat(), size.toFloat()))

        // Draw border
        g2.color = Color(128, 128, 128, 128)
        g2.stroke = BasicStroke(1f)
        g2.draw(Ellipse2D.Float(x + 0.5f, y + 0.5f, size - 1f, size - 1f))

        g2.dispose()
    }

    override fun getIconWidth(): Int = size
    override fun getIconHeight(): Int = size
}
