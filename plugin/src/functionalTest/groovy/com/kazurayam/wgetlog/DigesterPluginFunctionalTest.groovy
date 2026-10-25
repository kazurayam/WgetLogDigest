package com.kazurayam.wgetlog

import org.gradle.BuildResult
import spock.lang.Specification
import spock.lang.TempDir
import org.gradle.testkit.runner.GradleRunner

/**¶
 * A simple functional test for the 'com.kazurayam.wget-log-digest' plugin.
 */
class DigesterPluginFunctionalTest extends Specification {

    @TempDir
    private File projectDir

    private getBuildFile() {
        new File(projectDir, "build.gradle")
    }

    private getSettingsFile() {
        new File(projectDir, "settings.gradle")
    }

    def "can run task"() {
        given:
        settingsFile << ""
        buildFile << """                                             
plugins {                                                            
    id('com.kazurayam.wget-log-digest')                                       
}                                                                   
"""
        when:
        GradleRunner runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("wgetLogDigest")
        runner.withProjectDir(projectDir)
        BuildResult result = runner.build() as BuildResult

        then:
        result.output.contains("find output at ")
    }
}
