// A Quarkus service: Kotlin with the Quarkus plugin, classes Quarkus proxies left open, and
// parameter names kept for JAX-RS and CDI. It ships as an image, so no sources jar.
plugins {
    id("dev.buijs.conventions.kotlin")
    id("dev.buijs.conventions.repositories")
    id("dev.buijs.conventions.formatting")
    id("dev.buijs.conventions.coverage")
    id("dev.buijs.conventions.quarkus")
    id("org.jetbrains.kotlin.plugin.allopen")
}

extensions.configure<org.jetbrains.kotlin.allopen.gradle.AllOpenExtension> {
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("jakarta.persistence.Entity")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

tasks.withType<JavaCompile>().configureEach { options.compilerArgs.add("-parameters") }

tasks.named("sourcesJar") { enabled = false }
