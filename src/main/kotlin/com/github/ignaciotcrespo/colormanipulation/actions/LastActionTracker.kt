package com.github.ignaciotcrespo.colormanipulation.actions

import com.github.ignaciotcrespo.colormanipulation.model.ColorFormat
import com.github.ignaciotcrespo.colormanipulation.model.UnifiedColor

object LastActionTracker {
    var lastLabel: String? = null
        private set
    var lastTransform: ((UnifiedColor, ColorFormat) -> String?)? = null
        private set

    fun record(label: String, transform: (UnifiedColor, ColorFormat) -> String?) {
        lastLabel = label
        lastTransform = transform
    }
}
