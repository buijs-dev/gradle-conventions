// dev.buijs.conventions.settings for a build with Quarkus modules: also puts
// dev.buijs.conventions.quarkus and dev.buijs.conventions.quarkus-extension (and the Quarkus
// Gradle plugins) on the build's classpath.
// Applied imperatively: a plugins {} block would apply it outside settings evaluation while
// generating this script's accessors, where it may not declare repositories.
pluginManager.apply("dev.buijs.conventions.settings")
