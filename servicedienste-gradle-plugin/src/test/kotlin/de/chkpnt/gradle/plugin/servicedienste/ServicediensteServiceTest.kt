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
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import java.nio.file.Paths
import java.time.LocalDate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ServicediensteServiceTest {

    private lateinit var sut: ServicediensteService

    @BeforeEach
    fun setup() {
        sut = DefaultServicediensteService()
    }

    @Test
    fun `test loadPdf`() {
        val pdfFile = Paths.get(javaClass.classLoader.getResource("Rufnummernliste.pdf").file)

        val servicedienste = sut.loadPdf(pdfFile)

        assertThat(servicedienste.asOfDate).isEqualTo(LocalDate.parse("2020-05-11"))
        assertThat(servicedienste.phoneNumbers).hasSize(4935)
        assertThat(servicedienste.phoneNumbers.first()).isEqualTo(Servicedienst("040808081", LocalDate.parse("2011-05-04")))
        assertThat(servicedienste.phoneNumbers.last()).isEqualTo(Servicedienst("03030809006", LocalDate.parse("2020-05-11")))
    }
}
