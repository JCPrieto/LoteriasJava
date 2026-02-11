# Repository Guidelines

## Project Structure & Module Organization

The core application lives in `src/main/java`, organized under the `es.jklabs` package hierarchy (desktop GUI,
utilities, and Firebase JSON helpers). Runtime assets live in `src/main/resources`, with i18n bundles under
`src/main/resources/i18n` and icons under `src/main/resources/img`. Native packaging assets for `jpackage` live under
`src/main/jpackage` (currently Linux-specific resources/scripts). Build packaging is configured in `pom.xml`, with
assembly rules in `src/assembly/cfg.xml`. Top-level launchers (`LoteriaDeNavidad.sh`, `LoteriaDeNavidad.bat`) run the
packaged JAR.

## Build, Test, and Development Commands

- `mvn clean package`: compiles Java 21 sources, copies dependencies into `target/libs`, builds
  `target/LoteriaDeNavidad-<version>.jar`, and produces a distributable ZIP via the assembly plugin.
- `mvn -Pdist-linux package`: builds Linux native package (`.deb`) using `jpackage`.
- `mvn -Pdist-windows package`: builds Windows native package (`.msi`) using `jpackage`.
- `mvn -Pdist-mac package`: builds macOS native package (`.dmg`) using `jpackage`.
- `mvn test`: runs the unit test suite.
- `mvn -Pcoverage verify`: runs verification with JaCoCo XML coverage report generation.
- `java -jar target/LoteriaDeNavidad-<version>.jar`: runs the app locally after a build.
- `./LoteriaDeNavidad.sh` or `LoteriaDeNavidad.bat`: runs the packaged JAR placed alongside these scripts.

## Coding Style & Naming Conventions

Follow the existing Java style: 4-space indentation, `UpperCamelCase` for classes, `lowerCamelCase` for methods/fields,
and `UPPER_SNAKE_CASE` for constants. Keep package names lowercase (`es.jklabs...`). No automated formatter is
configured, so match surrounding style in modified files.

## Testing Guidelines

Tests live in `src/test/java` and use JUnit 5 + Mockito. Use `*Test.java` naming and keep tests focused on utility
classes and non-GUI logic where possible.
After introducing code changes, review and run the unit tests for the modified classes. If tests fail, fix them before
proceeding. Whenever a new public class or public method is added, implement its corresponding unit test.

## Commit & Pull Request Guidelines

Recent history uses short, descriptive summaries (often dependency-related). Use concise, imperative commit messages
like “Update Firebase admin to 9.7.0”. PRs should include a brief description, how to verify locally, and any
screenshots when UI changes are involved.

## Configuration & Runtime Notes

Java 21 is required. On Linux, LibNotify is needed for notifications. External API usage is defined in the README; keep
any new integrations documented there.
Linux native packaging uses explicit `--linux-package-deps` alternatives to improve compatibility between Ubuntu 22.04
and 24.04+.
Release notes/changelog are maintained in `CHANGELOG.md` and should be updated on version bumps.
