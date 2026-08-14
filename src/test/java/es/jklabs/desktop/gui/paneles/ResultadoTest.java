package es.jklabs.desktop.gui.paneles;

import es.jklabs.utilidades.BaseTest;
import es.jklabs.utilidades.Mensajes;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResultadoTest extends BaseTest {

    private static Resultado crearEnEdt(String numero, BigDecimal premio, BigDecimal cantidad) throws Exception {
        AtomicReference<Resultado> resultado = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> resultado.set(new Resultado(numero, premio, cantidad)));
        return resultado.get();
    }

    private static void assertEtiquetas(Resultado resultado, String numero, BigDecimal ganado) {
        assertEquals(2, resultado.getComponentCount());
        assertEquals(numero + ":", ((JLabel) resultado.getComponent(0)).getText());
        assertEquals(Mensajes.getMensaje("resultado.ha.ganado")
                        + NumberFormat.getCurrencyInstance().format(ganado),
                ((JLabel) resultado.getComponent(1)).getText());
    }

    @Test
    void muestraElPremioMultiplicadoPorLaCantidad() throws Exception {
        Resultado resultado = crearEnEdt("12345", new BigDecimal("10.25"), new BigDecimal("2"));

        assertEtiquetas(resultado, "12345", new BigDecimal("20.50"));
    }

    @Test
    void muestraCeroCuandoElPremioEsNulo() throws Exception {
        Resultado resultado = crearEnEdt("54321", null, new BigDecimal("2"));

        assertEtiquetas(resultado, "54321", BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY));
    }
}
