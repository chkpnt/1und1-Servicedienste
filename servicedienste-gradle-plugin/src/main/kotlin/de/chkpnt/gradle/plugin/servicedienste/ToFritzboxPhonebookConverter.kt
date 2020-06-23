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

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText
import java.time.ZoneId
import java.util.stream.Collectors.joining
import java.util.stream.IntStream
import org.apache.commons.text.StringEscapeUtils

class ToFritzboxPhonebookConverter {

    fun convert(servicedienste: Servicedienste, phonebookName: String, startingContactId: Int): String {

        // Why to care about the id? -> https://github.com/chkpnt/1und1-Servicedienste/issues/1
        val ids = IntStream.iterate(startingContactId) { n -> n + 1 }.iterator()

        val contactElements = servicedienste.phoneNumbers.stream()
            // .limit(2000) // max 2000 on my FritzBox 7590 (FRITZ!OS 07.12)
            .map { it -> contactElement(it, ids.nextInt()) }
            .collect(joining("\n"))

        return """
            <phonebooks>
                <phonebook owner="0" name="${StringEscapeUtils.escapeXml11(phonebookName)}">
${contactElements.prependIndent("                    ")}
                </phonebook>
            </phonebooks>
            """.trimIndent()
    }

    private fun contactElement(servicedienst: Servicedienst, id: Int): String {
        val number = servicedienst.phoneNumber
        val timestamp = servicedienst.chargedSince.atStartOfDay(ZoneId.of("Europe/Berlin")).toEpochSecond()
        return """
            <contact>
                <category>0</category>
                <person>
                    <realName>$number</realName>
                </person>
                <telephony nid="1">
                    <number type="work" prio="1" id="0">$number</number>
                </telephony>
                <services/>
                <setup/>
                <features doorphone="0"/>
                <mod_time>$timestamp</mod_time>
                <uniqueid>$id</uniqueid>
            </contact>
            """.trimIndent()
    }

    @JacksonXmlRootElement(localName = "phonebooks")
    data class Phonebooks(
        val phonebook: Phonebook
    )

    data class Phonebook(
        @JsonIgnore
        val name: String,

        @JacksonXmlElementWrapper(useWrapping = false)
        val contact: List<Contact>
    ) {
        @JacksonXmlProperty(isAttribute = true)
        val owner: Int = 0

        @JacksonXmlProperty(isAttribute = true, localName = "name")
        val _name: String = name
    }

    data class Contact(
        @JsonIgnore
        val number: String
    ) {
        val category: Int = 0
        val person: Person = Person(number)
        val telephony: Telephony = Telephony(WorkNumber(number))
        val services: Any? = null
        val setup: Any? = null
        val features: Features = Features()
        val mod_time: Int = 123123
        val uniqueid: Int = 25
    }

    data class Person(val realName: String)

    data class Telephony(val number: WorkNumber) {
        @JacksonXmlProperty(isAttribute = true)
        val nid: Int = 1
    }

    @JacksonXmlRootElement(localName = "features")
    data class Features(
        @JsonIgnore
        val text: String = ""
    ) {
        @JacksonXmlProperty(isAttribute = true)
        val doorphone: Int = 0
    }

    @JacksonXmlRootElement(localName = "number")
    data class WorkNumber(
        @JacksonXmlText
        val number: String
    ) {
        @JacksonXmlProperty(isAttribute = true)
        val type: String = "work"

        @JacksonXmlProperty(isAttribute = true)
        val prio: Int = 1

        @JacksonXmlProperty(isAttribute = true)
        val id: Int = 0
    }
}
