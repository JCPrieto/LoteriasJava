package es.jklabs.desktop.gui;

import es.jklabs.desktop.gui.dialogos.AcercaDe;
import es.jklabs.utilidades.BaseTest;
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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class VentanaTest extends BaseTest {

    private static Ventana createVentanaWithoutConstructor() throws Exception {
        Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) theUnsafeField.get(null);
        return (Ventana) unsafe.allocateInstance(Ventana.class);
    }

    private static void invokePrivate(Object target, String methodName) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(target);
    }

    private static SwingWorker<?, ?> createNuevaVersionWorker(Ventana ventana) throws Exception {
        Class<?> workerClass = Class.forName("es.jklabs.desktop.gui.Ventana$1");
        Constructor<?> constructor = workerClass.getDeclaredConstructor(Ventana.class);
        constructor.setAccessible(true);
        return (SwingWorker<?, ?>) constructor.newInstance(ventana);
    }

    private static Object getField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }

    private static Object getFieldUnchecked(Object target, String fieldName) {
        try {
            return getField(target, fieldName);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static void waitForCondition(Condition condition) throws Exception {
        long deadline = System.currentTimeMillis() + 3000;
        while (System.currentTimeMillis() < deadline) {
            flushEdt();
            if (condition.matches()) {
                return;
            }
            Thread.sleep(25);
        }
        fail("Condition was not met before timeout");
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
            Mockito.verify(mocked.constructed().get(0)).setVisible(true);
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

        invokePrivate(ventana, "agregarItemActualizacion");

        JMenuItem itemActualizacion = (JMenuItem) getField(ventana, "itemActualizacion");
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

        invokePrivate(ventana, "agregarItemActualizacion");

        assertSame(existente, getField(ventana, "itemActualizacion"));
        assertEquals(0, barraMenu.getComponentCount());
    }

    @Test
    void workerDeNuevaVersionAniadeLaEntradaCuandoHayNuevaVersion() throws Exception {
        Ventana ventana = createVentanaWithoutConstructor();
        setField(ventana, "barraMenu", new JMenuBar());

        try (MockedStatic<UtilidadesGitHubReleases> mocked = Mockito.mockStatic(UtilidadesGitHubReleases.class)) {
            mocked.when(UtilidadesGitHubReleases::existeNuevaVersion).thenReturn(true);

            SwingWorker<?, ?> worker = createNuevaVersionWorker(ventana);
            worker.run();

            waitForCondition(() -> getFieldUnchecked(ventana, "itemActualizacion") != null);
            assertNotNull(getField(ventana, "itemActualizacion"));
        }
    }

    @Test
    void workerDeNuevaVersionNoAniadeLaEntradaCuandoNoHayNuevaVersion() throws Exception {
        Ventana ventana = createVentanaWithoutConstructor();
        setField(ventana, "barraMenu", new JMenuBar());

        try (MockedStatic<UtilidadesGitHubReleases> mocked = Mockito.mockStatic(UtilidadesGitHubReleases.class)) {
            mocked.when(UtilidadesGitHubReleases::existeNuevaVersion).thenReturn(false);

            SwingWorker<?, ?> worker = createNuevaVersionWorker(ventana);
            worker.run();

            flushEdt();
            assertNull(getField(ventana, "itemActualizacion"));
        }
    }

    @Test
    void workerDeNuevaVersionGestionaErroresSinAniadirLaEntrada() throws Exception {
        Ventana ventana = createVentanaWithoutConstructor();
        setField(ventana, "barraMenu", new JMenuBar());

        try (MockedStatic<UtilidadesGitHubReleases> mocked = Mockito.mockStatic(UtilidadesGitHubReleases.class)) {
            mocked.when(UtilidadesGitHubReleases::existeNuevaVersion).thenThrow(new IOException("fallo"));

            SwingWorker<?, ?> worker = createNuevaVersionWorker(ventana);
            worker.run();

            flushEdt();
            assertNull(getField(ventana, "itemActualizacion"));
        }
    }

    @FunctionalInterface
    private interface Condition {
        boolean matches();
    }
}
