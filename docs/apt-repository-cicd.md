# Publicación automática del paquete `.deb` en el repositorio APT

Este proyecto publica automáticamente su paquete Debian en el repositorio:

* `https://github.com/JCPrieto/jklabs-apt-repo`

El flujo se ejecuta desde `/.github/workflows/release.yml` y, al crear una release de GitHub, hace lo siguiente:

1. Compila instaladores para Linux (`.deb`), Windows (`.msi`) y macOS (`.dmg`).
2. Publica los artefactos en la release.
3. Localiza el `.deb` generado y envía un `repository_dispatch` al repositorio APT con:
    * `package_name: loteriadenavidad`
    * `version`
    * `release_tag`
    * `deb_filename`
    * `deb_url`
    * metadatos de distribución (`distro`, `component`, `architecture`)

## Configuración requerida en GitHub

Configura en el repositorio `LoteriasJava`:

* Secret:
    * `APT_REPO_DISPATCH_TOKEN`: token con permisos para disparar eventos en `JCPrieto/jklabs-apt-repo`.
* Variables (opcionales):
    * `APT_REPO_OWNER` (por defecto: `JCPrieto`)
    * `APT_REPO_NAME` (por defecto: `jklabs-apt-repo`)

Si no se definen variables, el workflow usa los valores por defecto.

## Evento enviado al repositorio APT

* `event_type`: `deb-published`
* `client_payload.package_name`: `loteriadenavidad`

El repositorio APT debe tener un workflow que escuche ese evento y regenere índices (`Packages`, `Release`, firma, etc.)
antes de publicar en GitHub Pages.
