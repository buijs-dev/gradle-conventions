import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SourcesJar

plugins {
    `kotlin-dsl`
    id("dev.buijs.conventions.repositories")
    id("dev.buijs.conventions.publishing")
    id("dev.buijs.conventions.formatting")
    id("org.jetbrains.kotlinx.kover")
    id("org.jetbrains.dokka")
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

dependencies {
    testImplementation(gradleTestKit())
    testImplementation(libs.assertj)
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.params)
    testRuntimeOnly(libs.junit.engine)
    testRuntimeOnly(libs.junit.platform)
}

kover { useJacoco(libs.versions.jacoco.get()) }

tasks.test {
    useJUnitPlatform()
    // The plugins under test resolve their own dependencies; a test build needs the network.
    systemProperty("conventions.version", project.version.toString())
    val testRepository = rootProject.layout.buildDirectory.dir("test-repository")
    dependsOn(":gradle-catalog:publishMavenPublicationToTestRepository")
    systemProperty("conventions.testRepository", testRepository.get().asFile.absolutePath)
}

// As every published plugin: the sources, and the Dokka HTML as javadoc.
mavenPublishing {
    configure(GradlePlugin(JavadocJar.Dokka("dokkaGeneratePublicationHtml"), SourcesJar.Sources()))
}
