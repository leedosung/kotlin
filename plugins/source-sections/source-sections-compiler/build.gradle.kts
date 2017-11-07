
description = "Kotlin SourceSections Compiler Plugin"

apply { plugin("kotlin") }

configureIntellijPlugin()

dependencies {
    compileOnly(project(":compiler:frontend"))
    compileOnly(project(":compiler:frontend.script"))
    compileOnly(project(":compiler:plugin-api"))
    testCompile(project(":compiler:frontend"))
    testCompile(project(":compiler:frontend.script"))
    testCompile(project(":compiler:plugin-api"))
    testCompile(project(":compiler:util"))
    testCompile(project(":compiler:cli"))
    testCompile(project(":compiler:cli-common"))
    testCompile(project(":compiler:frontend.java"))
    testCompile(project(":compiler:daemon-common"))
    testCompile(project(":kotlin-daemon-client"))
    testCompile(projectTests(":compiler:tests-common"))
    testCompile(commonDep("junit:junit"))
}

afterEvaluate {
    dependencies {
        testCompile(intellij { include("idea.jar", "idea_rt.jar", "openapi.jar") })
    }
}

sourceSets {
    "main" { projectDefault() }
    "test" { projectDefault() }
}

projectTest {
    workingDir = rootDir
}

runtimeJar()
sourcesJar()
javadocJar()

dist()

publish()
