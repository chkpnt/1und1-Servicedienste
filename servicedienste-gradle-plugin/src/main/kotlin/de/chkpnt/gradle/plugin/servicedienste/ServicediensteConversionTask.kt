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

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import java.net.URL
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.MessageDigest
import org.gradle.api.DefaultTask
import org.gradle.api.logging.LogLevel
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Console
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException

open class ServicediensteConversionTask() : DefaultTask() {

    @Input
    val sourceUrl: Property<String> = project.objects.property(String::class.java)

    @Internal
    val pdf: Property<String> = project.objects.property(String::class.java)

    val pdfPath: Path
        @InputFile
        get() = fs.getPath(pdf.get())

    @Internal
    val jsonExportFile: Property<String> = project.objects.property(String::class.java)

    val jsonExportFilePath: Path
        @OutputFile
        get() = fs.getPath(jsonExportFile.get())

    @Internal
    var servicediensteService: ServicediensteService = DefaultServicediensteService()

    @Internal
    var fs: FileSystem = FileSystems.getDefault()

    private val mapper: ObjectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

    @Console
    override fun getDescription(): String {
        val url = URL(sourceUrl.get())
        val fileNameFromUrl = Paths.get(url.path).fileName
        return "Generate $jsonExportFilePath based on '$fileNameFromUrl' from ${url.host}"
    }

    @TaskAction
    fun convert() {
        var servicedienste = servicediensteService.loadPdf(pdfPath)
        if (servicedienste.phoneNumbers.isEmpty()) {
            throw TaskExecutionException(this, IllegalArgumentException("Failed to extract phone numbers from $pdfPath"))
        }
        servicedienste.sourceUrl = sourceUrl.get()
        servicedienste.sourceSha256 = pdfPath.sha256()

        val jsonExport = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(servicedienste)
        Files.write(jsonExportFilePath, jsonExport.toByteArray())
        project.logger.log(LogLevel.QUIET, "Generated $jsonExportFilePath")
    }
}

private fun Path.sha256(): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val bytes = Files.readAllBytes(this)
    val hash = digest.digest(bytes)
    return hash.toHexString()
}

private fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }
