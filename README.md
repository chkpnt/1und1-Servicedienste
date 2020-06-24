# 1&1 Servicedienste
<!-- markdown-link-check-disable -->
[![License](https://img.shields.io/github/license/chkpnt/1und1-Servicedienste.svg?label=License)](https://tldrlegal.com/license/apache-license-2.0-(apache-2.0)) 
[![auto-updating data](https://github.com/chkpnt/1und1-Servicedienste/workflows/auto-updating%20data/badge.svg)](https://github.com/chkpnt/1und1-Servicedienste/actions?query=workflow%3A%22auto-updating+data%22)
[![servicedienste-gradle-plugin](https://github.com/chkpnt/1und1-Servicedienste/workflows/servicedienste-gradle-plugin/badge.svg)](https://github.com/chkpnt/1und1-Servicedienste/actions?query=workflow%3Aservicedienste-gradle-plugin)
[![SonarQube](https://img.shields.io/badge/SonarQube-sonar.chkpnt.de-blue.svg)](https://sonar.chkpnt.de/dashboard?id=servicedienste-gradle-plugin)
<!-- markdown-link-check-enable -->

Although your 1&1 DSL or mobile phone contract includes a telephone flat rate, which allows you to make free calls
to the German fixed network, you [will be charged](https://hilfe-center.1und1.de/rechnung-c85326/rechnungspositionen-c85331/berechnung-von-service--und-konferenzdiensten-a793873.html)
if you call a number listed in a special exclusion list.

Unfortunately, these lists are only available as PDFs:
* [Rufnummernliste Service- und Konferenzdienste für DSL-Verträge](https://hilfe-center.1und1.de/bin_dea/article/793873/DSL_Rufnummernliste_Service_und_Konferenzdienste.pdf)
* [Rufnummernliste Service- und Konferenzdienste für Mobilfunkverträge](https://hilfe-center.1und1.de/bin_dea/article/793873/Mobile_Rufnummernliste_Service_und_Konferenzdienste.pdf)

Therefore, this repository exists to provide machine-readable versions of these files:
* [1und1-Servicedienste-DSL.json](1und1/1und1-Servicedienste-DSL.json)
* [1und1-Servicedienste-Mobilfunk.json](1und1/1und1-Servicedienste-Mobilfunk.json)

Some LDIF-files for your LDAP-directory are generated, too:
* [1und1-Servicedienste-DSL.ldif](1und1/1und1-Servicedienste-DSL.ldif)
* [1und1-Servicedienste-Mobilfunk.ldif](1und1/1und1-Servicedienste-Mobilfunk.ldif)

All files are updated automatically by a schedules GitHub workflow.

## FRITZ!Box Phonebooks 

Phonebook-XMLs are generated, too:
* [1und1-Servicedienste-DSL-FritzBox-Phonebook.xml](1und1/1und1-Servicedienste-DSL-FritzBox-Phonebook.xml)
* [1und1-Servicedienste-Mobilfunk-FritzBox-Phonebook.xml](1und1/1und1-Servicedienste-Mobilfunk-FritzBox-Phonebook.xml)

Unfortunately, they are rather useless for two reasons:
1. The Phonebook-XMLs contains too many contacts. For example on a FRITZ!Box 7590, a phonebook can contain at most 2000 entries.
1. Even if you could import the XML: A phonebook can only be used to block incoming calls, but not outgoing call.

## License

For the code in this repository: Apache-2.0

(Probably not for the generated JSONs)
