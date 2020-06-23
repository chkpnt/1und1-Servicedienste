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
            Servicedienst(phoneNumber = "040808081", chargedSince = LocalDate.parse("2011-05-04")),
            Servicedienst(phoneNumber = "0694005900", chargedSince = LocalDate.parse("2011-05-04"))
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
        sut.pdf.set(fs.getPath("Rufnummernliste.pdf"))
        sut.jsonExportFile.set(fs.getPath("Rufnummernliste.json"))
        sut.fritzboxPhonebookName.set("1&1 Servicedienste")
        sut.fritzboxPhonebookStartingContactId.set(10001)
        sut.fritzboxPhonebookFile.set(fs.getPath("Phonebook.xml"))
        sut.sourceUrl.set("https://1und1.de/Rufnummernliste.pdf")

        sut.convert()

        verify { servicediensteService.loadPdf(fs.getPath("Rufnummernliste.pdf")) }

        val actualJson = String(Files.readAllBytes(fs.getPath("Rufnummernliste.json")))
        assertThat(actualJson).isEqualTo(
            """
            {
              "sourceUrl" : "https://1und1.de/Rufnummernliste.pdf",
              "sourceSha256" : "49ee2bf93aac3b1fb4117e59095e07abe555c3383b38d608da37680a406096e8",
              "asOfDate" : "2020-04-01",
              "phoneNumbers" : [ {
                "phoneNumber" : "040808081",
                "chargedSince" : "2011-05-04"
              }, {
                "phoneNumber" : "0694005900",
                "chargedSince" : "2011-05-04"
              } ]
            }
            """.trimIndent()
        )

        val actualPhonebook = String(Files.readAllBytes(fs.getPath("Phonebook.xml")))
        assertThat(actualPhonebook).isEqualTo(
            """
            <phonebooks>
                <phonebook owner="0" name="1&amp;1 Servicedienste">
                    <contact>
                        <category>0</category>
                        <person>
                            <realName>040808081</realName>
                        </person>
                        <telephony nid="1">
                            <number type="work" prio="1" id="0">040808081</number>
                        </telephony>
                        <services/>
                        <setup/>
                        <features doorphone="0"/>
                        <mod_time>1304460000</mod_time>
                        <uniqueid>10001</uniqueid>
                    </contact>
                    <contact>
                        <category>0</category>
                        <person>
                            <realName>0694005900</realName>
                        </person>
                        <telephony nid="1">
                            <number type="work" prio="1" id="0">0694005900</number>
                        </telephony>
                        <services/>
                        <setup/>
                        <features doorphone="0"/>
                        <mod_time>1304460000</mod_time>
                        <uniqueid>10002</uniqueid>
                    </contact>
                </phonebook>
            </phonebooks>
            """.trimIndent()
        )
    }

    @Test
    fun `if no phoneNumbers can be extracted an exception is thrown`() {
        every { servicediensteService.loadPdf(any()) } returns emptyServicedienste
        sut.pdf.set(fs.getPath("Rufnummernliste.pdf"))
        sut.jsonExportFile.set(fs.getPath("Rufnummernliste.json"))
        sut.sourceUrl.set("https://1und1.de/Rufnummernliste.pdf")

        val exception = assertThrows(TaskExecutionException::class.java) {
            sut.convert()
        }
        val cause = exception.cause as IllegalArgumentException
        assertThat(cause.message).isEqualTo("Failed to extract phone numbers from Rufnummernliste.pdf")
    }
}
