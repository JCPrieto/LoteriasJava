![LoteriasJava](src/main/resources/img/icons/line-globe.png) LoteriasJava
============

Aplicación para la comprobación de los numeros premiados de la lotería de Navidad y de El Niño

### Requisitos ###

* Java 21
* LibNotify (Para las notificaciones en Linux)
* Para generar paquetes nativos: JDK 21 con `jpackage` y las herramientas del sistema operativo.
* Linux (Ubuntu): `dpkg-deb` (incluido por defecto) para crear `.deb`.
* Windows: WiX Toolset para crear `.msi`.
* macOS: herramientas del sistema (`hdiutil`) para crear `.dmg`.

### Distribución nativa (doble clic) ###

Los paquetes se generan con Maven y `jpackage`. Debes ejecutar el comando en el SO destino.

* Linux (Ubuntu): `mvn -Pdist-linux package`
  Salida: `target/dist/LoteriaDeNavidad-<version>_amd64.deb` (o similar).
* Windows: `mvn -Pdist-windows package`
  Salida: `target/dist/LoteriaDeNavidad-<version>.msi`.
* macOS: `mvn -Pdist-mac package`
  Salida: `target/dist/LoteriaDeNavidad-<version>.dmg`.

Al instalar el paquete, la app queda disponible en el menú del sistema y se puede abrir con doble clic.

Iconos: se usan los archivos en `src/main/resources/img/icons/app/` (`icon.png`, `icon.ico`, `icon.icns`). Actualmente
están generados a partir de un PNG 32x32, por lo que se recomienda sustituirlos por un PNG de mayor resolución si se
quiere mejor calidad visual.

AppStream (Linux): la ficha que ve GNOME Software se genera desde
`src/main/jpackage/linux/loteriadenavidad-LoteriaDeNavidad.metainfo.xml`. Ahí se actualizan descripción, licencia,
URL y notas de versión.

### Tecnologías utilizadas ###

* Iconos:
  * Papirus https://github.com/PapirusDevelopmentTeam/papirus-icon-theme
* Api:
  * Loterías y apuestas del estado https://www.loteriasyapuestas.es
* Librerias:
  * Jackson https://github.com/FasterXML/jackson-core/wiki
  * GitHub Releases https://docs.github.com/es/repositories/releasing-projects-on-github/about-releases

### Changelog ###

* 2.6.0
  * Añade distribución nativa con `jpackage` para Linux (`.deb`), Windows (`.msi`) y macOS (`.dmg`) mediante perfiles
    Maven.
  * Sustituye la descarga directa de nuevas versiones por apertura en navegador de la release de GitHub.
  * Actualiza el diálogo "Acerca de" para mostrar licencia GPL v3 y centraliza carga de iconos con `IconUtils`.
  * Refuerza la base de tests con utilidades comunes y nuevas pruebas unitarias para los cambios introducidos.

* 2.5.16
  * Extrae servicios de obtención de datos para mejorar testabilidad sin reflexión.

* 2.5.15
  * Elimina logs con más de 30 días.

* 2.5.14
    * Centraliza textos de UI en bundles i18n.
    * Mueve los logs a ~/.local/share/LoteriaDeNavidad/logs y rota archivos por tamaño.

* 2.5.13
  * Inicializa la UI en el EDT.
  * A\u00F1ade fallback a JOptionPane si notify-send no esta disponible.
  * Carga el icono Acerca de desde classpath.

* 2.5.12
  * Comparacion de versiones mas tolerante con sufijos en tags.
  * Gestiona errores de descarga sin reintentos recursivos y notifica al usuario.

* 2.5.11
  * Valida la cantidad jugada antes de buscar premios para evitar errores de formato.
  * Calcula importes con BigDecimal y formatea la salida como moneda.

* 2.5.10
  * Configura Mockito como agente para evitar la carga dinámica en tests.

* 2.5.9
  * Corrección en el empaquetado de la aplicación.

* 2.5.8

  * Evita bloqueos de la UI moviendo las llamadas de red a segundo plano y gestiona el ciclo de vida de los refrescos.
  * Obtiene la versión desde `pom.xml` cuando se ejecuta desde IDE.
  * Añade tests unitarios básicos para el menú principal.

* 2.5.7

  * Correciones de seguridad y estabilidad
  * Actualización de la API para la obtención de los numeros premiados.

* 2.5.6

  * Correciones de seguridad y estabilidad

* 2.5.5

  * Correciones de seguridad y estabilidad

* 2.5.4

  * Correciones de seguridad y estabilidad

* 2.5.3

  * Correciones de seguridad y estabilidad

* 2.5.2

  * Actualización de seguridad de despendencias.

* 2.5.1

  * Actualización de seguridad de la librería de Jackson

* 2.5.0

  * Migracion a Java 11.
  * Eliminamos ControlFX por problemas de compatibilidad con OpenJDK 11 y en su lugar utilizamos Systray en S.O Windows
    y LibNotofy en S.O Linux.

* 2.4.0:

  * Se elimina el icono de Systray, por la incompatibilidad con Gnome3 y utilizamos ControlsFX para monstrar las
    notificaciones.

### Licencia ###

![Licencia GPL v3](src/main/resources/img/icons/gplv3-with-text-136x68.png)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see http://www.gnu.org/licenses
