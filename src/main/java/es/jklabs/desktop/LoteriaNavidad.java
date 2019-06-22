package es.jklabs.desktop;

import es.jklabs.desktop.gui.Ventana;
import es.jklabs.gui.utilidades.Growls;
import es.jklabs.utilidades.Logger;

import javax.swing.*;

/**
 * @author juanky
 */
public final class LoteriaNavidad {

    private LoteriaNavidad() {

    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        Logger.init();
        try {
            Growls.init();
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            final Ventana vent = new Ventana();
            vent.setVisible(true);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            Logger.error("Cargar look and field del S.O.", e);
        }
    }

}
