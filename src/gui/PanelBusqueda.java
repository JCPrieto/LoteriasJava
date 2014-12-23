/**
 * 
 */
package gui;

import com.jklabs.loteriaselpais.Busqueda;
import com.jklabs.loteriaselpais.Conexion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author juanky
 * 
 */
public class PanelBusqueda extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	final transient private Ventana padre;
	transient private JButton buscar;
	transient private JButton limpiar;
	transient private JTextField numero;
	transient private JTextArea resultado;
	private String sorteo;
	private JTextField cantidad;

	public PanelBusqueda(final Ventana ventana, String string) {
		super();
		padre = ventana;
		sorteo = string;
		cargarElementos();
	}

	@Override
	public void actionPerformed(final ActionEvent evt) {
		if (evt.getSource() == buscar && !numero.getText().isEmpty()) {
			buscarPremio(numero.getText(), cantidad.getText());
		}
		if (evt.getSource() == limpiar) {
			resultado.setText("");
			numero.setText("");
			padre.pack();
		}
	}

	private void buscarPremio(final String text, String cantidadText) {
		Conexion c;
		c = new Conexion(sorteo, text);
		if (c.consulta()) {
			Busqueda bsc = new Busqueda(c.getResultado());
			resultado.setText(resultado.getText() + "\n" + text + ": Ha ganado: "
					+ (Math.round((bsc.getPremio() / Double.parseDouble(cantidadText)) * 100D) / 100D) + "€");
		} else {
			resultado.setText(resultado.getText()
					+ "\nHay un problema con el sevidor");
		}
		padre.pack();
	}

	private void cargarElementos() {
		super.setLayout(new BorderLayout());
		final JPanel entrada = new JPanel();
		numero = new JTextField(5);
		numero.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(final KeyEvent evt) {

			}

			@Override
			public void keyReleased(final KeyEvent evt) {

			}

			@Override
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

			@Override
			public void keyTyped(KeyEvent e) {
				final char caracter = e.getKeyChar();
				if (((caracter < '0') || (caracter > '9')) && (caracter != '.')) {
					e.consume();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}

			@Override
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
		resultado = new JTextArea();
		resultado.setEditable(false);
		super.add(entrada, BorderLayout.NORTH);
		super.add(limpiar, BorderLayout.SOUTH);
		super.add(resultado, BorderLayout.CENTER);
	}

}
