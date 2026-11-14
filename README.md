# Gradle Conventions

The build logic shared by all buijs-dev JVM repositories (StrataStaX, Fabrio, Rialto and others): convention plugins and a version catalog, published like any other artifact (releases to GitHub Packages, snapshots to Repsy). It is the Gradle counterpart of [ci-templates](https://github.com/buijs-dev/ci-templates).

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
| `buijs.library` | a published Kotlin library: all of the below plus `java-library` |
| `buijs.gradle-plugin` | a published Gradle plugin: all of the below plus `java-gradle-plugin` |
| `buijs.kotlin` | Kotlin JVM on Java 25, sources and javadoc jars, JUnit Platform |
| `buijs.repositories` | Gradle Plugin Portal, Maven Central, Maven Local, buijs-dev GitHub Packages (releases) and Repsy (snapshots) |
| `buijs.publishing` | POM defaults and the `GitHubPackages` / `Repsy` repositories that ci-templates publishes to |
| `buijs.formatting` | the style plugin with the license header (default MIT, Buijs Software) |
| `buijs.coverage` | Kover on JaCoCo, XML and HTML reports on `check` |
| `buijs.dokka` | Dokka HTML with source links to GitHub |
| `buijs.node` | Node.js (downloaded) for modules that run npm/npx tools |
| `buijs.quarkus`, `buijs.quarkus-extension` | Quarkus (`settings.quarkus` only) |
| `buijs.quarkus-app` | a Quarkus service: Kotlin, Quarkus, allOpen for JAX-RS, CDI, JPA and QuarkusTest, `-parameters`, no sources or javadoc jar (`settings.quarkus` only) |

Product-specific build logic, such as an OpenAPI generator for one product's server interfaces, does not belong here; it stays in that product's repository.
