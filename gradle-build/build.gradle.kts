plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    mavenLocal()
    // The style plugin, releases and snapshots.
    maven("https://repo.repsy.io/buijs-dev/maven2") {
        mavenContent {
            includeGroupByRegex("dev\\.buijs.*")
        }
    }
}

// The Kotlin behind this build's own scripts, shared with gradle-conventions so the two can't
// drift. Precompiled scripts are only picked up from src/main/kotlin, hence the small copies.
sourceSets.main { kotlin.srcDir("../gradle-conventions/src/shared/kotlin") }

dependencies {
    implementation(libs.buijs.style.plugin)
    implementation(libs.maven.publish.plugin)
}
