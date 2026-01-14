package es.jklabs.desktop.gui.paneles;

import es.jklabs.desktop.gui.Ventana;
import es.jklabs.desktop.gui.listener.ResumenMouseListener;
import es.jklabs.utilidades.Logger;
import es.jklabs.utilidades.UtilidadesEstadoSorteo;
import es.jklabs.utilidades.UtilidadesFecha;
import io.github.jcprieto.lib.loteria.conexion.Conexion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Serial;
import java.util.concurrent.ExecutionException;

/**
 * @author juanky
 */
public class ResumenNavidad extends JPanel implements ActionListener {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 1L;
    private final transient Ventana padre;
    private final Timer tiempo;
    private transient JLabel actualizacion;
    private transient JLabel estado;
    private transient JLabel gordo;
    private transient JLabel pdf;
    private JLabel segundo;
    private JLabel tercero;
    private io.github.jcprieto.lib.loteria.model.navidad.ResumenNavidad res;
    private JPanel panelCuarto;
    private JPanel panelQuinto;
    private boolean actualizando;

    ResumenNavidad(final Ventana ventana, io.github.jcprieto.lib.loteria.model.navidad.ResumenNavidad resultado) {
        super();
        padre = ventana;
        res = resultado;
        cargarElementos();
        tiempo = new Timer(30000, this);
        tiempo.start();
    }

    public void actionPerformed(final ActionEvent evt) {
        if (evt.getSource() == tiempo) {
            actualizarResumenAsync();
        }
    }

    private void actualizarResumenAsync() {
        if (actualizando) {
            return;
        }
        actualizando = true;
        SwingWorker<io.github.jcprieto.lib.loteria.model.navidad.ResumenNavidad, Void> worker = new SwingWorker<>() {
            @Override
            protected io.github.jcprieto.lib.loteria.model.navidad.ResumenNavidad doInBackground() throws IOException {
                final Conexion con = new Conexion();
                return con.getResumenNavidad();
            }

            @Override
            protected void done() {
                try {
                    res = get();
                    if (res == null) {
                        return;
                    }
                    gordo.setText(res.getGordo());
                    segundo.setText(res.getSegundo());
                    tercero.setText(res.getTercero());
                    setPanelCuarto(res.getCuarto());
                    setPanelQuinto(res.getQuinto());
                    estado.setText("Estado del Sorteo: " + UtilidadesEstadoSorteo.getHumanReadable(res.getEstado()));
                    actualizacion.setText("Ultima Actualización: " + UtilidadesFecha.getHumanReadable(res
                            .getFechaActualizacion()));
                    pdf.setText(res.getUrlPDF());
                    padre.pack();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    Logger.error("Actualizar datos de la pantalla", e);
                } finally {
                    actualizando = false;
                }
            }
        };
        worker.execute();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (!tiempo.isRunning()) {
            tiempo.start();
        }
    }

    @Override
    public void removeNotify() {
        if (tiempo.isRunning()) {
            tiempo.stop();
        }
        super.removeNotify();
    }

    private void setPanelQuinto(java.util.List<String> quinto) {
        panelQuinto.removeAll();
        quinto.forEach(q -> panelQuinto.add(new JLabel(q, SwingConstants.CENTER)));
    }

    private void setPanelCuarto(java.util.List<String> cuarto) {
        panelCuarto.removeAll();
        cuarto.forEach(c -> panelCuarto.add(new JLabel(c, SwingConstants.CENTER)));
    }

    private void cargarElementos() {
        super.setBorder(new EmptyBorder(10, 10, 10, 10));
        super.setLayout(new GridBagLayout());
        final GridBagConstraints cns = new GridBagConstraints();
        final JPanel panelGordo = new JPanel();
        panelGordo.setBorder(new TitledBorder("Premio Gordo"));
        gordo = new JLabel(res.getGordo(), SwingConstants.CENTER);
        panelGordo.add(gordo);
        cns.fill = GridBagConstraints.HORIZONTAL;
        cns.gridx = 0;
        cns.gridy = 0;
        cns.gridwidth = 2;
        cns.insets = new Insets(10, 0, 10, 0);
        super.add(panelGordo, cns);
        final JPanel panelSegundo = new JPanel();
        panelSegundo.setBorder(new TitledBorder("Segundo premio"));
        segundo = new JLabel(res.getSegundo(), SwingConstants.CENTER);
        panelSegundo.add(segundo);
        cns.gridx = 0;
        cns.gridy = 1;
        cns.weightx = 2;
        cns.gridwidth = 1;
        super.add(panelSegundo, cns);
        final JPanel panelTercero = new JPanel();
        panelTercero.setBorder(new TitledBorder("Tercer premio"));
        tercero = new JLabel(res.getTercero(), SwingConstants.CENTER);
        panelTercero.add(tercero);
        cns.gridx = 1;
        cns.gridy = 1;
        super.add(panelTercero, cns);
        panelCuarto = new JPanel(new GridLayout(0, 2));
        panelCuarto.setBorder(new TitledBorder("Cuarto premio"));
        setPanelCuarto(res.getCuarto());
        cns.gridx = 0;
        cns.gridy = 2;
        cns.gridwidth = 2;
        super.add(panelCuarto, cns);
        panelQuinto = new JPanel(new GridLayout(4, 2));
        panelQuinto.setBorder(new TitledBorder("Quinto premio"));
        setPanelQuinto(res.getQuinto());
        cns.gridy = 3;
        super.add(panelQuinto, cns);
        estado = new JLabel("Estado del Sorteo: " + UtilidadesEstadoSorteo.getHumanReadable(res.getEstado()),
                SwingConstants.CENTER);
        cns.gridx = 0;
        cns.gridy = 4;
        super.add(estado, cns);
        actualizacion = new JLabel("Ultima Actualización: " + UtilidadesFecha.getHumanReadable(res
                .getFechaActualizacion()), SwingConstants.CENTER);
        cns.gridx = 0;
        cns.gridy = 5;
        super.add(actualizacion, cns);
        pdf = new JLabel(res.getUrlPDF(), SwingConstants.CENTER);
        pdf.setForeground(Color.blue);
        ResumenMouseListener resumenMouseListener = new ResumenMouseListener(pdf);
        pdf.addMouseListener(resumenMouseListener);
        cns.gridx = 0;
        cns.gridy = 6;
        super.add(pdf, cns);
    }

}
