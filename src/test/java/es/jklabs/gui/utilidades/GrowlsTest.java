package es.jklabs.gui.utilidades;

import es.jklabs.utilidades.BaseTest;
import es.jklabs.utilidades.Constantes;
import es.jklabs.utilidades.Mensajes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
