package es.jklabs.desktop.gui.dialogos;

import es.jklabs.desktop.constant.Constant;
import es.jklabs.desktop.gui.Ventana;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author juanky
 */
public class AcercaDe extends JDialog implements MouseListener, ActionListener {

    private static final String CRITICO = "Mensaje Critico";
    /**
     *
     */
    private static final long serialVersionUID = 4L;
    private transient JButton botonOk;
    private transient JLabel etq3;
    private transient JLabel etq5;

    public AcercaDe(final Ventana ventana) {
        super(ventana, "Loterias de Navidad - Acerca de...");
        try {
            String version = Constant.getValor("version");
            super.setIconImage(new ImageIcon("res/line-globe.png").getImage());
            final JPanel panel = new JPanel();
            panel.setLayout(new GridBagLayout());
            final GridBagConstraints cns = new GridBagConstraints();
            final JLabel etq1 = new JLabel(
                    "<html><h1>Loteria de Navidad " + version + "</h1></html>", JLabel.CENTER);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(final ActionEvent evt) {
        if (evt.getSource() == botonOk) {
            this.dispose();
        }
    }

    @Override
    public void mouseClicked(final MouseEvent evt) {
        if (evt.getSource() == etq3) {
            try {
                Desktop.getDesktop()
                        .browse(new URI(
                                "mailto:JuanC.Prieto.Silos@gmail.com?subject=Loteria_Navidad_Java"));
                etq3.setForeground(Color.red);
            } catch (IOException | URISyntaxException e1) {
                Logger.getLogger("Correo").log(Level.SEVERE, CRITICO, e1);
            }
        }
        if (evt.getSource() == etq5) {
            try {
                Desktop.getDesktop().browse(
                        new URI("http://servicios.elpais.com"));
                etq5.setForeground(Color.red);
            } catch (IOException | URISyntaxException e1) {
                Logger.getLogger("Link").log(Level.SEVERE, CRITICO, e1);
            }
        }
    }

    @Override
    public void mouseEntered(final MouseEvent evt) {

    }

    @Override
    public void mouseExited(final MouseEvent evt) {

    }

    @Override
    public void mousePressed(final MouseEvent evt) {

    }

    @Override
    public void mouseReleased(final MouseEvent evt) {

    }

}
