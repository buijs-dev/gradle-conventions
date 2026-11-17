# Changelog

## 1.0.0 - unreleased

- Generic conventions: plugin ids `dev.buijs.conventions.*`, among them the settings plugin `dev.buijs.conventions.settings`; group, repository, display name and license header configurable through `conventions.*` properties; the project's own catalog is `local.versions.toml` (catalog `local`, renamed with `conventions.catalog`).
- `dev.buijs.conventions.quarkus-app`: the conventions of a Quarkus service.
- Catalog: `quarkus-opentelemetry`, `quarkus-vertx-http` and `opentelemetry-sdk-testing`.
- `dev.buijs.conventions.kotlin`: `-Xannotation-default-target=param-property`.
- Catalog: plugin alias `kotlin-jvm`, for builds outside the conventions.
- Catalog: `buijs-style-rules`, for tools that format generated code.
- `dev.buijs.conventions.publishing`: the vanniktech maven-publish plugin (catalog `maven-publish`, 0.37.0). Every project
  publishes to Repsy (`publishAllPublicationsToRepsyRepository`, `https://repo.repsy.io/mvn/buijs-dev/maven`): all snapshots
  and the releases of build tooling. `conventions.mavenCentral=true` adds Maven Central (`publishToMavenCentral`,
  Central Portal) for a public library. Signed when a signing key is present.
- `dev.buijs.conventions.repositories`: Repsy `https://repo.repsy.io/mvn/buijs-dev/maven` for `dev.buijs` snapshots and
  build tooling releases, read without credentials.
- The conventions themselves are released to Repsy, not to Maven Central.
