package com.kazurayam.wgetlog;

import org.gradle.api.Plugin;

public class WgetLogDigestPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.tasks.register("WgetLogDigestPlugin") {

        };
    }
}
