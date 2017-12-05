package es.jklabs.desktop.gui.paneles;

import com.jklabs.lib.loteria.conexion.Conexion;
import com.jklabs.lib.loteria.model.ResultadosNavidad;
import es.jklabs.desktop.gui.Ventana;
import es.jklabs.desktop.gui.listener.ResumenMouseListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author juanky
 */
public class ResumenNavidad extends JPanel implements ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final transient Ventana padre;
    private final Timer tiempo;
    private transient JLabel actualizacion;
    private transient JLabel cuarto1;
    private transient JLabel cuarto2;
    private transient JLabel estado;
    private transient JLabel gordo;
    private transient JLabel pdf;
    private transient JLabel quinto1;
    private transient JLabel quinto2;
    private transient JLabel quinto3;
    private transient JLabel quinto4;
    private transient JLabel quinto5;
    private transient JLabel quinto6;
    private transient JLabel quinto7;
    private transient JLabel quinto8;
    private ResultadosNavidad res;
    private JLabel segundo;
    private JLabel tercero;

    ResumenNavidad(final Ventana ventana, String resultado) {
        super();
        padre = ventana;
        res = new ResultadosNavidad(resultado);
        cargarElementos();
        tiempo = new Timer(30000, this);
        tiempo.start();
    }

    public void actionPerformed(final ActionEvent evt) {
        if (evt.getSource() == tiempo) {
            actualizarResumen();
        }
    }

    private void actualizarResumen() {
        final Conexion con = new Conexion("Navidad", "resumen");
        if (con.consulta()) {
            res = new ResultadosNavidad(con.getResultado());
            gordo.setText(res.getGordo());
            segundo.setText(res.getSegundo());
            tercero.setText(res.getTercero());
            cuarto1.setText(res.getCuarto1());
            cuarto2.setText(res.getCuarto2());
            quinto1.setText(res.getQuinto1());
            quinto2.setText(res.getQuinto2());
            quinto3.setText(res.getQuinto3());
            quinto4.setText(res.getQuinto4());
            quinto5.setText(res.getQuinto5());
            quinto6.setText(res.getQuinto6());
            quinto7.setText(res.getQuinto7());
            quinto8.setText(res.getQuinto8());
            estado.setText("Estado del Sorteo: " + res.getEstado());
            actualizacion.setText("Ultima Actualización: " + res.getFecha());
            pdf.setText(res.getPDF());
            padre.pack();
        }
    }

    private void cargarElementos() {
        super.setLayout(new GridBagLayout());
        final GridBagConstraints cns = new GridBagConstraints();
        final JPanel panelGordo = new JPanel();
        panelGordo.setBorder(new TitledBorder("Premio Gordo"));
        gordo = new JLabel(res.getGordo(), JLabel.CENTER);
        panelGordo.add(gordo);
        cns.fill = GridBagConstraints.HORIZONTAL;
        cns.gridx = 0;
        cns.gridy = 0;
        cns.gridwidth = 2;
        cns.insets = new Insets(10, 0, 10, 0);
        super.add(panelGordo, cns);
        final JPanel panelSegundo = new JPanel();
        panelSegundo.setBorder(new TitledBorder("Segundo premio"));
        segundo = new JLabel(res.getSegundo(), JLabel.CENTER);
        panelSegundo.add(segundo);
        cns.gridx = 0;
        cns.gridy = 1;
        cns.weightx = 2;
        cns.gridwidth = 1;
        super.add(panelSegundo, cns);
        final JPanel panelTercero = new JPanel();
        panelTercero.setBorder(new TitledBorder("Tercer premio"));
        tercero = new JLabel(res.getTercero(), JLabel.CENTER);
        panelTercero.add(tercero);
        cns.gridx = 1;
        cns.gridy = 1;
        super.add(panelTercero, cns);
        final JPanel panelCuarto = new JPanel(new GridLayout(0, 2));
        panelCuarto.setBorder(new TitledBorder("Cuarto premio"));
        cuarto1 = new JLabel(res.getCuarto1(), JLabel.CENTER);
        cuarto2 = new JLabel(res.getCuarto2(), JLabel.CENTER);
        panelCuarto.add(cuarto1);
        panelCuarto.add(cuarto2);
        cns.gridx = 0;
        cns.gridy = 2;
        cns.gridwidth = 2;
        super.add(panelCuarto, cns);
        final JPanel panelQuinto = new JPanel(new GridLayout(4, 2));
        panelQuinto.setBorder(new TitledBorder("Quinto premio"));
        quinto1 = new JLabel(res.getQuinto1(), JLabel.CENTER);
        quinto2 = new JLabel(res.getQuinto2(), JLabel.CENTER);
        quinto3 = new JLabel(res.getQuinto3(), JLabel.CENTER);
        quinto4 = new JLabel(res.getQuinto4(), JLabel.CENTER);
        quinto5 = new JLabel(res.getQuinto5(), JLabel.CENTER);
        quinto6 = new JLabel(res.getQuinto6(), JLabel.CENTER);
        quinto7 = new JLabel(res.getQuinto7(), JLabel.CENTER);
        quinto8 = new JLabel(res.getQuinto8(), JLabel.CENTER);
        panelQuinto.add(quinto1);
        panelQuinto.add(quinto2);
        panelQuinto.add(quinto3);
        panelQuinto.add(quinto4);
        panelQuinto.add(quinto5);
        panelQuinto.add(quinto6);
        panelQuinto.add(quinto7);
        panelQuinto.add(quinto8);
        cns.gridy = 3;
        super.add(panelQuinto, cns);
        estado = new JLabel("Estado del Sorteo: " + res.getEstado(),
                JLabel.CENTER);
        cns.gridx = 0;
        cns.gridy = 4;
        super.add(estado, cns);
        actualizacion = new JLabel("Ultima Actualización: " + res.getFecha(),
                JLabel.CENTER);
        cns.gridx = 0;
        cns.gridy = 5;
        super.add(actualizacion, cns);
        pdf = new JLabel(res.getPDF(), JLabel.CENTER);
        pdf.setForeground(Color.blue);
        ResumenMouseListener resumenMouseListener = new ResumenMouseListener(pdf);
        pdf.addMouseListener(resumenMouseListener);
        cns.gridx = 0;
        cns.gridy = 6;
        super.add(pdf, cns);
    }

}
