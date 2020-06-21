package de.chkpnt.gradle.plugin.servicedienste

import java.time.LocalDate

// 1&1 calls it "Servicedienste". Don't know an appropriate English term. :-/
data class Servicedienste(
        var sourceUrl: String? = null,
        var sourceSha256: String? = null,
        val asOfDate: LocalDate?,
        val phoneNumbers: List<Servicedienst>
)

data class Servicedienst(
        val phoneNumber: String,
        val chargedFrom: LocalDate
)

