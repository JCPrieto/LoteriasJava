package es.jklabs.desktop.gui.dialogos;

import es.jklabs.gui.utilidades.Growls;
import es.jklabs.gui.utilidades.listener.UrlMouseListener;
import es.jklabs.utilidades.BaseTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import sun.misc.Unsafe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

class AcercaDeTest extends BaseTest {

    @AfterEach
    void resetTestHooks() {
        AcercaDe.setMailBrowserForTests(null);
    }

    private static JLabel invokeGetInfo() throws Exception {
        Method method = AcercaDe.class.getDeclaredMethod("getInfo");
        method.setAccessible(true);
        return (JLabel) method.invoke(null);
    }

    private static TestAcercaDe createDialogWithoutConstructor() throws Exception {
        Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) theUnsafeField.get(null);
        return (TestAcercaDe) unsafe.allocateInstance(TestAcercaDe.class);
    }

    private static void setBotonOk(AcercaDe acercaDe, JButton button) throws Exception {
        Field botonOkField = AcercaDe.class.getDeclaredField("botonOk");
        botonOkField.setAccessible(true);
        botonOkField.set(acercaDe, button);
    }

    private static void invokeAddPowered(AcercaDe acercaDe, JPanel panel, GridBagConstraints cns,
                                         int y, String titulo, String url) throws Exception {
        Method method = AcercaDe.class.getDeclaredMethod(
                "addPowered", JPanel.class, GridBagConstraints.class, int.class, String.class, String.class);
        method.setAccessible(true);
        method.invoke(acercaDe, panel, cns, y, titulo, url);
    }

    @Test
    void getInfoBuildsMailLabelWithListener() throws Exception {
        JLabel label = invokeGetInfo();

        assertEquals("JuanC.Prieto.Silos@gmail.com", label.getText());
        assertEquals(SwingConstants.LEFT, label.getHorizontalAlignment());
        assertEquals(Component.CENTER_ALIGNMENT, label.getAlignmentX());
        assertEquals(1, label.getMouseListeners().length);
    }

    @Test
    void getInfoChangesCursorOnMouseEnterAndExit() throws Exception {
        JLabel label = invokeGetInfo();
        MouseListener listener = label.getMouseListeners()[0];

        listener.mouseEntered(new MouseEvent(label, MouseEvent.MOUSE_ENTERED,
                System.currentTimeMillis(), 0, 1, 1, 1, false));
        assertNotNull(label.getCursor());
        assertEquals(Cursor.HAND_CURSOR, label.getCursor().getType());

        listener.mouseExited(new MouseEvent(label, MouseEvent.MOUSE_EXITED,
                System.currentTimeMillis(), 0, 1, 1, 1, false));
        assertNotNull(label.getCursor());
        assertEquals(Cursor.DEFAULT_CURSOR, label.getCursor().getType());
    }

    @Test
    void getInfoMousePressedAndReleasedDoNotChangeState() throws Exception {
        JLabel label = invokeGetInfo();
        MouseListener listener = label.getMouseListeners()[0];
        Cursor initialCursor = label.getCursor();

        listener.mousePressed(new MouseEvent(label, MouseEvent.MOUSE_PRESSED,
                System.currentTimeMillis(), 0, 1, 1, 1, false));
        listener.mouseReleased(new MouseEvent(label, MouseEvent.MOUSE_RELEASED,
                System.currentTimeMillis(), 0, 1, 1, 1, false));

        assertSame(initialCursor, label.getCursor());
    }

    @Test
    void getInfoMouseClickedOpensMailUri() throws Exception {
        JLabel label = invokeGetInfo();
        MouseListener listener = label.getMouseListeners()[0];
        AtomicReference<URI> openedUri = new AtomicReference<>();
        AcercaDe.setMailBrowserForTests(openedUri::set);

        listener.mouseClicked(new MouseEvent(label, MouseEvent.MOUSE_CLICKED,
                System.currentTimeMillis(), 0, 1, 1, 1, false));

        assertEquals(URI.create("mailto:JuanC.Prieto.Silos@gmail.com?subject=Loteria_Navidad_Java"), openedUri.get());
    }

    @Test
    void getInfoMouseClickedShowsGrowlWhenMailCannotBeOpened() throws Exception {
        JLabel label = invokeGetInfo();
        MouseListener listener = label.getMouseListeners()[0];
        IOException exception = new IOException("mail client unavailable");
        AcercaDe.setMailBrowserForTests(uri -> {
            throw exception;
        });

        try (MockedStatic<Growls> growlsMock = Mockito.mockStatic(Growls.class)) {
            listener.mouseClicked(new MouseEvent(label, MouseEvent.MOUSE_CLICKED,
                    System.currentTimeMillis(), 0, 1, 1, 1, false));

            growlsMock.verify(() -> Growls.mostrarError("acerca.de", "app.envio.correo", exception), times(1));
        }
    }

    @Test
    void addPoweredAddsTitleAndUrlWhenUrlExists() throws Exception {
        TestAcercaDe acercaDe = createDialogWithoutConstructor();
        GridBagLayout layout = new GridBagLayout();
        JPanel panel = new JPanel(layout);
        GridBagConstraints cns = new GridBagConstraints();

        invokeAddPowered(acercaDe, panel, cns, 5, "Jackson", "https://example.com");

        assertEquals(2, panel.getComponentCount());
        JLabel titleLabel = (JLabel) panel.getComponent(0);
        JLabel urlLabel = (JLabel) panel.getComponent(1);

        assertEquals("<html><b>Jackson</b></html>", titleLabel.getText());
        assertInstanceOf(UrlMouseListener.class, titleLabel.getMouseListeners()[0]);
        GridBagConstraints titleConstraints = layout.getConstraints(titleLabel);
        assertEquals(0, titleConstraints.gridx);
        assertEquals(5, titleConstraints.gridy);
        assertEquals(1, titleConstraints.gridwidth);
        assertEquals("https://example.com", urlLabel.getText());
        assertInstanceOf(UrlMouseListener.class, urlLabel.getMouseListeners()[0]);
        GridBagConstraints urlConstraints = layout.getConstraints(urlLabel);
        assertEquals(1, urlConstraints.gridx);
        assertEquals(5, urlConstraints.gridy);
        assertEquals(2, urlConstraints.gridwidth);
    }

    @Test
    void addPoweredIncluyeTwoSlicesComoLibreriaDeNotificaciones() throws Exception {
        TestAcercaDe acercaDe = createDialogWithoutConstructor();
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cns = new GridBagConstraints();

        invokeAddPowered(acercaDe, panel, cns, 7, "two-slices", "https://github.com/sshtools/two-slices");

        assertEquals(2, panel.getComponentCount());
        JLabel titleLabel = (JLabel) panel.getComponent(0);
        JLabel urlLabel = (JLabel) panel.getComponent(1);
        assertEquals("<html><b>two-slices</b></html>", titleLabel.getText());
        assertEquals("https://github.com/sshtools/two-slices", urlLabel.getText());
        assertInstanceOf(UrlMouseListener.class, titleLabel.getMouseListeners()[0]);
        assertInstanceOf(UrlMouseListener.class, urlLabel.getMouseListeners()[0]);
    }

    @Test
    void addPoweredIncluyeDbusJavaComoLibreriaDeNotificaciones() throws Exception {
        TestAcercaDe acercaDe = createDialogWithoutConstructor();
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cns = new GridBagConstraints();

        invokeAddPowered(acercaDe, panel, cns, 8, "dbus-java", "https://github.com/hypfvieh/dbus-java");

        assertEquals(2, panel.getComponentCount());
        JLabel titleLabel = (JLabel) panel.getComponent(0);
        JLabel urlLabel = (JLabel) panel.getComponent(1);
        assertEquals("<html><b>dbus-java</b></html>", titleLabel.getText());
        assertEquals("https://github.com/hypfvieh/dbus-java", urlLabel.getText());
        assertInstanceOf(UrlMouseListener.class, titleLabel.getMouseListeners()[0]);
        assertInstanceOf(UrlMouseListener.class, urlLabel.getMouseListeners()[0]);
    }

    @Test
    void addPoweredAddsOnlyTitleWhenUrlIsNull() throws Exception {
        TestAcercaDe acercaDe = createDialogWithoutConstructor();
        GridBagLayout layout = new GridBagLayout();
        JPanel panel = new JPanel(layout);
        GridBagConstraints cns = new GridBagConstraints();

        invokeAddPowered(acercaDe, panel, cns, 6, "Papirus", null);

        assertEquals(1, panel.getComponentCount());
        JLabel titleLabel = (JLabel) panel.getComponent(0);
        assertEquals("<html><b>Papirus</b></html>", titleLabel.getText());
        assertEquals(0, titleLabel.getMouseListeners().length);
        GridBagConstraints titleConstraints = layout.getConstraints(titleLabel);
        assertEquals(0, titleConstraints.gridx);
        assertEquals(6, titleConstraints.gridy);
        assertEquals(1, titleConstraints.gridwidth);
    }

    @Test
    void actionPerformedDisposesDialogWhenSourceIsOkButton() throws Exception {
        TestAcercaDe acercaDe = createDialogWithoutConstructor();
        JButton okButton = new JButton("OK");
        setBotonOk(acercaDe, okButton);

        acercaDe.actionPerformed(new ActionEvent(okButton, ActionEvent.ACTION_PERFORMED, "click"));

        assertTrue(acercaDe.disposed);
    }

    @Test
    void actionPerformedDoesNotDisposeWhenSourceIsDifferent() throws Exception {
        TestAcercaDe acercaDe = createDialogWithoutConstructor();
        JButton okButton = new JButton("OK");
        JButton otherButton = new JButton("OTHER");
        setBotonOk(acercaDe, okButton);

        acercaDe.actionPerformed(new ActionEvent(otherButton, ActionEvent.ACTION_PERFORMED, "click"));

        assertFalse(acercaDe.disposed);
    }

    static class TestAcercaDe extends AcercaDe {
        boolean disposed;

        TestAcercaDe() {
            super(null);
        }

        @Override
        public void dispose() {
            disposed = true;
        }
    }
}
