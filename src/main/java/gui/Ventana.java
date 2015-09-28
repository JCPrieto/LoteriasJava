package gui;

import gui.paneles.MenuPrincipal;
import gui.paneles.PanelInferior;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

/**
 *
 */

/**
 * @author juanky
 */
public class Ventana extends JFrame implements ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = 2L;
    final transient private Timer tiempo;
    transient private JMenuItem acerca;
    transient private JPanel panel;
    private PanelInferior panelInferior;

    public Ventana() {
        super("Loterias de Navidad - " + getFecha());
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        super.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("img/icon/line-globe.png")).getImage());
        super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        super.setLayout(new BorderLayout());
        tiempo = new Timer(60000, this);
        tiempo.start();
        crearMenu();
        crearPanel();
        super.pack();
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
        super.add(barraMenu, BorderLayout.NORTH);
    }

    private void crearPanel() {
        panel = new MenuPrincipal(this);
        super.add(panel, BorderLayout.CENTER);
    }

    public JPanel getPanel() {
        return panel;
    }

    public void setPanel(JPanel panel) {
        super.remove(this.panel);
        this.panel = panel;
        super.add(panel, BorderLayout.CENTER);
    }

    public void eliminarPanelInferior() {
        super.remove(this.panelInferior);
    }

    public PanelInferior getPanelInferior() {
        return panelInferior;
    }

    public void setPanelInferior(PanelInferior panelInferior) {
        this.panelInferior = panelInferior;
        super.add(panelInferior, BorderLayout.SOUTH);
    }
}
