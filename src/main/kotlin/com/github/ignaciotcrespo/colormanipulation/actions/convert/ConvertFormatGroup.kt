package com.github.ignaciotcrespo.colormanipulation.actions.convert

import com.github.ignaciotcrespo.colormanipulation.actions.LastActionTracker
import com.github.ignaciotcrespo.colormanipulation.model.ColorConverter
import com.github.ignaciotcrespo.colormanipulation.model.ColorFormat
import com.github.ignaciotcrespo.colormanipulation.model.UnifiedColor
import com.github.ignaciotcrespo.colormanipulation.ui.ColorCircleIcon
import com.github.ignaciotcrespo.colormanipulation.ui.ConvertIcon
import com.github.ignaciotcrespo.colormanipulation.util.EditorUtil
import com.intellij.openapi.actionSystem.*

class ConvertFormatGroup : DefaultActionGroup("Convert Format...", true) {

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        if (editor == null || !EditorUtil.hasValidColorSelection(editor)) {
            e.presentation.isEnabled = false
            e.presentation.icon = null
            return
        }
        e.presentation.isEnabled = true
        e.presentation.icon = ConvertIcon()
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        if (e == null) return emptyArray()
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return emptyArray()
        val colors = EditorUtil.getSelectedColors(editor)

        val sourceIsNoHash = if (colors.isNotEmpty()) {
            colors.first().format.isNoHash
        } else {
            val text = editor.selectionModel.selectedText ?: return emptyArray()
            val found = ColorConverter.findAll(text)
            if (found.isEmpty()) return emptyArray()
            found.first().format.isNoHash
        }

        val actions = mutableListOf<AnAction>()

        // Toggle section
        actions.add(Separator.create("Toggle"))
        actions.add(ToggleHashAction())
        actions.add(Toggle0xAction())
        actions.add(SwapByteOrderAction())

        // Build format lists per category, adapting hash based on source
        val categoryFormats = mapOf(
            ColorFormat.Category.CSS to if (sourceIsNoHash) listOf(
                ColorFormat.HEX3_NO_HASH to "RGB (short)",
                ColorFormat.HEX6_NO_HASH to "RRGGBB",
                ColorFormat.HEX8_NO_HASH to "RRGGBBAA",
                ColorFormat.ARGB8_NO_HASH to "AARRGGBB",
                ColorFormat.RGB_FUNC to "rgb(r, g, b)",
                ColorFormat.RGBA_FUNC to "rgba(r, g, b, a)",
                ColorFormat.RGB_SPACE to "rgb(r g b)",
                ColorFormat.RGB_SPACE_ALPHA to "rgb(r g b / a)",
                ColorFormat.RGB_PERCENT to "rgb(r%, g%, b%)",
                ColorFormat.HSL_FUNC to "hsl(h, s%, l%)",
                ColorFormat.HSLA_FUNC to "hsla(h, s%, l%, a)",
                ColorFormat.HSL_SPACE to "hsl(h s% l%)",
                ColorFormat.HSL_SPACE_ALPHA to "hsl(h s% l% / a)",
                ColorFormat.HWB to "hwb(h w% b%)",
                ColorFormat.NAMED_CSS to "Named CSS Color",
            ) else listOf(
                ColorFormat.HEX3 to "#RGB (short)",
                ColorFormat.HEX6 to "#RRGGBB",
                ColorFormat.HEX8 to "#RRGGBBAA",
                ColorFormat.ARGB8 to "#AARRGGBB",
                ColorFormat.RGB_FUNC to "rgb(r, g, b)",
                ColorFormat.RGBA_FUNC to "rgba(r, g, b, a)",
                ColorFormat.RGB_SPACE to "rgb(r g b)",
                ColorFormat.RGB_SPACE_ALPHA to "rgb(r g b / a)",
                ColorFormat.RGB_PERCENT to "rgb(r%, g%, b%)",
                ColorFormat.HSL_FUNC to "hsl(h, s%, l%)",
                ColorFormat.HSLA_FUNC to "hsla(h, s%, l%, a)",
                ColorFormat.HSL_SPACE to "hsl(h s% l%)",
                ColorFormat.HSL_SPACE_ALPHA to "hsl(h s% l% / a)",
                ColorFormat.HWB to "hwb(h w% b%)",
                ColorFormat.NAMED_CSS to "Named CSS Color",
            ),
            ColorFormat.Category.ANDROID to listOf(
                ColorFormat.HEX_0X to "0xRRGGBB",
                ColorFormat.COMPOSE_COLOR to "Color(0xAARRGGBB)",
                ColorFormat.ANDROID_RGB to "Color.rgb(r, g, b)",
                ColorFormat.ANDROID_ARGB to "Color.argb(a, r, g, b)",
            ),
            ColorFormat.Category.SWIFT to listOf(
                ColorFormat.UICOLOR to "UIColor(red:green:blue:alpha:)",
                ColorFormat.UICOLOR_OBJC to "[UIColor colorWithRed:green:blue:alpha:]",
                ColorFormat.SWIFTUI_COLOR to "Color(red:green:blue:)",
            ),
            ColorFormat.Category.JAVA to listOf(
                ColorFormat.JAVA_COLOR to "new Color(r, g, b)",
                ColorFormat.JAVA_COLOR_ALPHA to "new Color(r, g, b, a)",
            ),
            ColorFormat.Category.GENERIC to listOf(
                ColorFormat.FLOAT_RGB to "(r, g, b) float 0.0-1.0",
                ColorFormat.FLOAT_RGBA to "(r, g, b, a) float 0.0-1.0",
                ColorFormat.FLOAT_RGB_NO_PAREN to "r, g, b float (no parens)",
                ColorFormat.FLOAT_RGBA_NO_PAREN to "r, g, b, a float (no parens)",
            ),
        )

        for ((category, formats) in categoryFormats) {
            actions.add(Separator.create(category.label))
            for ((format, label) in formats) {
                actions.add(DynamicConvertAction(label, format))
            }
        }

        return actions.toTypedArray()
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}

private class DynamicConvertAction(
    private val label: String,
    private val targetFormat: ColorFormat
) : AnAction(label) {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val project = e.project ?: return
        val transform = { color: UnifiedColor, _: ColorFormat ->
            ColorConverter.format(color, targetFormat)
        }
        LastActionTracker.record("Convert to $label", transform)
        EditorUtil.replaceSelections(editor, project, transform)
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        if (editor == null || !EditorUtil.hasValidColorSelection(editor)) {
            e.presentation.isEnabled = false
            e.presentation.icon = null
            return
        }
        e.presentation.isEnabled = true

        val colors = EditorUtil.getSelectedColors(editor)
        if (colors.isNotEmpty()) {
            e.presentation.icon = ColorCircleIcon(colors.first().color.toAwtColor())
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
