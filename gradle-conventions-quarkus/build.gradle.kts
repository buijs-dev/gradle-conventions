plugins {
    `kotlin-dsl`
    id("dev.buijs.conventions.repositories")
    id("dev.buijs.conventions.publishing")
    id("dev.buijs.conventions.formatting")
}

description = "The conventions plus the Quarkus convention plugins"

dependencies {
    api(project(":gradle-conventions"))
    implementation(libs.quarkus.plugin)
    implementation(libs.quarkus.extension.plugin)
    implementation(libs.kotlin.plugin.allopen)
}
