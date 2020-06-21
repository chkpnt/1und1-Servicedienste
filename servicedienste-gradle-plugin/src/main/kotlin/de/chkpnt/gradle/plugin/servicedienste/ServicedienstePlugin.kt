package de.chkpnt.gradle.plugin.servicedienste

import de.undercouch.gradle.tasks.download.Download
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.TaskProvider

class ServicedienstePlugin: Plugin<Project> {

    override fun apply(project: Project) {
        project.pluginManager
                .apply(BasePlugin::class.java)

        // DSL
        val extensionDsl = project.extensions
                .create(EXTENSION_NAME_SERVICEDIENSTE_DSL, ServicediensteExtension::class.java, project)
        val downloadDslPdfTask = registerDownloadTask(project, TASK_NAME_DOWNLOAD_DSL_PDF, extensionDsl)
        val convertDslTask = registerConvertTask(project, TASK_NAME_CONVERT_DSL, extensionDsl)
        convertDslTask.configure { it.dependsOn(downloadDslPdfTask) }

        // Mobilfunk
        val extensionMobilfunk = project.extensions
                .create(EXTENSION_NAME_SERVICEDIENSTE_MOBILFUNK, ServicediensteExtension::class.java, project)
        val downloadDslMobilfunkTask = registerDownloadTask(project, TASK_NAME_DOWNLOAD_MOBILFUNK_PDF, extensionMobilfunk)
        val convertMobilfunkTask = registerConvertTask(project, TASK_NAME_CONVERT_MOBILFUNK, extensionMobilfunk)
        convertMobilfunkTask.configure { it.dependsOn(downloadDslMobilfunkTask) }

        project.tasks.register(TASK_NAME_CONVERT_ALL) { task ->
            task.group = GROUP_NAME_CONVERSION
            task.description = "Converts all Servicedienste-PDFs from 1&1 into machine readable formats"

            task.dependsOn(convertDslTask, convertMobilfunkTask)
        }
    }

    private fun registerDownloadTask(project: Project, taskName: String, extension: ServicediensteExtension): TaskProvider<Download> {
        return project.tasks.register(taskName, Download::class.java) { task ->
            task.group = GROUP_NAME_DOWNLOADS

            task.src(extension.sourceUrl)
            task.dest(extension.downloadTo)
            task.onlyIfModified(true)
        }
    }

    private fun registerConvertTask(project: Project, taskName: String, extension: ServicediensteExtension): TaskProvider<ServicediensteConversionTask> {
        return project.tasks.register(taskName, ServicediensteConversionTask::class.java) { task ->
            task.group = GROUP_NAME_CONVERSION

            task.sourceUrl.set(extension.sourceUrl)
            task.pdf.set(extension.downloadTo)
            task.jsonExportFile.set(extension.jsonExportFile)
        }
    }

    companion object {

        private val GROUP_NAME_DOWNLOADS = "downloads"
        private val GROUP_NAME_CONVERSION = "1&1-Servicedienste-PDFs"

        private val TASK_NAME_DOWNLOAD_DSL_PDF = "downloadServicediensteDslPdf"
        private val TASK_NAME_DOWNLOAD_MOBILFUNK_PDF = "downloadServicediensteMobilfunkPdf"
        private val TASK_NAME_CONVERT_DSL = "convertServicediensteDsl"
        private val TASK_NAME_CONVERT_MOBILFUNK = "convertServicediensteMobilfunk"
        private val TASK_NAME_CONVERT_ALL = "convertServicedienste"

        private val EXTENSION_NAME_SERVICEDIENSTE_DSL = "servicediensteDsl"
        private val EXTENSION_NAME_SERVICEDIENSTE_MOBILFUNK = "servicediensteMobilfunk"

    }
}
