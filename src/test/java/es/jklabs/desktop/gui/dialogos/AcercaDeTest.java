package es.jklabs.desktop.gui.dialogos;

import es.jklabs.desktop.gui.Ventana;
import es.jklabs.gui.utilidades.listener.UrlMouseListener;
import es.jklabs.utilidades.BaseTest;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class AcercaDeTest extends BaseTest {

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
    void addPoweredAddsTitleAndUrlWhenUrlExists() throws Exception {
        TestAcercaDe acercaDe = createDialogWithoutConstructor();
        JPanel panel = new JPanel();
        GridBagConstraints cns = new GridBagConstraints();

        invokeAddPowered(acercaDe, panel, cns, 5, "Jackson", "https://example.com");

        assertEquals(2, panel.getComponentCount());
        JLabel titleLabel = (JLabel) panel.getComponent(0);
        JLabel urlLabel = (JLabel) panel.getComponent(1);

        assertEquals("<html><b>Jackson</b></html>", titleLabel.getText());
        assertTrue(titleLabel.getMouseListeners()[0] instanceof UrlMouseListener);
        assertEquals("https://example.com", urlLabel.getText());
        assertTrue(urlLabel.getMouseListeners()[0] instanceof UrlMouseListener);
    }

    @Test
    void addPoweredAddsOnlyTitleWhenUrlIsNull() throws Exception {
        TestAcercaDe acercaDe = createDialogWithoutConstructor();
        JPanel panel = new JPanel();
        GridBagConstraints cns = new GridBagConstraints();

        invokeAddPowered(acercaDe, panel, cns, 6, "Papirus", null);

        assertEquals(1, panel.getComponentCount());
        JLabel titleLabel = (JLabel) panel.getComponent(0);
        assertEquals("<html><b>Papirus</b></html>", titleLabel.getText());
        assertEquals(0, titleLabel.getMouseListeners().length);
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
            super((Ventana) null);
        }

        @Override
        public void dispose() {
            disposed = true;
        }
    }
}
