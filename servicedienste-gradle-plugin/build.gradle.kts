plugins {
    // Apply the Java Gradle plugin development plugin to add support for developing Gradle plugins
    `java-gradle-plugin`

    // Apply the Kotlin JVM plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.3.70"
    id("com.diffplug.gradle.spotless") version "4.4.0"
}

repositories {
    jcenter()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.apache.pdfbox:pdfbox:2.0.20")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.11.0")
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

tasks {

    test {
        useJUnitPlatform()
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "1.8"
    }
}

