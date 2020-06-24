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
import java.time.LocalDate
import org.junit.jupiter.api.Test

internal class ToLdifConverterTest {

    private val sut = ToLdifConverter()

    @Test
    fun `test serialization`() {
        val servicedienste = Servicedienste(
            sourceUrl = "https://1und1.de/Rufnummernliste.pdf",
            sourceSha256 = "49ee2bf93aac3b1fb4117e59095e07abe555c3383b38d608da37680a406096e8",
            asOfDate = LocalDate.parse("2020-04-01"),
            phoneNumbers = listOf(
                Servicedienst(phoneNumber = "040808081", chargedSince = LocalDate.parse("2011-05-04")),
                Servicedienst(phoneNumber = "0694005900", chargedSince = LocalDate.parse("2011-05-04"))
            )
        )

        val ldif = sut.convert(servicedienste, "1&1 Servicedienste")

        assertThat(ldif).isEqualTo(
            """
            # Generated by https://github.com/chkpnt/1und1-Servicedienste
            # based on https://1und1.de/Rufnummernliste.pdf
            # with sha256 49ee2bf93aac3b1fb4117e59095e07abe555c3383b38d608da37680a406096e8
            
            dn: dc=servicedienste,dc=1und1,dc=de
            objectClass: organizationalUnit
            objectClass: dcObject
            ou: 1&1 Servicedienste
            dc: servicedienste
            description: Calls to the numbers included in this organisation
             are charged by 1&1 as of 2020-04-01.

            dn: ou=040808081,dc=servicedienste,dc=1und1,dc=de
            objectClass: organizationalUnit
            ou: 040808081
            telephoneNumber: 040808081
            description: charged since 2011-05-04
            
            dn: ou=0694005900,dc=servicedienste,dc=1und1,dc=de
            objectClass: organizationalUnit
            ou: 0694005900
            telephoneNumber: 0694005900
            description: charged since 2011-05-04
            """.trimIndent()
        )
    }
}
