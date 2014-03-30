/**
 * 
 */
package gui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author juanky
 * 
 */
public class AcercaDe extends JDialog implements MouseListener, ActionListener {

	private static final String CRITICO = "Mensaje Critico";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final transient private JButton botonOk;
	final transient private JLabel etq3;
	final transient private JLabel etq5;

	public AcercaDe(final Ventana ventana) {
		// TODO Auto-generated constructor stub
		super(ventana, "Loterias de Navidad - Acerca de...");
		super.setIconImage(new ImageIcon("res/line-globe.png").getImage());
		final JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		final GridBagConstraints cns = new GridBagConstraints();
		final JLabel etq1 = new JLabel(
				"<html><h1>Loteria de Navidad 1.1</h1></html>", JLabel.CENTER);
		cns.fill = GridBagConstraints.HORIZONTAL;
		cns.insets = new Insets(10, 0, 10, 0);
		cns.gridx = 0;
		cns.gridy = 0;
		cns.gridwidth = 3;
		panel.add(etq1, cns);
		final JLabel etq2 = new JLabel("Creado por: Juan Carlos Prieto Silos",
				JLabel.CENTER);
		cns.gridy = 1;
		panel.add(etq2, cns);
		etq3 = new JLabel("JuanC.Prieto.Silos@gmail.com", JLabel.CENTER);
		etq3.setAlignmentX(CENTER_ALIGNMENT);
		etq3.setForeground(Color.blue);
		etq3.addMouseListener(this);
		cns.gridy = 2;
		panel.add(etq3, cns);
		final JLabel etq4 = new JLabel("Este programa hace uso de la API de ");
		cns.gridy = 3;
		cns.gridwidth = 1;
		panel.add(etq4, cns);
		etq5 = new JLabel("El Pais");
		etq5.setForeground(Color.blue);
		etq5.addMouseListener(this);
		cns.gridx = 1;
		panel.add(etq5, cns);
		final JLabel etq6 = new JLabel(
				" para la comprobacion de los numero premiados.");
		cns.gridx = 2;
		panel.add(etq6, cns);
		botonOk = new JButton("Aceptar");
		botonOk.addActionListener(this);
		cns.gridx = 0;
		cns.gridy = 4;
		cns.gridwidth = 3;
		panel.add(botonOk, cns);
		super.add(panel);
		super.pack();
	}

	@Override
	public void actionPerformed(final ActionEvent evt) {
		// TODO Auto-generated method stub
		if (evt.getSource() == botonOk) {
			this.dispose();
		}
	}

	@Override
	public void mouseClicked(final MouseEvent evt) {
		// TODO Auto-generated method stub
		if (evt.getSource() == etq3) {
			try {
				Desktop.getDesktop()
						.browse(new URI(
								"mailto:JuanC.Prieto.Silos@gmail.com?subject=Loteria_Navidad_Java"));
				etq3.setForeground(Color.red);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				Logger.getLogger("Correo").log(Level.SEVERE, CRITICO, e1);
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				Logger.getLogger("Correo").log(Level.SEVERE, CRITICO, e1);
			}
		}
		if (evt.getSource() == etq5) {
			try {
				Desktop.getDesktop().browse(
						new URI("http://servicios.elpais.com"));
				etq5.setForeground(Color.red);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				Logger.getLogger("Link").log(Level.SEVERE, CRITICO, e1);
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				Logger.getLogger("Link").log(Level.SEVERE, CRITICO, e1);
			}
		}
	}

	@Override
	public void mouseEntered(final MouseEvent evt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(final MouseEvent evt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(final MouseEvent evt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(final MouseEvent evt) {
		// TODO Auto-generated method stub

	}

}
