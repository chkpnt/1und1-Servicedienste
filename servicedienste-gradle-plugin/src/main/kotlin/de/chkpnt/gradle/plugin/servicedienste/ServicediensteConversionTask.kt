package de.chkpnt.gradle.plugin.servicedienste

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.gradle.api.DefaultTask
import org.gradle.api.logging.LogLevel
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.net.URL
import java.nio.file.*
import java.security.MessageDigest


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
        return "Generate ${jsonExportFilePath} based on '${fileNameFromUrl}' from ${url.host}"
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