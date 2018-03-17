package es.jklabs.desktop.gui;

import es.jklabs.desktop.constant.Constant;
import es.jklabs.desktop.gui.dialogos.AcercaDe;
import es.jklabs.desktop.gui.paneles.MenuPrincipal;
import es.jklabs.desktop.gui.paneles.PanelInferior;
import es.jklabs.gui.utilidades.Growls;
import es.jklabs.utilidades.Logger;
import es.jklabs.utilidades.UtilidadesFirebase;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Objects;

/**
 * @author juanky
 */
public class Ventana extends JFrame implements ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = 2L;
    private static final Logger LOG = Logger.getLogger();
    private static final String NOMBRE_APP = "nombre.app";
    private transient JMenuItem acerca;
    private transient JPanel panel;
    private PanelInferior panelInferior;
    private transient TrayIcon trayIcon;

    public Ventana() {
        super(Constant.getValor(NOMBRE_APP));
        super.setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("img/icons/line-globe.png"))).getImage());
        super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        super.setLayout(new BorderLayout());
        crearMenu();
        crearPanel();
        cargarNotificaciones();
        super.pack();
    }

    private void cargarNotificaciones() {
        SystemTray tray = SystemTray.getSystemTray();
        //Alternative (if the icon is on the classpath):
        try {
            trayIcon = new TrayIcon(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource
                    ("img/icons/line-globe.png"))).getImage(), Constant.getValor(NOMBRE_APP));
            //Let the system resizes the image if needed
            trayIcon.setImageAutoSize(true);
            //Set tooltip text for the tray icon
            trayIcon.setToolTip(Constant.getValor(NOMBRE_APP));
            tray.add(trayIcon);
        } catch (AWTException e) {
            LOG.error("Establecer icono del systray", e);
        }
    }

    public void actionPerformed(final ActionEvent evt) {
        if (evt.getSource() == acerca) {
            final AcercaDe dialogo = new AcercaDe(this);
            dialogo.setVisible(true);
        }
    }

    private void crearMenu() {
        final JMenuBar barraMenu = new JMenuBar();
        final JMenu ayuda = new JMenu("Ayuda");
        ayuda.setMargin(new Insets(5, 5, 5, 5));
        acerca = new JMenuItem("Acerca de...", new ImageIcon(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("img/icons/info.png"))));
        acerca.addActionListener(this);
        ayuda.add(acerca);
        barraMenu.add(ayuda);
        try {
            if (UtilidadesFirebase.existeNuevaVersion()) {
                barraMenu.add(Box.createHorizontalGlue());
                JMenuItem jmActualizacion = new JMenuItem("Existe una nueva versión", new ImageIcon(Objects.requireNonNull
                        (getClass().getClassLoader().getResource("img/icons/update.png"))));
                jmActualizacion.addActionListener(al -> descargarNuevaVersion());
                barraMenu.add(jmActualizacion);
            }
        } catch (IOException | InterruptedException e) {
            LOG.error("consultar.nueva.version", e);
        }
        super.setJMenuBar(barraMenu);
    }

    private void descargarNuevaVersion() {
        try {
            UtilidadesFirebase.descargaNuevaVersion(this);
        } catch (InterruptedException e) {
            Growls.mostrarError(this, "descargar.nueva.version", e);
            Thread.currentThread().interrupt();
        }
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

    public TrayIcon getTrayIcon() {
        return trayIcon;
    }

}
