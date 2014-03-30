/**
 * 
 */
package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.jklabs.loteriaselpais.Busqueda;
import com.jklabs.loteriaselpais.Conexion;

/**
 * @author juanky
 * 
 */
public class PanelBusqueda extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	transient private JButton buscar;
	transient private JButton limpiar;
	transient private JTextField numero;
	final transient private Ventana padre;
	transient private JTextArea resultado;
	private String sorteo;

	public PanelBusqueda(final Ventana ventana, String string) {
		super();
		padre = ventana;
		sorteo = string;
		cargarElementos();
	}

	@Override
	public void actionPerformed(final ActionEvent evt) {
		// TODO Auto-generated method stub
		if (evt.getSource() == buscar && !numero.getText().isEmpty()) {
			buscarPremio(numero.getText());
		}
		if (evt.getSource() == limpiar) {
			resultado.setText("");
			numero.setText("");
			padre.pack();
		}
	}

	private void buscarPremio(final String text) {
		// TODO Auto-generated method stub
		Conexion c;
		c = new Conexion(sorteo, text);
		if (c.consulta()) {
			Busqueda bsc = new Busqueda(c.getResultado());
			resultado.setText(resultado.getText() + "\n" + bsc.toString());
		} else {
			resultado.setText(resultado.getText()
					+ "\nHay un problema con el sevidor");
		}
		padre.pack();
	}

	private void cargarElementos() {
		// TODO Auto-generated method stub
		super.setLayout(new BorderLayout());
		final JPanel entrada = new JPanel();
		numero = new JTextField(5);
		numero.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(final KeyEvent evt) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(final KeyEvent evt) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyTyped(final KeyEvent evt) {
				// TODO Auto-generated method stub
				final char caracter = evt.getKeyChar();
				if (((caracter < '0') || (caracter > '9'))
						&& (caracter != '\b') || numero.getText().length() > 4) {
					evt.consume();
				}
			}
		});
		buscar = new JButton("Buscar premio");
		buscar.addActionListener(this);
		entrada.add(numero);
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
