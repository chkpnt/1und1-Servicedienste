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

import java.nio.file.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper

interface ServicediensteService {
    fun loadPdf(pdf: Path): Servicedienste
}

class DefaultServicediensteService : ServicediensteService {

    private val germanDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private val germanDateRegexString = """\d\d\.\d\d\.\d\d\d\d"""
    private val asOfDateRegex = """Stand:\s+($germanDateRegexString)""".toRegex()
    private val chargedPhoneNumberRegex = """^(\d+)\s+($germanDateRegexString)""".toRegex()

    override fun loadPdf(pdf: Path): Servicedienste {
        PDDocument.load(pdf.toFile()).use {
            val stripper = PDFTextStripper()
            val text = stripper.getText(it)

            var asOfDate: LocalDate? = null
            var phoneNumbers = mutableListOf<Servicedienst>()

            for (line in text.lineSequence()) {
                if (asOfDate == null) {
                    asOfDate = extractAsOfDate(line)
                    continue
                }
                addPhoneNumberTo(phoneNumbers, line)
            }

            return Servicedienste(asOfDate = asOfDate, phoneNumbers = phoneNumbers)
        }
    }

    private fun extractAsOfDate(line: String): LocalDate? {
        val match = asOfDateRegex.find(line) ?: return null
        val (asOfDate) = match.destructured
        return LocalDate.parse(asOfDate, germanDateFormatter)
    }

    private fun addPhoneNumberTo(list: MutableList<Servicedienst>, line: String) {
        val match = chargedPhoneNumberRegex.find(line) ?: return
        val (phoneNumber, chargedSince) = match.destructured
        val chargedSinceDate = LocalDate.parse(chargedSince, germanDateFormatter)
        list.add(Servicedienst(phoneNumber, chargedSince = chargedSinceDate))
    }
}
