LoteriasJava
============

Aplicación para la comprobación de los numeros premiados de la lotería de Navidad y de El Niño

### Requisitos ###

* Java 11
* LibNotify (Para las notificaciones en Linux)

### Tecnologías utilizadas ###

* Iconos: 
    * Papirus https://github.com/PapirusDevelopmentTeam/papirus-icon-theme
* Api:
    * El País http://servicios.elpais.com
* Librerias:
    * Jackson https://github.com/FasterXML/jackson-core/wiki
    * Firebase https://firebase.google.com
    
### Changelog ###

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
