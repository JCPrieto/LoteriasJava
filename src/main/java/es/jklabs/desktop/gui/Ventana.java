package es.jklabs.desktop.gui;

import es.jklabs.desktop.constant.Constant;
import es.jklabs.desktop.gui.dialogos.AcercaDe;
import es.jklabs.desktop.gui.paneles.MenuPrincipal;
import es.jklabs.desktop.gui.paneles.PanelInferior;
import es.jklabs.utilidades.Logger;
import es.jklabs.utilidades.UtilidadesFirebase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Calendar;
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
    private final transient Timer tiempo;
    private transient JMenuItem acerca;
    private transient JPanel panel;
    private PanelInferior panelInferior;
    private transient TrayIcon trayIcon;

    public Ventana() {
        super("Loterias de Navidad - " + getFecha());
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            LOG.error("Cargar look and field del S.O.", e);
        }
        super.setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("img/icons/line-globe.png"))).getImage());
        super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        super.setLayout(new BorderLayout());
        tiempo = new Timer(60000, this);
        tiempo.start();
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
                    ("img/icons/line-globe.png"))).getImage(), Constant.getValor("nombre.app"));
            //Let the system resizes the image if needed
            trayIcon.setImageAutoSize(true);
            //Set tooltip text for the tray icon
            trayIcon.setToolTip("BeyondDataBaseTransfer");
            tray.add(trayIcon);
        } catch (AWTException | IOException e) {
            LOG.error("Establecer icono del systray", e);
        }
    }

    /**
     * @return La fecha del sistema en formato DD-MM-YYYY HH:MM
     */
    private static String getFecha() {
        final Calendar date = Calendar.getInstance();
        date.setTimeInMillis(System.currentTimeMillis());
        int mes = date.get(Calendar.MONTH) + 1;
        return date.get(Calendar.DAY_OF_MONTH) + "-" + mes + "-"
                + date.get(Calendar.YEAR) + " "
                + date.get(Calendar.HOUR_OF_DAY) + ":"
                + date.get(Calendar.MINUTE);
    }

    public void actionPerformed(final ActionEvent evt) {
        if (evt.getSource() == acerca) {
            final AcercaDe dialogo = new AcercaDe(this);
            dialogo.setVisible(true);
        }
        if (evt.getSource() == tiempo) {
            super.setTitle("Loterias de Navidad - " + getFecha());
        }
    }

    private void crearMenu() {
        final JMenuBar barraMenu = new JMenuBar();
        final JMenu ayuda = new JMenu("Ayuda");
        acerca = new JMenuItem("Acerca de...");
        acerca.addActionListener(this);
        ayuda.add(acerca);
        barraMenu.add(ayuda);
        if (UtilidadesFirebase.existeNuevaVersion()) {
            barraMenu.add(Box.createHorizontalGlue());
            JMenuItem jmActualizacion = new JMenuItem("Existe una nueva versión", new ImageIcon(Objects.requireNonNull
                    (getClass().getClassLoader().getResource("img/icons/update.png"))));
            jmActualizacion.addActionListener(al -> UtilidadesFirebase.descargaNuevaVersion(this));
            barraMenu.add(jmActualizacion);
        }
        super.setJMenuBar(barraMenu);
    }

    private void crearPanel() {
        panel = new MenuPrincipal(this);
        super.add(panel, BorderLayout.CENTER);
    }

    public void setPanel(JPanel panel) {
        super.remove(this.panel);
        this.panel = panel;
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
