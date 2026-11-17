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

import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import java.io.File

/** A project for in-process tests, with the `conventions.*` properties in its gradle.properties. */
internal fun conventionsProject(
    dir: File,
    properties: Map<String, String> = DEFAULT_PROPERTIES,
    vararg plugins: String,
): Project {
    File(dir, "gradle.properties").writeText(
        properties.entries.joinToString("\n") { "${it.key}=${it.value}" },
    )
    return ProjectBuilder.builder().withProjectDir(dir).withName("sample").build().also { project ->
        plugins.forEach { project.pluginManager.apply(it) }
    }
}

/** Runs the afterEvaluate actions, as Gradle does once the build script ran. */
internal fun Project.evaluated(): Project = also { (it as ProjectInternal).evaluate() }

internal val DEFAULT_PROPERTIES =
    mapOf(
        "version" to "1.2.3",
        "conventions.group" to "dev.buijs.sample",
        "conventions.repository" to "sample-jvm",
        "conventions.displayName" to "Sample JVM",
    )
