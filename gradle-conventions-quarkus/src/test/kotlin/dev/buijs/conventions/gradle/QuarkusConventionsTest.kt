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
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

/** The Quarkus conventions: a service in process, and the settings plugin in a real build. */
class QuarkusConventionsTest {

    @field:TempDir private lateinit var dir: File

    private fun project(plugin: String): Project {
        File(dir, "gradle.properties").writeText(
            "conventions.group=dev.buijs.sample\nconventions.repository=sample\nconventions.displayName=Sample\n",
        )
        return ProjectBuilder
            .builder()
            .withProjectDir(
                dir,
            ).build()
            .also { it.pluginManager.apply(plugin) }
    }

    // The extension keeps its annotations internal; the field is all there is to read.
    @Suppress("UNCHECKED_CAST")
    private fun AllOpenExtension.openAnnotations(): Iterable<String> =
        AllOpenExtension::class.java
            .getDeclaredField("myAnnotations")
            .apply { isAccessible = true }
            .get(this) as Iterable<String>

    @Test
    fun `a Quarkus service leaves classes Quarkus proxies open and keeps parameter names`() {
        val project = project("dev.buijs.conventions.quarkus-app")

        assertThat(project.pluginManager.hasPlugin("io.quarkus")).isTrue()
        assertThat(project.extensions.getByType(AllOpenExtension::class.java).openAnnotations())
            .containsExactlyInAnyOrder(
                "jakarta.ws.rs.Path",
                "jakarta.enterprise.context.ApplicationScoped",
                "jakarta.persistence.Entity",
                "io.quarkus.test.junit.QuarkusTest",
            )
        assertThat(project.tasks.withType(JavaCompile::class.java).map { it.options.compilerArgs })
            .isNotEmpty()
            .allMatch { "-parameters" in it }
    }

    @Test
    fun `a Quarkus service ships as an image, without sources or javadoc jar`() {
        val project = project("dev.buijs.conventions.quarkus-app")

        assertThat(project.tasks.getByName("sourcesJar").enabled).isFalse()
        assertThat(project.tasks.getByName("javadocJar").enabled).isFalse()
    }

    @Test
    fun `a Quarkus extension module applies the Quarkus extension plugin`() {
        val project = project("dev.buijs.conventions.quarkus-extension")

        assertThat(project.pluginManager.hasPlugin("io.quarkus.extension")).isTrue()
    }

    @Test
    fun `the Quarkus settings plugin registers the shared catalog too`() {
        val repository = System.getProperty("conventions.testRepository").replace("\\", "/")
        File(dir, "settings.gradle.kts").writeText(
            """
            plugins {
                id("dev.buijs.conventions.settings.quarkus")
            }

            dependencyResolutionManagement {
                repositories { maven { url = uri("$repository") } }
            }

            rootProject.name = "sample"
            """.trimIndent() + "\n",
        )
        File(dir, "build.gradle.kts").writeText(
            """
            tasks.register("quarkusVersion") {
                val quarkus = libs.versions.quarkus.get()
                doLast { println("quarkus=${'$'}quarkus") }
            }
            """.trimIndent() + "\n",
        )

        val output =
            GradleRunner
                .create()
                .withProjectDir(dir)
                .withPluginClasspath()
                .withArguments("quarkusVersion", "-q")
                .build()
                .output

        val quarkus =
            Regex(
                """(?m)^quarkus = "(.+)"""",
            ).find(File("../libs.versions.toml").readText())!!
        assertThat(output).contains("quarkus=${quarkus.groupValues[1]}")
    }
}
