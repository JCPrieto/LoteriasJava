package es.jklabs.gui.utilidades;

import es.jklabs.utilidades.BaseTest;
import es.jklabs.utilidades.Constantes;
import es.jklabs.utilidades.Mensajes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class GrowlsTest extends BaseTest {

    @AfterEach
    void resetHooks() {
        Growls.resetTestHooks();
    }

    @Test
    void mostrarInfoHaceFallbackADialogoCuandoNoHayNotifySend() {
        AtomicReference<String> titulo = new AtomicReference<>();
        AtomicReference<String> mensaje = new AtomicReference<>();
        AtomicReference<Integer> tipo = new AtomicReference<>();
        AtomicInteger llamadas = new AtomicInteger();
        Growls.setNotifySendAvailableForTests(() -> false);
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
    void mostrarErrorHaceFallbackADialogoCuandoNoHayNotifySend() {
        AtomicReference<String> titulo = new AtomicReference<>();
        AtomicReference<String> mensaje = new AtomicReference<>();
        AtomicReference<Integer> tipo = new AtomicReference<>();
        AtomicInteger llamadas = new AtomicInteger();
        Growls.setNotifySendAvailableForTests(() -> false);
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
    void mostrarInfoUsaTrayCuandoEstaInicializado() {
        AtomicReference<String> titulo = new AtomicReference<>();
        AtomicReference<String> mensaje = new AtomicReference<>();
        AtomicReference<TrayIcon.MessageType> tipo = new AtomicReference<>();
        Growls.setTrayNotificationDisplayerForTests((t, m, ty) -> {
            titulo.set(t);
            mensaje.set(m);
            tipo.set(ty);
        });
        Growls.setNotifySendAvailableForTests(() -> {
            throw new AssertionError("No debe consultar notify-send");
        });

        Growls.mostrarInfo("app.titulo", "nueva.version.descargada");

        assertEquals(Mensajes.getMensaje("app.titulo"), titulo.get());
        assertEquals(Mensajes.getMensaje("nueva.version.descargada"), mensaje.get());
        assertEquals(TrayIcon.MessageType.INFO, tipo.get());
    }

    @Test
    void mostrarInfoEjecutaNotifySendCuandoEstaDisponible() {
        AtomicReference<String[]> command = new AtomicReference<>();
        Growls.setNotifySendAvailableForTests(() -> true);
        Growls.setCommandExecutorForTests(command::set);
        Growls.setDialogDisplayerForTests((t, m, ty) -> {
            throw new AssertionError("No debe mostrar un dialogo");
        });

        Growls.mostrarInfo(null, "nueva.version.descargada");

        assertArrayEquals(new String[]{"notify-send", Constantes.NOMBRE_APP,
                Mensajes.getMensaje("nueva.version.descargada"), "--icon=dialog-information"}, command.get());
    }

    @Test
    void mostrarInfoHaceFallbackCuandoNotifySendFalla() {
        AtomicReference<Integer> tipo = new AtomicReference<>();
        Growls.setNotifySendAvailableForTests(() -> true);
        Growls.setCommandExecutorForTests(command -> {
            throw new IOException("notify-send no disponible");
        });
        Growls.setDialogDisplayerForTests((t, m, ty) -> tipo.set(ty));

        Growls.mostrarInfo("app.titulo", "nueva.version.descargada");

        assertEquals(JOptionPane.INFORMATION_MESSAGE, tipo.get());
    }

    @Test
    void mostrarErrorUsaTrayConTituloPredeterminado() {
        AtomicReference<String> titulo = new AtomicReference<>();
        AtomicReference<TrayIcon.MessageType> tipo = new AtomicReference<>();
        Growls.setTrayNotificationDisplayerForTests((t, m, ty) -> {
            titulo.set(t);
            tipo.set(ty);
        });

        Growls.mostrarError("descargar.nueva.version", new RuntimeException("boom"));

        assertEquals(Constantes.NOMBRE_APP, titulo.get());
        assertEquals(TrayIcon.MessageType.ERROR, tipo.get());
    }

    @Test
    void mostrarErrorEjecutaNotifySendCuandoEstaDisponible() {
        AtomicReference<String[]> command = new AtomicReference<>();
        Growls.setNotifySendAvailableForTests(() -> true);
        Growls.setCommandExecutorForTests(command::set);

        Growls.mostrarError("app.titulo", "descargar.nueva.version", new RuntimeException("boom"));

        assertArrayEquals(new String[]{"notify-send", Mensajes.getMensaje("app.titulo"),
                Mensajes.getError("descargar.nueva.version"), "--icon=dialog-error"}, command.get());
    }

    @Test
    void mostrarErrorHaceFallbackCuandoNotifySendFalla() {
        AtomicReference<Integer> tipo = new AtomicReference<>();
        Growls.setNotifySendAvailableForTests(() -> true);
        Growls.setCommandExecutorForTests(command -> {
            throw new IOException("notify-send no disponible");
        });
        Growls.setDialogDisplayerForTests((t, m, ty) -> tipo.set(ty));

        Growls.mostrarError(null, "descargar.nueva.version", new RuntimeException("boom"));

        assertEquals(JOptionPane.ERROR_MESSAGE, tipo.get());
    }

    @Test
    void initNoInicializaTrayFueraDeWindows() {
        Growls.setWindowsForTests(() -> false);
        Growls.setTrayInitializerForTests(() -> {
            throw new AssertionError("No debe inicializar el tray");
        });

        Growls.init();
    }

    @Test
    void initInicializaTrayEnWindows() {
        AtomicInteger mensajes = new AtomicInteger();
        Growls.setWindowsForTests(() -> true);
        Growls.setTrayInitializerForTests(() -> (t, m, ty) -> mensajes.incrementAndGet());

        Growls.init();
        Growls.mostrarInfo(null, "nueva.version.descargada");

        assertEquals(1, mensajes.get());
    }

    @Test
    void initMantieneTrayNuloCuandoLaInicializacionFalla() {
        AtomicInteger dialogos = new AtomicInteger();
        Growls.setWindowsForTests(() -> true);
        Growls.setTrayInitializerForTests(() -> {
            throw new AWTException("tray no disponible");
        });
        Growls.setNotifySendAvailableForTests(() -> false);
        Growls.setDialogDisplayerForTests((t, m, ty) -> dialogos.incrementAndGet());

        Growls.init();
        Growls.mostrarInfo(null, "nueva.version.descargada");

        assertEquals(1, dialogos.get());
    }

    @Test
    void detectaNotifySendEnLaPrimeraRuta() {
        AtomicInteger comprobaciones = new AtomicInteger();
        AtomicInteger ejecuciones = new AtomicInteger();
        Growls.setExecutableCheckerForTests(path -> {
            comprobaciones.incrementAndGet();
            return "/usr/bin/notify-send".equals(path);
        });
        Growls.setNotifySendAvailableForTests(null);
        Growls.setCommandExecutorForTests(command -> ejecuciones.incrementAndGet());

        Growls.mostrarInfo(null, "nueva.version.descargada");

        assertEquals(1, comprobaciones.get());
        assertEquals(1, ejecuciones.get());
    }

    @Test
    void detectaNotifySendEnLaSegundaRuta() {
        AtomicInteger comprobaciones = new AtomicInteger();
        AtomicInteger ejecuciones = new AtomicInteger();
        Growls.setExecutableCheckerForTests(path -> {
            comprobaciones.incrementAndGet();
            return "/bin/notify-send".equals(path);
        });
        Growls.setNotifySendAvailableForTests(null);
        Growls.setCommandExecutorForTests(command -> ejecuciones.incrementAndGet());

        Growls.mostrarInfo(null, "nueva.version.descargada");

        assertEquals(2, comprobaciones.get());
        assertEquals(1, ejecuciones.get());
    }

    @Test
    void usaDialogoCuandoNotifySendNoExisteEnNingunaRuta() {
        AtomicInteger dialogos = new AtomicInteger();
        Growls.setExecutableCheckerForTests(path -> false);
        Growls.setNotifySendAvailableForTests(null);
        Growls.setDialogDisplayerForTests((t, m, ty) -> dialogos.incrementAndGet());

        Growls.mostrarInfo(null, "nueva.version.descargada");

        assertEquals(1, dialogos.get());
    }

    @Test
    void settersDePruebaRestauranImplementacionesPredeterminadasConNull() {
        assertDoesNotThrow(() -> {
            Growls.setDialogDisplayerForTests(null);
            Growls.setCommandExecutorForTests(null);
            Growls.setWindowsForTests(null);
            Growls.setTrayInitializerForTests(null);
            Growls.setExecutableCheckerForTests(null);
        });
    }
}
