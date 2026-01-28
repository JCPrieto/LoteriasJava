package es.jklabs.desktop.gui.paneles;

import es.jklabs.utilidades.Mensajes;

import javax.swing.*;
import java.io.Serial;

/**
 * Created by juanky on 26/12/14.
 */
class Resultado extends JPanel {
    @Serial
    private static final long serialVersionUID = -3631961411117909743L;

    Resultado(String numero, Double premio, java.math.BigDecimal cantidad) {
        super();
        java.math.BigDecimal premioValor = premio == null ? java.math.BigDecimal.ZERO : java.math.BigDecimal.valueOf(premio);
        java.math.BigDecimal ganado = premioValor.multiply(cantidad).setScale(2, java.math.RoundingMode.HALF_UP);
        java.text.NumberFormat formato = java.text.NumberFormat.getCurrencyInstance();
        super.add(new JLabel(numero + ":"));
        super.add(new JLabel(Mensajes.getMensaje("resultado.ha.ganado") + formato.format(ganado)));
    }
}
