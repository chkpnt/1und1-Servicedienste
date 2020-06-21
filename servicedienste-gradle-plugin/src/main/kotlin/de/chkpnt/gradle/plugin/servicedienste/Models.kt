package de.chkpnt.gradle.plugin.servicedienste

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

// 1&1 calls it "Servicedienste". Don't know an appropriate English term. :-/
data class Servicedienste(
        val sourceUrl: String? = null,
        val sourceSha256: String? = null,
        val asOfDate: LocalDate?,
        val phoneNumbers: List<Servicedienst>
)

data class Servicedienst(
        val phoneNumber: String,
        val chargedFrom: LocalDate
)

