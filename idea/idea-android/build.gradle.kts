
apply { plugin("kotlin") }

configureIntellijPlugin {
    setPlugins("android", "copyright", "coverage", "gradle", "Groovy", "IntelliLang",
               "java-decompiler", "java-i18n", "junit", "maven", "properties", "testng")
}

dependencies {
    compile(projectDist(":kotlin-reflect"))
    compile(project(":compiler:util"))
    compile(project(":compiler:light-classes"))
    compile(project(":compiler:frontend"))
    compile(project(":compiler:frontend.java"))
    compile(project(":idea"))
    compile(project(":idea:idea-core"))
    compile(project(":idea:ide-common"))
    compile(project(":idea:idea-gradle"))

    compile(preloadedDeps("dx", subdir = "android-5.0/lib"))

    testCompile(projectDist(":kotlin-test:kotlin-test-jvm"))
    testCompile(project(":idea:idea-test-framework")) { isTransitive = false }
    testCompile(project(":plugins:lint")) { isTransitive = false }
    testCompile(project(":idea:idea-jvm"))
    testCompile(projectTests(":compiler:tests-common"))
    testCompile(projectTests(":idea"))
    testCompile(projectTests(":idea:idea-gradle"))
    testCompile(commonDep("junit:junit"))

    testRuntime(projectDist(":kotlin-compiler"))
    testRuntime(project(":plugins:android-extensions-ide"))
    testRuntime(project(":sam-with-receiver-ide-plugin"))
    testRuntime(project(":noarg-ide-plugin"))
    testRuntime(project(":allopen-ide-plugin"))
}

afterEvaluate {
    dependencies {
        compile(intellij { include("openapi.jar", "idea.jar") })
        compile(intellijPlugin("android") {
            include("android.jar", "android-common.jar", "sdk-common.jar", "sdklib.jar", "sdk-tools.jar", "layoutlib-api.jar")
        })
        testCompile(intellij { include("gson-*.jar") })
        testCompile(intellijPlugin("properties"))
        testRuntime(intellij())
        testRuntime(intellijPlugins("android", "copyright", "coverage", "gradle", "Groovy", "IntelliLang",
                                    "java-decompiler", "java-i18n", "junit", "maven", "testng"))
    }
}

sourceSets {
    "main" { projectDefault() }
    "test" { projectDefault() }
}

projectTest {
    workingDir = rootDir
}

testsJar {}

