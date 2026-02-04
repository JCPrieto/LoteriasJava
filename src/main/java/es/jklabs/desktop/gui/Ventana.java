package es.jklabs.desktop.gui;

import es.jklabs.desktop.gui.dialogos.AcercaDe;
import es.jklabs.desktop.gui.paneles.MenuPrincipal;
import es.jklabs.desktop.gui.paneles.PanelInferior;
import es.jklabs.utilidades.Constantes;
import es.jklabs.utilidades.Logger;
import es.jklabs.utilidades.Mensajes;
import es.jklabs.utilidades.UtilidadesGitHubReleases;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
import java.util.Objects;

/**
 * @author juanky
 */
public class Ventana extends JFrame implements ActionListener {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 2L;
    private transient JMenuItem acerca;
    private transient JPanel panel;
    private transient JMenuBar barraMenu;
    private transient JMenuItem itemActualizacion;

    private PanelInferior panelInferior;

    public Ventana() {
        super(Constantes.NOMBRE_APP);
        super.setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("img/icons/line-globe.png"))).getImage());
        super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        super.setLayout(new BorderLayout());
        crearMenu();
        crearPanel();
        super.pack();
    }

    public void actionPerformed(final ActionEvent evt) {
        if (evt.getSource() == acerca) {
            final AcercaDe dialogo = new AcercaDe(this);
            dialogo.setVisible(true);
        }
    }

    private void crearMenu() {
        barraMenu = new JMenuBar();
        final JMenu ayuda = new JMenu(Mensajes.getMensaje("menu.ayuda"));
        ayuda.setMargin(new Insets(5, 5, 5, 5));
        acerca = new JMenuItem(Mensajes.getMensaje("acerca.de"), new ImageIcon(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("img/icons/info.png"))));
        acerca.addActionListener(this);
        ayuda.add(acerca);
        barraMenu.add(ayuda);
        super.setJMenuBar(barraMenu);
        consultarNuevaVersionAsync();
    }

    private void consultarNuevaVersionAsync() {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return UtilidadesGitHubReleases.existeNuevaVersion();
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        agregarItemActualizacion();
                    }
                } catch (InterruptedException e) {
                    Logger.error("consultar.nueva.version", e);
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    Logger.error("consultar.nueva.version", e);
                }
            }
        };
        worker.execute();
    }

    private void agregarItemActualizacion() {
        if (itemActualizacion != null) {
            return;
        }
        barraMenu.add(Box.createHorizontalGlue());
        itemActualizacion = new JMenuItem(Mensajes.getMensaje("menu.nueva.version"), new ImageIcon(Objects.requireNonNull
                (getClass().getClassLoader().getResource("img/icons/update.png"))));
        itemActualizacion.addActionListener(al -> UtilidadesGitHubReleases.abrirNuevaVersionEnNavegador());
        barraMenu.add(itemActualizacion);
        barraMenu.revalidate();
        barraMenu.repaint();
    }

    private void crearPanel() {
        panel = new MenuPrincipal(this);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        super.add(panel, BorderLayout.CENTER);
    }

    public void setPanel(JPanel panel) {
        super.remove(this.panel);
        this.panel = panel;
        this.panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        super.add(panel, BorderLayout.CENTER);
    }

    public void eliminarPanelInferior() {
        super.remove(this.panelInferior);
    }

    public void setPanelInferior(PanelInferior panelInferior) {
        this.panelInferior = panelInferior;
        super.add(panelInferior, BorderLayout.SOUTH);
    }

}
