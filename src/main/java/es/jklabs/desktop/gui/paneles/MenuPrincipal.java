package es.jklabs.desktop.gui.paneles;

import com.jklabs.lib.loteria.conexion.Conexion;
import es.jklabs.desktop.gui.Ventana;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by juanky on 26/09/15.
 */
public class MenuPrincipal extends JPanel implements ActionListener {

    private static final long serialVersionUID = -5513004495828699993L;
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

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnResumenNavidad)) {
            Conexion con = new Conexion("Navidad", "resumen");
            if (con.consulta()) {
                padre.setPanel(new ResumenNavidad(padre, con.getResultado()));
                padre.setPanelInferior(new PanelInferior(padre));
                padre.pack();
            } else {
                JOptionPane.showMessageDialog(padre, "Hay un problema con el servidor, intentelo en unos minutos", "Atención!", JOptionPane.WARNING_MESSAGE);
            }
        }
        if (e.getSource().equals(btnBuscarPremioNavidad)) {
            padre.setPanel(new PanelBusqueda(padre, "Navidad"));
            padre.setPanelInferior(new PanelInferior(padre));
            padre.pack();
        }
        if (e.getSource().equals(btnResumenNino)) {
            Conexion con = new Conexion("Nino", "resumen");
            if (con.consulta()) {
                padre.setPanel(new ResumenNino(padre, con.getResultado()));
                padre.setPanelInferior(new PanelInferior(padre));
                padre.pack();
            } else {
                JOptionPane.showMessageDialog(padre, "Hay un problema con el servidor, intentelo en unos minutos", "Atención!", JOptionPane.WARNING_MESSAGE);
            }
        }
        if (e.getSource().equals(btnBuscarPremioNino)) {
            padre.setPanel(new PanelBusqueda(padre, "Nino"));
            padre.setPanelInferior(new PanelInferior(padre));
            padre.pack();
        }
    }
}
