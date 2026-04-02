package es.jklabs.desktop.gui.paneles;

import es.jklabs.desktop.gui.Ventana;
import es.jklabs.utilidades.BaseTest;
import es.jklabs.utilidades.Mensajes;
import es.jklabs.utilidades.UtilidadesEstadoSorteo;
import es.jklabs.utilidades.UtilidadesFecha;
import io.github.jcprieto.lib.loteria.conexion.Conexion;
import io.github.jcprieto.lib.loteria.enumeradores.EstadoSorteo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResumenNinoTest extends BaseTest {

    private static io.github.jcprieto.lib.loteria.model.nino.ResumenNino crearResumenBase() {
        return crearResumen(
                "11111", "22222", "33333",
                List.of("4444", "5555"),
                List.of("666", "777", "888"),
                List.of("10", "20", "30", "40"),
                List.of("1", "2"),
                EstadoSorteo.NO_INICIADO,
                LocalDateTime.of(2026, 1, 6, 8, 0),
                "https://example.test/inicial-nino.pdf"
        );
    }

    private static io.github.jcprieto.lib.loteria.model.nino.ResumenNino crearResumen(
            String primero,
            String segundo,
            String tercero,
            List<String> cuatroCifras,
            List<String> tresCifras,
            List<String> dosCifras,
            List<String> reintegros,
            EstadoSorteo estado,
            LocalDateTime fechaActualizacion,
            String urlPdf) {
        io.github.jcprieto.lib.loteria.model.nino.ResumenNino resumen =
                new io.github.jcprieto.lib.loteria.model.nino.ResumenNino();
        resumen.setPrimero(primero);
        resumen.setSegundo(segundo);
        resumen.setTercero(tercero);
        resumen.setCuatroCifras(cuatroCifras);
        resumen.setTresCifras(tresCifras);
        resumen.setDosCifras(dosCifras);
        resumen.setReintegros(reintegros);
        resumen.setEstado(estado);
        resumen.setFechaActualizacion(fechaActualizacion);
        resumen.setUrlPDF(urlPdf);
        return resumen;
    }

    private static <T> T crearEnEdt(EdtSupplier<T> supplier) throws Exception {
        final Object[] holder = new Object[1];
        SwingUtilities.invokeAndWait(() -> holder[0] = supplier.get());
        @SuppressWarnings("unchecked")
        T result = (T) holder[0];
        return result;
    }

    private static void esperarEdt() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            // Drena el EDT para ejecutar el done del SwingWorker.
        });
    }

    private static void esperarHastaQueFinaliceActualizacion(ResumenNino panel) throws Exception {
        for (int i = 0; i < 100; i++) {
            esperarEdt();
            if (!getBooleanField(panel, "actualizando")) {
                return;
            }
            Thread.sleep(20);
        }
        fail("La actualización asíncrona no finalizó a tiempo");
    }

    private static JLabel getLabel(ResumenNino panel, String fieldName) throws Exception {
        return (JLabel) getField(panel, fieldName);
    }

    private static JPanel getPanel(ResumenNino panel, String fieldName) throws Exception {
        return (JPanel) getField(panel, fieldName);
    }

    private static Timer getTimer(ResumenNino panel) throws Exception {
        Timer timer = (Timer) getField(panel, "tiempo");
        assertNotNull(timer);
        return timer;
    }

    private static boolean getBooleanField(ResumenNino panel, String fieldName) throws Exception {
        return (boolean) getField(panel, fieldName);
    }

    private static void setBooleanField(ResumenNino panel, String fieldName, boolean value) throws Exception {
        Field field = ResumenNino.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.setBoolean(panel, value);
    }

    private static void detenerTimer(ResumenNino panel) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                getTimer(panel).stop();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private static void invokePrivate(ResumenNino panel, String methodName, List<String> values) throws Exception {
        Method method = ResumenNino.class.getDeclaredMethod(methodName, List.class);
        method.setAccessible(true);
        SwingUtilities.invokeAndWait(() -> {
            try {
                method.invoke(panel, values);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private static Object getField(ResumenNino panel, String fieldName) throws Exception {
        Field field = ResumenNino.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(panel);
    }

    private static ResumenNino crearPanel(Ventana ventana,
                                          io.github.jcprieto.lib.loteria.model.nino.ResumenNino resumen,
                                          Conexion conexion) throws Exception {
        return crearEnEdt(() -> new ResumenNinoTestable(ventana, resumen, conexion));
    }

    @Test
    void creaResumenConDatosIniciales() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        io.github.jcprieto.lib.loteria.model.nino.ResumenNino resumen = crearResumen(
                "11111", "22222", "33333",
                List.of("4444", "5555", "6666"),
                List.of("111", "222", "333", "444"),
                List.of("10", "20", "30", "40", "50"),
                List.of("7", "8", "9"),
                EstadoSorteo.EN_PROCESO,
                LocalDateTime.of(2026, 1, 6, 9, 30),
                "https://example.test/nino.pdf"
        );

        ResumenNino panel = crearEnEdt(() -> new ResumenNino(ventana, resumen));
        try {
            assertEquals("22222", getLabel(panel, "segundo").getText());
            assertEquals("33333", getLabel(panel, "tercero").getText());
            assertEquals(3, getPanel(panel, "panelExt4").getComponentCount());
            assertEquals(4, getPanel(panel, "panelExt3").getComponentCount());
            assertEquals(5, getPanel(panel, "panelExt2").getComponentCount());
            assertEquals(3, getPanel(panel, "panelReintegros").getComponentCount());
            assertEquals(
                    Mensajes.getMensaje("resumen.estado") + UtilidadesEstadoSorteo.getHumanReadable(EstadoSorteo.EN_PROCESO),
                    getLabel(panel, "estado").getText()
            );
            assertEquals(
                    Mensajes.getMensaje("resumen.actualizacion") + UtilidadesFecha.getHumanReadable(resumen.getFechaActualizacion()),
                    getLabel(panel, "actualizacion").getText()
            );
            JLabel pdf = getLabel(panel, "pdf");
            assertEquals("https://example.test/nino.pdf", pdf.getText());
            assertEquals(Color.blue, pdf.getForeground());
            assertTrue(getTimer(panel).isRunning());
        } finally {
            detenerTimer(panel);
        }
    }

    @Test
    void ignoraEventosQueNoVienenDelTimer() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        Conexion conexion = Mockito.mock(Conexion.class);
        ResumenNino panel = crearPanel(ventana, crearResumenBase(), conexion);
        try {
            panel.actionPerformed(new ActionEvent(new JButton("otro"), ActionEvent.ACTION_PERFORMED, "cmd"));
            esperarEdt();

            Mockito.verifyNoInteractions(ventana);
            Mockito.verifyNoInteractions(conexion);
            assertFalse(getBooleanField(panel, "actualizando"));
        } finally {
            detenerTimer(panel);
        }
    }

    @Test
    void removeNotifyDetieneElTimerYAddNotifyLoReinicia() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        ResumenNino panel = crearPanel(ventana, crearResumenBase(), Mockito.mock(Conexion.class));
        Timer timer = getTimer(panel);
        try {
            SwingUtilities.invokeAndWait(panel::removeNotify);
            assertFalse(timer.isRunning());

            SwingUtilities.invokeAndWait(panel::addNotify);
            assertTrue(timer.isRunning());
        } finally {
            detenerTimer(panel);
        }
    }

    @Test
    void actualizaPanelesInternosCuandoSeInvocanMetodosPrivados() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        ResumenNino panel = crearPanel(ventana, crearResumenBase(), Mockito.mock(Conexion.class));
        try {
            invokePrivate(panel, "setPanelExt4", List.of("1212", "3434", "5656"));
            invokePrivate(panel, "setPanelExt3", List.of("101", "202"));
            invokePrivate(panel, "setPanelExt2", List.of("11", "22", "33", "44"));
            invokePrivate(panel, "setPanelReintegros", List.of("5", "6", "7"));

            JPanel panelExt4 = getPanel(panel, "panelExt4");
            JPanel panelExt3 = getPanel(panel, "panelExt3");
            JPanel panelExt2 = getPanel(panel, "panelExt2");
            JPanel panelReintegros = getPanel(panel, "panelReintegros");

            assertEquals(3, panelExt4.getComponentCount());
            assertEquals("1212", ((JLabel) panelExt4.getComponent(0)).getText());
            assertEquals("5656", ((JLabel) panelExt4.getComponent(2)).getText());
            assertEquals(2, panelExt3.getComponentCount());
            assertEquals("101", ((JLabel) panelExt3.getComponent(0)).getText());
            assertEquals("202", ((JLabel) panelExt3.getComponent(1)).getText());
            assertEquals(4, panelExt2.getComponentCount());
            assertEquals("11", ((JLabel) panelExt2.getComponent(0)).getText());
            assertEquals("44", ((JLabel) panelExt2.getComponent(3)).getText());
            assertEquals(3, panelReintegros.getComponentCount());
            assertEquals("5", ((JLabel) panelReintegros.getComponent(0)).getText());
            assertEquals("7", ((JLabel) panelReintegros.getComponent(2)).getText());
        } finally {
            detenerTimer(panel);
        }
    }

    @Test
    void ignoraTickDelTimerSiYaHayUnaActualizacionEnCurso() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        Conexion conexion = Mockito.mock(Conexion.class);
        ResumenNino panel = crearPanel(ventana, crearResumenBase(), conexion);
        try {
            setBooleanField(panel, "actualizando", true);

            panel.actionPerformed(new ActionEvent(getTimer(panel), ActionEvent.ACTION_PERFORMED, "tick"));
            esperarEdt();

            assertTrue(getBooleanField(panel, "actualizando"));
            Mockito.verifyNoInteractions(conexion);
            Mockito.verifyNoInteractions(ventana);
        } finally {
            detenerTimer(panel);
        }
    }

    @Test
    void actualizaResumenCuandoConexionDevuelveDatos() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        Conexion conexion = Mockito.mock(Conexion.class);
        ResumenNino panel = crearPanel(ventana, crearResumenBase(), conexion);
        io.github.jcprieto.lib.loteria.model.nino.ResumenNino actualizado = crearResumen(
                "99999", "88888", "77777",
                List.of("1234", "5678"),
                List.of("111", "222", "333"),
                List.of("12", "34"),
                List.of("8", "9", "0"),
                EstadoSorteo.TERMINADO,
                LocalDateTime.of(2026, 1, 6, 12, 0),
                "https://example.test/final-nino.pdf"
        );

        try {
            Mockito.when(conexion.getResumenNino()).thenReturn(actualizado);
            panel.actionPerformed(new ActionEvent(getTimer(panel), ActionEvent.ACTION_PERFORMED, "tick"));
            esperarHastaQueFinaliceActualizacion(panel);

            Mockito.verify(conexion).getResumenNino();
            assertEquals("88888", getLabel(panel, "segundo").getText());
            assertEquals("77777", getLabel(panel, "tercero").getText());
            assertEquals(2, getPanel(panel, "panelExt4").getComponentCount());
            assertEquals(3, getPanel(panel, "panelExt3").getComponentCount());
            assertEquals(2, getPanel(panel, "panelExt2").getComponentCount());
            assertEquals(3, getPanel(panel, "panelReintegros").getComponentCount());
            assertEquals(
                    Mensajes.getMensaje("resumen.estado") + UtilidadesEstadoSorteo.getHumanReadable(EstadoSorteo.TERMINADO),
                    getLabel(panel, "estado").getText()
            );
            assertEquals(
                    Mensajes.getMensaje("resumen.actualizacion") + UtilidadesFecha.getHumanReadable(actualizado.getFechaActualizacion()),
                    getLabel(panel, "actualizacion").getText()
            );
            assertEquals("https://example.test/final-nino.pdf", getLabel(panel, "pdf").getText());
            assertFalse(getBooleanField(panel, "actualizando"));
            Mockito.verify(ventana).pack();
        } finally {
            detenerTimer(panel);
        }
    }

    @Test
    void noActualizaPanelesSiConexionDevuelveNull() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        Conexion conexion = Mockito.mock(Conexion.class);
        ResumenNino panel = crearPanel(ventana, crearResumenBase(), conexion);
        String segundoInicial = getLabel(panel, "segundo").getText();
        String terceroInicial = getLabel(panel, "tercero").getText();
        int ext4Inicial = getPanel(panel, "panelExt4").getComponentCount();
        int ext3Inicial = getPanel(panel, "panelExt3").getComponentCount();
        int ext2Inicial = getPanel(panel, "panelExt2").getComponentCount();
        int reintegrosInicial = getPanel(panel, "panelReintegros").getComponentCount();
        String estadoInicial = getLabel(panel, "estado").getText();
        String actualizacionInicial = getLabel(panel, "actualizacion").getText();
        String pdfInicial = getLabel(panel, "pdf").getText();

        try {
            Mockito.when(conexion.getResumenNino()).thenReturn(null);
            panel.actionPerformed(new ActionEvent(getTimer(panel), ActionEvent.ACTION_PERFORMED, "tick"));
            esperarHastaQueFinaliceActualizacion(panel);

            Mockito.verify(conexion).getResumenNino();
            assertEquals(segundoInicial, getLabel(panel, "segundo").getText());
            assertEquals(terceroInicial, getLabel(panel, "tercero").getText());
            assertEquals(ext4Inicial, getPanel(panel, "panelExt4").getComponentCount());
            assertEquals(ext3Inicial, getPanel(panel, "panelExt3").getComponentCount());
            assertEquals(ext2Inicial, getPanel(panel, "panelExt2").getComponentCount());
            assertEquals(reintegrosInicial, getPanel(panel, "panelReintegros").getComponentCount());
            assertEquals(estadoInicial, getLabel(panel, "estado").getText());
            assertEquals(actualizacionInicial, getLabel(panel, "actualizacion").getText());
            assertEquals(pdfInicial, getLabel(panel, "pdf").getText());
            assertFalse(getBooleanField(panel, "actualizando"));
            Mockito.verifyNoInteractions(ventana);
        } finally {
            detenerTimer(panel);
        }
    }

    @Test
    void reseteaEstadoDeActualizacionSiConexionFalla() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        Conexion conexion = Mockito.mock(Conexion.class);
        ResumenNino panel = crearPanel(ventana, crearResumenBase(), conexion);
        String segundoInicial = getLabel(panel, "segundo").getText();
        String terceroInicial = getLabel(panel, "tercero").getText();
        String pdfInicial = getLabel(panel, "pdf").getText();

        try {
            Mockito.when(conexion.getResumenNino()).thenThrow(new IOException("boom"));
            panel.actionPerformed(new ActionEvent(getTimer(panel), ActionEvent.ACTION_PERFORMED, "tick"));
            esperarHastaQueFinaliceActualizacion(panel);

            Mockito.verify(conexion).getResumenNino();
            assertEquals(segundoInicial, getLabel(panel, "segundo").getText());
            assertEquals(terceroInicial, getLabel(panel, "tercero").getText());
            assertEquals(pdfInicial, getLabel(panel, "pdf").getText());
            assertFalse(getBooleanField(panel, "actualizando"));
            Mockito.verifyNoInteractions(ventana);
        } finally {
            detenerTimer(panel);
        }
    }

    private interface EdtSupplier<T> {
        T get();
    }

    private static final class ResumenNinoTestable extends ResumenNino {
        private final Conexion conexion;

        private ResumenNinoTestable(Ventana ventana,
                                    io.github.jcprieto.lib.loteria.model.nino.ResumenNino resultado,
                                    Conexion conexion) {
            super(ventana, resultado);
            this.conexion = conexion;
        }

        @Override
        Conexion crearConexion() {
            return conexion;
        }
    }
}
