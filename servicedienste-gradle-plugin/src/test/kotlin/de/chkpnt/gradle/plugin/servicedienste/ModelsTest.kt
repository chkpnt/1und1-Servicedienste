package de.chkpnt.gradle.plugin.servicedienste

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class ModelsTest {

    @Test
    fun `distinct on Servicedienste keeps the oldest numbers only`() {
        val servicedienste = Servicedienste(
            sourceUrl = "https://1und1.de/Rufnummernliste.pdf",
            sourceSha256 = "49ee2bf93aac3b1fb4117e59095e07abe555c3383b38d608da37680a406096e8",
            asOfDate = LocalDate.parse("2020-04-01"),
            phoneNumbers = listOf(
                Servicedienst(phoneNumber = "0694005900", chargedSince = LocalDate.parse("2012-05-04")),
                Servicedienst(phoneNumber = "040808081", chargedSince = LocalDate.parse("2011-05-04")),
                Servicedienst(phoneNumber = "0694005900", chargedSince = LocalDate.parse("2011-05-04")),
                Servicedienst(phoneNumber = "040808081", chargedSince = LocalDate.parse("2012-05-04"))
            )
        )

        val result = servicedienste.sortAndDistinct()

        val expectedServicedienste = Servicedienste(
            sourceUrl = "https://1und1.de/Rufnummernliste.pdf",
            sourceSha256 = "49ee2bf93aac3b1fb4117e59095e07abe555c3383b38d608da37680a406096e8",
            asOfDate = LocalDate.parse("2020-04-01"),
            phoneNumbers = listOf(
                Servicedienst(phoneNumber = "040808081", chargedSince = LocalDate.parse("2011-05-04")),
                Servicedienst(phoneNumber = "0694005900", chargedSince = LocalDate.parse("2011-05-04"))
            )
        )
        assertThat(result).isEqualTo(expectedServicedienste)
    }
}


