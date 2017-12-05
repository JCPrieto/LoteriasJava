package es.jklabs.desktop.gui.listener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResumenMouseListener implements MouseListener {

    private static final String CRITICO = "Error Critico";

    private final JLabel pdf;

    public ResumenMouseListener(JLabel pdf) {
        this.pdf = pdf;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == pdf) {
            try {
                Desktop.getDesktop().browse(new URI(pdf.getText()));
                pdf.setForeground(Color.red);
            } catch (IOException | URISyntaxException e1) {
                Logger.getLogger("PDF").log(Level.SEVERE, CRITICO, e1);
            }
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

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
