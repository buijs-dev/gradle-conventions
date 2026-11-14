// dev.buijs.conventions.settings for a build with Quarkus modules: also puts buijs.quarkus
// and buijs.quarkus-extension (and the Quarkus Gradle plugins) on the build's classpath.
// Applied imperatively: a plugins {} block would apply it outside settings evaluation while
// generating this script's accessors, where it may not declare repositories.
pluginManager.apply("dev.buijs.conventions.settings")
