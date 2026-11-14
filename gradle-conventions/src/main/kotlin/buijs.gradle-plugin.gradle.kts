// A published Gradle plugin module; `java-gradle-plugin` brings its own `pluginMaven` publication.
plugins {
    id("buijs.kotlin")
    id("buijs.repositories")
    id("buijs.publishing")
    id("buijs.formatting")
    id("buijs.coverage")
    id("buijs.dokka")
    `java-gradle-plugin`
}
