apply { plugin("kotlin") }

configureIntellijPlugin {
    setPlugins("gradle")
    setExtraDependencies("intellij-core")
}

dependencies {
    compile(projectDist(":kotlin-stdlib"))
    compile(project(":core"))
    compile(project(":compiler:frontend"))
    compile(project(":compiler:frontend.java"))
    compile(project(":compiler:frontend.script"))
    compile(project(":compiler:light-classes"))
    compile(project(":compiler:util"))
    compile(project(":j2k"))
    compile(project(":idea:ide-common"))
    compile(project(":idea:idea-jps-common"))
    compile(project(":plugins:android-extensions-compiler"))
    compile(preloadedDeps("kotlinx-coroutines-core", "kotlinx-coroutines-jdk8"))
}

afterEvaluate {
    dependencies {
        compile(intellijCoreJar())
        compile(intellij { include("util.jar", "openapi.jar", "idea.jar") })
        compile(intellijPlugin("gradle") { include("gradle-tooling-api-*.jar", "gradle.jar") })
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
