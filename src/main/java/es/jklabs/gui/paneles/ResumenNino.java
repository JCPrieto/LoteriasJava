package es.jklabs.gui.paneles;

import es.jklabs.desktop.gui.listener.ResumenMouseListener;
import es.jklabs.gui.Ventana;
import es.jklabs.lib.loteria.conexion.Conexion;
import es.jklabs.utilidades.Logger;
import es.jklabs.utilidades.UtilidadesEstadoSorteo;
import es.jklabs.utilidades.UtilidadesFecha;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * @author juanky
 */
public class ResumenNino extends JPanel implements ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = 2L;
    private final transient Ventana padre;
    private final Timer tiempo;
    private es.jklabs.lib.loteria.model.nino.ResumenNino res;
    private transient JLabel actualizacion;
    private transient JLabel estado;
    private transient JPanel panelExt2;
    private transient JPanel panelExt3;
    private transient JPanel panelReintegros;
    private transient JLabel pdf;
    private JLabel segundo;
    private JLabel tercero;
    private JPanel panelExt4;

    public ResumenNino(final Ventana ventana, es.jklabs.lib.loteria.model.nino.ResumenNino resultado) {
        super();
        padre = ventana;
        res = resultado;
        cargarElementos();
        tiempo = new Timer(30000, this);
        tiempo.start();
    }

    public void actionPerformed(final ActionEvent evt) {
        if (evt.getSource() == tiempo) {
            try {
                actualizarResumen();
            } catch (IOException e) {
                Logger.error("Actualizar datos de la pantalla", e);
            }
        }
    }

    /**
     * Actualiza los paneles con los resultados del sorteo
     */
    private void actualizarResumen() throws IOException {
        final Conexion con = new Conexion();
        res = con.getResumenNino();
        segundo.setText(res.getSegundo());
            tercero.setText(res.getTercero());
        setPanelExt4(res.getCuatroCifras());
        setPanelExt3(res.getTresCifras());
        setPanelExt2(res.getDosCifras());
            setPanelReintegros(res.getReintegros());
        estado.setText("Estado del Sorteo: " + UtilidadesEstadoSorteo.getHumanReadable(res.getEstado()));
        actualizacion.setText("Ultima Actualización: " + UtilidadesFecha.getHumanReadable(res
                .getFechaActualizacion()));
        pdf.setText(res.getUrlPDF());
            padre.pack();
    }

    private void setPanelExt4(java.util.List<String> extraccionCuatro) {
        panelExt4.removeAll();
        for (String extraccionDo : extraccionCuatro) {
            panelExt4.add(new JLabel(extraccionDo, SwingConstants.CENTER));
        }
    }

    /**
     * Realiza la primera carga de los paneles con los resultados del sorteo.
     */
    private void cargarElementos() {
        super.setBorder(new EmptyBorder(10, 10, 10, 10));
        super.setLayout(new GridBagLayout());
        final GridBagConstraints cns = new GridBagConstraints();
        JPanel panelPrimero = new JPanel();
        panelPrimero.setBorder(new TitledBorder("Primer Premio"));
        JLabel primero = new JLabel(res.getPrimero(), SwingConstants.CENTER);
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
        segundo = new JLabel(res.getSegundo(), SwingConstants.CENTER);
        panelSegundo.add(segundo);
        cns.gridwidth = 1;
        cns.gridx = 0;
        cns.gridy = 2;
        super.add(panelSegundo, cns);
        JPanel panelTercero = new JPanel();
        panelTercero.setBorder(new TitledBorder("Tercer Premio"));
        tercero = new JLabel(res.getTercero(), SwingConstants.CENTER);
        panelTercero.add(tercero);
        cns.gridx = 1;
        cns.gridy = 2;
        super.add(panelTercero, cns);
        panelExt4 = new JPanel(new GridLayout(0, 2));
        panelExt4.setBorder(new TitledBorder("Extracciones de 4 Cifras"));
        setPanelExt4(res.getCuatroCifras());
        cns.gridx = 0;
        cns.gridy = 3;
        cns.gridwidth = 2;
        super.add(panelExt4, cns);
        panelExt3 = new JPanel(new GridLayout(7, 2));
        panelExt3.setBorder(new TitledBorder("Extracciones de 3 Cifras"));
        setPanelExt3(res.getTresCifras());
        cns.gridy = 4;
        super.add(panelExt3, cns);
        panelExt2 = new JPanel(new GridLayout(0, 5));
        panelExt2.setBorder(new TitledBorder("Extracciones de 2 Cifras"));
        setPanelExt2(res.getDosCifras());
        cns.fill = GridBagConstraints.HORIZONTAL;
        cns.gridy = 5;
        super.add(panelExt2, cns);
        panelReintegros = new JPanel(new GridLayout(0, 3));
        panelReintegros.setBorder(new TitledBorder("Reintegros"));
        setPanelReintegros(res.getReintegros());
        cns.gridy = 6;
        super.add(panelReintegros, cns);
        estado = new JLabel("Estado del Sorteo: " + UtilidadesEstadoSorteo.getHumanReadable(res.getEstado()),
                SwingConstants.CENTER);
        cns.gridy = 7;
        super.add(estado, cns);
        actualizacion = new JLabel("Ultima Actualización: " + UtilidadesFecha.getHumanReadable(res
                .getFechaActualizacion()), SwingConstants.CENTER);
        cns.gridy = 8;
        super.add(actualizacion, cns);
        pdf = new JLabel(res.getUrlPDF(), SwingConstants.CENTER);
        pdf.setForeground(Color.blue);
        ResumenMouseListener resumenMouseListener = new ResumenMouseListener(pdf);
        pdf.addMouseListener(resumenMouseListener);
        cns.gridy = 9;
        super.add(pdf, cns);
    }

    /**
     * Establece los numeros de la estraccion de 2 cifras en el panel.
     *
     * @param extraccionDos Array de cadena que contiene los números.
     */
    private void setPanelExt2(java.util.List<String> extraccionDos) {
        panelExt2.removeAll();
        for (String extraccionDo : extraccionDos) {
            panelExt2.add(new JLabel(extraccionDo, SwingConstants.CENTER));
        }
    }

    /**
     * Establece los numeros de la estraccion de 3 cifras en el panel.
     *
     * @param extraccionTres Array de cadena que contiene los números
     */
    private void setPanelExt3(java.util.List<String> extraccionTres) {
        panelExt3.removeAll();
        for (String extraccionDo : extraccionTres) {
            panelExt3.add(new JLabel(extraccionDo, SwingConstants.CENTER));
        }
    }

    /**
     * Establece los numeros de reintegro en el panel.
     *
     * @param reintegros2 Array de cadena que contiene los números
     */
    private void setPanelReintegros(java.util.List<String> reintegros2) {
        panelReintegros.removeAll();
        for (String extraccionDo : reintegros2) {
            panelReintegros.add(new JLabel(extraccionDo, SwingConstants.CENTER));
        }
    }

}
