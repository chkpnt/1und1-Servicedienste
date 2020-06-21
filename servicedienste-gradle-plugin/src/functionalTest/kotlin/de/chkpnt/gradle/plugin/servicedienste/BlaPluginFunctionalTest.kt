/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package de.chkpnt.gradle.plugin.servicedienste

import java.io.File
import org.gradle.testkit.runner.GradleRunner

/**
 * A simple functional test for the 'de.chkpnt.gradle.plugin.servicedienste.greeting' plugin.
 */
class BlaPluginFunctionalTest {
    //@Test
    fun `can run task`() {
        // Setup the test build
        val projectDir = File("build/functionalTest")
        projectDir.mkdirs()
        projectDir.resolve("settings.gradle").writeText("")
        projectDir.resolve("build.gradle").writeText("""
            plugins {
                id('de.chkpnt.servicedienste')
            }
        """)

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("greeting")
        runner.withProjectDir(projectDir)
        val result = runner.build();

        // Verify the result
        //assertTrue(result.output.contains("Hello from plugin 'de.chkpnt.gradle.plugin.servicedienste.greeting'"))
    }
}