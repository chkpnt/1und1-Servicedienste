plugins {
    `java-gradle-plugin`
    jacoco

    id("org.jetbrains.kotlin.jvm") version "1.3.70"
    id("com.diffplug.gradle.spotless") version "4.4.0"
    id("org.sonarqube") version "3.0"
}

repositories {
    jcenter()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.apache.pdfbox:pdfbox:2.0.20")
    implementation("org.apache.commons:commons-text:1.8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.11.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.11.0")
    implementation("de.undercouch:gradle-download-task:4.0.4")

    testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.22")
    testImplementation("io.mockk:mockk:1.10.0")
    testImplementation("com.google.jimfs:jimfs:1.1") // In-Memory Filesystem
}

gradlePlugin {
    // Define the plugin
    val servicedienste by plugins.creating {
        id = "de.chkpnt.servicedienste"
        implementationClass = "de.chkpnt.gradle.plugin.servicedienste.ServicedienstePlugin"
    }
}

spotless {
    kotlin {
        ktlint()
        licenseHeaderFile("gradle/formatter/spotless.license.txt")
    }
}

sonarqube {
    properties {
        property("sonar.host.url", "https://sonar.chkpnt.de")
        property("sonar.login", System.getenv("SONARQUBE_TOKEN"))
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
        property("sonar.jacoco.reportPath", "")
    }
}

tasks {

    test {
        useJUnitPlatform()
        finalizedBy(jacocoTestReport)
    }

    jacocoTestReport {
        reports {
            xml.isEnabled = true
            html.isEnabled = true
        }
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "11"
    }
}

