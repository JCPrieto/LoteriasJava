package es.jklabs.gui.utilidades;

import com.sshtools.twoslices.ToastType;
import com.sshtools.twoslices.ToasterFactory;
import com.sshtools.twoslices.ToasterSettings;
import es.jklabs.utilidades.BaseTest;
import es.jklabs.utilidades.Constantes;
import es.jklabs.utilidades.Mensajes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class GrowlsTest extends BaseTest {

    @AfterEach
    void resetHooks() {
        Growls.resetTestHooks();
    }

    @Test
    void mostrarInfoUsaTwoSlicesConTituloPredeterminado() {
        AtomicReference<ToastType> tipo = new AtomicReference<>();
        AtomicReference<String> titulo = new AtomicReference<>();
        AtomicReference<String> mensaje = new AtomicReference<>();
        AtomicReference<String> icono = new AtomicReference<>();
        Growls.setToastSenderForTests((ty, t, m, i) -> {
            tipo.set(ty);
            titulo.set(t);
            mensaje.set(m);
            icono.set(i);
        });
        Growls.setDialogDisplayerForTests((t, m, ty) -> {
            throw new AssertionError("No debe mostrar un dialogo");
        });

        Growls.mostrarInfo(null, "nueva.version.descargada");

        assertEquals(ToastType.INFO, tipo.get());
        assertEquals(Constantes.NOMBRE_APP, titulo.get());
        assertEquals(Mensajes.getMensaje("nueva.version.descargada"), mensaje.get());
        assertIconoAplicacion(icono.get());
    }

    @Test
    void mostrarInfoUsaTwoSlicesConTituloTraducido() {
        AtomicReference<String> titulo = new AtomicReference<>();
        Growls.setToastSenderForTests((ty, t, m, i) -> titulo.set(t));

        Growls.mostrarInfo("app.titulo", "nueva.version.descargada");

        assertEquals(Mensajes.getMensaje("app.titulo"), titulo.get());
    }

    @Test
    void mostrarInfoHaceFallbackADialogoCuandoTwoSlicesFalla() {
        AtomicReference<String> titulo = new AtomicReference<>();
        AtomicReference<String> mensaje = new AtomicReference<>();
        AtomicReference<Integer> tipo = new AtomicReference<>();
        AtomicInteger llamadas = new AtomicInteger();
        Growls.setToastSenderForTests((ty, t, m, i) -> {
            throw new IllegalStateException("notificaciones no disponibles");
        });
        Growls.setDialogDisplayerForTests((t, m, ty) -> {
            titulo.set(t);
            mensaje.set(m);
            tipo.set(ty);
            llamadas.incrementAndGet();
        });

        Growls.mostrarInfo(null, "nueva.version.descargada");

        assertEquals(1, llamadas.get());
        assertEquals(Constantes.NOMBRE_APP, titulo.get());
        assertEquals(Mensajes.getMensaje("nueva.version.descargada"), mensaje.get());
        assertEquals(JOptionPane.INFORMATION_MESSAGE, tipo.get());
    }

    @Test
    void mostrarErrorUsaTwoSlicesConTituloPredeterminado() {
        AtomicReference<ToastType> tipo = new AtomicReference<>();
        AtomicReference<String> titulo = new AtomicReference<>();
        AtomicReference<String> mensaje = new AtomicReference<>();
        AtomicReference<String> icono = new AtomicReference<>();
        Growls.setToastSenderForTests((ty, t, m, i) -> {
            tipo.set(ty);
            titulo.set(t);
            mensaje.set(m);
            icono.set(i);
        });

        Growls.mostrarError("descargar.nueva.version", new RuntimeException("boom"));

        assertEquals(ToastType.ERROR, tipo.get());
        assertEquals(Constantes.NOMBRE_APP, titulo.get());
        assertEquals(Mensajes.getError("descargar.nueva.version"), mensaje.get());
        assertIconoAplicacion(icono.get());
    }

    @Test
    void mostrarErrorUsaTwoSlicesConTituloTraducido() {
        AtomicReference<String> titulo = new AtomicReference<>();
        Growls.setToastSenderForTests((ty, t, m, i) -> titulo.set(t));

        Growls.mostrarError("app.titulo", "descargar.nueva.version", new RuntimeException("boom"));

        assertEquals(Mensajes.getMensaje("app.titulo"), titulo.get());
    }

    @Test
    void mostrarErrorHaceFallbackADialogoCuandoTwoSlicesFalla() {
        AtomicReference<String> titulo = new AtomicReference<>();
        AtomicReference<String> mensaje = new AtomicReference<>();
        AtomicReference<Integer> tipo = new AtomicReference<>();
        AtomicInteger llamadas = new AtomicInteger();
        Growls.setToastSenderForTests((ty, t, m, i) -> {
            throw new IllegalStateException("notificaciones no disponibles");
        });
        Growls.setDialogDisplayerForTests((t, m, ty) -> {
            titulo.set(t);
            mensaje.set(m);
            tipo.set(ty);
            llamadas.incrementAndGet();
        });

        Growls.mostrarError("app.titulo", "descargar.nueva.version", new RuntimeException("boom"));

        assertEquals(1, llamadas.get());
        assertEquals(Mensajes.getMensaje("app.titulo"), titulo.get());
        assertEquals(Mensajes.getError("descargar.nueva.version"), mensaje.get());
        assertEquals(JOptionPane.ERROR_MESSAGE, tipo.get());
    }

    @Test
    void initConfiguraNotificaciones() {
        AtomicInteger llamadas = new AtomicInteger();
        Growls.setSettingsConfigurerForTests(llamadas::incrementAndGet);

        Growls.init();

        assertEquals(1, llamadas.get());
    }

    @Test
    void mostrarInfoConfiguraNotificacionesAntesDeMostrarToast() {
        AtomicInteger configuraciones = new AtomicInteger();
        AtomicInteger notificaciones = new AtomicInteger();
        Growls.setSettingsConfigurerForTests(configuraciones::incrementAndGet);
        Growls.setToastSenderForTests((ty, t, m, i) -> notificaciones.incrementAndGet());

        Growls.mostrarInfo(null, "nueva.version.descargada");

        assertEquals(1, configuraciones.get());
        assertEquals(1, notificaciones.get());
    }

    @Test
    void initConfiguraTwoSlicesConNombreIconoYBackendLinux() {
        Growls.init();

        ToasterSettings settings = ToasterFactory.getSettings();

        assertEquals(Constantes.NOMBRE_APP, settings.getAppName());
        assertNotNull(settings.getDefaultImage());
        if (System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("linux")) {
            assertEquals("com.sshtools.twoslices.impl.DBUSNotifyToaster", settings.getPreferredToasterClassName());
        }
    }

    @Test
    void settersDePruebaRestauranImplementacionesPredeterminadasConNull() {
        assertDoesNotThrow(() -> {
            Growls.setDialogDisplayerForTests(null);
            Growls.setToastSenderForTests(null);
            Growls.setSettingsConfigurerForTests(null);
        });
    }

    private void assertIconoAplicacion(String icono) {
        assertNotNull(icono);
        assertTrue(icono.endsWith(".png"));
        assertTrue(Files.isRegularFile(Path.of(icono)));
    }
}
