LoteriasJava
============

Aplicación para la comprobación de los numeros premiados de la lotería de Navidad y de El Niño

### Requisitos ###

* Java 21
* LibNotify (Para las notificaciones en Linux)

### Tecnologías utilizadas ###

* Iconos:
  * Papirus https://github.com/PapirusDevelopmentTeam/papirus-icon-theme
* Api:
  * Loterías y apuestas del estado https://www.loteriasyapuestas.es
* Librerias:
  * Jackson https://github.com/FasterXML/jackson-core/wiki
  * GitHub Releases https://docs.github.com/es/repositories/releasing-projects-on-github/about-releases

### Changelog ###

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

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
