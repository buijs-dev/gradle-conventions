# Changelog

## 1.0.0 - unreleased

- Generic conventions, derived from stratastax-gradle 1.2.0: plugin ids `buijs.*` and `dev.buijs.conventions.settings`; group, repository, display name and license header configurable through `conventions.*` properties; the project's own catalog is `local.versions.toml` (catalog `local`, renamed with `conventions.catalog`).
- `buijs.quarkus-app`: the conventions of a Quarkus service, generalized from `stratastax.quarkus-app`.
- Catalog: `quarkus-opentelemetry`, `quarkus-vertx-http` and `opentelemetry-sdk-testing`.
- `buijs.kotlin`: `-Xannotation-default-target=param-property`, as the StrataStaX libraries used.
- Catalog: plugin alias `kotlin-jvm`, for builds outside the conventions.
- Catalog: `buijs-style-rules`, for tools that format generated code.
- `buijs.publishing`: the vanniktech maven-publish plugin (catalog `maven-publish`, 0.37.0). Releases go to Maven Central
  (`publishToMavenCentral`, Central Portal), signed when a signing key is present; snapshots go to Repsy
  (`publishAllPublicationsToRepsyRepository`). Plugin markers outside `dev.buijs` stay off Central. GitHub Packages is
  gone, from publishing and from `buijs.repositories`.
