package com.github.ignaciotcrespo.colormanipulation.actions.definitions

import com.github.ignaciotcrespo.colormanipulation.actions.ActionEntry
import com.github.ignaciotcrespo.colormanipulation.actions.MenuDefinition
import com.github.ignaciotcrespo.colormanipulation.actions.info.ClosestMatchAction
import com.github.ignaciotcrespo.colormanipulation.actions.info.MatchSystem

val ClosestMatchMenu = MenuDefinition("Closest Match...", listOf(
    ActionEntry.Sep("Named CSS"),
    ActionEntry.Raw(ClosestMatchAction(MatchSystem.CSS, useName = true)),
    ActionEntry.Raw(ClosestMatchAction(MatchSystem.CSS, useName = false)),
    ActionEntry.Sep("Tailwind"),
    ActionEntry.Raw(ClosestMatchAction(MatchSystem.TAILWIND, useName = true)),
    ActionEntry.Raw(ClosestMatchAction(MatchSystem.TAILWIND, useName = false)),
    ActionEntry.Sep("Bootstrap"),
    ActionEntry.Raw(ClosestMatchAction(MatchSystem.BOOTSTRAP, useName = true)),
    ActionEntry.Raw(ClosestMatchAction(MatchSystem.BOOTSTRAP, useName = false)),
    ActionEntry.Sep("Material Design"),
    ActionEntry.Raw(ClosestMatchAction(MatchSystem.MATERIAL, useName = true)),
    ActionEntry.Raw(ClosestMatchAction(MatchSystem.MATERIAL, useName = false)),
    ActionEntry.Sep("iOS System"),
    ActionEntry.Raw(ClosestMatchAction(MatchSystem.IOS, useName = true)),
    ActionEntry.Raw(ClosestMatchAction(MatchSystem.IOS, useName = false)),
))
