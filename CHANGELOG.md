# Changelog

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
