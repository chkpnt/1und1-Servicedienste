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

import java.io.File
import java.nio.file.Path
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider

open class ServicediensteExtension(private val project: Project) {

    internal val sourceUrl: Property<String> = project.objects.property(String::class.java)
    internal val downloadTo: Property<Path> = project.objects.property(Path::class.java)
    internal val jsonExportFile: Property<Path> = project.objects.property(Path::class.java)
    internal val ldapOrganizationalUnitName: Property<String> = project.objects.property(String::class.java)
    internal val ldapLdifFile: Property<Path> = project.objects.property(Path::class.java)
    internal val fritzboxPhonebookName: Property<String> = project.objects.property(String::class.java)
    internal val fritzboxPhonebookFile: Property<Path> = project.objects.property(Path::class.java)

    internal val downloadToFile: Provider<File> = downloadTo.map { it.toFile() }

    fun sourceUrl(value: String) = sourceUrl.set(value)
    fun downloadTo(value: String) = downloadTo.set(value.toPath())
    fun jsonExportFile(value: String) = jsonExportFile.set(value.toPath())
    fun ldapOrganizationalUnitName(value: String) = ldapOrganizationalUnitName.set(value)
    fun ldapLdifFile(value: String) = ldapLdifFile.set(value.toPath())
    fun fritzboxPhonebookName(value: String) = fritzboxPhonebookName.set(value)
    fun fritzboxPhonebookFile(value: String) = fritzboxPhonebookFile.set(value.toPath())

    private fun String.toPath(): Path {
        return if (Path.of(this).nameCount == 1) {
            project.buildDir.toPath().resolve("1und1/$this")
        } else {
            project.file(this).toPath()
        }
    }
}
