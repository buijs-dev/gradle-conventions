plugins {
    `version-catalog`
    id("dev.buijs.conventions.publishing")
}

description = "The version catalog shared by all buijs-dev JVM builds"

catalog {
    versionCatalog { from(files("../libs.versions.toml")) }
}
