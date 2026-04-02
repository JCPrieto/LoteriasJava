![LoteriasJava](src/main/resources/img/icons/line-globe.png) LoteriasJava
============
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=JCPrieto_LoteriasJava&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=JCPrieto_LoteriasJava)

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
  Salida: `target/dist/loteriadenavidad_<version>_amd64.deb`.
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

### Generación de empaquetados por SO ###

La generación de paquetes nativos debe hacerse en el sistema operativo destino.

#### Linux (Ubuntu) `.deb`

1. Verifica Java y `jpackage`:
  * `java -version`
  * `jpackage --version`
2. Genera el paquete:
  * `mvn clean -Pdist-linux package`
3. Resultado:
  * `target/dist/loteriadenavidad_<version>_amd64.deb`
4. Verificación local (opcional):
  * `sudo apt install ./target/dist/loteriadenavidad_<version>_amd64.deb`
  * `apt show loteriadenavidad`

Nota: para evitar incompatibilidades de dependencias entre Ubuntu 22.04 y 24.04+, el paquete define alternativas
(`libasound2 | libasound2t64`, etc.).

### Actualizaciones en Linux vía APT ###

El proyecto publica paquetes `.deb` en el repositorio APT:

* `https://jcprieto.github.io/jklabs-apt-repo`
* El paquete publicado en APT se construye en Ubuntu 22.04 para mantener una variante estable por versión.

Configuración en Debian/Ubuntu:

```bash
curl -fsSL https://jcprieto.github.io/jklabs-apt-repo/public.key | gpg --dearmor | sudo tee /usr/share/keyrings/jklabs-archive-keyring.gpg >/dev/null
echo "deb [signed-by=/usr/share/keyrings/jklabs-archive-keyring.gpg] https://jcprieto.github.io/jklabs-apt-repo stable main" | sudo tee /etc/apt/sources.list.d/jklabs.list
sudo apt update
sudo apt install loteriadenavidad
```

Después, las nuevas versiones se reciben con:

* `sudo apt upgrade`

#### Windows `.msi`

1. Instala JDK 21 (con `jpackage`) y WiX Toolset.
2. Abre terminal en la raíz del proyecto.
3. Genera el paquete:
  * `mvn clean -Pdist-windows package`
4. Resultado:
  * `target/dist/LoteriaDeNavidad-<version>.msi`

#### macOS `.dmg`

1. Instala JDK 21 (con `jpackage`) en macOS.
2. Abre terminal en la raíz del proyecto.
3. Genera el paquete:
  * `mvn clean -Pdist-mac package`
4. Resultado:
  * `target/dist/LoteriaDeNavidad-<version>.dmg`

### Tecnologías utilizadas ###

* Iconos:
  * Papirus https://github.com/PapirusDevelopmentTeam/papirus-icon-theme
* Api:
  * Loterías y apuestas del estado https://www.loteriasyapuestas.es
* Librerias:
  * Jackson https://github.com/FasterXML/jackson-core/wiki
  * GitHub Releases https://docs.github.com/es/repositories/releasing-projects-on-github/about-releases

### Changelog ###

El historial de versiones está disponible en [`CHANGELOG.md`](CHANGELOG.md).

### Notas de CI/CD ###

La documentación de publicación automática de `.deb` hacia el repositorio APT central está en:

* [docs/apt-repository-cicd.md](docs/apt-repository-cicd.md)
* El workflow de `CI` ejecuta el análisis de SonarQube con `mvn -Pcoverage verify sonar:sonar`, apoyándose en la
  versión fijada de `sonar-maven-plugin` en `pom.xml` para evitar roturas por resolución dinámica del plugin.

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
