// Loads the plugins the modules share once, in this class loader: the maven-publish plugin has
// one build service for every module and needs the Kotlin plugin next to it.
plugins {
    `kotlin-dsl` apply false
    id("dev.buijs.conventions.publishing") apply false
    id("dev.buijs.conventions.repositories")
    id("org.jetbrains.kotlinx.kover") version libs.versions.kover.get()
}

// One coverage report over the modules with tests: build/reports/kover/report.xml.
dependencies {
    kover(project(":gradle-conventions"))
    kover(project(":gradle-conventions-quarkus"))
}

kover { useJacoco(libs.versions.jacoco.get()) }
