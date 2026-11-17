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

description = "The conventions plus the Quarkus convention plugins"

dependencies {
    api(project(":gradle-conventions"))
    implementation(libs.quarkus.plugin)
    implementation(libs.quarkus.extension.plugin)
    implementation(libs.kotlin.plugin.allopen)
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
