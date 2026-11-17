plugins {
    `kotlin-dsl`
    id("buijs.repositories")
    id("buijs.publishing")
    id("buijs.formatting")
}

description = "Convention plugins and the settings plugin shared by all buijs-dev JVM builds"

dependencies {
    implementation(libs.kotlin.plugin.jvm)
    implementation(libs.kover.plugin)
    implementation(libs.node.plugin)
    implementation(libs.dokka.plugin)
    implementation(libs.maven.publish.plugin)
    implementation(libs.buijs.style.plugin)
}

// Versions the plugins need at runtime: their own (for the catalog) and JaCoCo's.
val generateBuildConstants =
    tasks.register("generateBuildConstants") {
        description = "Generate build constants for the plugins"
        val output = layout.buildDirectory.dir("generated/constants")
        val version = project.version.toString()
        val jacoco = libs.versions.jacoco.get()
        inputs.property("version", version)
        inputs.property("jacoco", jacoco)
        outputs.dir(output)
        doLast {
            val file = output.get().file("dev/buijs/conventions/gradle/Conventions.kt").asFile
            file.parentFile.mkdirs()
            file.writeText(
                """
                |package dev.buijs.conventions.gradle
                |
                |internal object Conventions {
                |    const val VERSION = "$version"
                |    const val JACOCO_VERSION = "$jacoco"
                |}
                |
                """.trimMargin(),
            )
        }
    }

sourceSets.main {
    kotlin.srcDir("src/shared/kotlin")
    kotlin.srcDir(generateBuildConstants)
}
