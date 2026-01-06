package es.jklabs.desktop.gui.paneles;

import es.jklabs.desktop.gui.Ventana;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class MenuPrincipalTest {

    private static JButton obtenerBoton(MenuPrincipal panel, String fieldName) throws Exception {
        Field field = MenuPrincipal.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (JButton) field.get(panel);
    }

    private static void invocarSetCargando(MenuPrincipal panel, boolean activo) throws Exception {
        Method method = MenuPrincipal.class.getDeclaredMethod("setCargando", boolean.class);
        method.setAccessible(true);
        SwingUtilities.invokeAndWait(() -> {
            try {
                method.invoke(panel, activo);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
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
        assertEquals("Resumen de la Lotería de Navidad", btnResumenNavidad.getText());
        assertEquals("Buscar premios de la Lotería de Navidad", btnBuscarPremioNavidad.getText());
        assertEquals("Resumen de la Lotería del Niño", btnResumenNino.getText());
        assertEquals("Buscar premios de la Lotería del Niño", btnBuscarPremioNino.getText());
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
