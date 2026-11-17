plugins {
    `version-catalog`
    id("dev.buijs.conventions.publishing")
}

description = "The version catalog shared by all buijs-dev JVM builds"

catalog {
    versionCatalog { from(files("../libs.versions.toml")) }
}

// For the functional tests of the settings plugin, which resolve this catalog from a repository.
publishing {
    repositories.maven {
        name = "test"
        url = uri(rootProject.layout.buildDirectory.dir("test-repository"))
    }
}
