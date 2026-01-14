package es.jklabs.desktop.gui.paneles;

import es.jklabs.desktop.gui.Ventana;
import es.jklabs.utilidades.Logger;
import io.github.jcprieto.lib.loteria.conexion.Conexion;
import io.github.jcprieto.lib.loteria.enumeradores.Sorteo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Serial;

/**
 * Created by juanky on 26/09/15.
 */
public class MenuPrincipal extends JPanel implements ActionListener {

    @Serial
    private static final long serialVersionUID = -5513004495828699993L;
    private final Ventana padre;
    private JButton btnResumenNavidad;
    private JButton btnBuscarPremioNavidad;
    private JButton btnResumenNino;
    private JButton btnBuscarPremioNino;
    private boolean cargando;

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
            cargarResumenNavidad();
        }
        if (e.getSource().equals(btnBuscarPremioNavidad)) {
            padre.setPanel(new PanelBusqueda(padre, Sorteo.NAVIDAD));
            padre.setPanelInferior(new PanelInferior(padre));
            padre.pack();
        }
        if (e.getSource().equals(btnResumenNino)) {
            cargarResumenNino();
        }
        if (e.getSource().equals(btnBuscarPremioNino)) {
            padre.setPanel(new PanelBusqueda(padre, Sorteo.NINO));
            padre.setPanelInferior(new PanelInferior(padre));
            padre.pack();
        }
    }

    private void cargarResumenNavidad() {
        if (cargando) {
            return;
        }
        setCargando(true);
        SwingWorker<io.github.jcprieto.lib.loteria.model.navidad.ResumenNavidad, Void> worker = new SwingWorker<>() {
            @Override
            protected io.github.jcprieto.lib.loteria.model.navidad.ResumenNavidad doInBackground() throws IOException {
                Conexion con = new Conexion();
                return con.getResumenNavidad();
            }

            @Override
            protected void done() {
                try {
                    io.github.jcprieto.lib.loteria.model.navidad.ResumenNavidad resumen = get();
                    if (resumen == null) {
                        JOptionPane.showMessageDialog(padre, "Hay un problema con el servidor, intentelo en unos minutos", "Atención!", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    padre.setPanel(new ResumenNavidad(padre, resumen));
                    padre.setPanelInferior(new PanelInferior(padre));
                    padre.pack();
                } catch (Exception e) {
                    Logger.error("Cargar resumen Navidad", e);
                    JOptionPane.showMessageDialog(padre, "Hay un problema con el servidor, intentelo en unos minutos", "Atención!", JOptionPane.WARNING_MESSAGE);
                } finally {
                    setCargando(false);
                }
            }
        };
        worker.execute();
    }

    private void cargarResumenNino() {
        if (cargando) {
            return;
        }
        setCargando(true);
        SwingWorker<io.github.jcprieto.lib.loteria.model.nino.ResumenNino, Void> worker = new SwingWorker<>() {
            @Override
            protected io.github.jcprieto.lib.loteria.model.nino.ResumenNino doInBackground() throws IOException {
                Conexion con = new Conexion();
                return con.getResumenNino();
            }

            @Override
            protected void done() {
                try {
                    io.github.jcprieto.lib.loteria.model.nino.ResumenNino resumen = get();
                    if (resumen == null) {
                        JOptionPane.showMessageDialog(padre, "Hay un problema con el servidor, intentelo en unos minutos", "Atención!", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    padre.setPanel(new ResumenNino(padre, resumen));
                    padre.setPanelInferior(new PanelInferior(padre));
                    padre.pack();
                } catch (Exception e) {
                    Logger.error("Cargar resumen Niño", e);
                    JOptionPane.showMessageDialog(padre, "Hay un problema con el servidor, intentelo en unos minutos", "Atención!", JOptionPane.WARNING_MESSAGE);
                } finally {
                    setCargando(false);
                }
            }
        };
        worker.execute();
    }

    private void setCargando(boolean activo) {
        cargando = activo;
        btnResumenNavidad.setEnabled(!activo);
        btnBuscarPremioNavidad.setEnabled(!activo);
        btnResumenNino.setEnabled(!activo);
        btnBuscarPremioNino.setEnabled(!activo);
        setCursor(activo ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }
}
