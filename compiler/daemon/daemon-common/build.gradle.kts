
apply { plugin("kotlin") }

jvmTarget = "1.6"

configureIntellijPlugin {
    setExtraDependencies("intellij-core")
}

dependencies {
    compile(project(":core"))
    compile(project(":compiler:util"))
    compile(project(":compiler:cli-common"))
    compile(project(":kotlin-stdlib"))
}

afterEvaluate {
    dependencies {
        compile(intellijCoreJar())
        compile(intellijCoreJarDependencies())
    }
}

sourceSets {
    "main" { projectDefault() }
    "test" {}
}

