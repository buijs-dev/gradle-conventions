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

import dev.buijs.kotlinstyle.gradle.KotlinStyleExtension
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.junitplatform.JUnitPlatformOptions
import org.gradle.internal.artifacts.repositories.AuthenticationSupportedInternal
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import org.gradle.api.tasks.testing.Test as TestTask

/** The convention plugins besides publishing, in process. */
class ConventionPluginsTest {

    @field:TempDir private lateinit var dir: File

    private fun project(vararg plugins: String): Project =
        conventionsProject(dir, plugins = plugins)

    private fun Project.dependencyNames(task: String) =
        tasks.getByName(task).let { t -> t.taskDependencies.getDependencies(t).map { it.name } }

    @Test
    fun `repositories are the public ones, Maven Local and Repsy, in that order`() {
        val repositories = project("dev.buijs.conventions.repositories").repositories

        assertThat(repositories.map { it.name })
            .containsExactly("Gradle Central Plugin Repository", "MavenRepo", "MavenLocal", "Repsy")
        assertThat((repositories.getByName("Repsy") as MavenArtifactRepository).url.toString())
            .isEqualTo("https://repo.repsy.io/buijs-dev/maven2")
    }

    @Test
    fun `Repsy is read anonymously`() {
        val repsy =
            project("dev.buijs.conventions.repositories").repositories.getByName("Repsy")
                as AuthenticationSupportedInternal

        assertThat(repsy.configuredCredentials.isPresent).isFalse()
    }

    @Test
    fun `kotlin compiles for Java 25 with the shared compiler options`() {
        val project = project("dev.buijs.conventions.kotlin")
        val java = project.extensions.getByType(JavaPluginExtension::class.java)
        val compile =
            project.tasks
                .withType(
                    KotlinJvmCompile::class.java,
                ).getByName("compileKotlin")

        assertThat(
            java.toolchain.languageVersion
                .get()
                .asInt(),
        ).isEqualTo(25)
        assertThat(java.sourceCompatibility.majorVersion).isEqualTo("25")
        assertThat(compile.compilerOptions.jvmTarget.get()).isEqualTo(JvmTarget.JVM_25)
        assertThat(compile.compilerOptions.javaParameters.get()).isTrue()
        assertThat(compile.compilerOptions.freeCompilerArgs.get())
            .containsExactly("-Xjsr305=strict", "-Xannotation-default-target=param-property")
        assertThat(project.tasks.names).contains("sourcesJar").doesNotContain("javadocJar")
    }

    @Test
    fun `kotlin tests run on the JUnit Platform with the agent and open flags`() {
        val test = project("dev.buijs.conventions.kotlin").tasks.getByName("test") as TestTask

        assertThat(test.options).isInstanceOf(JUnitPlatformOptions::class.java)
        assertThat(test.jvmArgs)
            .contains(
                "-XX:+EnableDynamicAgentLoading",
                "-Xshare:off",
                "--add-opens=java.base/java.lang.invoke=ALL-UNNAMED",
            )
    }

    @Test
    fun `library brings Kotlin, publishing, formatting, coverage and Dokka`() {
        val project = project("dev.buijs.conventions.library")

        listOf(
            "java-library",
            "org.jetbrains.kotlin.jvm",
            "com.vanniktech.maven.publish",
            "dev.buijs.kotlin-style",
            "org.jetbrains.kotlinx.kover",
            "org.jetbrains.dokka",
        ).forEach { assertThat(project.pluginManager.hasPlugin(it)).describedAs(it).isTrue() }
    }

    @Test
    fun `gradle-plugin brings java-gradle-plugin with the same conventions`() {
        val project = project("dev.buijs.conventions.gradle-plugin")

        listOf(
            "java-gradle-plugin",
            "org.jetbrains.kotlin.jvm",
            "com.vanniktech.maven.publish",
            "dev.buijs.kotlin-style",
        ).forEach { assertThat(project.pluginManager.hasPlugin(it)).describedAs(it).isTrue() }
    }

    @Test
    fun `formatting uses the MIT header by default`() {
        val style =
            project(
                "dev.buijs.conventions.formatting",
            ).extensions.getByType(KotlinStyleExtension::class.java)

        assertThat(style.licenseHeader.get()).isEqualTo(LICENSE_HEADER.trim())
    }

    @Test
    fun `formatting reads the header from conventions licenseHeaderFile`() {
        File(dir, "HEADER").writeText("\n/** Licensed */\n\n")
        val project =
            conventionsProject(
                dir,
                DEFAULT_PROPERTIES + ("conventions.licenseHeaderFile" to "HEADER"),
                "dev.buijs.conventions.formatting",
            )

        assertThat(
            project.extensions
                .getByType(KotlinStyleExtension::class.java)
                .licenseHeader
                .get(),
        ).isEqualTo("/** Licensed */")
    }

    @Test
    fun `coverage writes the XML and HTML reports on check`() {
        val project = project("dev.buijs.conventions.coverage")

        assertThat(project.dependencyNames("check")).contains("koverXmlReport", "koverHtmlReport")
    }

    @Test
    fun `dokka names the root after the display name and a module after itself`() {
        val root = project("dev.buijs.conventions.dokka")
        val module =
            ProjectBuilder
                .builder()
                .withParent(
                    root,
                ).withName("core")
                .withProjectDir(File(dir, "core"))
                .build()
        module.pluginManager.apply("dev.buijs.conventions.dokka")

        assertThat(
            root.extensions
                .getByType(DokkaExtension::class.java)
                .moduleName
                .get(),
        ).isEqualTo("Sample JVM")
        assertThat(
            module.extensions
                .getByType(DokkaExtension::class.java)
                .moduleName
                .get(),
        ).isEqualTo("core")
    }

    @Test
    fun `dokka links every source set to its source on GitHub`() {
        val project = project("dev.buijs.conventions.kotlin", "dev.buijs.conventions.dokka")
        val sourceSet =
            project.extensions
                .getByType(
                    DokkaExtension::class.java,
                ).dokkaSourceSets
                .getByName("main")
        val link = sourceSet.sourceLinks.single()

        assertThat(
            link.remoteUrl.get().toString(),
        ).isEqualTo("https://github.com/buijs-dev/sample-jvm/tree/main")
        assertThat(link.remoteLineSuffix.get()).isEqualTo("#L")
        assertThat(link.localDirectory.get().asFile).isEqualTo(project.rootDir)
    }
}
