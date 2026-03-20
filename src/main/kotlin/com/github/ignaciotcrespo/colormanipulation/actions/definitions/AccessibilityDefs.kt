package com.github.ignaciotcrespo.colormanipulation.actions.definitions

import com.github.ignaciotcrespo.colormanipulation.actions.ActionEntry
import com.github.ignaciotcrespo.colormanipulation.actions.MenuDefinition
import com.github.ignaciotcrespo.colormanipulation.actions.info.ContrastCheckAction
import com.github.ignaciotcrespo.colormanipulation.model.ColorTransforms
import com.github.ignaciotcrespo.colormanipulation.model.UnifiedColor

private val white = UnifiedColor(255.0, 255.0, 255.0, 1.0)
private val black = UnifiedColor(0.0, 0.0, 0.0, 1.0)

val AccessibilityMenu = MenuDefinition("Accessibility...", listOf(
    ActionEntry.Raw(ContrastCheckAction()),
    ActionEntry.Sep("Auto-fix AA (4.5:1)"),
    ActionEntry.Transform("Make AA on White") { c, _ -> ColorTransforms.adjustForContrast(c, white, 4.5) },
    ActionEntry.Transform("Make AA on Black") { c, _ -> ColorTransforms.adjustForContrast(c, black, 4.5) },
    ActionEntry.Sep("Auto-fix AAA (7:1)"),
    ActionEntry.Transform("Make AAA on White") { c, _ -> ColorTransforms.adjustForContrast(c, white, 7.0) },
    ActionEntry.Transform("Make AAA on Black") { c, _ -> ColorTransforms.adjustForContrast(c, black, 7.0) },
))
