// KDoc as HTML: `./gradlew dokkaGenerate` writes build/dokka/html.
// Applied to a multi-module root together with `dependencies { dokka(project(":module")) }`
// for one site over all modules.
plugins {
    id("dev.buijs.conventions.repositories")
    id("org.jetbrains.dokka")
}

val repository = providers.gradleProperty("conventions.repository").get()

dokka {
    moduleName.set(
        if (project == rootProject) {
            providers.gradleProperty("conventions.displayName").get()
        } else {
            project.name
        },
    )

    dokkaSourceSets.configureEach {
        sourceLink {
            localDirectory.set(rootDir)
            remoteUrl("https://github.com/buijs-dev/$repository/tree/main")
            remoteLineSuffix.set("#L")
        }
    }
}
