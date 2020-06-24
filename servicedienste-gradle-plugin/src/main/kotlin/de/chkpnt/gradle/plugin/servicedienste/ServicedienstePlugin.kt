/*
 * Copyright 2020 Gregor Dschung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.chkpnt.gradle.plugin.servicedienste

import de.undercouch.gradle.tasks.download.Download
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.TaskProvider

class ServicedienstePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.pluginManager
            .apply(BasePlugin::class.java)

        var extensionKnownNumbers = project.extensions
            .create(EXTENSION_NAME_KNOWN_NUMBERS, KnownNumbersExtension::class.java, project)

        // DSL
        val extensionDsl = project.extensions
            .create(EXTENSION_NAME_SERVICEDIENSTE_DSL, ServicediensteExtension::class.java, project)
        val downloadDslPdfTask = registerDownloadTask(project, TASK_NAME_DOWNLOAD_DSL_PDF, extensionDsl)
        val convertDslTask = registerConvertTask(project, TASK_NAME_CONVERT_DSL, extensionDsl, extensionKnownNumbers)
        convertDslTask.configure { it.dependsOn(downloadDslPdfTask) }

        // Mobilfunk
        val extensionMobilfunk = project.extensions
            .create(EXTENSION_NAME_SERVICEDIENSTE_MOBILFUNK, ServicediensteExtension::class.java, project)
        val downloadDslMobilfunkTask =
            registerDownloadTask(project, TASK_NAME_DOWNLOAD_MOBILFUNK_PDF, extensionMobilfunk)
        val convertMobilfunkTask = registerConvertTask(project, TASK_NAME_CONVERT_MOBILFUNK, extensionMobilfunk, extensionKnownNumbers)
        convertMobilfunkTask.configure { it.dependsOn(downloadDslMobilfunkTask) }

        project.tasks.register(TASK_NAME_CONVERT_ALL) { task ->
            task.group = GROUP_NAME_CONVERSION
            task.description = "Converts all Servicedienste-PDFs from 1&1 into machine readable formats"

            task.dependsOn(convertDslTask, convertMobilfunkTask)
        }
    }

    private fun registerDownloadTask(
        project: Project,
        taskName: String,
        extension: ServicediensteExtension
    ): TaskProvider<Download> {
        return project.tasks.register(taskName, Download::class.java) { task ->
            task.group = GROUP_NAME_DOWNLOADS

            task.src(extension.sourceUrl)
            task.dest(extension.downloadToFile)
            task.onlyIfModified(true)
        }
    }

    private val fritzboxPhonebookStartingContactIds = mapOf(
        TASK_NAME_CONVERT_DSL to 10001,
        TASK_NAME_CONVERT_MOBILFUNK to 20001
    )

    fun registerConvertTask(
        project: Project,
        taskName: String,
        extension: ServicediensteExtension,
        knownNumbersExtension: KnownNumbersExtension
    ): TaskProvider<ServicediensteConversionTask> {
        return project.tasks.register(taskName, ServicediensteConversionTask::class.java) { task ->
            task.group = GROUP_NAME_CONVERSION

            task.sourceUrl.set(extension.sourceUrl)
            task.pdf.set(extension.downloadTo)
            task.jsonExportFile.set(extension.jsonExportFile)
            task.ldapOrganizationalUnitName.set(extension.ldapOrganizationalUnitName)
            task.ldapLdifFile.set(extension.ldapLdifFile)
            task.fritzboxPhonebookName.set(extension.fritzboxPhonebookName)
            task.fritzboxPhonebookStartingContactId.set(fritzboxPhonebookStartingContactIds.getOrDefault(taskName, 1))
            task.fritzboxPhonebookFile.set(extension.fritzboxPhonebookFile)
            task.knownNumbers.set(knownNumbersExtension.knownNumbers)
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
        private val EXTENSION_NAME_KNOWN_NUMBERS = "knownNumbers"
    }
}
