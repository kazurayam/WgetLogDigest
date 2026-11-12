package com.kazurayam.wgetlog

import org.gradle.testkit.runner.BuildResult
import spock.lang.Specification
import org.gradle.testkit.runner.GradleRunner

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**¶
 * A simple functional test for the 'com.kazurayam.wget-log-digest' plugin.
 */
class DigesterPluginFunctionalTest extends Specification {

    private File projectDir = newProjectDir('adding-digest-task')

    private File inputFile = getInputFile('issue15-wget.log')

    private getBuildFile() {
        new File(projectDir, "build.gradle")
    }

    private getSettingsFile() {
        new File(projectDir, "settings.gradle")
    }

    def "can run wgetLogDigest task"() {
        given:
        settingsFile << "rootProject.name = 'functional-test-project'"
        buildFile << """                                             
plugins {                                                            
    id('com.kazurayam.wget-log-digest-plugin')                                       
}

tasks.register('demonstrate') {
    def output = layout.buildDirectory.file("tmp/functionalTest/straight/output.txt")
    wgetLogDigest {
        wgetLogFile = project.file("${inputFile.toString()}")
        logEncoding = "Shift_JIS"
        outputFile = output
    }
    doLast {
        println "output at " + output.get()
        assert output.get().asFile.exists()
    }
}                                                                   
"""
        when:
        GradleRunner runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("demonstrate", '--stacktrace')
        runner.withProjectDir(projectDir)
        BuildResult result = runner.build()

        then:
        result.output.contains("output at ")
    }

    private static File newProjectDir(String testCase) {
        String outputRoot = System.getProperty('functionalTestOutputDir')
        if (!outputRoot) {
            throw new IllegalStateException('functionalTestOutputDirが設定されていません')
        }
        File dir = new File(outputRoot, "${getClass().simpleName}/${testCase}-${UUID.randomUUID()}")
        if (!dir.mkdirs()) {
            throw new IllegalStateException('ディレクトリを作成できません: ${dir}')
        }
        println "Functional testproject: ${dir.absolutePath}"
        return dir
    }

    private static File getInputFile(String subpath) {
        String fixturesDir = System.getProperty('functionalTestFixturesDir')
        if (!fixturesDir) {
            throw new IllegalStateException('functionalTestFixturesDirが設定されていません')
        }
        Path inputFile = Paths.get(fixturesDir).resolve(subpath)
        if (!Files.exists(inputFile)) {
            throw new IllegalStateException('inputFileが存在しません: ${inputFile}')
        }
        println "Functional test inputFile: ${inputFile.toAbsolutePath()}"
        return inputFile.toFile()
    }
}
