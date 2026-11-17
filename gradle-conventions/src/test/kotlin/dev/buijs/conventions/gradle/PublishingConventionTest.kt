/**
 * Copyright (c) 2021 - 2026 Buijs Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.buijs.conventions.gradle

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.internal.TaskInternal
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.internal.publication.MavenPomInternal
import org.gradle.api.publish.maven.internal.publication.MavenPublicationInternal
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

/** dev.buijs.conventions.publishing, in process: coordinates, POM, repositories and signing. */
class PublishingConventionTest {

    @field:TempDir private lateinit var dir: File

    private fun library(properties: Map<String, String> = DEFAULT_PROPERTIES) =
        conventionsProject(dir, properties, "dev.buijs.conventions.library").evaluated()

    private val Project.publications
        get() = extensions.getByType(PublishingExtension::class.java).publications

    private val Project.pom
        get() = (publications.getByName("maven") as MavenPublication).pom as MavenPomInternal

    @Test
    fun `the group comes from conventions group`() {
        assertThat(library().group).isEqualTo("dev.buijs.sample")
    }

    @Test
    fun `a missing conventions property names the property to set`() {
        assertThatThrownBy {
            conventionsProject(
                dir,
                DEFAULT_PROPERTIES - "conventions.repository",
                "dev.buijs.conventions.publishing",
            )
        }.rootCause().hasMessage("Set conventions.repository in gradle.properties")
    }

    @Test
    fun `the POM has everything Maven Central requires, MIT by default`() {
        val project = library()
        project.description = "A sample"
        val pom = project.pom

        assertThat(pom.name.get()).isEqualTo("Sample JVM")
        assertThat(pom.description.get()).isEqualTo("A sample")
        assertThat(pom.url.get()).isEqualTo("https://github.com/buijs-dev/sample-jvm")
        assertThat(pom.licenses.map { it.name.get() to it.url.get() })
            .containsExactly(
                "MIT License" to "https://github.com/buijs-dev/sample-jvm/blob/main/LICENSE",
            )
        assertThat(pom.developers.map { listOf(it.id.get(), it.name.get(), it.email.get()) })
            .containsExactly(listOf("buijs-dev", "Gillian Buijs", "info@buijs.dev"))
        assertThat(
            pom.scm!!.connection.get(),
        ).isEqualTo("scm:git:https://github.com/buijs-dev/sample-jvm.git")
        assertThat(pom.scm!!.developerConnection.get())
            .isEqualTo("scm:git:ssh://git@github.com/buijs-dev/sample-jvm.git")
        assertThat(pom.scm!!.url.get()).isEqualTo("https://github.com/buijs-dev/sample-jvm")
    }

    @Test
    fun `conventions license Apache-2 selects the Apache license`() {
        val pom = library(DEFAULT_PROPERTIES + ("conventions.license" to "apache-2.0")).pom
        val license = pom.licenses.single()

        assertThat(license.name.get()).isEqualTo("Apache License, Version 2.0")
        assertThat(license.url.get()).isEqualTo("https://www.apache.org/licenses/LICENSE-2.0")
        assertThat(license.distribution.get()).isEqualTo("repo")
    }

    @Test
    fun `a module's POM name is the display name and the module name`() {
        val root = conventionsProject(dir)
        val module =
            ProjectBuilder
                .builder()
                .withParent(
                    root,
                ).withName("core")
                .withProjectDir(File(dir, "core"))
                .build()
        module.pluginManager.apply("dev.buijs.conventions.library")
        module.evaluated()

        assertThat(module.pom.name.get()).isEqualTo("Sample JVM - core")
    }

    @Test
    fun `the library publication carries the jar, the sources and the javadoc`() {
        val publication = library().publications.getByName("maven") as MavenPublicationInternal

        assertThat(publication.publishableArtifacts.map { it.classifier })
            .contains("sources", "javadoc")
        assertThat(publication.artifactId).isEqualTo("sample")
    }

    @Test
    fun `with conventions mavenCentral the publication still carries the sources and the javadoc`() {
        val publication =
            library(CENTRAL_PROPERTIES).publications.getByName("maven") as MavenPublicationInternal

        assertThat(publication.publishableArtifacts.map { it.classifier })
            .contains("sources", "javadoc")
    }

    @Test
    fun `without conventions mavenCentral everything goes to Repsy only`() {
        val project = library()

        assertThat(project.repsy.url.toString()).isEqualTo("https://repo.repsy.io/buijs-dev/maven2")
        assertThat(project.tasks.names)
            .contains(
                "publishAllPublicationsToRepsyRepository",
                "publishMavenPublicationToRepsyRepository",
            ).doesNotContain(
                "publishToMavenCentral",
                "publishMavenPublicationToMavenCentralRepository",
            )
        assertThat(project.publishingRepositories.names).containsExactly("Repsy")
    }

    @Test
    fun `conventions mavenCentral false sets up no Maven Central`() {
        val project = library(DEFAULT_PROPERTIES + ("conventions.mavenCentral" to "false"))

        assertThat(project.tasks.names).doesNotContain("publishToMavenCentral")
        assertThat(project.publishingRepositories.names).containsExactly("Repsy")
    }

    @Test
    fun `conventions mavenCentral adds Maven Central next to Repsy`() {
        val project = library(CENTRAL_PROPERTIES)

        assertThat(project.repsy.url.toString()).isEqualTo("https://repo.repsy.io/buijs-dev/maven2")
        assertThat(project.tasks.names)
            .contains(
                "publishAllPublicationsToRepsyRepository",
                "publishToMavenCentral",
                "publishMavenPublicationToMavenCentralRepository",
            )
        assertThat(
            project.publishingRepositories.names,
        ).containsExactlyInAnyOrder("Repsy", "mavenCentral")
    }

    @Test
    fun `the Repsy repository reads its credentials from REPSY_USERNAME and REPSY_PASSWORD`() {
        val credentials = library().repsy.credentials

        assertThat(credentials.username).isEqualTo(System.getenv("REPSY_USERNAME"))
        assertThat(credentials.password).isEqualTo(System.getenv("REPSY_PASSWORD"))
    }

    @Test
    fun `without a signing key nothing is signed`() {
        val project = library()

        assertThat(project.pluginManager.hasPlugin("signing")).isFalse()
        assertThat(project.tasks.names.filter { it.startsWith("sign") }).isEmpty()
    }

    @Test
    fun `with conventions mavenCentral but without a signing key nothing is signed`() {
        val project = library(CENTRAL_PROPERTIES)

        assertThat(project.pluginManager.hasPlugin("signing")).isFalse()
        assertThat(project.tasks.names.filter { it.startsWith("sign") }).isEmpty()
    }

    @Test
    fun `with conventions mavenCentral an in-memory signing key signs every publication`() {
        val project = library(CENTRAL_PROPERTIES + ("signingInMemoryKey" to "key"))

        assertThat(project.tasks.names).contains("signMavenPublication")
    }

    @Test
    fun `an in-memory signing key signs every publication`() {
        val project = library(DEFAULT_PROPERTIES + ("signingInMemoryKey" to "key"))

        assertThat(project.tasks.names).contains("signMavenPublication")
    }

    @Test
    fun `a signing key id from gradle properties signs every publication`() {
        val project = library(DEFAULT_PROPERTIES + ("signing.keyId" to "0123ABCD"))

        assertThat(project.tasks.names).contains("signMavenPublication")
    }

    @Test
    fun `a Gradle plugin publishes its marker to Repsy only by default`() {
        val project = gradlePlugin(DEFAULT_PROPERTIES)

        assertThat(project.publications.names)
            .contains("pluginMaven", "samplePluginMarkerMaven")
        assertThat(project.tasks.names)
            .contains("publishSamplePluginMarkerMavenPublicationToRepsyRepository")
            .doesNotContain("publishSamplePluginMarkerMavenPublicationToMavenCentralRepository")
    }

    @Test
    fun `with conventions mavenCentral a Gradle plugin publishes its marker to Maven Central and Repsy alike`() {
        val project = gradlePlugin(CENTRAL_PROPERTIES)

        assertThat(project.publications.names)
            .contains("pluginMaven", "samplePluginMarkerMaven")
        listOf("MavenCentral", "Repsy").forEach { repository ->
            val task =
                project.tasks.getByName(
                    "publishSamplePluginMarkerMavenPublicationTo${repository}Repository",
                ) as TaskInternal

            assertThat(task.onlyIf.isSatisfiedBy(task)).describedAs(repository).isTrue()
        }
    }

    private fun gradlePlugin(properties: Map<String, String>): Project {
        val project =
            conventionsProject(dir, properties, "dev.buijs.conventions.gradle-plugin")
        project.extensions
            .getByType(org.gradle.plugin.devel.GradlePluginDevelopmentExtension::class.java)
            .plugins
            .create("sample") {
                id = "dev.buijs.sample.plugin"
                implementationClass = "dev.buijs.sample.SamplePlugin"
            }

        return project.evaluated()
    }

    private val Project.publishingRepositories
        get() = extensions.getByType(PublishingExtension::class.java).repositories

    private val Project.repsy
        get() = publishingRepositories.getByName("Repsy") as MavenArtifactRepository

    private companion object {
        val CENTRAL_PROPERTIES = DEFAULT_PROPERTIES + ("conventions.mavenCentral" to "true")
    }
}
