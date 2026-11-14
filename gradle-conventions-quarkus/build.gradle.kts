plugins {
    `kotlin-dsl`
    id("buijs.repositories")
    id("buijs.publishing")
    id("buijs.formatting")
}

description = "The conventions plus the Quarkus convention plugins"

dependencies {
    api(project(":gradle-conventions"))
    implementation(libs.quarkus.plugin)
    implementation(libs.quarkus.extension.plugin)
    implementation(libs.kotlin.plugin.allopen)
}
