/**
 *
 */
package es.jklabs.desktop.gui.paneles;

import com.jklabs.lib.loteria.conexion.Conexion;
import com.jklabs.lib.loteria.service.Busqueda;
import es.jklabs.desktop.gui.Ventana;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author juanky
 */
public class PanelBusqueda extends JPanel implements ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = 3L;
    final transient private Ventana padre;
    transient private JButton buscar;
    transient private JButton limpiar;
    transient private JTextField numero;
    transient private JPanel resultado;
    private String sorteo;
    private JTextField cantidad;
    private GridBagConstraints cns;
    private int contador;

    public PanelBusqueda(final Ventana ventana, String string) {
        super();
        padre = ventana;
        sorteo = string;
        cargarElementos();
    }


    public void actionPerformed(final ActionEvent evt) {
        if (evt.getSource() == buscar && !numero.getText().isEmpty()) {
            buscarPremio(numero.getText(), cantidad.getText());
        }
        if (evt.getSource() == limpiar) {
            resultado.removeAll();
            resultado.repaint();
            numero.setText("");
            cantidad.setText("");
            contador = 0;
            padre.pack();
        }
    }

    private void buscarPremio(final String text, String cantidadText) {
        Conexion c;
        c = new Conexion(sorteo, text);
        if (c.consulta()) {
            Busqueda bsc = new Busqueda(c.getResultado());
            cns.gridy = contador++;
            resultado.add(new Resultado(text, bsc.getPremio(), cantidadText), cns);
            contador++;
        } else {
            JOptionPane.showMessageDialog(padre, "Hay un problema con el servidor, intentelo en unos minutos", "Atención!", JOptionPane.WARNING_MESSAGE);
        }
        padre.pack();
    }

    private void cargarElementos() {
        super.setLayout(new BorderLayout());
        final JPanel entrada = new JPanel();
        numero = new JTextField(5);
        numero.addKeyListener(new KeyListener() {


            public void keyPressed(final KeyEvent evt) {

            }


            public void keyReleased(final KeyEvent evt) {

            }


            public void keyTyped(final KeyEvent evt) {
                final char caracter = evt.getKeyChar();
                if (((caracter < '0') || (caracter > '9'))
                        && (caracter != '\b') || numero.getText().length() > 4) {
                    evt.consume();
                }
            }
        });
        cantidad = new JTextField(3);
        cantidad.addKeyListener(new KeyListener() {


            public void keyTyped(KeyEvent e) {
                final char caracter = e.getKeyChar();
                if (((caracter < '0') || (caracter > '9')) && (caracter != '.')) {
                    e.consume();
                }
            }


            public void keyPressed(KeyEvent e) {

            }


            public void keyReleased(KeyEvent e) {

            }
        });
        buscar = new JButton("Buscar premio");
        buscar.addActionListener(this);
        entrada.add(new JLabel("Número:"));
        entrada.add(numero);
        entrada.add(new JLabel("Cantidad Jugada:"));
        entrada.add(cantidad);
        entrada.add(new JLabel("€"));
        entrada.add(buscar);
        limpiar = new JButton("Limpiar resultados");
        limpiar.addActionListener(this);
        resultado = new JPanel();
        resultado.setLayout(new GridBagLayout());
        cns = new GridBagConstraints();
        cns.gridx = 0;
        cns.gridy = 0;
        cns.gridwidth = 1;
        cns.weightx = 1;
        cns.insets = new Insets(10, 0, 10, 0);
        JScrollPane scrollPane = new JScrollPane(resultado);
        super.add(entrada, BorderLayout.NORTH);
        super.add(limpiar, BorderLayout.SOUTH);
        super.add(scrollPane, BorderLayout.CENTER);
    }

}
