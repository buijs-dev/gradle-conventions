import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.jvm")
}

java {
    toolchain { languageVersion = JavaLanguageVersion.of(25) }

    sourceCompatibility = JavaVersion.VERSION_25
    withSourcesJar()
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_25
        javaParameters = true
        // Annotations on constructor properties go to the parameter and the property: the default since
        // Kotlin 2.4, so no flag is needed; frameworks such as Quarkus read them from either.
        freeCompilerArgs.set(listOf("-Xjsr305=strict"))
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs(
        "-XX:+EnableDynamicAgentLoading",
        "-Xshare:off",
        "--add-opens=java.base/java.lang.invoke=ALL-UNNAMED",
    )
}
