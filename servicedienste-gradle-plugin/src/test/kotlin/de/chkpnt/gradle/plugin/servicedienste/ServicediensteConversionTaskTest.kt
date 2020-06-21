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

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.nio.file.FileSystem
import java.nio.file.Files
import java.time.LocalDate
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ServicediensteConversionTaskTest {

    private lateinit var sut: ServicediensteConversionTask
    private lateinit var fs: FileSystem
    private lateinit var servicediensteService: ServicediensteService

    private val servicedienste = Servicedienste(
            sourceUrl = "https://1und1.de/Rufnummernliste.pdf",
            sourceSha256 = "49ee2bf93aac3b1fb4117e59095e07abe555c3383b38d608da37680a406096e8",
            asOfDate = LocalDate.parse("2020-04-01"),
            phoneNumbers = listOf(
                    Servicedienst(phoneNumber = "040808081", chargedFrom = LocalDate.parse("2011-05-04")),
                    Servicedienst(phoneNumber = "0694005900", chargedFrom = LocalDate.parse("2011-05-04"))
            )
    )

    private val emptyServicedienste = Servicedienste(
            sourceUrl = "https://1und1.de/Rufnummernliste.pdf",
            sourceSha256 = "49ee2bf93aac3b1fb4117e59095e07abe555c3383b38d608da37680a406096e8",
            asOfDate = null,
            phoneNumbers = emptyList()
    )

    @BeforeEach
    fun setup() {
        fs = Jimfs.newFileSystem(Configuration.unix())
        Files.write(fs.getPath("Rufnummernliste.pdf"), byteArrayOf(0x11, 0x22, 0x33))

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
        sut.sourceUrl.set("https://1und1.de/Rufnummernliste.pdf")

        sut.convert()

        verify { servicediensteService.loadPdf(fs.getPath("Rufnummernliste.pdf")) }

        val actualJson = String(Files.readAllBytes(fs.getPath("Rufnummernliste.json")))
        assertThat(actualJson).isEqualTo("""
            {
              "sourceUrl" : "https://1und1.de/Rufnummernliste.pdf",
              "sourceSha256" : "49ee2bf93aac3b1fb4117e59095e07abe555c3383b38d608da37680a406096e8",
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
        sut.sourceUrl.set("https://1und1.de/Rufnummernliste.pdf")

        val exception = assertThrows(TaskExecutionException::class.java) {
            sut.convert()
        }
        val cause = exception.cause as IllegalArgumentException
        assertThat(cause.message).isEqualTo("Failed to extract phone numbers from Rufnummernliste.pdf")
    }
}
