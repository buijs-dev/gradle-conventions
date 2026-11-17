import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SourcesJar

// A published Gradle plugin module; `java-gradle-plugin` brings its own `pluginMaven` publication
// and a marker publication per plugin.
plugins {
    id("buijs.kotlin")
    id("buijs.repositories")
    id("buijs.publishing")
    id("buijs.formatting")
    id("buijs.coverage")
    id("buijs.dokka")
    `java-gradle-plugin`
}

// The sources and javadoc jars Maven Central requires come from buijs.kotlin (withSourcesJar,
// withJavadocJar), so the plugin adds none of its own.
mavenPublishing {
    configure(GradlePlugin(JavadocJar.None(), SourcesJar.None()))
}
