package es.jklabs.desktop.gui.paneles;

import es.jklabs.desktop.gui.Ventana;
import es.jklabs.utilidades.BaseTest;
import es.jklabs.utilidades.Mensajes;
import io.github.jcprieto.lib.loteria.enumeradores.EstadoSorteo;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class MenuPrincipalTest extends BaseTest {

    private static JButton obtenerBoton(MenuPrincipal panel, String fieldName) {
        return switch (fieldName) {
            case "btnResumenNavidad" -> panel.getBtnResumenNavidadForTests();
            case "btnBuscarPremioNavidad" -> panel.getBtnBuscarPremioNavidadForTests();
            case "btnResumenNino" -> panel.getBtnResumenNinoForTests();
            case "btnBuscarPremioNino" -> panel.getBtnBuscarPremioNinoForTests();
            default -> throw new IllegalArgumentException("Campo no soportado");
        };
    }

    private static void invocarSetCargando(MenuPrincipal panel, boolean activo) throws Exception {
        SwingUtilities.invokeAndWait(() -> panel.setCargando(activo));
    }

    private static void clickEnEdt(JButton boton) throws Exception {
        SwingUtilities.invokeAndWait(boton::doClick);
    }

    private static void ejecutarEventoEnEdt(MenuPrincipal panel, Object source) throws Exception {
        SwingUtilities.invokeAndWait(() -> panel.actionPerformed(
                new ActionEvent(source, ActionEvent.ACTION_PERFORMED, "cmd")));
    }

    private static void esperarCarga(MenuPrincipal panel) throws Exception {
        long deadline = System.currentTimeMillis() + 3000;
        while (System.currentTimeMillis() < deadline && !panel.getBtnResumenNavidadForTests().isEnabled()) {
            Thread.sleep(20);
        }
        SwingUtilities.invokeAndWait(() -> {
            // Drena el EDT para ejecutar el done del SwingWorker.
        });
    }

    private static <T> T crearEnEdt(EdtSupplier<T> supplier) throws Exception {
        final Object[] holder = new Object[1];
        SwingUtilities.invokeAndWait(() -> holder[0] = supplier.get());
        @SuppressWarnings("unchecked")
        T result = (T) holder[0];
        return result;
    }

    private static io.github.jcprieto.lib.loteria.model.navidad.ResumenNavidad crearResumenNavidad() {
        io.github.jcprieto.lib.loteria.model.navidad.ResumenNavidad resumen =
                new io.github.jcprieto.lib.loteria.model.navidad.ResumenNavidad();
        resumen.setGordo("00000");
        resumen.setSegundo("11111");
        resumen.setTercero("22222");
        resumen.setCuarto(List.of("33333", "44444"));
        resumen.setQuinto(List.of("55555", "66666"));
        resumen.setEstado(EstadoSorteo.TERMINADO);
        resumen.setFechaActualizacion(LocalDateTime.of(2025, 12, 22, 14, 0));
        resumen.setUrlPDF("https://example.com/navidad.pdf");
        return resumen;
    }

    private static io.github.jcprieto.lib.loteria.model.nino.ResumenNino crearResumenNino() {
        io.github.jcprieto.lib.loteria.model.nino.ResumenNino resumen =
                new io.github.jcprieto.lib.loteria.model.nino.ResumenNino();
        resumen.setPrimero("00000");
        resumen.setSegundo("11111");
        resumen.setTercero("22222");
        resumen.setCuatroCifras(List.of("3333", "4444"));
        resumen.setTresCifras(List.of("333", "444"));
        resumen.setDosCifras(List.of("33", "44"));
        resumen.setReintegros(List.of("3", "4"));
        resumen.setEstado(EstadoSorteo.TERMINADO);
        resumen.setFechaActualizacion(LocalDateTime.of(2026, 1, 6, 14, 0));
        resumen.setUrlPDF("https://example.com/nino.pdf");
        return resumen;
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
        assertFalse(btnResumenNavidad.isEnabled());
        assertFalse(btnBuscarPremioNavidad.isEnabled());
        assertFalse(btnResumenNino.isEnabled());
        assertFalse(btnBuscarPremioNino.isEnabled());
        assertEquals(Cursor.WAIT_CURSOR, panel.getCursor().getType());

        invocarSetCargando(panel, false);
        assertTrue(btnResumenNavidad.isEnabled());
        assertTrue(btnBuscarPremioNavidad.isEnabled());
        assertTrue(btnResumenNino.isEnabled());
        assertTrue(btnBuscarPremioNino.isEnabled());
        assertEquals(Cursor.DEFAULT_CURSOR, panel.getCursor().getType());
    }

    @Test
    void clickEnBuscarNavidadMuestraPanelBusqueda() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        MenuPrincipal panel = crearEnEdt(() -> new MenuPrincipal(ventana));

        clickEnEdt(panel.getBtnBuscarPremioNavidadForTests());

        InOrder inOrder = Mockito.inOrder(ventana);
        inOrder.verify(ventana).setPanel(Mockito.argThat(p -> p instanceof PanelBusqueda));
        inOrder.verify(ventana).setPanelInferior(Mockito.argThat(Objects::nonNull));
        inOrder.verify(ventana).pack();
    }

    @Test
    void clickEnBuscarNinoMuestraPanelBusqueda() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        MenuPrincipal panel = crearEnEdt(() -> new MenuPrincipal(ventana));

        clickEnEdt(panel.getBtnBuscarPremioNinoForTests());

        InOrder inOrder = Mockito.inOrder(ventana);
        inOrder.verify(ventana).setPanel(Mockito.argThat(p -> p instanceof PanelBusqueda));
        inOrder.verify(ventana).setPanelInferior(Mockito.argThat(Objects::nonNull));
        inOrder.verify(ventana).pack();
    }

    @Test
    void ignoraEventosDeOrigenDesconocido() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        MenuPrincipal.ResumenService resumenService = Mockito.mock(MenuPrincipal.ResumenService.class);
        MenuPrincipal panel = crearEnEdt(() -> new MenuPrincipal(ventana, resumenService));

        ejecutarEventoEnEdt(panel, new JButton("otro"));

        Mockito.verifyNoInteractions(ventana, resumenService);
    }

    @Test
    void ignoraResumenNavidadSiYaEstaCargando() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        MenuPrincipal.ResumenService resumenService = Mockito.mock(MenuPrincipal.ResumenService.class);
        MenuPrincipal panel = crearEnEdt(() -> new MenuPrincipal(ventana, resumenService));

        invocarSetCargando(panel, true);
        ejecutarEventoEnEdt(panel, panel.getBtnResumenNavidadForTests());

        Mockito.verifyNoInteractions(ventana, resumenService);
    }

    @Test
    void ignoraResumenNinoSiYaEstaCargando() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        MenuPrincipal.ResumenService resumenService = Mockito.mock(MenuPrincipal.ResumenService.class);
        MenuPrincipal panel = crearEnEdt(() -> new MenuPrincipal(ventana, resumenService));

        invocarSetCargando(panel, true);
        ejecutarEventoEnEdt(panel, panel.getBtnResumenNinoForTests());

        Mockito.verifyNoInteractions(ventana, resumenService);
    }

    @Test
    void clickEnResumenNavidadMuestraResumenCuandoHayDatos() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        MenuPrincipal.ResumenService resumenService = Mockito.mock(MenuPrincipal.ResumenService.class);
        Mockito.when(resumenService.getResumenNavidad()).thenReturn(crearResumenNavidad());
        TestMenuPrincipal panel = crearEnEdt(() -> new TestMenuPrincipal(ventana, resumenService));

        clickEnEdt(panel.getBtnResumenNavidadForTests());
        esperarCarga(panel);

        ArgumentCaptor<JPanel> captor = ArgumentCaptor.forClass(JPanel.class);
        InOrder inOrder = Mockito.inOrder(ventana);
        inOrder.verify(ventana).setPanel(captor.capture());
        inOrder.verify(ventana).setPanelInferior(Mockito.argThat(Objects::nonNull));
        inOrder.verify(ventana).pack();
        assertInstanceOf(ResumenNavidad.class, captor.getValue());
        assertNull(panel.getLastWarningForTests());
        SwingUtilities.invokeAndWait(captor.getValue()::removeNotify);
    }

    @Test
    void clickEnResumenNinoMuestraResumenCuandoHayDatos() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        MenuPrincipal.ResumenService resumenService = Mockito.mock(MenuPrincipal.ResumenService.class);
        Mockito.when(resumenService.getResumenNino()).thenReturn(crearResumenNino());
        TestMenuPrincipal panel = crearEnEdt(() -> new TestMenuPrincipal(ventana, resumenService));

        clickEnEdt(panel.getBtnResumenNinoForTests());
        esperarCarga(panel);

        ArgumentCaptor<JPanel> captor = ArgumentCaptor.forClass(JPanel.class);
        InOrder inOrder = Mockito.inOrder(ventana);
        inOrder.verify(ventana).setPanel(captor.capture());
        inOrder.verify(ventana).setPanelInferior(Mockito.argThat(Objects::nonNull));
        inOrder.verify(ventana).pack();
        assertInstanceOf(ResumenNino.class, captor.getValue());
        assertNull(panel.getLastWarningForTests());
        SwingUtilities.invokeAndWait(captor.getValue()::removeNotify);
    }

    @Test
    void clickEnResumenNavidadMuestraAvisoCuandoNoHayDatos() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        MenuPrincipal.ResumenService resumenService = Mockito.mock(MenuPrincipal.ResumenService.class);
        Mockito.when(resumenService.getResumenNavidad()).thenReturn(null);
        TestMenuPrincipal panel = crearEnEdt(() -> new TestMenuPrincipal(ventana, resumenService));

        clickEnEdt(panel.getBtnResumenNavidadForTests());
        esperarCarga(panel);

        assertEquals(Mensajes.getMensaje(MenuPrincipal.WARNING_PROBLEMA_SERVIDOR), panel.getLastWarningForTests());
        Mockito.verify(resumenService).getResumenNavidad();
        Mockito.verifyNoInteractions(ventana);
        assertTrue(panel.getBtnResumenNavidadForTests().isEnabled());
    }

    @Test
    void clickEnResumenNinoMuestraAvisoCuandoNoHayDatos() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        MenuPrincipal.ResumenService resumenService = Mockito.mock(MenuPrincipal.ResumenService.class);
        Mockito.when(resumenService.getResumenNino()).thenReturn(null);
        TestMenuPrincipal panel = crearEnEdt(() -> new TestMenuPrincipal(ventana, resumenService));

        clickEnEdt(panel.getBtnResumenNinoForTests());
        esperarCarga(panel);

        assertEquals(Mensajes.getMensaje(MenuPrincipal.WARNING_PROBLEMA_SERVIDOR), panel.getLastWarningForTests());
        Mockito.verify(resumenService).getResumenNino();
        Mockito.verifyNoInteractions(ventana);
        assertTrue(panel.getBtnResumenNinoForTests().isEnabled());
    }

    @Test
    void clickEnResumenNavidadMuestraAvisoCuandoFallaElServicio() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        MenuPrincipal.ResumenService resumenService = Mockito.mock(MenuPrincipal.ResumenService.class);
        Mockito.when(resumenService.getResumenNavidad()).thenThrow(new IOException("boom"));
        TestMenuPrincipal panel = crearEnEdt(() -> new TestMenuPrincipal(ventana, resumenService));

        clickEnEdt(panel.getBtnResumenNavidadForTests());
        esperarCarga(panel);

        assertEquals(Mensajes.getMensaje(MenuPrincipal.WARNING_PROBLEMA_SERVIDOR), panel.getLastWarningForTests());
        Mockito.verify(resumenService).getResumenNavidad();
        Mockito.verifyNoInteractions(ventana);
    }

    @Test
    void clickEnResumenNinoMuestraAvisoCuandoFallaElServicio() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        MenuPrincipal.ResumenService resumenService = Mockito.mock(MenuPrincipal.ResumenService.class);
        Mockito.when(resumenService.getResumenNino()).thenThrow(new IOException("boom"));
        TestMenuPrincipal panel = crearEnEdt(() -> new TestMenuPrincipal(ventana, resumenService));

        clickEnEdt(panel.getBtnResumenNinoForTests());
        esperarCarga(panel);

        assertEquals(Mensajes.getMensaje(MenuPrincipal.WARNING_PROBLEMA_SERVIDOR), panel.getLastWarningForTests());
        Mockito.verify(resumenService).getResumenNino();
        Mockito.verifyNoInteractions(ventana);
    }

    private static final class TestMenuPrincipal extends MenuPrincipal {
        private String lastWarning;

        private TestMenuPrincipal(Ventana ventana, ResumenService resumenService) {
            super(ventana, resumenService);
        }

        private String getLastWarningForTests() {
            return lastWarning;
        }

        @Override
        protected void showWarning(String message) {
            lastWarning = message;
        }
    }

    private interface EdtSupplier<T> {
        T get();
    }
}
