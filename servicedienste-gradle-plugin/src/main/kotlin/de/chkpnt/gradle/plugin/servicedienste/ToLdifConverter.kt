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

import java.util.stream.Collectors.joining

class ToLdifConverter {

    fun convert(servicedienste: Servicedienste, organizationUnit: String): String {

        val contactElements = servicedienste.phoneNumbers.stream()
            .map(this::contactElement)
            .collect(joining("\n\n"))

        val header = """
            # Generated by https://github.com/chkpnt/1und1-Servicedienste
            # based on ${servicedienste.sourceUrl}
            # with sha256 ${servicedienste.sourceSha256}

            dn: dc=servicedienste,dc=1und1,dc=de
            objectClass: organizationalUnit
            objectClass: dcObject
            ou: $organizationUnit
            dc: servicedienste
            description: Calls to the numbers included in this organisation
             are charged by 1&1 as of ${servicedienste.asOfDate}.

${contactElements.prependIndent("            ")}
        """.trimIndent()
        return header
    }

    private fun contactElement(servicedienst: Servicedienst): String {
        val number = servicedienst.phoneNumber
        val numberBelongsTo = servicedienst.description
        if (numberBelongsTo != null) {
            return """
                dn: ou=$number,dc=servicedienste,dc=1und1,dc=de
                objectClass: organizationalUnit
                objectClass: organization
                o: $numberBelongsTo
                ou: $number
                telephoneNumber: $number
                description: charged since ${servicedienst.chargedSince}
                """.trimIndent()
        } else {
            return """
                dn: ou=$number,dc=servicedienste,dc=1und1,dc=de
                objectClass: organizationalUnit
                ou: $number
                telephoneNumber: $number
                description: charged since ${servicedienst.chargedSince}
                """.trimIndent()
        }
    }
}
