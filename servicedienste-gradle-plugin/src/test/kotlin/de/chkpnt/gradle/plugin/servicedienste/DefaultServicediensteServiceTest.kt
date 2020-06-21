package de.chkpnt.gradle.plugin.servicedienste

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Paths
import java.time.LocalDate

class ServicediensteServiceTest {

    private lateinit var sut: ServicediensteService

    @BeforeEach
    fun setup() {
        sut = DefaultServicediensteService()
    }

    @Test
    fun bla() {
        val pdfFile = Paths.get(javaClass.classLoader.getResource("Rufnummernliste.pdf").file)

        val servicedienste = sut.loadPdf(pdfFile)

        assertThat(servicedienste.asOfDate).isEqualTo(LocalDate.parse("2020-05-11"))
        assertThat(servicedienste.phoneNumbers).hasSize(4935)
        assertThat(servicedienste.phoneNumbers.first()).isEqualTo(Servicedienst("020125879359", LocalDate.parse("2012-09-27")))
        assertThat(servicedienste.phoneNumbers.last()).isEqualTo(Servicedienst("097793273030", LocalDate.parse("2014-03-28")))
    }

}