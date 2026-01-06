package es.jklabs.desktop.gui.paneles;

import es.jklabs.desktop.gui.Ventana;
import io.github.jcprieto.lib.loteria.conexion.Conexion;
import io.github.jcprieto.lib.loteria.enumeradores.Sorteo;
import io.github.jcprieto.lib.loteria.excepciones.PremioDecimoNoDisponibleException;
import io.github.jcprieto.lib.loteria.model.Premio;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PanelBusquedaTest {

    @BeforeAll
    static void setHeadless() {
        System.setProperty("java.awt.headless", "true");
    }

    private static void invokeBuscarPremioAsync(PanelBusqueda panel, String numero, String cantidad) throws Exception {
        Method method = PanelBusqueda.class.getDeclaredMethod("buscarPremioAsync", String.class, String.class);
        method.setAccessible(true);
        SwingUtilities.invokeAndWait(() -> {
            try {
                method.invoke(panel, numero, cantidad);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static JPanel getResultadoPanel(PanelBusqueda panel) throws Exception {
        Field field = PanelBusqueda.class.getDeclaredField("resultado");
        field.setAccessible(true);
        return (JPanel) field.get(panel);
    }

    private static boolean isBuscando(PanelBusqueda panel) throws Exception {
        Field field = PanelBusqueda.class.getDeclaredField("buscando");
        field.setAccessible(true);
        return (boolean) field.get(panel);
    }

    private static void waitForBusqueda(PanelBusqueda panel) throws Exception {
        long deadline = System.currentTimeMillis() + 2000;
        while (System.currentTimeMillis() < deadline && isBuscando(panel)) {
            Thread.sleep(20);
        }
        SwingUtilities.invokeAndWait(() -> {
            // Drena la cola del EDT para asegurar el done del SwingWorker
        });
    }

    @Test
    void buscarPremioAgregaResultadoCuandoExiste() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        TestPanelBusqueda panel = new TestPanelBusqueda(ventana, Sorteo.NAVIDAD);
        panel.setConexion(new Conexion() {
            @Override
            public Premio getPremio(Sorteo sorteo, String numero) throws IOException {
                Premio premio = new Premio();
                premio.setCantidad(123.0);
                return premio;
            }
        });

        invokeBuscarPremioAsync(panel, "12345", "1");
        waitForBusqueda(panel);

        JPanel resultado = getResultadoPanel(panel);
        assertEquals(1, resultado.getComponentCount());
        assertNull(panel.getLastWarning());
    }

    @Test
    void buscarPremioMuestraAvisoCuandoNoDisponible() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        TestPanelBusqueda panel = new TestPanelBusqueda(ventana, Sorteo.NAVIDAD);
        panel.setConexion(new Conexion() {
            @Override
            public Premio getPremio(Sorteo sorteo, String numero) throws IOException {
                throw new PremioDecimoNoDisponibleException("No disponible");
            }
        });

        invokeBuscarPremioAsync(panel, "12345", "1");
        waitForBusqueda(panel);

        JPanel resultado = getResultadoPanel(panel);
        assertEquals(0, resultado.getComponentCount());
        assertEquals("No disponible", panel.getLastWarning());
    }

    private static class TestPanelBusqueda extends PanelBusqueda {
        private Conexion conexion;
        private String lastWarning;

        TestPanelBusqueda(Ventana ventana, Sorteo sorteo) {
            super(ventana, sorteo);
        }

        void setConexion(Conexion conexion) {
            this.conexion = conexion;
        }

        String getLastWarning() {
            return lastWarning;
        }

        @Override
        protected Conexion createConexion() {
            return conexion;
        }

        @Override
        protected void showWarning(String message) {
            lastWarning = message;
        }
    }
}
