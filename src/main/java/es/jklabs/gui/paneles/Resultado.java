package es.jklabs.gui.paneles;

import javax.swing.*;

/**
 * Created by juanky on 26/12/14.
 */
public class Resultado extends JPanel {
    private static final long serialVersionUID = -3631961411117909743L;

    Resultado(String numero, Double premio, String cantidad) {
        super();
        double ganado = Math.round(premio * Double.parseDouble(cantidad));
        super.add(new JLabel(numero + ":"));
        super.add(new JLabel("Ha ganado: " + ganado + "€"));
    }
}
