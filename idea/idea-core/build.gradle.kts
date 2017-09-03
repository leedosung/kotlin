apply { plugin("kotlin") }

dependencies {
    compile(project(":compiler:frontend.script"))
    compile(project(":compiler:light-classes"))
    compile(project(":idea:ide-common"))
    compile(preloadedDeps("kotlinx-coroutines-core", "kotlinx-coroutines-jdk8"))

    if (System.getProperty("build.for.clion") != "true") {
        compile(project(":j2k"))
        compile(project(":idea:idea-jps-common"))
        compile(project(":android-extensions-compiler"))

        compile(ideaSdkCoreDeps("intellij-core", "util"))
        compile(ideaSdkDeps("openapi", "idea"))
        compile(ideaPluginDeps("gradle-tooling-api", "gradle", plugin = "gradle"))
    } else {
        compile(preloadedDeps("java-api", "java-impl"))
        compile(clionSdkDeps("openapi", "clion"))
    }
}

sourceSets {
    "main" {
        projectDefault()
        java.srcDir("../idea-analysis/src")
        resources.srcDir("../idea-analysis/src").apply { include("**/*.properties") }
    }
    "test" {}
}