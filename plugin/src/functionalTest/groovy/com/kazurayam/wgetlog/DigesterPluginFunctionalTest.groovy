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

    private File outputFile = projectDir.toPath().resolve('build/digested.txt').toFile()

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

def log = project.file("${inputFile.toString()}")    
def output = project.file("${outputFile.toString()}")

logger.lifecycle("output at ${outputFile}")

tasks.register('digest', com.kazurayam.wgetlog.DigesterTask) {
    wgetLogFile = log
    logEncoding = "Shift_JIS"
    outputFile = output
}
"""
        when:
        GradleRunner runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("digest")
        runner.withProjectDir(projectDir)
        BuildResult result = runner.build()

        then:
        outputFile.exists()
        result.getOutput().contains("output at")
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
        return inputFile.toFile()
    }
}
