package de.chkpnt.gradle.plugin.servicedienste

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.FileSystem
import java.nio.file.Files
import java.time.LocalDate

class ServicediensteConversionTaskTest {

    private lateinit var sut: ServicediensteConversionTask
    private lateinit var fs: FileSystem
    private lateinit var servicediensteService: ServicediensteService

    private val servicedienste = Servicedienste(
            sourceUrl = "https://1und1.de/Rufnummernliste.pdf",
            sourceSha256 = "hash",
            asOfDate = LocalDate.parse("2020-04-01"),
            phoneNumbers = listOf(
                    Servicedienst(phoneNumber = "040808081", chargedFrom = LocalDate.parse("2011-05-04")),
                    Servicedienst(phoneNumber = "0694005900", chargedFrom = LocalDate.parse("2011-05-04"))
            )
    )

    private val emptyServicedienste = Servicedienste(
            sourceUrl = "https://1und1.de/Rufnummernliste.pdf",
            sourceSha256 = "hash",
            asOfDate = null,
            phoneNumbers = emptyList()
    )

    @BeforeEach
    fun setup() {
        fs = Jimfs.newFileSystem(Configuration.unix())

        servicediensteService = mockk<ServicediensteService>()
        every { servicediensteService.loadPdf(any()) } returns servicedienste

        val project = ProjectBuilder.builder().build()
        sut = project.tasks.create("convertServicedienste", ServicediensteConversionTask::class.java)
        sut.servicediensteService = servicediensteService
        sut.fs = fs
    }

    @Test
    fun `test serialization`() {
        sut.pdf.set("Rufnummernliste.pdf")
        sut.jsonExportFile.set("Rufnummernliste.json")

        sut.convert()

        verify { servicediensteService.loadPdf(fs.getPath("Rufnummernliste.pdf")) }

        val actualJson = String(Files.readAllBytes(fs.getPath("Rufnummernliste.json")))
        assertThat(actualJson).isEqualTo("""
            {
              "sourceUrl" : "https://1und1.de/Rufnummernliste.pdf",
              "sourceSha256" : "hash",
              "asOfDate" : "2020-04-01",
              "phoneNumbers" : [ {
                "phoneNumber" : "040808081",
                "chargedFrom" : "2011-05-04"
              }, {
                "phoneNumber" : "0694005900",
                "chargedFrom" : "2011-05-04"
              } ]
            }
        """.trimIndent())
    }

    @Test
    fun `if no phoneNumbers can be extracted an exception is thrown`() {
        every { servicediensteService.loadPdf(any()) } returns emptyServicedienste
        sut.pdf.set("Rufnummernliste.pdf")
        sut.jsonExportFile.set("Rufnummernliste.json")

        val exception = assertThrows(TaskExecutionException::class.java) {
            sut.convert()
        }
        val cause = exception.cause as IllegalArgumentException
        assertThat(cause.message).isEqualTo("Failed to extract phone numbers from Rufnummernliste.pdf")
    }
}
