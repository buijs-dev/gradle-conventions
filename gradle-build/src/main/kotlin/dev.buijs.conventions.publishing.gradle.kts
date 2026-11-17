// Snapshots and build tooling to Repsy, libraries with conventions.mavenCentral=true also to
// Maven Central; see configurePublishing.
import dev.buijs.conventions.gradle.configurePublishing

plugins {
    id("com.vanniktech.maven.publish")
}

configurePublishing()
