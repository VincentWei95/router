package com.example.router.plugin;

import com.android.build.gradle.BaseExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class RouterPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getExtensions().getByType(BaseExtension.class)
                .registerTransform(new RouterTransform());
    }
}
