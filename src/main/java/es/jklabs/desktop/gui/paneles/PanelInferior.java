package es.jklabs.desktop.gui.paneles;

import es.jklabs.desktop.gui.Ventana;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by juanky on 28/09/15.
 */
public class PanelInferior extends JPanel implements ActionListener {

    private final JButton btnBack;
    private final Ventana padre;

    public PanelInferior(Ventana padre) {
        super();
        this.padre = padre;
        super.setLayout(new BorderLayout(10, 10));
        btnBack = new JButton("Volver");
        btnBack.addActionListener(this);
        super.add(btnBack, BorderLayout.EAST);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnBack)) {
            padre.eliminarPanelInferior();
            padre.setPanel(new MenuPrincipal(padre));
            padre.pack();
        }
    }
}
