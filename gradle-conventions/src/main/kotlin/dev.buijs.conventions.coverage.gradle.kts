// Code coverage with Kover on the JaCoCo engine: `check` writes build/reports/kover/report.xml
// (JaCoCo XML) and build/reports/kover/html. Applied to a multi-module root together with
// `dependencies { kover(project(":module")) }` for one report over all modules.
import dev.buijs.conventions.gradle.Conventions

plugins {
    base
    id("dev.buijs.conventions.repositories")
    id("org.jetbrains.kotlinx.kover")
}

kover {
    useJacoco(Conventions.JACOCO_VERSION)

    reports {
        filters { excludes { annotatedBy("*Generated*") } }
        total {
            xml { onCheck = true }
            html { onCheck = true }
        }
    }
}
