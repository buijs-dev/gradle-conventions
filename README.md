# Gradle Conventions

The build logic shared by all buijs-dev JVM repositories: convention plugins and a version catalog, published as build tooling: releases and snapshots go to the public Repsy repository `https://repo.repsy.io/buijs-dev/maven2`, never to Maven Central. It is the Gradle counterpart of [ci-templates](https://github.com/buijs-dev/ci-templates).

## Use

In `settings.gradle.kts`:

```kotlin
pluginManagement {
    repositories {
        maven("https://repo.repsy.io/buijs-dev/maven2") {
            mavenContent { includeGroupByRegex("dev\\.buijs.*") }
        }
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("dev.buijs.conventions.settings") version "<version>"
    // or, for a build with Quarkus modules:
    // id("dev.buijs.conventions.settings.quarkus") version "<version>"
}
```

This registers the buijs-dev repositories, the shared `libs` catalog (`gradle-catalog`, same version as the plugin) and, when `local.versions.toml` exists in the project root, a `local` catalog for the versions of the project's own modules.

In `gradle.properties`:

```properties
version=1.0.0-SNAPSHOT
conventions.group=dev.buijs.rialto
conventions.repository=rialto-jvm
conventions.displayName=Rialto JVM
# optional: conventions.licenseHeaderFile=LICENSE_HEADER
# optional: conventions.license=Apache-2.0 (POM licence; default MIT)
# optional: conventions.mavenCentral=true (a public library that releases to Maven Central)
```

Reading Repsy needs no credentials.

## Plugins

| Plugin | What it configures |
|---|---|
| `dev.buijs.conventions.library` | a published Kotlin library: all of the below plus `java-library`; the javadoc jar holds the module's Dokka HTML |
| `dev.buijs.conventions.gradle-plugin` | a published Gradle plugin: all of the below plus `java-gradle-plugin`; the javadoc jar holds the module's Dokka HTML |
| `dev.buijs.conventions.kotlin` | Kotlin JVM on Java 25, a sources jar, JUnit Platform |
| `dev.buijs.conventions.repositories` | Gradle Plugin Portal, Maven Central (library releases), Maven Local and Repsy (`dev.buijs` snapshots and build tooling, read without credentials) |
| `dev.buijs.conventions.publishing` | the vanniktech maven-publish plugin: POM defaults and the javadoc and sources jars; the `Repsy` repository (`publishAllPublicationsToRepsyRepository`, with `REPSY_USERNAME` and `REPSY_PASSWORD`) for all snapshots and for build tooling releases; with `conventions.mavenCentral=true` also Maven Central (`publishToMavenCentral`). Signed when a signing key is present. These are the targets of the ci-templates publish workflows |
| `dev.buijs.conventions.formatting` | the style plugin with the license header (default MIT, Buijs Software) |
| `dev.buijs.conventions.coverage` | Kover on JaCoCo, XML and HTML reports on `check` |
| `dev.buijs.conventions.dokka` | Dokka HTML per module, with source links to GitHub |
| `dev.buijs.conventions.node` | Node.js (downloaded) for modules that run npm/npx tools |
| `dev.buijs.conventions.quarkus`, `dev.buijs.conventions.quarkus-extension` | Quarkus (`settings.quarkus` only) |
| `dev.buijs.conventions.quarkus-app` | a Quarkus service: Kotlin, Quarkus, allOpen for JAX-RS, CDI, JPA and QuarkusTest, `-parameters`, no sources jar (`settings.quarkus` only) |

Product-specific build logic, such as an OpenAPI generator for one product's server interfaces, does not belong here; it stays in that product's repository.
