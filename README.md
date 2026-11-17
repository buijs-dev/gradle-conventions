# Gradle Conventions

The build logic shared by all buijs-dev JVM repositories: convention plugins and a version catalog, published like any other artifact (releases to Maven Central, snapshots to Repsy). It is the Gradle counterpart of [ci-templates](https://github.com/buijs-dev/ci-templates).

## Use

In `settings.gradle.kts`:

```kotlin
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
```

## Plugins

| Plugin | What it configures |
|---|---|
| `dev.buijs.conventions.library` | a published Kotlin library: all of the below plus `java-library` |
| `dev.buijs.conventions.gradle-plugin` | a published Gradle plugin: all of the below plus `java-gradle-plugin` |
| `dev.buijs.conventions.kotlin` | Kotlin JVM on Java 25, sources and javadoc jars, JUnit Platform |
| `dev.buijs.conventions.repositories` | Gradle Plugin Portal, Maven Central (releases), Maven Local and Repsy (`dev.buijs` snapshots) |
| `dev.buijs.conventions.publishing` | the vanniktech maven-publish plugin: POM defaults, releases to Maven Central (`publishToMavenCentral`, signed when a signing key is present) and snapshots to the `Repsy` repository (`publishAllPublicationsToRepsyRepository`), as ci-templates publishes them |
| `dev.buijs.conventions.formatting` | the style plugin with the license header (default MIT, Buijs Software) |
| `dev.buijs.conventions.coverage` | Kover on JaCoCo, XML and HTML reports on `check` |
| `dev.buijs.conventions.dokka` | Dokka HTML with source links to GitHub |
| `dev.buijs.conventions.node` | Node.js (downloaded) for modules that run npm/npx tools |
| `dev.buijs.conventions.quarkus`, `dev.buijs.conventions.quarkus-extension` | Quarkus (`settings.quarkus` only) |
| `dev.buijs.conventions.quarkus-app` | a Quarkus service: Kotlin, Quarkus, allOpen for JAX-RS, CDI, JPA and QuarkusTest, `-parameters`, no sources or javadoc jar (`settings.quarkus` only) |

Product-specific build logic, such as an OpenAPI generator for one product's server interfaces, does not belong here; it stays in that product's repository.
