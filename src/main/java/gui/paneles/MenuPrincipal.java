package gui.paneles;

import gui.PanelBusqueda;
import gui.ResumenNavidad;
import gui.ResumenNino;
import gui.Ventana;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by juanky on 26/09/15.
 */
public class MenuPrincipal extends JPanel implements ActionListener {
    private final Ventana padre;
    private JButton btnResumenNavidad;
    private JButton btnBuscarPremioNavidad;
    private JButton btnResumenNino;
    private JButton btnBuscarPremioNino;

    public MenuPrincipal(Ventana ventana) {
        super(new GridLayout(4, 1, 10, 10));
        padre = ventana;
        cargarBotonera();
    }

    private void cargarBotonera() {
        btnResumenNavidad = new JButton("Resumen de la Lotería de Navidad");
        btnResumenNavidad.addActionListener(this);
        btnBuscarPremioNavidad = new JButton("Buscar premios de la Lotería de Navidad");
        btnBuscarPremioNavidad.addActionListener(this);
        btnResumenNino = new JButton("Resumen de la Lotería del Niño");
        btnResumenNino.addActionListener(this);
        btnBuscarPremioNino = new JButton("Buscar premios de la Lotería del Niño");
        btnBuscarPremioNino.addActionListener(this);
        super.add(btnResumenNavidad);
        super.add(btnBuscarPremioNavidad);
        super.add(btnResumenNino);
        super.add(btnBuscarPremioNino);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnResumenNavidad)) {
            padre.setPanel(new ResumenNavidad(padre));
        }
        if (e.getSource().equals(btnBuscarPremioNavidad)) {
            padre.setPanel(new PanelBusqueda(padre, "Navidad"));
        }
        if (e.getSource().equals(btnResumenNino)) {
            padre.setPanel(new ResumenNino(padre));
        }
        if (e.getSource().equals(btnBuscarPremioNino)) {
            padre.setPanel(new PanelBusqueda(padre, "Nino"));
        }
    }
}
