# Repository Guidelines

## Project Structure & Module Organization

The core application lives in `src/main/java`, organized under the `es.jklabs` package hierarchy (desktop GUI,
utilities, and Firebase JSON helpers). Runtime assets live in `src/main/resources`, with i18n bundles under
`src/main/resources/i18n` and icons under `src/main/resources/img`. Build packaging is configured in `pom.xml`, with
assembly rules in `src/assembly/cfg.xml`. Top-level launchers (`LoteriaDeNavidad.sh`, `LoteriaDeNavidad.bat`) run the
packaged JAR.

## Build, Test, and Development Commands

- `mvn clean package`: compiles Java 11 sources, copies dependencies into `target/libs`, builds
  `target/LoteriaDeNavidad-<version>.jar`, and produces a distributable ZIP via the assembly plugin.
- `mvn test`: runs tests if present (none are configured today).
- `java -jar target/LoteriaDeNavidad-<version>.jar`: runs the app locally after a build.
- `./LoteriaDeNavidad.sh` or `LoteriaDeNavidad.bat`: runs the packaged JAR placed alongside these scripts.

## Coding Style & Naming Conventions

Follow the existing Java style: 4-space indentation, `UpperCamelCase` for classes, `lowerCamelCase` for methods/fields,
and `UPPER_SNAKE_CASE` for constants. Keep package names lowercase (`es.jklabs...`). No automated formatter is
configured, so match surrounding style in modified files.

## Testing Guidelines

There is no `src/test/java` or test framework dependency. If you add tests, create `src/test/java`, use `*Test.java`
naming, and consider adding JUnit 5 to `pom.xml`. Keep tests focused on utility classes and non-GUI logic where
possible.

## Commit & Pull Request Guidelines

Recent history uses short, descriptive summaries (often dependency-related). Use concise, imperative commit messages
like “Update Firebase admin to 9.7.0”. PRs should include a brief description, how to verify locally, and any
screenshots when UI changes are involved.

## Configuration & Runtime Notes

Java 11 is required. On Linux, LibNotify is needed for notifications. External API usage is defined in the README; keep
any new integrations documented there.
