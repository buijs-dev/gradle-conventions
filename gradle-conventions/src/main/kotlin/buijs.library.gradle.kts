import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.SourcesJar

// A published library module: apply this one and set `description`.
plugins {
    id("buijs.kotlin")
    id("buijs.repositories")
    id("buijs.publishing")
    id("buijs.formatting")
    id("buijs.coverage")
    id("buijs.dokka")
    `java-library`
}

// The sources and javadoc jars Maven Central requires come from buijs.kotlin (withSourcesJar,
// withJavadocJar), so the plugin adds none of its own.
mavenPublishing {
    configure(KotlinJvm(JavadocJar.None(), SourcesJar.None()))
}
