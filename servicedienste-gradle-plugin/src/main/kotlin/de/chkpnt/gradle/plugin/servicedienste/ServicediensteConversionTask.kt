package de.chkpnt.gradle.plugin.servicedienste

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.nio.file.Path
import java.io.File
import java.nio.file.Files

open class ServicediensteConversionTask() : DefaultTask() {

    @InputFile
    val pdf: Property<Path> = project.objects.property(Path::class.java)

    @OutputFile
    val jsonExportFile: Property<Path> = project.objects.property(Path::class.java)

    @Internal
    var servicediensteService: ServicediensteService = DefaultServicediensteService()

    @Internal
    val mapper: ObjectMapper = ObjectMapper().findAndRegisterModules()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

    @Console
    override fun getDescription(): String {
        val inputDirName = project.projectDir
                .toPath()
                //.relativize(inputDir.get())
                .toString()
        return "Adds all certificates found under '$inputDirName' to the TrustStore."
    }

    @TaskAction
    fun convert() {
        val servicedienste = servicediensteService.loadPdf(pdf.get());
        val jsonExport = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(servicedienste)
        Files.write(jsonExportFile.get(), jsonExport.toByteArray() )
    }

}
