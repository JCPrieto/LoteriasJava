package es.jklabs.desktop.gui.paneles;

import es.jklabs.desktop.gui.Ventana;
import es.jklabs.lib.loteria.conexion.Conexion;
import es.jklabs.lib.loteria.enumeradores.Sorteo;
import es.jklabs.lib.loteria.excepciones.PremioDecimoNoDisponibleException;
import es.jklabs.lib.loteria.model.Premio;
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

    private static void invokeBuscarPremio(PanelBusqueda panel, String numero, String cantidad) throws Exception {
        Method method = PanelBusqueda.class.getDeclaredMethod("buscarPremio", String.class, String.class);
        method.setAccessible(true);
        method.invoke(panel, numero, cantidad);
    }

    private static JPanel getResultadoPanel(PanelBusqueda panel) throws Exception {
        Field field = PanelBusqueda.class.getDeclaredField("resultado");
        field.setAccessible(true);
        return (JPanel) field.get(panel);
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

        invokeBuscarPremio(panel, "12345", "1");

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

        invokeBuscarPremio(panel, "12345", "1");

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
