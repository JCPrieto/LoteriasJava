package gui;

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
    transient private JTabbedPane panel;

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
        super.setIconImage(new ImageIcon("res/line-globe.png").getImage());
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
        // TODO Auto-generated method stub
        final Calendar date = Calendar.getInstance();
        date.setTimeInMillis(System.currentTimeMillis());
        int mes = date.get(Calendar.MONTH) + 1;
        return date.get(Calendar.DAY_OF_MONTH) + "-" + mes + "-"
                + date.get(Calendar.YEAR) + " "
                + date.get(Calendar.HOUR_OF_DAY) + ":"
                + date.get(Calendar.MINUTE);
    }

    public void actionPerformed(final ActionEvent evt) {
        // TODO Auto-generated method stub
        if (evt.getSource() == acerca) {
            final AcercaDe dialogo = new AcercaDe(this);
            dialogo.setVisible(true);
        }
        if (evt.getSource() == tiempo) {
            super.setTitle("Loterias de Navidad - " + getFecha());
        }
    }

    private void crearBusquedaNavidad() {
        // TODO Auto-generated method stub
        final PanelBusqueda busqueda = new PanelBusqueda(this, "Navidad");
        panel.add("<html>Buscar Nº<br>Loteria de Navidad</html>", busqueda);
    }

    private void crearBusquedaNino() {
        // TODO Auto-generated method stub
        final PanelBusqueda busqueda = new PanelBusqueda(this, "Nino");
        panel.add("<html>Buscar Nº<br>Loteria del Niño</html>", busqueda);
    }

    private void crearMenu() {
        // TODO Auto-generated method stub
        final JMenuBar barraMenu = new JMenuBar();
        final JMenu ayuda = new JMenu("Ayuda");
        acerca = new JMenuItem("Acerca de...");
        acerca.addActionListener(this);
        ayuda.add(acerca);
        barraMenu.add(ayuda);
        super.add(barraMenu, BorderLayout.NORTH);
    }

    private void crearPanel() {
        // TODO Auto-generated method stub
        panel = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
        crearResumenNavidad();
        crearBusquedaNavidad();
        crearResumenNino();
        crearBusquedaNino();
        super.add(panel, BorderLayout.CENTER);
    }

    private void crearResumenNavidad() {
        // TODO Auto-generated method stub
        final ResumenNavidad resumen = new ResumenNavidad(this);
        panel.add("<html>Resumen<br>Sorteo Navidad</html>", resumen);
    }

    private void crearResumenNino() {
        // TODO Auto-generated method stub
        final ResumenNino resumen = new ResumenNino(this);
        panel.add("<html>Resumen<br>Sorteo del Niño</html>", resumen);
    }

}
