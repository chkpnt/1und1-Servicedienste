package de.chkpnt.gradle.plugin.servicedienste

import org.gradle.api.Project

open class ServicediensteExtension(project: Project) {

    var sourceUrl: String? = null
    var downloadTo: String? = null
    var jsonExportFile: String? = null

}