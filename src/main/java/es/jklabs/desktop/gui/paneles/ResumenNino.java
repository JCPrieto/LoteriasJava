/**
 *
 */
package es.jklabs.desktop.gui.paneles;

import com.jklabs.lib.loteria.conexion.Conexion;
import com.jklabs.lib.loteria.model.ResultadosNino;
import es.jklabs.desktop.gui.Ventana;

import javax.swing.*;
import javax.swing.border.TitledBorder;
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
public class ResumenNino extends JPanel implements ActionListener,
        MouseListener {

    private static final String CRITICO = "Error Critico";
    /**
     *
     */
    private static final long serialVersionUID = 2L;
    final transient private Ventana padre;
    final private Timer tiempo;
    transient private JLabel actualizacion;
    transient private JLabel estado;
    transient private JPanel panelExt2;
    transient private JPanel panelExt3;
    transient private JPanel panelReintegros;
    transient private JLabel pdf;
    private JLabel primero;
    transient private ResultadosNino res;
    private JLabel segundo;
    private JLabel tercero;
    private JPanel panelExt4;

    public ResumenNino(final Ventana ventana) {
        super();
        padre = ventana;
        cargarDatos();
        tiempo = new Timer(30000, this);
        tiempo.start();
    }

    @Override
    public void actionPerformed(final ActionEvent evt) {
        if (evt.getSource() == tiempo) {
            actualizarResumen();
        }
    }

    /**
     * Actualiza los paneles con los resultados del sorteo
     */
    private void actualizarResumen() {
        final Conexion con = new Conexion("Nino", "resumen");
        if (con.consulta()) {
            res = new ResultadosNino(con.getResultado());
            segundo.setText(res.getSegundo());
            tercero.setText(res.getTercero());
            setPanelExt4(res.getExtraccionCuatro());
            setPanelExt3(res.getExtraccionTres());
            setPanelExt2(res.getExtraccionDos());
            setPanelReintegros(res.getReintegros());
            estado.setText("Estado del Sorteo: " + res.getEstado());
            actualizacion.setText("Ultima Actualización: " + res.getFecha());
            pdf.setText(res.getPDF());
            padre.pack();
        }
    }

    private void setPanelExt4(String[] extraccionCuatro) {
        panelExt4.removeAll();
        for (String extraccionDo : extraccionCuatro) {
            panelExt4.add(new JLabel(extraccionDo, JLabel.CENTER));
        }
    }

    private void cargarDatos() {
        final Conexion con = new Conexion("Nino", "resumen");
        if (con.consulta()) {
            res = new ResultadosNino(con.getResultado());
            cargarElementos();
        }
    }

    /**
     * Realiza la primera carga de los paneles con los resultados del sorteo.
     */
    private void cargarElementos() {
        super.setLayout(new GridBagLayout());
        final GridBagConstraints cns = new GridBagConstraints();
        JPanel panelPrimero = new JPanel();
        panelPrimero.setBorder(new TitledBorder("Primer Premio"));
        primero = new JLabel(res.getPrimero(), JLabel.CENTER);
        panelPrimero.add(primero);
        cns.fill = GridBagConstraints.HORIZONTAL;
        cns.gridx = 0;
        cns.gridy = 0;
        cns.gridwidth = 2;
        cns.weightx = 1;
        cns.insets = new Insets(10, 0, 10, 0);
        super.add(panelPrimero, cns);
        JPanel panelSegundo = new JPanel();
        panelSegundo.setBorder(new TitledBorder("Segundo Premio"));
        segundo = new JLabel(res.getSegundo(), JLabel.CENTER);
        panelSegundo.add(segundo);
        cns.gridwidth = 1;
        cns.gridx = 0;
        cns.gridy = 2;
        super.add(panelSegundo, cns);
        JPanel panelTercero = new JPanel();
        panelTercero.setBorder(new TitledBorder("Tercer Premio"));
        tercero = new JLabel(res.getTercero(), JLabel.CENTER);
        panelTercero.add(tercero);
        cns.gridx = 1;
        cns.gridy = 2;
        super.add(panelTercero, cns);
        panelExt4 = new JPanel(new GridLayout(0, 2));
        panelExt4.setBorder(new TitledBorder("Extracciones de 4 Cifras"));
        setPanelExt4(res.getExtraccionCuatro());
        cns.gridx = 0;
        cns.gridy = 3;
        cns.gridwidth = 2;
        super.add(panelExt4, cns);
        panelExt3 = new JPanel(new GridLayout(7, 2));
        panelExt3.setBorder(new TitledBorder("Extracciones de 3 Cifras"));
        setPanelExt3(res.getExtraccionTres());
        cns.gridy = 4;
        super.add(panelExt3, cns);
        panelExt2 = new JPanel(new GridLayout(0, 5));
        panelExt2.setBorder(new TitledBorder("Extracciones de 2 Cifras"));
        setPanelExt2(res.getExtraccionDos());
        cns.fill = GridBagConstraints.HORIZONTAL;
        cns.gridy = 5;
        super.add(panelExt2, cns);
        panelReintegros = new JPanel(new GridLayout(0, 3));
        panelReintegros.setBorder(new TitledBorder("Reintegros"));
        setPanelReintegros(res.getReintegros());
        cns.gridy = 6;
        super.add(panelReintegros, cns);
        estado = new JLabel("Estado del Sorteo: " + res.getEstado(),
                JLabel.CENTER);
        cns.gridy = 7;
        super.add(estado, cns);
        actualizacion = new JLabel("Ultima Actualización: " + res.getFecha(),
                JLabel.CENTER);
        cns.gridy = 8;
        super.add(actualizacion, cns);
        pdf = new JLabel(res.getPDF(), JLabel.CENTER);
        pdf.setForeground(Color.blue);
        pdf.addMouseListener(this);
        cns.gridy = 9;
        super.add(pdf, cns);
    }

    @Override
    public void mouseClicked(final MouseEvent evt) {
        if (evt.getSource() == pdf) {
            try {
                Desktop.getDesktop().browse(new URI(pdf.getText()));
                pdf.setForeground(Color.red);
            } catch (IOException e1) {
                Logger.getLogger("PDF").log(Level.SEVERE, CRITICO, e1);
            } catch (URISyntaxException e1) {
                Logger.getLogger("PDF").log(Level.SEVERE, CRITICO, e1);
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

    /**
     * Establece los numeros de la estraccion de 2 cifras en el panel.
     *
     * @param extraccionDos Array de cadena que contiene los números.
     */
    private void setPanelExt2(String[] extraccionDos) {
        panelExt2.removeAll();
        for (String extraccionDo : extraccionDos) {
            panelExt2.add(new JLabel(extraccionDo, JLabel.CENTER));
        }
    }

    /**
     * Establece los numeros de la estraccion de 3 cifras en el panel.
     *
     * @param extraccionTres Array de cadena que contiene los números
     */
    private void setPanelExt3(String[] extraccionTres) {
        panelExt3.removeAll();
        for (String extraccionDo : extraccionTres) {
            panelExt3.add(new JLabel(extraccionDo, JLabel.CENTER));
        }
    }

    /**
     * Establece los numeros de reintegro en el panel.
     *
     * @param reintegros2 Array de cadena que contiene los números
     */
    private void setPanelReintegros(String[] reintegros2) {
        panelReintegros.removeAll();
        for (String extraccionDo : reintegros2) {
            panelReintegros.add(new JLabel(extraccionDo, JLabel.CENTER));
        }
    }

}
