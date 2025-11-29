package com.github.jellynugget.laravellastmigration.actions

import com.github.jellynugget.laravellastmigration.MyBundle
import com.github.jellynugget.laravellastmigration.settings.MigrationSettingsState
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.components.service
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

private fun findMigrationsDirectory(project: Project): VirtualFile? {
    val basePath = project.basePath ?: return null
    val settings = project.service<MigrationSettingsState>()

    val relative = settings.baseMigrationsRelativePath
        .replace("\\", "/")
        .trim('/')

    if (relative.isEmpty()) {
        return null
    }

    val segments = relative.split('/').toTypedArray()
    val migrationsPath = Paths.get(basePath, *segments)
    if (!Files.isDirectory(migrationsPath)) return null
    return LocalFileSystem.getInstance().findFileByPath(migrationsPath.toString())
}

private fun openFileInEditor(project: Project, file: VirtualFile?) {
    if (file == null) return
    val editorManager = FileEditorManager.getInstance(project)
    editorManager.openFile(file, true)
}

private fun findLastMigrationByName(migrationsDir: VirtualFile): VirtualFile? {
    return migrationsDir.children
        ?.filter { !it.isDirectory && it.extension == "php" }
        ?.maxByOrNull { it.name }
}

private fun findLastMigrationByModifiedDate(migrationsDir: VirtualFile): VirtualFile? {
    return migrationsDir.children
        ?.filter { !it.isDirectory && it.extension == "php" }
        ?.maxByOrNull { it.timeStamp }
}

abstract class BaseGotoLastMigrationAction : AnAction() {

    protected fun resolveProject(e: AnActionEvent): Project? {
        val project = e.project ?: e.getData(CommonDataKeys.PROJECT)
        if (project == null || project.isDisposed) {
            return null
        }
        return project
    }

    override fun update(e: AnActionEvent) {
        val presentation = e.presentation
        val project = resolveProject(e)
        if (project == null) {
            presentation.isEnabled = false
            return
        }

        val migrationsDir = findMigrationsDirectory(project)
        presentation.isEnabled = migrationsDir != null
    }
}

class GotoLastMigrationByNameAction : BaseGotoLastMigrationAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = resolveProject(e) ?: return
        val migrationsDir = findMigrationsDirectory(project) ?: return

        val lastByName = findLastMigrationByName(migrationsDir)
        if (lastByName != null) {
            openFileInEditor(project, lastByName)
        } else {
            // Optional: show notification/toast later if needed
        }
    }
}

class GotoLastMigrationByDateAction : BaseGotoLastMigrationAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = resolveProject(e) ?: return
        val migrationsDir = findMigrationsDirectory(project) ?: return

        val lastByDate = findLastMigrationByModifiedDate(migrationsDir)
        if (lastByDate != null) {
            openFileInEditor(project, lastByDate)
        } else {
            // Optional: show notification/toast later if needed
        }
    }
}


