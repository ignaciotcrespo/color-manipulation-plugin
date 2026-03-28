package com.github.ignaciotcrespo.colormanipulation.palette

import com.github.ignaciotcrespo.colormanipulation.actions.ActionEntry
import com.github.ignaciotcrespo.colormanipulation.actions.LastActionTracker
import com.github.ignaciotcrespo.colormanipulation.actions.MenuDefinition
import com.github.ignaciotcrespo.colormanipulation.actions.definitions.*
import com.github.ignaciotcrespo.colormanipulation.actions.info.ColorInfoPanelBuilder
import com.github.ignaciotcrespo.colormanipulation.model.ColorConverter
import com.github.ignaciotcrespo.colormanipulation.model.ColorFormat
import com.github.ignaciotcrespo.colormanipulation.model.UnifiedColor
import com.github.ignaciotcrespo.colormanipulation.ui.ColorCircleIcon
import com.github.ignaciotcrespo.colormanipulation.ui.CustomColorDialog
import com.github.ignaciotcrespo.colormanipulation.ui.CustomPercentageDialog
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import javax.swing.*

/**
 * Builds a JPopupMenu for the Color Palette tree with the same transform actions
 * as the editor's Color Manipulation menu, but operating on [ProjectColor] occurrences
 * instead of editor selections.
 */
object PalettePopupMenuBuilder {

    /**
     * @param project the current project
     * @param occurrences all color occurrences to transform when an action is chosen
     * @param previewColor a representative color for showing preview icons
     * @param onComplete callback after a transform is applied (e.g. to re-scan)
     */
    fun build(
        project: Project,
        occurrences: List<ProjectColor>,
        previewColor: UnifiedColor,
        onComplete: (Int) -> Unit
    ): JPopupMenu {
        val popup = JPopupMenu()

        if (occurrences.isEmpty()) return popup

        val previewFormat = occurrences.first().format

        // Copy color value
        val copyMenu = JMenu("Copy Color")
        copyMenu.icon = ColorCircleIcon(previewColor.toAwtColor())
        for ((label, fmt) in listOf(
            "#RRGGBB" to ColorFormat.HEX6,
            "rgb()" to ColorFormat.RGB_FUNC,
            "hsl()" to ColorFormat.HSL_FUNC,
            "0xRRGGBB" to ColorFormat.HEX_0X,
        )) {
            val formatted = ColorConverter.format(previewColor, fmt)
            val item = JMenuItem("$label  $formatted")
            item.addActionListener {
                val clipboard = Toolkit.getDefaultToolkit().systemClipboard
                clipboard.setContents(StringSelection(formatted), null)
            }
            copyMenu.add(item)
        }
        popup.add(copyMenu)

        // Color Info (single color only)
        val infoItem = JMenuItem("Color Info")
        infoItem.addActionListener { showColorInfoPopup(project, previewColor) }
        popup.add(infoItem)

        popup.addSeparator()

        // Repeat Last Action
        val lastTransform = LastActionTracker.lastTransform
        if (lastTransform != null) {
            val label = LastActionTracker.lastLabel ?: "Repeat Last"
            val item = JMenuItem("Repeat: $label")
            val preview = applyTransformForPreview(previewColor, previewFormat, lastTransform)
            if (preview != null) item.icon = ColorCircleIcon(preview.toAwtColor())
            item.addActionListener {
                val count = PaletteColorReplacer.replaceAll(project, occurrences, lastTransform)
                onComplete(count)
            }
            popup.add(item)
            popup.addSeparator()
        }

        // Convert Format
        addConvertFormatMenu(popup, project, occurrences, previewColor, onComplete)

        // Replace with Custom Color
        addReplaceWithColorItem(popup, project, occurrences, previewColor, onComplete)

        popup.addSeparator()

        // Adjustments
        val adjustmentsMenu = JMenu("Adjustments")
        addMenuDefinition(adjustmentsMenu, LightenDarkenMenu, project, occurrences, previewColor, previewFormat, onComplete)
        addMenuDefinition(adjustmentsMenu, SaturateMenu, project, occurrences, previewColor, previewFormat, onComplete)
        addMenuDefinition(adjustmentsMenu, AlphaMenu, project, occurrences, previewColor, previewFormat, onComplete)
        addMenuDefinition(adjustmentsMenu, HueRotateMenu, project, occurrences, previewColor, previewFormat, onComplete)
        addMenuDefinition(adjustmentsMenu, TemperatureMenu, project, occurrences, previewColor, previewFormat, onComplete)
        popup.add(adjustmentsMenu)

        // Mixing (no Palette Generate — it doesn't make sense for batch)
        val mixingMenu = JMenu("Mixing")
        addMenuDefinition(mixingMenu, ColorMixingMenu, project, occurrences, previewColor, previewFormat, onComplete)
        popup.add(mixingMenu)

        popup.addSeparator()

        // Accessibility
        addMenuDefinition(popup, AccessibilityMenu, project, occurrences, previewColor, previewFormat, onComplete)

        // Color Blindness Simulation
        addMenuDefinition(popup, ColorBlindnessMenu, project, occurrences, previewColor, previewFormat, onComplete)

        return popup
    }

    private fun addMenuDefinition(
        parent: JComponent,
        menuDef: MenuDefinition,
        project: Project,
        occurrences: List<ProjectColor>,
        previewColor: UnifiedColor,
        previewFormat: ColorFormat,
        onComplete: (Int) -> Unit
    ) {
        val subMenu = JMenu(menuDef.text)
        for (entry in menuDef.entries) {
            when (entry) {
                is ActionEntry.Transform -> {
                    val item = JMenuItem(entry.text)
                    val preview = entry.transform(previewColor, previewFormat)
                    if (preview != null) item.icon = ColorCircleIcon(preview.toAwtColor())
                    item.addActionListener {
                        val transform = buildTransformFn(entry.transform)
                        LastActionTracker.record(entry.text, transform)
                        val count = PaletteColorReplacer.replaceAll(project, occurrences, transform)
                        onComplete(count)
                    }
                    subMenu.add(item)
                }
                is ActionEntry.CustomDialog -> {
                    val item = JMenuItem(entry.text)
                    item.addActionListener {
                        val dialog = CustomPercentageDialog(
                            project, entry.dialogTitle, entry.dialogLabel,
                            entry.min, entry.max, entry.default
                        )
                        if (!dialog.showAndGet()) return@addActionListener
                        val value = dialog.result
                        val transform = { color: UnifiedColor, format: ColorFormat ->
                            entry.transform(color, format, value)
                        }
                        LastActionTracker.record("${entry.dialogTitle} ($value)", transform)
                        val count = PaletteColorReplacer.replaceAll(project, occurrences, transform)
                        onComplete(count)
                    }
                    subMenu.add(item)
                }
                is ActionEntry.Sep -> {
                    if (entry.label != null) {
                        subMenu.addSeparator()
                        val label = JMenuItem(entry.label)
                        label.isEnabled = false
                        subMenu.add(label)
                    } else {
                        subMenu.addSeparator()
                    }
                }
                is ActionEntry.Palette -> {
                    // Skip palette generation in batch mode — doesn't apply
                }
                is ActionEntry.Raw -> {
                    // Skip raw actions — they require editor context
                }
            }
        }
        parent.add(subMenu)
    }

    private fun addReplaceWithColorItem(
        parent: JPopupMenu,
        project: Project,
        occurrences: List<ProjectColor>,
        previewColor: UnifiedColor,
        onComplete: (Int) -> Unit
    ) {
        val item = JMenuItem("Replace with Color...")
        item.addActionListener {
            val dialog = CustomColorDialog(project, previewColor)
            if (!dialog.showAndGet()) return@addActionListener
            val newColor = dialog.resultColor ?: return@addActionListener
            // Replace each occurrence, keeping it in its original format
            val transform = { _: UnifiedColor, format: ColorFormat ->
                ColorConverter.format(newColor, format)
            }
            val hexLabel = ColorConverter.format(newColor, ColorFormat.HEX6)
            LastActionTracker.record("Replace with $hexLabel", transform)
            val count = PaletteColorReplacer.replaceAll(project, occurrences, transform)
            onComplete(count)
        }
        parent.add(item)
    }

    private fun addConvertFormatMenu(
        parent: JPopupMenu,
        project: Project,
        occurrences: List<ProjectColor>,
        previewColor: UnifiedColor,
        onComplete: (Int) -> Unit
    ) {
        val convertMenu = JMenu("Convert Format")

        data class FormatEntry(val label: String, val format: ColorFormat)

        val categories = linkedMapOf(
            "CSS" to listOf(
                FormatEntry("#RRGGBB", ColorFormat.HEX6),
                FormatEntry("#RRGGBBAA", ColorFormat.HEX8),
                FormatEntry("#AARRGGBB", ColorFormat.ARGB8),
                FormatEntry("rgb(r, g, b)", ColorFormat.RGB_FUNC),
                FormatEntry("rgba(r, g, b, a)", ColorFormat.RGBA_FUNC),
                FormatEntry("hsl(h, s%, l%)", ColorFormat.HSL_FUNC),
                FormatEntry("hsla(h, s%, l%, a)", ColorFormat.HSLA_FUNC),
                FormatEntry("hwb(h w% b%)", ColorFormat.HWB),
            ),
            "Android/Kotlin" to listOf(
                FormatEntry("0xRRGGBB", ColorFormat.HEX_0X),
                FormatEntry("Color(0xAARRGGBB)", ColorFormat.COMPOSE_COLOR),
                FormatEntry("Color.rgb(r, g, b)", ColorFormat.ANDROID_RGB),
                FormatEntry("Color.argb(a, r, g, b)", ColorFormat.ANDROID_ARGB),
            ),
            "iOS/Swift" to listOf(
                FormatEntry("UIColor(red:green:blue:alpha:)", ColorFormat.UICOLOR),
                FormatEntry("Color(red:green:blue:)", ColorFormat.SWIFTUI_COLOR),
            ),
            "Java" to listOf(
                FormatEntry("new Color(r, g, b)", ColorFormat.JAVA_COLOR),
                FormatEntry("new Color(r, g, b, a)", ColorFormat.JAVA_COLOR_ALPHA),
            ),
        )

        for ((category, formats) in categories) {
            convertMenu.addSeparator()
            val catLabel = JMenuItem(category)
            catLabel.isEnabled = false
            convertMenu.add(catLabel)
            for (fe in formats) {
                val item = JMenuItem(fe.label)
                item.icon = ColorCircleIcon(previewColor.toAwtColor())
                item.addActionListener {
                    val targetFormat = fe.format
                    val transform = { color: UnifiedColor, _: ColorFormat ->
                        ColorConverter.format(color, targetFormat)
                    }
                    LastActionTracker.record("Convert to ${fe.label}", transform)
                    val count = PaletteColorReplacer.replaceAll(project, occurrences, transform)
                    onComplete(count)
                }
                convertMenu.add(item)
            }
        }

        parent.add(convertMenu)
    }

    /**
     * Wraps a (UnifiedColor, ColorFormat) -> UnifiedColor? transform into the
     * (UnifiedColor, ColorFormat) -> String? form expected by PaletteColorReplacer,
     * handling alpha-aware format upgrades.
     */
    private fun buildTransformFn(
        transformFn: (UnifiedColor, ColorFormat) -> UnifiedColor?
    ): (UnifiedColor, ColorFormat) -> String? {
        return { color, format ->
            val result = transformFn(color, format)
            if (result != null) {
                val outputFormat = if (result.a < 1.0 && !format.supportsAlpha) {
                    format.alphaVariant()
                } else {
                    format
                }
                ColorConverter.format(result, outputFormat)
            } else null
        }
    }

    private fun applyTransformForPreview(
        color: UnifiedColor,
        format: ColorFormat,
        transform: (UnifiedColor, ColorFormat) -> String?
    ): UnifiedColor? {
        val text = transform(color, format) ?: return null
        return ColorConverter.parse(text)?.first
    }

    private fun showColorInfoPopup(project: Project, color: UnifiedColor) {
        val panel = ColorInfoPanelBuilder.buildPanel(color)

        val popup = JBPopupFactory.getInstance()
            .createComponentPopupBuilder(panel, null)
            .setTitle("Color Info")
            .setMovable(true)
            .setResizable(true)
            .setRequestFocus(true)
            .createPopup()

        popup.showCenteredInCurrentWindow(project)
    }
}
