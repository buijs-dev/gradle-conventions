// The entry point of a build, applied in settings.gradle.kts:
//     plugins { id("dev.buijs.conventions.settings") version "<version>" }
// Puts the dev.buijs.conventions.* plugins on the build's classpath (apply them without a version),
// resolves dependencies from the buijs-dev repositories and registers the shared `libs` catalog
// in the same version as this plugin. Versions of the project's own modules belong in
// modules.versions.toml, registered as the `modules` catalog when present.
import dev.buijs.conventions.gradle.Conventions
import dev.buijs.conventions.gradle.buijsDev

dependencyResolutionManagement {
    repositories { buijsDev() }

    versionCatalogs {
        create("libs") { from("dev.buijs.conventions:gradle-catalog:${Conventions.VERSION}") }

        // The project's own catalog, `local` unless `conventions.catalog` names it; not `modules`,
        // which a `dependencies {}` block would shadow with DependencyHandler.modules.
        val name = providers.gradleProperty("conventions.catalog").getOrElse("local")
        val local = layout.rootDirectory.file("$name.versions.toml")
        if (local.asFile.exists()) {
            create(name) { from(layout.rootDirectory.files(local)) }
        }
    }
}
