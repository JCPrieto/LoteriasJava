package es.jklabs.gui.paneles;

import es.jklabs.gui.Ventana;
import es.jklabs.gui.utilidades.listener.IntegerKeyListener;
import es.jklabs.lib.loteria.conexion.Conexion;
import es.jklabs.lib.loteria.enumeradores.Sorteo;
import es.jklabs.utilidades.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

/**
 * @author juanky
 */
public class PanelBusqueda extends JPanel implements ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = 3L;
    private final transient Ventana padre;
    private transient JButton buscar;
    private transient JButton limpiar;
    private transient JTextField numero;
    private transient JPanel resultado;
    private Sorteo sorteo;
    private JTextField cantidad;
    private GridBagConstraints cns;
    private int contador;

    public PanelBusqueda(final Ventana ventana, Sorteo sorteo) {
        super();
        padre = ventana;
        this.sorteo = sorteo;
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
        c = new Conexion();
        cns.gridy = contador++;
        try {
            resultado.add(new Resultado(text, c.getPremio(sorteo, text).getCantidad(), cantidadText), cns);
            contador++;
            padre.pack();
        } catch (IOException e) {
            contador--;
            Logger.error("Buscar premio", e);
            JOptionPane.showMessageDialog(padre, "Hay un problema con el servidor, intentelo en unos minutos", "Atención!", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cargarElementos() {
        super.setBorder(new EmptyBorder(10, 10, 10, 10));
        super.setLayout(new BorderLayout());
        final JPanel entrada = new JPanel();
        numero = new JTextField(5);
        numero.addKeyListener(new IntegerKeyListener(numero));
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
