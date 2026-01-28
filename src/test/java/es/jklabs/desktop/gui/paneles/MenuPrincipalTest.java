package es.jklabs.desktop.gui.paneles;

import es.jklabs.desktop.gui.Ventana;
import es.jklabs.utilidades.Mensajes;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class MenuPrincipalTest {

    private static JButton obtenerBoton(MenuPrincipal panel, String fieldName) throws Exception {
        return switch (fieldName) {
            case "btnResumenNavidad" -> panel.getBtnResumenNavidadForTests();
            case "btnBuscarPremioNavidad" -> panel.getBtnBuscarPremioNavidadForTests();
            case "btnResumenNino" -> panel.getBtnResumenNinoForTests();
            case "btnBuscarPremioNino" -> panel.getBtnBuscarPremioNinoForTests();
            default -> throw new IllegalArgumentException("Campo no soportado");
        };
    }

    private static void invocarSetCargando(MenuPrincipal panel, boolean activo) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            panel.setCargando(activo);
        });
    }

    private static <T> T crearEnEdt(EdtSupplier<T> supplier) throws Exception {
        final Object[] holder = new Object[1];
        SwingUtilities.invokeAndWait(() -> holder[0] = supplier.get());
        @SuppressWarnings("unchecked")
        T result = (T) holder[0];
        return result;
    }

    @Test
    void creaBotonesConTextoEsperado() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        MenuPrincipal panel = crearEnEdt(() -> new MenuPrincipal(ventana));

        JButton btnResumenNavidad = obtenerBoton(panel, "btnResumenNavidad");
        JButton btnBuscarPremioNavidad = obtenerBoton(panel, "btnBuscarPremioNavidad");
        JButton btnResumenNino = obtenerBoton(panel, "btnResumenNino");
        JButton btnBuscarPremioNino = obtenerBoton(panel, "btnBuscarPremioNino");

        assertNotNull(btnResumenNavidad);
        assertNotNull(btnBuscarPremioNavidad);
        assertNotNull(btnResumenNino);
        assertNotNull(btnBuscarPremioNino);
        assertEquals(Mensajes.getMensaje("panel.resumen.navidad"), btnResumenNavidad.getText());
        assertEquals(Mensajes.getMensaje("panel.buscar.navidad"), btnBuscarPremioNavidad.getText());
        assertEquals(Mensajes.getMensaje("panel.resumen.nino"), btnResumenNino.getText());
        assertEquals(Mensajes.getMensaje("panel.buscar.nino"), btnBuscarPremioNino.getText());
        assertEquals(4, panel.getComponentCount());
    }

    @Test
    void deshabilitaBotonesMientrasCarga() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        MenuPrincipal panel = crearEnEdt(() -> new MenuPrincipal(ventana));

        JButton btnResumenNavidad = obtenerBoton(panel, "btnResumenNavidad");
        JButton btnBuscarPremioNavidad = obtenerBoton(panel, "btnBuscarPremioNavidad");
        JButton btnResumenNino = obtenerBoton(panel, "btnResumenNino");
        JButton btnBuscarPremioNino = obtenerBoton(panel, "btnBuscarPremioNino");

        invocarSetCargando(panel, true);
        assertTrue(!btnResumenNavidad.isEnabled());
        assertTrue(!btnBuscarPremioNavidad.isEnabled());
        assertTrue(!btnResumenNino.isEnabled());
        assertTrue(!btnBuscarPremioNino.isEnabled());
        assertEquals(Cursor.WAIT_CURSOR, panel.getCursor().getType());

        invocarSetCargando(panel, false);
        assertTrue(btnResumenNavidad.isEnabled());
        assertTrue(btnBuscarPremioNavidad.isEnabled());
        assertTrue(btnResumenNino.isEnabled());
        assertTrue(btnBuscarPremioNino.isEnabled());
        assertEquals(Cursor.DEFAULT_CURSOR, panel.getCursor().getType());
    }

    private interface EdtSupplier<T> {
        T get();
    }
}
