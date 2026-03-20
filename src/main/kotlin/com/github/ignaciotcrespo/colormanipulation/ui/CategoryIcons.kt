package com.github.ignaciotcrespo.colormanipulation.ui

import java.awt.*
import java.awt.geom.AffineTransform
import java.awt.geom.GeneralPath
import javax.swing.Icon

private fun g2(g: Graphics): Graphics2D = (g.create() as Graphics2D).apply {
    setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)
}

/** Two arrows forming a cycle — format conversion */
class ConvertIcon(private val size: Int = 14) : Icon {
    override fun getIconWidth() = size
    override fun getIconHeight() = size
    override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
        val g2 = g2(g)
        g2.color = Color(100, 160, 255)
        g2.stroke = BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        // Top arrow (right)
        g2.drawArc(x + 2, y + 2, size - 4, size - 4, 30, 150)
        g2.fillPolygon(
            intArrayOf(x + size - 3, x + size - 1, x + size - 5),
            intArrayOf(y + 3, y + 6, y + 6), 3
        )
        // Bottom arrow (left)
        g2.drawArc(x + 2, y + 2, size - 4, size - 4, 210, 150)
        g2.fillPolygon(
            intArrayOf(x + 3, x + 1, x + 5),
            intArrayOf(y + size - 3, y + size - 6, y + size - 6), 3
        )
        g2.dispose()
    }
}

/** Sliders — adjustments */
class AdjustmentsIcon(private val size: Int = 14) : Icon {
    override fun getIconWidth() = size
    override fun getIconHeight() = size
    override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
        val g2 = g2(g)
        g2.stroke = BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        val colors = arrayOf(Color(255, 100, 100), Color(100, 200, 100), Color(100, 150, 255))
        val knobPositions = intArrayOf(8, 4, 10) // different positions for each slider
        for (i in 0..2) {
            val ly = y + 3 + i * 4
            g2.color = Color(160, 160, 160, 120)
            g2.drawLine(x + 1, ly, x + size - 2, ly)
            g2.color = colors[i]
            val kx = x + knobPositions[i]
            g2.fillOval(kx - 2, ly - 2, 4, 4)
        }
        g2.dispose()
    }
}

/** Three overlapping circles — mixing & palette */
class MixingIcon(private val size: Int = 14) : Icon {
    override fun getIconWidth() = size
    override fun getIconHeight() = size
    override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
        val g2 = g2(g)
        g2.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f)
        val r = size / 3
        g2.color = Color(255, 80, 80)
        g2.fillOval(x + 1, y + 1, r * 2, r * 2)
        g2.color = Color(80, 200, 80)
        g2.fillOval(x + size - r * 2 - 1, y + 1, r * 2, r * 2)
        g2.color = Color(80, 120, 255)
        g2.fillOval(x + size / 2 - r, y + size - r * 2 - 1, r * 2, r * 2)
        g2.dispose()
    }
}

/** Eye with checkmark — accessibility */
class AccessibilityIcon(private val size: Int = 14) : Icon {
    override fun getIconWidth() = size
    override fun getIconHeight() = size
    override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
        val g2 = g2(g)
        g2.color = Color(80, 180, 120)
        g2.stroke = BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        // Eye shape
        val cx = x + size / 2
        val cy = y + size / 2
        g2.drawArc(x + 1, cy - 3, size - 2, 8, 0, 180)
        g2.drawArc(x + 1, cy - 5, size - 2, 8, 180, 180)
        // Pupil
        g2.fillOval(cx - 2, cy - 2, 5, 5)
        // Small check at bottom-right
        g2.color = Color(60, 200, 100)
        g2.stroke = BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        g2.drawLine(x + size - 5, y + size - 4, x + size - 3, y + size - 2)
        g2.drawLine(x + size - 3, y + size - 2, x + size - 1, y + size - 5)
        g2.dispose()
    }
}

/** Eye with strikethrough — color blindness */
class ColorBlindIcon(private val size: Int = 14) : Icon {
    override fun getIconWidth() = size
    override fun getIconHeight() = size
    override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
        val g2 = g2(g)
        g2.color = Color(180, 140, 80)
        g2.stroke = BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        val cy = y + size / 2
        // Eye shape
        g2.drawArc(x + 1, cy - 3, size - 2, 8, 0, 180)
        g2.drawArc(x + 1, cy - 5, size - 2, 8, 180, 180)
        // Pupil (desaturated)
        g2.color = Color(150, 150, 150)
        g2.fillOval(x + size / 2 - 2, cy - 2, 5, 5)
        // Diagonal line through
        g2.color = Color(200, 80, 80)
        g2.stroke = BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        g2.drawLine(x + 2, y + size - 2, x + size - 2, y + 2)
        g2.dispose()
    }
}

/** Target/crosshair — closest match */
class ClosestMatchIcon(private val size: Int = 14) : Icon {
    override fun getIconWidth() = size
    override fun getIconHeight() = size
    override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
        val g2 = g2(g)
        g2.color = Color(200, 120, 255)
        g2.stroke = BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        val cx = x + size / 2
        val cy = y + size / 2
        // Outer circle
        g2.drawOval(x + 2, y + 2, size - 4, size - 4)
        // Inner circle
        g2.drawOval(cx - 2, cy - 2, 4, 4)
        // Crosshair lines
        g2.drawLine(cx, y + 1, cx, y + 3)
        g2.drawLine(cx, y + size - 3, cx, y + size - 1)
        g2.drawLine(x + 1, cy, x + 3, cy)
        g2.drawLine(x + size - 3, cy, x + size - 1, cy)
        g2.dispose()
    }
}

/** Wrench/gear — practical utils */
class UtilsIcon(private val size: Int = 14) : Icon {
    override fun getIconWidth() = size
    override fun getIconHeight() = size
    override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
        val g2 = g2(g)
        g2.color = Color(160, 170, 190)
        g2.stroke = BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        val cx = x + size / 2
        val cy = y + size / 2
        // Gear outer
        val r = size / 2 - 1
        val ir = r - 3
        val teeth = 6
        for (i in 0 until teeth) {
            val angle1 = Math.toRadians(i * 360.0 / teeth - 15)
            val angle2 = Math.toRadians(i * 360.0 / teeth + 15)
            val ox1 = cx + (r * Math.cos(angle1)).toInt()
            val oy1 = cy + (r * Math.sin(angle1)).toInt()
            val ox2 = cx + (r * Math.cos(angle2)).toInt()
            val oy2 = cy + (r * Math.sin(angle2)).toInt()
            g2.drawLine(ox1, oy1, ox2, oy2)
        }
        g2.drawOval(cx - ir, cy - ir, ir * 2, ir * 2)
        // Center dot
        g2.fillOval(cx - 1, cy - 1, 3, 3)
        g2.dispose()
    }
}

/** Info circle — show color info */
class InfoIcon(private val size: Int = 14) : Icon {
    override fun getIconWidth() = size
    override fun getIconHeight() = size
    override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
        val g2 = g2(g)
        g2.color = Color(100, 160, 220)
        g2.stroke = BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        // Circle
        g2.drawOval(x + 1, y + 1, size - 3, size - 3)
        // "i" letter
        val cx = x + size / 2
        g2.fillOval(cx - 1, y + 3, 3, 2)
        g2.stroke = BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        g2.drawLine(cx, y + 6, cx, y + size - 4)
        g2.dispose()
    }
}
