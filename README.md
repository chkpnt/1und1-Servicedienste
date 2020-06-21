# 1&1 Servicedienste
<!-- markdown-link-check-disable -->
[![License](https://img.shields.io/github/license/chkpnt/1und1-Servicedienste.svg?label=License)](https://tldrlegal.com/license/apache-license-2.0-(apache-2.0)) 
[![auto-updating data](https://github.com/chkpnt/1und1-Servicedienste/workflows/auto-updating%20data/badge.svg)](https://github.com/chkpnt/1und1-Servicedienste/actions?query=workflow%3A%22auto-updating+data%22)
[![servicedienste-gradle-plugin](https://github.com/chkpnt/1und1-Servicedienste/workflows/servicedienste-gradle-plugin/badge.svg)](https://github.com/chkpnt/1und1-Servicedienste/actions?query=workflow%3Aservicedienste-gradle-plugin)
[![SonarQube](https://img.shields.io/badge/SonarQube-sonar.chkpnt.de-blue.svg)](https://sonar.chkpnt.de/dashboard?id=servicedienste-gradle-plugin)
<!-- markdown-link-check-enable -->

Although your 1&1 DSL or mobile phone contract includes a telephone flat rate, which allows you to make free calls
to the German fixed network, you [will be charged](https://hilfe-center.1und1.de/rechnung-c85326/rechnungspositionen-c85331/berechnung-von-service--und-konferenzdiensten-a793873.html)
if you call a number listed in special exclusion list.

Unfortunately, those lists are only available as PDFs:
* [Rufnummernliste Service- und Konferenzdienste für DSL-Verträge](https://hilfe-center.1und1.de/bin_dea/article/793873/DSL_Rufnummernliste_Service_und_Konferenzdienste.pdf)
* [Rufnummernliste Service- und Konferenzdienste für Mobilfunkverträge](https://hilfe-center.1und1.de/bin_dea/article/793873/Mobile_Rufnummernliste_Service_und_Konferenzdienste.pdf)

Therefore, this repository exists to provide machine-readable versions of these files:
* [servicedienste-dsl.json](servicedienste-dsl.json)
* [servicedienste-mobilfunk.json](servicedienste-mobilfunk.json)

They are updated automatically.

## License

For the code in this repository: Apache-2.0

(Probably not for the generated JSONs)
