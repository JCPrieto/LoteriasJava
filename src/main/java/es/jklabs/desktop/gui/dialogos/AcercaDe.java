package es.jklabs.desktop.gui.dialogos;

import es.jklabs.desktop.constant.Constant;
import es.jklabs.desktop.gui.Ventana;
import es.jklabs.gui.utilidades.Growls;
import es.jklabs.gui.utilidades.listener.UrlMouseListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author juanky
 */
public class AcercaDe extends JDialog implements ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = 4L;
    private static ResourceBundle mensajes = ResourceBundle.getBundle("i18n/mensajes", Locale.getDefault());
    private transient JButton botonOk;
    private final Ventana padre;

    public AcercaDe(final Ventana ventana) {
        super(ventana, Constant.getValor("nombre.app") + " - Acerca de...");
        this.padre = ventana;
        String version = Constant.getValor("version");
        super.setIconImage(new ImageIcon("res/line-globe.png").getImage());
        final JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setLayout(new GridBagLayout());
        final GridBagConstraints cns = new GridBagConstraints();
        final JLabel jLabelTitle = new JLabel(
                "<html><h1>" + Constant.getValor("nombre.app") + " " + version + "</h1></html>", new ImageIcon
                (Objects.requireNonNull(getClass()
                        .getClassLoader().getResource
                                ("img/icons/line-globe.png"))), JLabel.CENTER);
        cns.fill = GridBagConstraints.HORIZONTAL;
        cns.insets = new Insets(10, 10, 10, 10);
        cns.gridx = 0;
        cns.gridy = 0;
        cns.gridwidth = 3;
        panel.add(jLabelTitle, cns);
        final JLabel jLabelCreadoPor = new JLabel(mensajes.getString("creado.por"), JLabel.LEFT);
        cns.insets = new Insets(10, 10, 3, 10);
        cns.gridy = 1;
        cns.gridwidth = 1;
        panel.add(jLabelCreadoPor, cns);
        final JLabel jLabelMyName = new JLabel("<html><b>Juan Carlos Prieto Silos</b></html>", JLabel.LEFT);
        cns.insets = new Insets(3, 10, 3, 10);
        cns.gridy = 2;
        panel.add(jLabelMyName, cns);
        final JLabel jLabelMyWeb = new JLabel("JCPrieto.tk", JLabel.LEFT);
        jLabelMyWeb.addMouseListener(new UrlMouseListener(ventana, jLabelMyWeb, "https://jcprieto.tk"));
        cns.gridx = 1;
        panel.add(jLabelMyWeb, cns);
        JLabel jLabelMyMail = new JLabel("JuanC.Prieto.Silos@gmail.com", JLabel.LEFT);
        jLabelMyMail.setAlignmentX(CENTER_ALIGNMENT);
        jLabelMyMail.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(
                            "mailto:JuanC.Prieto.Silos@gmail.com?subject=Loteria_Navidad_Java"));
                } catch (IOException | URISyntaxException e1) {
                    Growls.mostrarError(ventana, "acerca.de", "app.envio.correo", e1);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                jLabelMyMail.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                jLabelMyMail.setCursor(null);
            }
        });
        cns.gridx = 2;
        panel.add(jLabelMyMail, cns);
        final JLabel jLabelPoweredBy = new JLabel(mensajes.getString("powered.by"), JLabel.LEFT);
        cns.insets = new Insets(10, 10, 3, 10);
        cns.gridx = 0;
        cns.gridy = 4;
        panel.add(jLabelPoweredBy, cns);
        addPowered(panel, cns, 5, "Papirus", "https://github.com/PapirusDevelopmentTeam/papirus-icon-theme");
        addPowered(panel, cns, 6, "El País", Constant.getValor("url.api"));
        addPowered(panel, cns, 7, "Jackson", "https://github.com/FasterXML/jackson-core/wiki");
        addPowered(panel, cns, 8, "Firebase", "https://firebase.google.com");
        JLabel jLabelLicense = new JLabel
                ("<html><i>Esta obra está bajo una licencia de Creative Commons " +
                        "Reconocimiento-NoComercial-CompartirIgual 4.0 Internacional</i><html>", new ImageIcon(Objects
                        .requireNonNull(getClass().getClassLoader().getResource
                                ("img/icons/creative_commons.png"))), JLabel.TRAILING);
        jLabelLicense.addMouseListener(new UrlMouseListener(ventana, jLabelLicense, "http://creativecommons" +
                ".org/licenses/by-nc-sa/4.0/"));
        cns.insets = new Insets(10, 10, 10, 10);
        cns.gridx = 0;
        cns.gridy = 9;
        cns.gridwidth = 3;
        panel.add(jLabelLicense, cns);
        botonOk = new JButton("Aceptar");
        botonOk.addActionListener(this);
        cns.gridy = 10;
        panel.add(botonOk, cns);
        super.add(panel);
        super.pack();
    }

    private void addPowered(JPanel panel, GridBagConstraints cns, int y, String titulo, String url) {
        JLabel jLabelTitulo = new JLabel("<html><b>" + titulo + "</b></html>", JLabel.LEFT);
        if (url != null) {
            jLabelTitulo.addMouseListener(new UrlMouseListener(padre, jLabelTitulo, url));
        }
        cns.insets = new Insets(3, 10, 3, 10);
        cns.gridx = 0;
        cns.gridy = y;
        cns.gridwidth = 1;
        panel.add(jLabelTitulo, cns);
        if (url != null) {
            JLabel jLabelUrl = new JLabel(url, JLabel.LEFT);
            jLabelUrl.addMouseListener(new UrlMouseListener(padre, jLabelUrl, url));
            cns.gridx = 1;
            cns.gridy = y;
            cns.gridwidth = 2;
            panel.add(jLabelUrl, cns);
        }
    }

    @Override
    public void actionPerformed(final ActionEvent evt) {
        if (evt.getSource() == botonOk) {
            this.dispose();
        }
    }

}
