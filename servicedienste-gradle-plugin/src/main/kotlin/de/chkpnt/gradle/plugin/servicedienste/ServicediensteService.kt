package de.chkpnt.gradle.plugin.servicedienste

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.nio.file.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter

interface ServicediensteService {
    fun loadPdf(pdf: Path): Servicedienste
}

class DefaultServicediensteService: ServicediensteService {

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

            return Servicedienste(
                    asOfDate = asOfDate,
                    phoneNumbers = phoneNumbers.sortedBy { it.phoneNumber }
            )
        }
    }

    private fun extractAsOfDate(line: String): LocalDate? {
        val match = asOfDateRegex.find(line) ?: return null
        val (asOfDate) = match.destructured
        return LocalDate.parse(asOfDate, germanDateFormatter)
    }

    private fun addPhoneNumberTo(list: MutableList<Servicedienst>, line: String) {
        val match = chargedPhoneNumberRegex.find(line) ?: return
        val (phoneNumber, chargedFrom) = match.destructured
        val chargedFromDate = LocalDate.parse(chargedFrom, germanDateFormatter)
        list.add(Servicedienst(phoneNumber, chargedFromDate))
    }

}