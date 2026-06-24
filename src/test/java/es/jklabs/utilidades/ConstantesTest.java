package es.jklabs.utilidades;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ConstantesTest extends BaseTest {

    @TempDir
    Path tempDir;

    @Test
    void constructorPrivadoNoHaceNada() throws Exception {
        Constructor<Constantes> constructor = Constantes.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertDoesNotThrow(() -> {
            try {
                constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new AssertionError(e);
            }
        });
    }

    @Test
    void cargarVersionPriorizaPropertiesYLuegoPom() {
        assertEquals("1.2.3", Constantes.cargarVersion(() -> "1.2.3", () -> "4.5.6"));
        assertEquals("4.5.6", Constantes.cargarVersion(() -> null, () -> "4.5.6"));
        assertEquals("desconocida", Constantes.cargarVersion(() -> null, () -> null));
    }

    @Test
    void cargarVersionDesdePropertiesDevuelveNullSinStreamVersionOConVersionEnBlanco() {
        assertNull(Constantes.cargarVersionDesdeProperties(() -> null));
        assertNull(Constantes.cargarVersionDesdeProperties(() -> stream("name=app\n")));
        assertNull(Constantes.cargarVersionDesdeProperties(() -> stream("version=   \n")));
    }

    @Test
    void cargarVersionDesdePropertiesDevuelveVersionNormalizada() {
        assertEquals("2.7.12", Constantes.cargarVersionDesdeProperties(() -> stream("version= 2.7.12 \n")));
    }

    @Test
    void cargarVersionDesdePropertiesDevuelveNullSiFallaElProveedor() {
        assertNull(Constantes.cargarVersionDesdeProperties(() -> {
            throw new IOException("fallo al abrir");
        }));
    }

    @Test
    void cargarVersionDesdePropertiesDevuelveNullSiFallaLaLectura() {
        assertNull(Constantes.cargarVersionDesdeProperties(() -> new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("fallo de lectura");
            }
        }));
    }

    @Test
    void cargarVersionDesdePropertiesDevuelveNullSiFallaElCierre() {
        assertNull(Constantes.cargarVersionDesdeProperties(() -> new InputStream() {
            @Override
            public int read() {
                return -1;
            }

            @Override
            public void close() throws IOException {
                throw new IOException("fallo de cierre");
            }
        }));
    }

    @Test
    void cargarVersionDesdePropertiesDevuelveNullSiFallanLecturaYCierre() {
        assertNull(Constantes.cargarVersionDesdeProperties(() -> new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("fallo de lectura");
            }

            @Override
            public void close() throws IOException {
                throw new IOException("fallo de cierre");
            }
        }));
    }

    @Test
    void cargarVersionDesdePomDevuelveNullSiNoExisteONoHayVersion() throws IOException {
        assertNull(Constantes.cargarVersionDesdePom(tempDir.resolve("pom.xml")));

        Path pomSinVersion = escribirPom("""
                <project>
                    <modelVersion>4.0.0</modelVersion>
                </project>
                """);

        assertNull(Constantes.cargarVersionDesdePom(pomSinVersion));
    }

    @Test
    void cargarVersionDesdePomLeeVersionDirecta() throws IOException {
        Path pom = escribirPom("""
                <project xmlns="http://maven.apache.org/POM/4.0.0">
                    <modelVersion>4.0.0</modelVersion>
                    <version> 3.4.5 </version>
                </project>
                """);

        assertEquals("3.4.5", Constantes.cargarVersionDesdePom(pom));
    }

    @Test
    void cargarVersionDesdePomUsaVersionDelParentComoFallback() throws IOException {
        Path pom = escribirPom("""
                <project xmlns="http://maven.apache.org/POM/4.0.0">
                    <modelVersion>4.0.0</modelVersion>
                    <parent>
                        <groupId>es.jklabs</groupId>
                        <artifactId>parent</artifactId>
                        <version>5.6.7</version>
                    </parent>
                    <version>   </version>
                </project>
                """);

        assertEquals("5.6.7", Constantes.cargarVersionDesdePom(pom));
    }

    @Test
    void cargarVersionDesdePomDevuelveNullConXmlInvalidoODoctype() throws IOException {
        assertNull(Constantes.cargarVersionDesdePom(escribirPom("<project><version>1.0.0</project>")));
        assertNull(Constantes.cargarVersionDesdePom(escribirPom("""
                <!DOCTYPE project [
                    <!ENTITY version SYSTEM "file:///etc/passwd">
                ]>
                <project>
                    <version>&version;</version>
                </project>
                """)));
    }

    @Test
    void normalizarVersionGestionaNullBlancoYVersionValida() {
        assertNull(Constantes.normalizarVersion(null));
        assertNull(Constantes.normalizarVersion("  "));
        assertEquals("1.0.0", Constantes.normalizarVersion(" 1.0.0 "));
    }

    private Path escribirPom(String contenido) throws IOException {
        Path pom = Files.createTempFile(tempDir, "pom-", ".xml");
        Files.writeString(pom, contenido, StandardCharsets.UTF_8);
        return pom;
    }

    private InputStream stream(String contenido) {
        return new ByteArrayInputStream(contenido.getBytes(StandardCharsets.UTF_8));
    }
}
