package com.kazurayam.wgetlog

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property

abstract class DigesterExtension {
    abstract RegularFileProperty getWgetLogFile()
    abstract Property<String> getLogEncoding()
    abstract RegularFileProperty getOutputFile()
}
