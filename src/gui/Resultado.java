package gui;

import javax.swing.*;

/**
 * Created by juanky on 26/12/14.
 */
public class Resultado extends JPanel {
    private final double ganado;

    public Resultado(String numero, Double premio, String cantidad) {
        super();
        this.ganado = Math.round((premio / Double.parseDouble(cantidad)) * 100D) / 100D;
        super.add(new JLabel(numero + ":"));
        super.add(new JLabel("Ha ganado: " + ganado + "€"));
    }
}
