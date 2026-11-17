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
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

/** Real builds with the settings plugin: the catalogs, and a library published end to end. */
class SettingsPluginFunctionalTest {

    @field:TempDir private lateinit var projectDir: File

    private val testRepository = System.getProperty("conventions.testRepository").replace("\\", "/")

    private fun write(
        path: String,
        content: String,
    ) = File(projectDir, path).apply {
        parentFile.mkdirs()
        writeText(content.trimIndent() + "\n")
    }

    private fun settings(extra: String = "") =
        write(
            "settings.gradle.kts",
            """
            plugins {
                id("dev.buijs.conventions.settings")
            }

            // The shared catalog, published by this build's gradle-catalog.
            dependencyResolutionManagement {
                repositories { maven { url = uri("$testRepository") } }
            }

            rootProject.name = "sample"
            $extra
            """,
        )

    private fun properties(extra: String = "") =
        write(
            "gradle.properties",
            """
            version=2.0.0
            conventions.group=dev.buijs.sample
            conventions.repository=sample-jvm
            conventions.displayName=Sample JVM
            $extra
            """,
        )

    private fun run(vararg arguments: String) =
        GradleRunner
            .create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments(*arguments, "--stacktrace")
            .build()

    @Test
    fun `the settings plugin registers the shared catalog in its own version, and the local one`() {
        settings()
        properties()
        write("local.versions.toml", "[versions]\nsample = \"9.9\"")
        write(
            "build.gradle.kts",
            """
            plugins { id("dev.buijs.conventions.node") }

            tasks.register("versions") {
                val java = libs.versions.java.get()
                val sample = local.versions.sample.get()
                val node = node.version.get()
                doLast { println("java=${'$'}java sample=${'$'}sample node=${'$'}node") }
            }
            """,
        )

        val output = run("versions", "-q").output

        val catalog = File("../libs.versions.toml").readText()
        val java = Regex("""(?m)^java = "(.+)"""").find(catalog)!!.groupValues[1]
        val node = Regex("""(?m)^node = "(.+)"""").find(catalog)!!.groupValues[1]
        assertThat(output).contains("java=$java sample=9.9 node=$node")
    }

    @Test
    fun `conventions catalog renames the project's own catalog`() {
        settings()
        properties("conventions.catalog=modules2")
        write("modules2.versions.toml", "[versions]\nsample = \"1.0\"")
        write(
            "build.gradle.kts",
            """
            tasks.register("versions") {
                val sample = modules2.versions.sample.get()
                doLast { println("sample=${'$'}sample") }
            }
            """,
        )

        assertThat(run("versions", "-q").output).contains("sample=1.0")
    }

    @Test
    fun `a library publishes its jar, sources, javadoc and a complete POM`() {
        settings("include(\"core\")")
        properties("conventions.license=Apache-2.0")
        write(
            "core/build.gradle.kts",
            """
            plugins { id("dev.buijs.conventions.library") }

            description = "The core"
            """,
        )
        write(
            "core/src/main/kotlin/dev/buijs/sample/Core.kt",
            LICENSE_HEADER.trim() +
                "\n\npackage dev.buijs.sample\n\n/** The core. */\nclass Core\n",
        )
        val repository = File(projectDir, "m2")

        run(":core:publishToMavenLocal", "-Dmaven.repo.local=${repository.path}")

        val published = File(repository, "dev/buijs/sample/core/2.0.0")
        assertThat(published.list()!!.toList())
            .contains(
                "core-2.0.0.jar",
                "core-2.0.0-sources.jar",
                "core-2.0.0-javadoc.jar",
                "core-2.0.0.pom",
            )
        assertThat(File(published, "core-2.0.0.pom").readText())
            .contains("<name>Sample JVM - core</name>")
            .contains("<description>The core</description>")
            .contains("<url>https://github.com/buijs-dev/sample-jvm</url>")
            .contains("<name>Apache License, Version 2.0</name>")
            .contains(
                "<developerConnection>scm:git:ssh://git@github.com/buijs-dev/sample-jvm.git</developerConnection>",
            )
    }
}
