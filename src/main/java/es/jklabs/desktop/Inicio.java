package es.jklabs.desktop;

import es.jklabs.desktop.gui.Ventana;

/**
 * @author juanky
 */
public final class Inicio {

    private static Inicio instance = new Inicio();

    private Inicio() {

    }

    public static Inicio getInstance() {
        return instance;
    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        final Ventana vent = new Ventana();
        vent.setVisible(true);
    }

}
