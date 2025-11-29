package com.github.jellynugget.laravellastmigration.settings

import com.github.jellynugget.laravellastmigration.MyBundle
import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

class MigrationSettingsConfigurable(private val project: Project) : Configurable {

    private val settings: MigrationSettingsState = project.service()

    private val basePathField = JBTextField().apply {
        emptyText.text = MigrationSettingsState.DEFAULT_MIGRATIONS_RELATIVE_PATH
    }

    private var panel: JPanel? = null

    override fun getDisplayName(): String =
        MyBundle.message("settings.migrations.displayName")

    override fun createComponent(): JComponent {
        if (panel == null) {
            panel = FormBuilder.createFormBuilder()
                .addLabeledComponent(
                    JBLabel(MyBundle.message("settings.migrations.basePath.label")),
                    basePathField,
                    1,
                    false
                )
                .addComponent(
                    JBLabel(MyBundle.message("settings.migrations.basePath.help")),
                    1
                )
                .panel
        }

        reset()
        return panel!!
    }

    override fun isModified(): Boolean {
        return basePathField.text.trim() != settings.baseMigrationsRelativePath
    }

    override fun apply() {
        settings.baseMigrationsRelativePath = basePathField.text
    }

    override fun reset() {
        basePathField.text = settings.baseMigrationsRelativePath
    }
}


