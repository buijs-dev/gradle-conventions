pluginManagement {
    repositories {
        mavenLocal()
        maven("https://repo.repsy.io/mvn/buijs-dev/maven") {
            mavenContent {
                includeGroupByRegex("dev\\.buijs.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

// The conventions can't build themselves: gradle-build compiles a few of their own scripts
// (repositories, publishing, formatting) straight from gradle-conventions/src.
includeBuild("gradle-build")

dependencyResolutionManagement {
    versionCatalogs { create("libs") { from(files("libs.versions.toml")) } }
}

include(":gradle-catalog")
include(":gradle-conventions")
include(":gradle-conventions-quarkus")
