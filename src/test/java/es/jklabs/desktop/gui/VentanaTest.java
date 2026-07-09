package es.jklabs.desktop.gui;

import es.jklabs.desktop.gui.dialogos.AcercaDe;
import es.jklabs.utilidades.BaseTest;
import es.jklabs.utilidades.Logger;
import es.jklabs.utilidades.Mensajes;
import es.jklabs.utilidades.UtilidadesGitHubReleases;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import sun.misc.Unsafe;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

class VentanaTest extends BaseTest {

    private static Ventana createVentanaWithoutConstructor() throws Exception {
        Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) theUnsafeField.get(null);
        return (Ventana) unsafe.allocateInstance(Ventana.class);
    }

    private static void invokePrivate(Object target) throws Exception {
        Method method = target.getClass().getDeclaredMethod("agregarItemActualizacion");
        method.setAccessible(true);
        method.invoke(target);
    }

    private static Object getField(Object target) throws Exception {
        Field field = target.getClass().getDeclaredField("itemActualizacion");
        field.setAccessible(true);
        return field.get(target);
    }

    private static Object getFieldUnchecked(Object target) {
        try {
            return getField(target);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static void waitForCondition(Callable<Boolean> condition) {
        await()
                .pollInSameThread()
                .atMost(Duration.ofSeconds(3))
                .pollInterval(Duration.ofMillis(25))
                .until(() -> {
                    flushEdt();
                    return condition.call();
                });
    }

    private static void flushEdt() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
        });
    }

    @Test
    void actionPerformedAbreDialogoAcercaDeCuandoElEventoCorresponde() throws Exception {
        Ventana ventana = createVentanaWithoutConstructor();
        JMenuItem acerca = new JMenuItem("Acerca de");
        setField(ventana, "acerca", acerca);

        try (MockedConstruction<AcercaDe> mocked = Mockito.mockConstruction(AcercaDe.class)) {
            ventana.actionPerformed(new ActionEvent(acerca, ActionEvent.ACTION_PERFORMED, "click"));

            assertEquals(1, mocked.constructed().size());
            Mockito.verify(mocked.constructed().getFirst()).setVisible(true);
        }
    }

    @Test
    void actionPerformedIgnoraEventosDeOtrosComponentes() throws Exception {
        Ventana ventana = createVentanaWithoutConstructor();
        setField(ventana, "acerca", new JMenuItem("Acerca de"));

        try (MockedConstruction<AcercaDe> mocked = Mockito.mockConstruction(AcercaDe.class)) {
            ventana.actionPerformed(new ActionEvent(new JButton("otro"), ActionEvent.ACTION_PERFORMED, "click"));

            assertTrue(mocked.constructed().isEmpty());
        }
    }

    @Test
    void agregarItemActualizacionAniadeEntradaYAbreNavegadorAlPulsar() throws Exception {
        Ventana ventana = createVentanaWithoutConstructor();
        JMenuBar barraMenu = new JMenuBar();
        setField(ventana, "barraMenu", barraMenu);

        invokePrivate(ventana);

        JMenuItem itemActualizacion = (JMenuItem) getField(ventana);
        assertNotNull(itemActualizacion);
        assertEquals(Mensajes.getMensaje("menu.nueva.version"), itemActualizacion.getText());
        assertEquals(2, barraMenu.getComponentCount());

        try (MockedStatic<UtilidadesGitHubReleases> mocked = Mockito.mockStatic(UtilidadesGitHubReleases.class)) {
            ActionListener actionListener = itemActualizacion.getActionListeners()[0];
            actionListener.actionPerformed(new ActionEvent(itemActualizacion, ActionEvent.ACTION_PERFORMED, "click"));

            mocked.verify(UtilidadesGitHubReleases::abrirNuevaVersionEnNavegador);
        }
    }

    @Test
    void agregarItemActualizacionNoDuplicaLaEntradaSiYaExiste() throws Exception {
        Ventana ventana = createVentanaWithoutConstructor();
        JMenuBar barraMenu = new JMenuBar();
        JMenuItem existente = new JMenuItem("existente");
        setField(ventana, "barraMenu", barraMenu);
        setField(ventana, "itemActualizacion", existente);

        invokePrivate(ventana);

        assertSame(existente, getField(ventana));
        assertEquals(0, barraMenu.getComponentCount());
    }

    @Test
    void workerDeNuevaVersionAniadeLaEntradaCuandoHayNuevaVersion() throws Exception {
        Ventana ventana = createVentanaWithoutConstructor();
        setField(ventana, "barraMenu", new JMenuBar());

        try (MockedStatic<UtilidadesGitHubReleases> mocked = Mockito.mockStatic(UtilidadesGitHubReleases.class)) {
            mocked.when(UtilidadesGitHubReleases::existeNuevaVersion).thenReturn(true);

            SwingWorker<?, ?> worker = ventana.crearWorkerNuevaVersion();
            worker.run();

            waitForCondition(() -> getFieldUnchecked(ventana) != null);
            assertNotNull(getField(ventana));
        }
    }

    @Test
    void workerDeNuevaVersionNoAniadeLaEntradaCuandoNoHayNuevaVersion() throws Exception {
        Ventana ventana = createVentanaWithoutConstructor();
        setField(ventana, "barraMenu", new JMenuBar());

        try (MockedStatic<UtilidadesGitHubReleases> mocked = Mockito.mockStatic(UtilidadesGitHubReleases.class)) {
            mocked.when(UtilidadesGitHubReleases::existeNuevaVersion).thenReturn(false);

            SwingWorker<?, ?> worker = ventana.crearWorkerNuevaVersion();
            worker.run();

            flushEdt();
            assertNull(getField(ventana));
        }
    }

    @Test
    void workerDeNuevaVersionGestionaErroresSinAniadirLaEntrada() throws Exception {
        Ventana ventana = createVentanaWithoutConstructor();
        setField(ventana, "barraMenu", new JMenuBar());

        try (MockedStatic<UtilidadesGitHubReleases> mocked = Mockito.mockStatic(UtilidadesGitHubReleases.class)) {
            mocked.when(UtilidadesGitHubReleases::existeNuevaVersion).thenThrow(new IOException("fallo"));

            SwingWorker<?, ?> worker = ventana.crearWorkerNuevaVersion();
            worker.run();

            flushEdt();
            assertNull(getField(ventana));
        }
    }

    @Test
    void procesarResultadoNuevaVersionAniadeEntradaCuandoResultadoEsTrue() throws Exception {
        Ventana ventana = createVentanaWithoutConstructor();
        setField(ventana, "barraMenu", new JMenuBar());

        ventana.procesarResultadoNuevaVersion(() -> Boolean.TRUE);

        assertNotNull(getField(ventana));
    }

    @Test
    void procesarResultadoNuevaVersionNoAniadeEntradaCuandoResultadoEsFalse() throws Exception {
        Ventana ventana = createVentanaWithoutConstructor();
        setField(ventana, "barraMenu", new JMenuBar());

        ventana.procesarResultadoNuevaVersion(() -> Boolean.FALSE);

        assertNull(getField(ventana));
    }

    @Test
    void procesarResultadoNuevaVersionIgnoraResultadoNulo() throws Exception {
        Ventana ventana = createVentanaWithoutConstructor();
        setField(ventana, "barraMenu", new JMenuBar());

        ventana.procesarResultadoNuevaVersion(() -> null);

        assertNull(getField(ventana));
    }

    @Test
    void procesarResultadoNuevaVersionRegistraExcepcionSinAniadirEntrada() throws Exception {
        Ventana ventana = createVentanaWithoutConstructor();
        setField(ventana, "barraMenu", new JMenuBar());
        IOException exception = new IOException("fallo");
        ExecutionException executionException = new ExecutionException(exception);

        try (MockedStatic<Logger> mockedLogger = Mockito.mockStatic(Logger.class)) {
            ventana.procesarResultadoNuevaVersion(() -> {
                throw executionException;
            });

            mockedLogger.verify(() -> Logger.error("consultar.nueva.version", executionException));
            assertNull(getField(ventana));
        }
    }

    @Test
    void procesarResultadoNuevaVersionRestauraInterrupcionSiConsultaSeInterrumpe() throws Exception {
        Ventana ventana = createVentanaWithoutConstructor();
        InterruptedException exception = new InterruptedException("interrumpido");
        Thread.interrupted();

        try (MockedStatic<Logger> mockedLogger = Mockito.mockStatic(Logger.class)) {
            ventana.procesarResultadoNuevaVersion(() -> {
                throw exception;
            });

            mockedLogger.verify(() -> Logger.error("consultar.nueva.version", exception));
            assertTrue(Thread.currentThread().isInterrupted());
        } finally {
            Thread.interrupted();
        }
    }

}
