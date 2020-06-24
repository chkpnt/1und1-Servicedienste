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

internal class ToFritzboxPhonebookConverterTest {

    private val sut = ToFritzboxPhonebookConverter()

    @Test
    fun `test serialization`() {
        val servicedienste = Servicedienste(
            sourceUrl = "https://1und1.de/Rufnummernliste.pdf",
            sourceSha256 = "49ee2bf93aac3b1fb4117e59095e07abe555c3383b38d608da37680a406096e8",
            asOfDate = LocalDate.parse("2020-04-01"),
            phoneNumbers = listOf(
                Servicedienst(phoneNumber = "040808081", chargedSince = LocalDate.parse("2011-05-04")),
                Servicedienst(phoneNumber = "0694005900", chargedSince = LocalDate.parse("2011-05-04"), description = "Spam")
            )
        )

        val serializedPhonebook = sut.convert(servicedienste, "Konferenzdienste", 10001)

        assertThat(serializedPhonebook).isEqualTo(
            """
            <phonebooks>
                <phonebook owner="0" name="Konferenzdienste">
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
                            <realName>Spam</realName>
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
}
