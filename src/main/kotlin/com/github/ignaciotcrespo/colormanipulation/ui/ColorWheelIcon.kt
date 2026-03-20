package com.github.ignaciotcrespo.colormanipulation.ui

import java.awt.*
import javax.swing.Icon

class ColorWheelIcon(private val size: Int = 14) : Icon {

    override fun getIconWidth() = size
    override fun getIconHeight() = size

    override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
        val g2 = g.create() as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        val cx = x + size / 2.0
        val cy = y + size / 2.0
        val radius = size / 2.0

        // Draw color wheel segments
        val segments = 12
        for (i in 0 until segments) {
            val startAngle = (i * 360.0 / segments).toInt()
            val arcAngle = (360.0 / segments).toInt() + 1
            val hue = i.toFloat() / segments
            g2.color = Color.getHSBColor(hue, 0.8f, 0.9f)
            g2.fillArc(x, y, size, size, startAngle, arcAngle)
        }

        // White center dot
        val innerR = (radius * 0.3).toInt()
        g2.color = Color(255, 255, 255, 220)
        g2.fillOval(
            (cx - innerR).toInt(), (cy - innerR).toInt(),
            innerR * 2, innerR * 2
        )

        // Subtle border
        g2.color = Color(128, 128, 128, 80)
        g2.drawOval(x, y, size - 1, size - 1)

        g2.dispose()
    }
}
