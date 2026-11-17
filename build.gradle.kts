// Loads the plugins the modules share once, in this class loader: the maven-publish plugin has
// one build service for every module and needs the Kotlin plugin next to it.
plugins {
    `kotlin-dsl` apply false
    id("buijs.publishing") apply false
}
