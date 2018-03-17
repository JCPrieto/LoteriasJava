package es.jklabs.desktop;

import es.jklabs.desktop.gui.Ventana;
import es.jklabs.utilidades.Logger;

import javax.swing.*;

/**
 * @author juanky
 */
public final class Inicio {

    private static final Logger LOG = Logger.getLogger();

    private Inicio() {

    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            LOG.error("Cargar look and field del S.O.", e);
        }
        final Ventana vent = new Ventana();
        vent.setVisible(true);
    }

}
