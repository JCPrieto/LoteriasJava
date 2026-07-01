# Changelog

## 2.7.14

- Refactoriza las notificaciones de escritorio con `ToastBuilder` y un `DesktopNotifier` inyectable, eliminando la
  copia del icono a directorios temporales y manteniendo fallback a diálogo Swing si falla el backend.
- Asegura que las notificaciones inicialicen siempre `two-slices` con el nombre real de la aplicación, el icono de la
  aplicación y el backend D-Bus en Linux, incluso cuando se muestran antes de una llamada explícita a `Growls.init()`.
- Actualiza dependencias de mantenimiento: `com.sshtools:two-slices` a `0.9.7`,
  `com.github.hypfvieh:dbus-java-*` a `5.2.0` y `org.junit.jupiter:junit-jupiter` a `6.1.1`.
- Documenta `dbus-java` en el README y en el diálogo "Acerca de".
- Mejora la testabilidad de `Ventana` extrayendo la comprobación asíncrona de nuevas versiones a métodos
  package-private, y amplía la cobertura de condiciones para resultados positivos, negativos, nulos, errores e
  interrupciones.
- Refuerza utilidades internas evitando la instanciación de `UtilidadesFecha`.

## 2.7.13

- Actualiza `io.github.jcprieto:loteria-navidad` a `6.0.12`.
- Configura las notificaciones de escritorio con el nombre de la aplicación y un icono real extraído de los recursos,
  y añade las dependencias D-Bus necesarias para priorizar ese backend cuando está disponible.
- Mejora la testabilidad de `Constantes` con hooks package-private para aislar la carga de versión desde
  `pom.properties` y `pom.xml`, manteniendo el mismo comportamiento público.
- Amplía la cobertura de `ConstantesTest` para fallbacks de versión, valores nulos o en blanco, errores de I/O,
  versión heredada desde `parent` y XML inválido o con DOCTYPE bloqueado.
- Refuerza utilidades internas evitando la instanciación de `UtilidadesEstadoSorteo`.

## 2.7.12

- Sustituye el sistema propio de notificaciones de escritorio basado en `SystemTray` y ejecución directa de
  `notify-send` por `com.sshtools:two-slices`, manteniendo fallback a diálogo Swing cuando no hay backend de
  notificaciones disponible.
- Elimina LibNotify como requisito explícito en Linux y documenta `two-slices` en el README y en el diálogo
  "Acerca de".
- Actualiza `io.github.jcprieto:loteria-navidad` a `6.0.11` e incorpora `com.sshtools:two-slices` `0.9.6`.
- Amplía la cobertura de `PanelBusqueda` para validación de cantidades, búsquedas concurrentes, errores del servicio,
  limpieza de resultados y filtros de teclado.
- Ajusta tests de `Logger` para evitar el uso del reloj del sistema y mantiene determinista la limpieza de logs
  antiguos.

## 2.7.11

- Actualiza dependencias de mantenimiento: `com.fasterxml.jackson.core:jackson-databind` a `2.22.0`,
  `maven-surefire-plugin` a `3.5.6`, `jacoco-maven-plugin` a `0.8.15` y `sonar-maven-plugin` a `5.7.0.6970`.
- Mejora la resiliencia de las notificaciones de escritorio: si Windows no puede registrar el icono de bandeja, la
  aplicación conserva disponibles los mecanismos de fallback, y las notificaciones sin título usan el nombre de la
  aplicación de forma consistente.
- Refactoriza `Growls` mediante colaboradores internos inyectables y amplía sus tests para cubrir todas las rutas de
  bandeja, `notify-send`, diálogo y detección del sistema operativo, alcanzando cobertura completa de condiciones.
- Simplifica `UrlMouseListener` extendiendo `MouseAdapter` y elimina callbacks vacíos sin cambiar la interacción de los
  enlaces.

## 2.7.10

- Actualiza dependencias de mantenimiento: `org.junit.jupiter:junit-jupiter` a `6.1.0` e
  `io.github.jcprieto:loteria-navidad` a `6.0.10`.
- Mejora la testabilidad de `MenuPrincipal` centralizando el aviso de error en `showWarning`, manteniendo el mismo
  diálogo en producción.
- Amplía la cobertura de `MenuPrincipalTest` para navegación, eventos ignorados, cargas concurrentes y respuestas de
  resumen correctas, nulas o con error.
- Refactoriza la limpieza de logs antiguos en `Logger` y documenta callbacks vacíos de `UrlMouseListener` sin cambiar
  el comportamiento público.

## 2.7.9

- Actualiza `org.slf4j:slf4j-api` y `org.slf4j:slf4j-simple` a `2.0.18`.
- Mejora la testabilidad del diálogo `AcercaDe` extrayendo la apertura del correo a un hook package-private para tests,
  manteniendo el uso de `Desktop` en producción.
- Amplía la cobertura de `AcercaDeTest` para el enlace de correo, el flujo de error con `Growls`, eventos de ratón y
  las dos ramas de `addPowered`.
- Refuerza utilidades internas evitando instanciar `IconUtils` y ajustando nombres de hooks de `Growls` a estilo
  `lowerCamelCase` sin cambiar comportamiento.

## 2.7.8

- Actualiza dependencias de mantenimiento: `io.github.jcprieto:loteria-navidad` a `6.0.9` y
  `com.fasterxml.jackson.core:jackson-databind` a `2.21.3`.
- Refina la interfaz interna de consulta de premios para declarar `IOException`, alineándola con el contrato real de
  `Conexion#getPremio` sin cambiar el comportamiento de la búsqueda.
- Mejora la testabilidad de `UtilidadesGitHubReleases` inyectando la factoría de conexiones HTTP en tests y haciendo
  que `existeNuevaVersion()` use el provider de releases existente.
- Amplía la cobertura de `UtilidadesGitHubReleasesTest` para cubrir comparación de versiones, parsing de releases,
  selección de assets, respuestas HTTP y errores de apertura en navegador.

## 2.7.7

- Amplía la cobertura con `LoggerTest`, validando la inicialización del directorio de logs, la limpieza de logs
  antiguos, la configuración idempotente de handlers y la publicación de mensajes de error e información.
- Refactoriza `PanelBusqueda#buscarPremioAsync` extrayendo la lógica de procesado y gestión de errores para reducir
  complejidad cognitiva sin cambiar el comportamiento.
- Sustituye usos de `JLabel.CENTER` por `SwingConstants.CENTER` en `ResumenNino` para mejorar claridad y consistencia
  del código Swing.
- Actualiza `org.sonarsource.scanner.maven:sonar-maven-plugin` a `5.6.0.6792`.

## 2.7.6

- Amplía la cobertura unitaria de `Ventana`, validando acciones de menú, manejo de eventos y la lógica de
  comprobación de nuevas versiones.
- Refactoriza `PanelBusqueda` renombrando el campo `cantidad` a `jtCantidad` para mejorar claridad y consistencia
  interna sin cambiar el comportamiento.
- Actualiza la dependencia `io.github.jcprieto:loteria-navidad` a `6.0.8`.

## 2.7.5

- Fija explícitamente la versión de `org.sonarsource.scanner.maven:sonar-maven-plugin` en Maven para evitar fallos de
  resolución en CI al ejecutar el análisis de SonarQube.
- Simplifica el workflow `CI` para ejecutar `sonar:sonar` usando la configuración declarada en `pom.xml`.

## 2.7.4

- Corrige un problema de serialización en `PanelBusqueda` marcando `premioService` como `transient`, alineándolo con el
  resto de dependencias no serializables de la UI.
- Amplía la cobertura de tests Swing con `ResumenNinoTest`, validando estado inicial, ciclo de vida del `Timer`,
  refresco asíncrono, actualización de paneles internos y manejo de errores o respuestas nulas.
- Refactoriza `ResumenNino` con un punto de creación de `Conexion` sobreescribible para mejorar el aislamiento de los
  tests sin cambiar el comportamiento en producción.
- Actualiza dependencias de mantenimiento: `io.github.jcprieto:loteria-navidad` a `6.0.7` y
  `com.fasterxml.jackson.core:jackson-databind` a `2.21.2`.

## 2.7.3

- Refuerza la resiliencia de `MenuPrincipal` en cargas asíncronas del resumen, restaurando el estado de interrupción
  también en el flujo del sorteo de El Niño y reutilizando la clave i18n `dialogo.atencion` desde una constante.
- Amplía la cobertura de tests Swing con `ResumenNavidadTest`, validando inicialización del panel, gestión del `Timer`,
  actualización interna de premios y protección frente a refrescos concurrentes.
- Actualiza dependencias de mantenimiento: `org.mockito:mockito-core` a `5.23.0`,
  `io.github.jcprieto:loteria-navidad` a `6.0.6` y `maven-resources-plugin` a `3.5.0`.

## 2.7.2

- Mejora la resiliencia de carga del resumen de Navidad en `MenuPrincipal`, gestionando explícitamente
  `InterruptedException`, notificando al usuario y restaurando el estado de interrupción del hilo.
- Refactoriza `MenuPrincipal` para centralizar la clave i18n `warning.problema.servidor` en una constante reutilizable.
- Amplía la cobertura con `PanelInferiorTest`, validando layout, texto del botón "volver" y navegación al menú
  principal.
- Actualiza dependencias: `org.mockito:mockito-core` a `5.22.0` e `io.github.jcprieto:loteria-navidad` a `6.0.5`.

## 2.7.1

- Refuerza la seguridad del parseo XML al cargar la versión desde `pom.xml`, deshabilitando entidades externas/DTD y
  activando procesamiento seguro para mitigar XXE (Sonar `java:S2755`).
- Amplía la cobertura con tests unitarios de `LoteriaNavidad` para validar el flujo de arranque y el manejo de errores
  al aplicar el look and feel.
- Actualiza dependencias: `io.github.jcprieto:loteria-navidad` a `6.0.4`,
  `com.fasterxml.jackson.core:jackson-databind` a `2.21.1` y `maven-surefire-plugin` a `3.5.5`.
- Ajusta el orden de modificadores en `MenuPrincipal` (`final transient`) para mantener consistencia de estilo.

## 2.7.0

- Añade publicación automática del paquete `.deb` al repositorio APT mediante `repository_dispatch` desde el workflow
  de `Release`, habilitando actualizaciones en Linux vía `apt upgrade`.
- Documenta la instalación y actualización por APT en `README.md` y añade la guía técnica de CI/CD en
  `docs/apt-repository-cicd.md`.
- Actualiza dependencias clave: `io.github.jcprieto:loteria-navidad` a `6.0.3`, `org.junit.jupiter:junit-jupiter` a
  `6.0.3` y `org.jacoco:jacoco-maven-plugin` a `0.8.14`.
- Refactoriza el parseo de URL en `ResumenMouseListener` para mejorar legibilidad/reutilización y amplía la cobertura
  de tests con `AcercaDeTest`.

## 2.6.5

- Corrige la resolución de `surefireArgLine` en `maven-surefire-plugin` usando `@{surefireArgLine}` para mantener la
  inyección de JaCoCo y Mockito en tests.
- Actualiza el workflow de `Release` para ejecutar la build Linux en `ubuntu-22.04`, mejorando consistencia del
  empaquetado.
- Refuerza seguridad y reproducibilidad del pipeline moviendo `contents: write` al job `publish` y fijando
  `softprops/action-gh-release` a un commit específico.

## 2.6.4

- Ajusta el orden de agentes Java en tests (Mockito/JaCoCo) para evitar interferencias en la instrumentación y
  desbloquear la cobertura en SonarCloud.

## 2.6.3

- Corrige la integración de cobertura para SonarCloud evitando que el `argLine` de Surefire pise el agente de JaCoCo.
- Define explícitamente la ruta del reporte XML de JaCoCo para el análisis de cobertura.
- Actualiza `jacoco-maven-plugin` a `0.8.12` para mejorar compatibilidad con Java 21.

## 2.6.2

- Añade workflow de `SonarQube` en GitHub Actions con análisis en `push` a `master` y en `pull_request`.
- Incorpora perfil Maven `coverage` (JaCoCo XML) para enriquecer el análisis de calidad en Sonar.
- Ajusta el workflow de `Release` para ejecutarse solo tras finalizar con éxito `SonarQube` en `master`.
- Elimina la duplicación de tests en `Release`, manteniendo la validación en el pipeline de calidad previo.

## 2.6.1

- Corrige un posible `NullPointerException` en la comprobación asíncrona de actualizaciones (`Ventana`).
- Evita problemas de serialización marcando `resumenService` como `transient` en `MenuPrincipal`.
- Mejora la precisión de cálculos monetarios al consolidar el uso de `BigDecimal` en resultados y tests.
- Actualiza dependencias de build/runtime (`loteria-navidad` 6.0.0, `maven-resources-plugin` 3.4.0,
  `exec-maven-plugin` 3.6.3).
- Refuerza la generación del paquete Linux `.deb` con `--linux-package-deps` para compatibilidad entre Ubuntu
  22.04 y 24.04+.
- Amplía la documentación de empaquetado multiplataforma y añade badge de SonarCloud.

## 2.6.0

- Añade distribución nativa con `jpackage` para Linux (`.deb`), Windows (`.msi`) y macOS (`.dmg`) mediante perfiles
  Maven.
- Sustituye la descarga directa de nuevas versiones por apertura en navegador de la release de GitHub.
- Actualiza el diálogo "Acerca de" para mostrar licencia GPL v3 y centraliza carga de iconos con `IconUtils`.
- Refuerza la base de tests con utilidades comunes y nuevas pruebas unitarias para los cambios introducidos.

## 2.5.16

- Extrae servicios de obtención de datos para mejorar testabilidad sin reflexión.

## 2.5.15

- Elimina logs con más de 30 días.

## 2.5.14

- Centraliza textos de UI en bundles i18n.
- Mueve los logs a ~/.local/share/LoteriaDeNavidad/logs y rota archivos por tamaño.

## 2.5.13

- Inicializa la UI en el EDT.
- A\u00F1ade fallback a JOptionPane si notify-send no esta disponible.
- Carga el icono Acerca de desde classpath.

## 2.5.12

- Comparacion de versiones mas tolerante con sufijos en tags.
- Gestiona errores de descarga sin reintentos recursivos y notifica al usuario.

## 2.5.11

- Valida la cantidad jugada antes de buscar premios para evitar errores de formato.
- Calcula importes con BigDecimal y formatea la salida como moneda.

## 2.5.10

- Configura Mockito como agente para evitar la carga dinámica en tests.

## 2.5.9

- Corrección en el empaquetado de la aplicación.

## 2.5.8

- Evita bloqueos de la UI moviendo las llamadas de red a segundo plano y gestiona el ciclo de vida de los refrescos.
- Obtiene la versión desde `pom.xml` cuando se ejecuta desde IDE.
- Añade tests unitarios básicos para el menú principal.

## 2.5.7

- Correciones de seguridad y estabilidad.
- Actualización de la API para la obtención de los numeros premiados.

## 2.5.6

- Correciones de seguridad y estabilidad.

## 2.5.5

- Correciones de seguridad y estabilidad.

## 2.5.4

- Correciones de seguridad y estabilidad.

## 2.5.3

- Correciones de seguridad y estabilidad.

## 2.5.2

- Actualización de seguridad de despendencias.

## 2.5.1

- Actualización de seguridad de la librería de Jackson.

## 2.5.0

- Migracion a Java 11.
- Eliminamos ControlFX por problemas de compatibilidad con OpenJDK 11 y en su lugar utilizamos Systray en S.O Windows
  y LibNotofy en S.O Linux.

## 2.4.0

- Se elimina el icono de Systray, por la incompatibilidad con Gnome3 y utilizamos ControlsFX para monstrar las
  notificaciones.
