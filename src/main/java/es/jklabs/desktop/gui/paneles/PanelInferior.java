package es.jklabs.desktop.gui.paneles;

import es.jklabs.desktop.gui.Ventana;
import es.jklabs.utilidades.Mensajes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;

/**
 * Created by juanky on 28/09/15.
 */
public class PanelInferior extends JPanel implements ActionListener {

    @Serial
    private static final long serialVersionUID = 6132315657313753236L;
    private final JButton btnBack;
    private final Ventana padre;

    PanelInferior(Ventana padre) {
        super();
        super.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.padre = padre;
        super.setLayout(new BorderLayout(10, 10));
        btnBack = new JButton(Mensajes.getMensaje("panel.volver"));
        btnBack.addActionListener(this);
        super.add(btnBack, BorderLayout.EAST);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnBack)) {
            padre.eliminarPanelInferior();
            padre.setPanel(new MenuPrincipal(padre));
            padre.pack();
        }
    }
}
