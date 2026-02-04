package es.jklabs.desktop.gui.listener;

import es.jklabs.utilidades.BaseTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResumenMouseListenerMouseClickedTest extends BaseTest {

    @AfterEach
    void resetHooks() {
        ResumenMouseListener.resetTestHooks();
    }

    @Test
    void mouseClickedAbreUrlYColoreaEtiqueta() {
        JLabel label = new JLabel("https://example.com/mi ruta/archivo.pdf");
        ResumenMouseListener listener = new ResumenMouseListener(label);

        AtomicReference<URI> uriCapturada = new AtomicReference<>();
        ResumenMouseListener.setBrowserForTests(uriCapturada::set);

        MouseEvent event = new MouseEvent(label, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
                0, 10, 10, 1, false);
        listener.mouseClicked(event);

        assertEquals("https://example.com/mi%20ruta/archivo.pdf", uriCapturada.get().toString());
        assertEquals(Color.red, label.getForeground());
    }

    @Test
    void mouseClickedIgnoraOtrosComponentes() {
        JLabel label = new JLabel("https://example.com/archivo.pdf");
        JLabel other = new JLabel("otro");
        ResumenMouseListener listener = new ResumenMouseListener(label);
        Color original = label.getForeground();

        AtomicInteger llamadas = new AtomicInteger();
        ResumenMouseListener.setBrowserForTests(uri -> llamadas.incrementAndGet());

        MouseEvent event = new MouseEvent(other, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
                0, 10, 10, 1, false);
        listener.mouseClicked(event);

        assertEquals(0, llamadas.get());
        assertEquals(original, label.getForeground());
    }
}
