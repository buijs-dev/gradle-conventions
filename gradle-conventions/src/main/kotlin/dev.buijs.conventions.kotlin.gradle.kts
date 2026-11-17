import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.jvm")
}

java {
    toolchain { languageVersion = JavaLanguageVersion.of(25) }

    sourceCompatibility = JavaVersion.VERSION_25
    withSourcesJar()
    withJavadocJar()
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_25
        javaParameters = true
        // Annotations on constructor properties go to the parameter and the property, as Kotlin
        // will do by default; frameworks such as Quarkus read them from either.
        freeCompilerArgs.set(
            listOf("-Xjsr305=strict", "-Xannotation-default-target=param-property"),
        )
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
