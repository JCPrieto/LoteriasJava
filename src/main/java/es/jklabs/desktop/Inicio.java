package es.jklabs.desktop;

import es.jklabs.desktop.gui.Ventana;

/**
 * @author juanky
 */
public final class Inicio {

    private Inicio() {

    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        final Ventana vent = new Ventana();
        vent.setVisible(true);
    }

}
