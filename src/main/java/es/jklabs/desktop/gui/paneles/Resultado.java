package es.jklabs.desktop.gui.paneles;

import javax.swing.*;

/**
 * Created by juanky on 26/12/14.
 */
class Resultado extends JPanel {
    private static final long serialVersionUID = -3631961411117909743L;

    Resultado(String numero, Double premio, java.math.BigDecimal cantidad) {
        super();
        java.math.BigDecimal premioValor = premio == null ? java.math.BigDecimal.ZERO : java.math.BigDecimal.valueOf(premio);
        java.math.BigDecimal ganado = premioValor.multiply(cantidad).setScale(2, java.math.RoundingMode.HALF_UP);
        java.text.NumberFormat formato = java.text.NumberFormat.getCurrencyInstance();
        super.add(new JLabel(numero + ":"));
        super.add(new JLabel("Ha ganado: " + formato.format(ganado)));
    }
}
