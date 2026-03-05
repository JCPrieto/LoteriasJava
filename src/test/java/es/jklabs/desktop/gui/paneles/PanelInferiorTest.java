package es.jklabs.desktop.gui.paneles;

import es.jklabs.desktop.gui.Ventana;
import es.jklabs.utilidades.BaseTest;
import es.jklabs.utilidades.Mensajes;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PanelInferiorTest extends BaseTest {

    private static <T> T crearEnEdt(EdtSupplier<T> supplier) throws Exception {
        final Object[] holder = new Object[1];
        SwingUtilities.invokeAndWait(() -> holder[0] = supplier.get());
        @SuppressWarnings("unchecked")
        T result = (T) holder[0];
        return result;
    }

    @Test
    void creaBotonVolverConTextoEsperado() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        PanelInferior panel = crearEnEdt(() -> new PanelInferior(ventana));

        assertEquals(new BorderLayout(10, 10).getClass(), panel.getLayout().getClass());
        assertEquals(1, panel.getComponentCount());

        Component component = panel.getComponent(0);
        assertTrue(component instanceof JButton);

        JButton botonVolver = (JButton) component;
        assertTrue(botonVolver.getActionListeners().length > 0);
        assertEquals(Mensajes.getMensaje("panel.volver"), botonVolver.getText());
    }

    @Test
    void clickEnVolverRegresaAMenuPrincipal() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        PanelInferior panel = crearEnEdt(() -> new PanelInferior(ventana));

        SwingUtilities.invokeAndWait(() -> ((JButton) panel.getComponent(0)).doClick());

        InOrder inOrder = Mockito.inOrder(ventana);
        inOrder.verify(ventana).eliminarPanelInferior();
        inOrder.verify(ventana).setPanel(Mockito.argThat(p -> p instanceof MenuPrincipal));
        inOrder.verify(ventana).pack();
    }

    @Test
    void ignoraEventosQueNoVienenDelBotonVolver() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        PanelInferior panel = crearEnEdt(() -> new PanelInferior(ventana));

        panel.actionPerformed(new ActionEvent(new JButton("otro"), ActionEvent.ACTION_PERFORMED, "cmd"));

        Mockito.verifyNoInteractions(ventana);
    }

    private interface EdtSupplier<T> {
        T get();
    }
}
