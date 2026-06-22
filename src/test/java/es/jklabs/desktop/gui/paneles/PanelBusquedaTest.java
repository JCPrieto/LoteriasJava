package es.jklabs.desktop.gui.paneles;

import es.jklabs.desktop.gui.Ventana;
import es.jklabs.utilidades.BaseTest;
import es.jklabs.utilidades.Mensajes;
import io.github.jcprieto.lib.loteria.enumeradores.EstadoSorteo;
import io.github.jcprieto.lib.loteria.enumeradores.Sorteo;
import io.github.jcprieto.lib.loteria.excepciones.PremioDecimoNoDisponibleException;
import io.github.jcprieto.lib.loteria.model.Premio;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

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

    private static boolean isConsumedAfterKeyTyped(JTextField field, char keyChar) {
        KeyEvent event = new KeyEvent(field, KeyEvent.KEY_TYPED, 0L, 0,
                KeyEvent.VK_UNDEFINED, keyChar);
        for (var listener : field.getKeyListeners()) {
            listener.keyTyped(event);
        }
        return event.isConsumed();
    }

    @Test
    void buscarPremioMuestraAvisoCuandoPremioEsNull() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        TestPanelBusqueda panel = new TestPanelBusqueda(ventana, Sorteo.NAVIDAD, (sorteo, numero) -> null);

        panel.getNumeroFieldForTests().setText("12345");
        panel.getCantidadFieldForTests().setText("1");
        panel.getBuscarButtonForTests().doClick();
        waitForBusqueda(panel);

        assertEquals(0, panel.getResultadoPanelForTests().getComponentCount());
        assertEquals(Mensajes.getMensaje("warning.no.datos"), panel.getLastWarningForTests());
    }

    @Test
    void buscarPremioMuestraAvisoCuandoCantidadNoEsValida() {
        Ventana ventana = Mockito.mock(Ventana.class);
        AtomicInteger llamadas = new AtomicInteger();
        TestPanelBusqueda panel = new TestPanelBusqueda(ventana, Sorteo.NAVIDAD, (sorteo, numero) -> {
            llamadas.incrementAndGet();
            return new Premio();
        });

        panel.getNumeroFieldForTests().setText("12345");
        panel.getCantidadFieldForTests().setText("0");
        panel.getBuscarButtonForTests().doClick();

        assertEquals(0, llamadas.get());
        assertFalse(panel.isBuscandoForTests());
        assertEquals(Mensajes.getMensaje("warning.cantidad.invalida"), panel.getLastWarningForTests());
    }

    @Test
    void parseCantidadAceptaSoloNumerosPositivos() {
        Ventana ventana = Mockito.mock(Ventana.class);
        TestPanelBusqueda panel = new TestPanelBusqueda(ventana, Sorteo.NAVIDAD, (sorteo, numero) -> new Premio());

        assertEquals(new BigDecimal("1.50"), panel.parseCantidad(" 1.50 "));
        assertNull(panel.parseCantidad(null));
        assertNull(panel.parseCantidad(""));
        assertNull(panel.parseCantidad("-1"));
        assertNull(panel.parseCantidad("abc"));
    }

    @Test
    void buscarPremioConNumeroVacioNoHaceNada() {
        Ventana ventana = Mockito.mock(Ventana.class);
        AtomicInteger llamadas = new AtomicInteger();
        TestPanelBusqueda panel = new TestPanelBusqueda(ventana, Sorteo.NAVIDAD, (sorteo, numero) -> {
            llamadas.incrementAndGet();
            return new Premio();
        });

        panel.getNumeroFieldForTests().setText("");
        panel.getCantidadFieldForTests().setText("1");
        panel.getBuscarButtonForTests().doClick();

        assertEquals(0, llamadas.get());
        assertNull(panel.getLastWarningForTests());
    }

    @Test
    void buscarPremioIgnoraNuevaBusquedaMientrasHayUnaActiva() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        CountDownLatch servicioIniciado = new CountDownLatch(1);
        CountDownLatch liberarServicio = new CountDownLatch(1);
        AtomicInteger llamadas = new AtomicInteger();
        TestPanelBusqueda panel = new TestPanelBusqueda(ventana, Sorteo.NAVIDAD, (sorteo, numero) -> {
            llamadas.incrementAndGet();
            servicioIniciado.countDown();
            try {
                assertTrue(liberarServicio.await(2, TimeUnit.SECONDS));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException(e);
            }
            Premio premio = new Premio();
            premio.setCantidad(BigDecimal.TEN);
            return premio;
        });

        panel.getNumeroFieldForTests().setText("12345");
        panel.getCantidadFieldForTests().setText("1");
        panel.getBuscarButtonForTests().doClick();
        assertTrue(servicioIniciado.await(2, TimeUnit.SECONDS));

        panel.actionPerformed(new ActionEvent(panel.getBuscarButtonForTests(), ActionEvent.ACTION_PERFORMED, "buscar"));

        assertEquals(1, llamadas.get());
        liberarServicio.countDown();
        waitForBusqueda(panel);
        assertEquals(1, panel.getResultadoPanelForTests().getComponentCount());
    }

    @Test
    void buscarPremioMuestraMensajeDeExcepcionDeDecimoNoDisponible() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        String mensaje = "El premio no esta disponible";
        TestPanelBusqueda panel = new TestPanelBusqueda(ventana, Sorteo.NAVIDAD, (sorteo, numero) -> {
            throw new PremioDecimoNoDisponibleException(mensaje);
        });

        panel.getNumeroFieldForTests().setText("12345");
        panel.getCantidadFieldForTests().setText("1");
        panel.getBuscarButtonForTests().doClick();
        waitForBusqueda(panel);

        assertEquals(mensaje, panel.getLastWarningForTests());
        assertEquals(0, panel.getResultadoPanelForTests().getComponentCount());
    }

    @Test
    void buscarPremioMuestraAvisoGenericoCuandoServicioFalla() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        TestPanelBusqueda panel = new TestPanelBusqueda(ventana, Sorteo.NAVIDAD, (sorteo, numero) -> {
            throw new IOException("Sin conexion");
        });

        panel.getNumeroFieldForTests().setText("12345");
        panel.getCantidadFieldForTests().setText("1");
        panel.getBuscarButtonForTests().doClick();
        waitForBusqueda(panel);

        assertEquals(Mensajes.getMensaje("warning.problema.servidor"), panel.getLastWarningForTests());
        assertEquals(0, panel.getResultadoPanelForTests().getComponentCount());
    }

    @Test
    void limpiarEliminaResultadosYCampos() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        PanelBusqueda.PremioService premioService = (sorteo, numero) -> {
            Premio premio = new Premio();
            premio.setCantidad(BigDecimal.TEN);
            return premio;
        };
        TestPanelBusqueda panel = new TestPanelBusqueda(ventana, Sorteo.NAVIDAD, premioService);

        panel.getNumeroFieldForTests().setText("12345");
        panel.getCantidadFieldForTests().setText("1");
        panel.getBuscarButtonForTests().doClick();
        waitForBusqueda(panel);
        panel.getClearButtonForTests().doClick();

        assertEquals(0, panel.getResultadoPanelForTests().getComponentCount());
        assertEquals("", panel.getNumeroFieldForTests().getText());
        assertEquals("", panel.getCantidadFieldForTests().getText());
        Mockito.verify(ventana, Mockito.atLeastOnce()).pack();
    }

    @Test
    void filtroNumeroSoloPermiteDigitosBackspaceYMaximoCincoCaracteres() {
        Ventana ventana = Mockito.mock(Ventana.class);
        TestPanelBusqueda panel = new TestPanelBusqueda(ventana, Sorteo.NAVIDAD, (sorteo, numero) -> new Premio());
        JTextField numero = panel.getNumeroFieldForTests();

        assertFalse(isConsumedAfterKeyTyped(numero, '1'));
        assertFalse(isConsumedAfterKeyTyped(numero, '\b'));
        assertTrue(isConsumedAfterKeyTyped(numero, 'a'));

        numero.setText("12345");
        assertTrue(isConsumedAfterKeyTyped(numero, '6'));
    }

    @Test
    void filtroCantidadSoloPermiteDigitosYPunto() {
        Ventana ventana = Mockito.mock(Ventana.class);
        TestPanelBusqueda panel = new TestPanelBusqueda(ventana, Sorteo.NAVIDAD, (sorteo, numero) -> new Premio());
        JTextField cantidad = panel.getCantidadFieldForTests();

        assertFalse(isConsumedAfterKeyTyped(cantidad, '1'));
        assertFalse(isConsumedAfterKeyTyped(cantidad, '.'));
        assertTrue(isConsumedAfterKeyTyped(cantidad, 'a'));
    }

    private static class TestPanelBusqueda extends PanelBusqueda {
        private String lastWarning;

        TestPanelBusqueda(Ventana ventana, Sorteo sorteo, PremioService premioService) {
            super(ventana, sorteo, premioService);
        }

        String getLastWarningForTests() {
            return lastWarning;
        }

        JButton getClearButtonForTests() {
            return getLimpiarButtonForTests();
        }

        @Override
        protected void showWarning(String message) {
            lastWarning = message;
        }
    }
}
