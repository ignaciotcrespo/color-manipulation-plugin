package com.github.ignaciotcrespo.colormanipulation.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*

class CustomPercentageDialog(
    project: Project,
    title: String,
    private val label: String,
    private val min: Double,
    private val max: Double,
    private val default: Double
) : DialogWrapper(project) {

    private val spinner = JSpinner(
        SpinnerNumberModel(default, min, max, 1.0)
    )

    var result: Double = default
        private set

    init {
        this.title = title
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(BorderLayout(8, 8)).apply {
            preferredSize = Dimension(300, 60)
            add(JLabel(label), BorderLayout.NORTH)
            add(spinner, BorderLayout.CENTER)
        }
        return panel
    }

    override fun doOKAction() {
        result = (spinner.value as Number).toDouble()
        super.doOKAction()
    }

    override fun getPreferredFocusedComponent(): JComponent = spinner
}
