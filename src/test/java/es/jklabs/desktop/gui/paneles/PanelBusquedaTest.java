package es.jklabs.desktop.gui.paneles;

import es.jklabs.desktop.gui.Ventana;
import es.jklabs.utilidades.BaseTest;
import es.jklabs.utilidades.Mensajes;
import io.github.jcprieto.lib.loteria.enumeradores.EstadoSorteo;
import io.github.jcprieto.lib.loteria.enumeradores.Sorteo;
import io.github.jcprieto.lib.loteria.model.Premio;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PanelBusquedaTest extends BaseTest {

    private static void waitForBusqueda(PanelBusqueda panel) throws Exception {
        long deadline = System.currentTimeMillis() + 2000;
        while (System.currentTimeMillis() < deadline && panel.isBuscandoForTests()) {
            Thread.sleep(20);
        }
        SwingUtilities.invokeAndWait(() -> {
            // Drena la cola del EDT para asegurar el done del SwingWorker
        });
    }

    @Test
    void buscarPremioAgregaResultadoCuandoExiste() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        PanelBusqueda.PremioService premioService = (sorteo, numero) -> {
            Premio premio = new Premio();
            premio.setCantidad(BigDecimal.valueOf(123.0));
            return premio;
        };
        TestPanelBusqueda panel = new TestPanelBusqueda(ventana, Sorteo.NAVIDAD, premioService);

        panel.getNumeroFieldForTests().setText("12345");
        panel.getCantidadFieldForTests().setText("1");
        panel.getBuscarButtonForTests().doClick();
        waitForBusqueda(panel);

        JPanel resultado = panel.getResultadoPanelForTests();
        assertEquals(1, resultado.getComponentCount());
        assertNull(panel.getLastWarningForTests());
    }

    @Test
    void buscarPremioMuestraAvisoCuandoNoHayDatos() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        PanelBusqueda.PremioService premioService = (sorteo, numero) -> {
            Premio premio = new Premio();
            premio.setCantidad(BigDecimal.ZERO);
            premio.setEstado(EstadoSorteo.NO_INICIADO);
            return premio;
        };
        TestPanelBusqueda panel = new TestPanelBusqueda(ventana, Sorteo.NAVIDAD, premioService);

        panel.getNumeroFieldForTests().setText("12345");
        panel.getCantidadFieldForTests().setText("1");
        panel.getBuscarButtonForTests().doClick();
        waitForBusqueda(panel);

        JPanel resultado = panel.getResultadoPanelForTests();
        assertEquals(0, resultado.getComponentCount());
        assertEquals(Mensajes.getMensaje("warning.no.datos"), panel.getLastWarningForTests());
    }

    private static class TestPanelBusqueda extends PanelBusqueda {
        private String lastWarning;

        TestPanelBusqueda(Ventana ventana, Sorteo sorteo, PremioService premioService) {
            super(ventana, sorteo, premioService);
        }

        String getLastWarningForTests() {
            return lastWarning;
        }

        @Override
        protected void showWarning(String message) {
            lastWarning = message;
        }
    }
}
