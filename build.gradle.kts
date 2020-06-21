plugins {
    id("de.chkpnt.servicedienste")
}

servicediensteDsl {
    sourceUrl = "https://hilfe-center.1und1.de/bin_dea/article/793873/DSL_Rufnummernliste_Service_und_Konferenzdienste.pdf"
    downloadTo = "build/1und1/DSL_Rufnummernliste_Service_und_Konferenzdienste.pdf"
    jsonExportFile = "servicedienste-dsl.json"
}

servicediensteMobilfunk {
    sourceUrl = "https://hilfe-center.1und1.de/bin_dea/article/793873/Mobile_Rufnummernliste_Service_und_Konferenzdienste.pdf"
    downloadTo = "build/1und1/Mobile_Rufnummernliste_Service_und_Konferenzdienste.pdf"
    jsonExportFile = "servicedienste-mobilfunk.json"
}
