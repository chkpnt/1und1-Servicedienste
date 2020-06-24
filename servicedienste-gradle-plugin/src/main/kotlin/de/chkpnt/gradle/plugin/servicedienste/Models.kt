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
    val chargedSince: LocalDate
)

fun Servicedienste.sortAndDistinct() = Servicedienste(
    sourceUrl = this.sourceUrl,
    sourceSha256 = this.sourceSha256,
    asOfDate = this.asOfDate,
    phoneNumbers = phoneNumbers
        .sortedWith(compareBy(Servicedienst::phoneNumber, Servicedienst::chargedSince))
        .distinctBy(Servicedienst::phoneNumber)
)