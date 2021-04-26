plugins {
    id("de.chkpnt.servicedienste")
    id("org.ajoberstar.git-publish") version "3.0.0"
}

servicediensteDsl {
    sourceUrl("https://hilfe-center.1und1.de/bin_dea/article/793873/DSL_Rufnummernliste_Service_und_Konferenzdienste.pdf")
    downloadTo("DSL_Rufnummernliste_Service_und_Konferenzdienste.pdf")

    jsonExportFile("1und1-Servicedienste-DSL.json")

    ldapOrganizationalUnitName("1&1 Servicedienste")
    ldapLdifFile("1und1-Servicedienste-DSL.ldif")

    fritzboxPhonebookName("1&1 Servicedienste (DSL)")
    fritzboxPhonebookFile("1und1-Servicedienste-DSL-FritzBox-Phonebook.xml")
}

servicediensteMobilfunk {
    sourceUrl("https://hilfe-center.1und1.de/bin_dea/article/793873/Mobile_Rufnummernliste_Service_und_Konferenzdienste.pdf")
    downloadTo("Mobile_Rufnummernliste_Service_und_Konferenzdienste.pdf")

    jsonExportFile("1und1-Servicedienste-Mobilfunk.json")

    ldapOrganizationalUnitName("1&1 Servicedienste")
    ldapLdifFile("1und1-Servicedienste-Mobilfunk.ldif")

    fritzboxPhonebookName("1&1 Servicedienste (Mobilfunk)")
    fritzboxPhonebookFile("1und1-Servicedienste-Mobilfunk-FritzBox-Phonebook.xml")
}

// Only already public available data should be listed here:
knownNumbers {
    number("069257367300", belongsTo = "GoToMeeting")  // https://www.wemgehoert.de/nummer/069257367300
}

gitPublish {
    repoUri.set("git@github.com:chkpnt/1und1-Servicedienste.git")
    branch.set("master")
    commitMessage.set("Update 1&1 Servicedienste")
    preserve { include("**/*") }
    contents {
        from("$buildDir/1und1/") {
            include("1und1-Servicedienste-*.json")
            include("1und1-Servicedienste-*.ldif")
            include("1und1-Servicedienste-*.xml")
        }
        into("1und1")
    }
}