package com.github.jellynugget.laravellastmigration.settings

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project

/**
 * Project-level settings for the Laravel Last Migration plugin.
 */
@State(
    name = "LaravelLastMigrationSettings",
    storages = [Storage("laravelLastMigration.xml")]
)
@Service(Service.Level.PROJECT)
class MigrationSettingsState(private val project: Project) : PersistentStateComponent<MigrationSettingsState.State> {

    companion object {
        const val DEFAULT_MIGRATIONS_RELATIVE_PATH: String = "database/migrations"
    }

    data class State(
        var baseMigrationsRelativePath: String = DEFAULT_MIGRATIONS_RELATIVE_PATH
    )

    private var state: State = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    var baseMigrationsRelativePath: String
        get() = state.baseMigrationsRelativePath.ifBlank { DEFAULT_MIGRATIONS_RELATIVE_PATH }
        set(value) {
            state.baseMigrationsRelativePath = value.trim().ifBlank { DEFAULT_MIGRATIONS_RELATIVE_PATH }
        }
}


