// KDoc as HTML, per module: the library and gradle-plugin conventions publish it as the
// Javadoc jar (dokkaGeneratePublicationHtml), which javadoc.io shows.
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
