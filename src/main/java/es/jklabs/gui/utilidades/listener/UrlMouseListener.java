package es.jklabs.gui.utilidades.listener;

import es.jklabs.desktop.gui.Ventana;
import es.jklabs.gui.utilidades.Growls;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class UrlMouseListener implements MouseListener {
    private final Ventana padre;
    private final JLabel etiqueta;
    private final String url;

    public UrlMouseListener(Ventana ventana, JLabel jLabel, String url) {
        this.padre = ventana;
        this.etiqueta = jLabel;
        this.url = url;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e1) {
            Growls.mostrarError(padre, "abrir.enlace", e1);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        etiqueta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        etiqueta.setCursor(null);
    }
}
