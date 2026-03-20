package com.github.ignaciotcrespo.colormanipulation.actions.definitions

import com.github.ignaciotcrespo.colormanipulation.actions.ActionEntry
import com.github.ignaciotcrespo.colormanipulation.actions.MenuDefinition
import com.github.ignaciotcrespo.colormanipulation.actions.info.ConvertFromNameAction
import com.github.ignaciotcrespo.colormanipulation.actions.info.NameSystem

val ConvertFromNameMenu = MenuDefinition("Convert from Name...", listOf(
    ActionEntry.Raw(ConvertFromNameAction(NameSystem.CSS)),
    ActionEntry.Raw(ConvertFromNameAction(NameSystem.TAILWIND)),
    ActionEntry.Raw(ConvertFromNameAction(NameSystem.BOOTSTRAP)),
    ActionEntry.Raw(ConvertFromNameAction(NameSystem.MATERIAL)),
    ActionEntry.Raw(ConvertFromNameAction(NameSystem.IOS)),
))
