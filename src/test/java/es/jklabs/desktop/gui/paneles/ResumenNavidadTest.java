package es.jklabs.desktop.gui.paneles;

import es.jklabs.desktop.gui.Ventana;
import es.jklabs.utilidades.BaseTest;
import es.jklabs.utilidades.Mensajes;
import es.jklabs.utilidades.UtilidadesEstadoSorteo;
import es.jklabs.utilidades.UtilidadesFecha;
import io.github.jcprieto.lib.loteria.conexion.Conexion;
import io.github.jcprieto.lib.loteria.enumeradores.EstadoSorteo;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResumenNavidadTest extends BaseTest {

    private static io.github.jcprieto.lib.loteria.model.navidad.ResumenNavidad crearResumenBase() {
        return crearResumen(
                "11111", "22222", "33333",
                List.of("44444", "55555"),
                List.of("66666", "77777", "88888"),
                EstadoSorteo.NO_INICIADO,
                LocalDateTime.of(2026, 12, 22, 8, 0),
                "https://example.test/inicial.pdf"
        );
    }

    private static io.github.jcprieto.lib.loteria.model.navidad.ResumenNavidad crearResumen(
            String gordo,
            String segundo,
            String tercero,
            List<String> cuarto,
            List<String> quinto,
            EstadoSorteo estado,
            LocalDateTime fechaActualizacion,
            String urlPdf) {
        io.github.jcprieto.lib.loteria.model.navidad.ResumenNavidad resumen =
                new io.github.jcprieto.lib.loteria.model.navidad.ResumenNavidad();
        resumen.setGordo(gordo);
        resumen.setSegundo(segundo);
        resumen.setTercero(tercero);
        resumen.setCuarto(cuarto);
        resumen.setQuinto(quinto);
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

    private static JLabel getLabel(ResumenNavidad panel, String fieldName) throws Exception {
        return (JLabel) getField(panel, fieldName);
    }

    private static JPanel getPanel(ResumenNavidad panel, String fieldName) throws Exception {
        return (JPanel) getField(panel, fieldName);
    }

    private static Timer getTimer(ResumenNavidad panel) throws Exception {
        Timer timer = (Timer) getField(panel, "tiempo");
        assertNotNull(timer);
        return timer;
    }

    private static boolean getBooleanField(ResumenNavidad panel, String fieldName) throws Exception {
        return (boolean) getField(panel, fieldName);
    }

    private static void setBooleanField(ResumenNavidad panel, String fieldName, boolean value) throws Exception {
        Field field = ResumenNavidad.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.setBoolean(panel, value);
    }

    private static void detenerTimer(ResumenNavidad panel) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                getTimer(panel).stop();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private static void invokePrivate(ResumenNavidad panel, String methodName, List<String> values) throws Exception {
        Method method = ResumenNavidad.class.getDeclaredMethod(methodName, List.class);
        method.setAccessible(true);
        SwingUtilities.invokeAndWait(() -> {
            try {
                method.invoke(panel, values);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private static Object getField(ResumenNavidad panel, String fieldName) throws Exception {
        Field field = ResumenNavidad.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(panel);
    }

    @Test
    void creaResumenConDatosIniciales() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        io.github.jcprieto.lib.loteria.model.navidad.ResumenNavidad resumen = crearResumen(
                "11111", "22222", "33333",
                List.of("44444", "55555"),
                List.of("66666", "77777", "88888"),
                EstadoSorteo.EN_PROCESO,
                LocalDateTime.of(2026, 12, 22, 9, 30),
                "https://example.test/navidad.pdf"
        );

        ResumenNavidad panel = crearEnEdt(() -> new ResumenNavidad(ventana, resumen));
        try {
            assertEquals("11111", getLabel(panel, "gordo").getText());
            assertEquals("22222", getLabel(panel, "segundo").getText());
            assertEquals("33333", getLabel(panel, "tercero").getText());
            assertEquals(2, getPanel(panel, "panelCuarto").getComponentCount());
            assertEquals(3, getPanel(panel, "panelQuinto").getComponentCount());
            assertEquals(
                    Mensajes.getMensaje("resumen.estado") + UtilidadesEstadoSorteo.getHumanReadable(EstadoSorteo.EN_PROCESO),
                    getLabel(panel, "estado").getText()
            );
            assertEquals(
                    Mensajes.getMensaje("resumen.actualizacion") + UtilidadesFecha.getHumanReadable(resumen.getFechaActualizacion()),
                    getLabel(panel, "actualizacion").getText()
            );
            JLabel pdf = getLabel(panel, "pdf");
            assertEquals("https://example.test/navidad.pdf", pdf.getText());
            assertEquals(Color.blue, pdf.getForeground());
            assertTrue(getTimer(panel).isRunning());
        } finally {
            detenerTimer(panel);
        }
    }

    @Test
    void ignoraEventosQueNoVienenDelTimer() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        ResumenNavidad panel = crearEnEdt(() -> new ResumenNavidad(ventana, crearResumenBase()));
        try {
            panel.actionPerformed(new ActionEvent(new JButton("otro"), ActionEvent.ACTION_PERFORMED, "cmd"));
            esperarEdt();

            Mockito.verifyNoInteractions(ventana);
            assertFalse(getBooleanField(panel, "actualizando"));
        } finally {
            detenerTimer(panel);
        }
    }

    @Test
    void removeNotifyDetieneElTimerYAddNotifyLoReinicia() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        ResumenNavidad panel = crearEnEdt(() -> new ResumenNavidad(ventana, crearResumenBase()));
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
    void actualizaPanelesDePremiosCuandoSeInvocanMetodosInternos() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        ResumenNavidad panel = crearEnEdt(() -> new ResumenNavidad(ventana, crearResumenBase()));
        try {
            invokePrivate(panel, "setPanelCuarto", List.of("12121", "34343", "56565", "78787"));
            invokePrivate(panel, "setPanelQuinto", List.of("90909", "10101"));

            JPanel panelCuarto = getPanel(panel, "panelCuarto");
            JPanel panelQuinto = getPanel(panel, "panelQuinto");

            assertEquals(4, panelCuarto.getComponentCount());
            assertEquals("12121", ((JLabel) panelCuarto.getComponent(0)).getText());
            assertEquals("78787", ((JLabel) panelCuarto.getComponent(3)).getText());
            assertEquals(2, panelQuinto.getComponentCount());
            assertEquals("90909", ((JLabel) panelQuinto.getComponent(0)).getText());
            assertEquals("10101", ((JLabel) panelQuinto.getComponent(1)).getText());
        } finally {
            detenerTimer(panel);
        }
    }

    @Test
    void ignoraTickDelTimerSiYaHayUnaActualizacionEnCurso() throws Exception {
        Ventana ventana = Mockito.mock(Ventana.class);
        ResumenNavidad panel = crearEnEdt(() -> new ResumenNavidad(ventana, crearResumenBase()));
        try (MockedConstruction<Conexion> mocked = Mockito.mockConstruction(Conexion.class)) {
            setBooleanField(panel, "actualizando", true);

            panel.actionPerformed(new ActionEvent(getTimer(panel), ActionEvent.ACTION_PERFORMED, "tick"));
            esperarEdt();

            assertTrue(getBooleanField(panel, "actualizando"));
            assertEquals(0, mocked.constructed().size());
            Mockito.verifyNoInteractions(ventana);
        } finally {
            detenerTimer(panel);
        }
    }

    private interface EdtSupplier<T> {
        T get();
    }
}
