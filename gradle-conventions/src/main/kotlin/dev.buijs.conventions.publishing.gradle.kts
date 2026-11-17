// Releases to Maven Central, snapshots to Repsy; see configurePublishing.
import dev.buijs.conventions.gradle.configurePublishing

plugins {
    id("com.vanniktech.maven.publish")
}

configurePublishing()
