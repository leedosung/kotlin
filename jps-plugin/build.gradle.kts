apply { plugin("kotlin") }

val compilerModules: Array<String> by rootProject.extra

configureIntellijPlugin {
    setExtraDependencies("intellij-core", "jps-standalone", "jps-build-test")
}

dependencies {
    compile(project(":kotlin-build-common"))
    compile(project(":core"))
    compile(project(":kotlin-compiler-runner"))
    compile(project(":compiler:daemon-common"))
    compile(projectRuntimeJar(":kotlin-daemon-client"))
    compile(project(":compiler:frontend.java"))
    compile(projectRuntimeJar(":kotlin-preloader"))
    compile(project(":idea:idea-jps-common"))
    testCompile(project(":compiler:incremental-compilation-impl"))
    testCompile(projectTests(":compiler:tests-common"))
    testCompile(projectTests(":compiler:incremental-compilation-impl"))
    testCompile(commonDep("junit:junit"))
    testCompile(projectDist(":kotlin-test:kotlin-test-jvm"))
    testCompile(projectTests(":kotlin-build-common"))
    compilerModules.forEach {
        testRuntime(project(it))
    }
}

afterEvaluate {
    dependencies {
        compile(intellijExtra("jps-standalone") { include("jps-builders.jar", "jps-builders-6.jar") })
        testCompile(intellij { include("openapi.jar", "idea.jar") })
        testCompile(intellijExtra("jps-build-test"))
        testRuntime(intellijCoreJar())
        testRuntime(intellij())
        testRuntime(intellijExtra("jps-standalone"))
    }
}

sourceSets {
    "main" { projectDefault() }
    "test" {
        java.srcDirs("jps-tests/test"
                     /*, "kannotator-jps-plugin-test/test"*/ // Obsolete
        )
    }
}

projectTest {
    workingDir = rootDir
}

testsJar {}
