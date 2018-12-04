package es.jklabs.desktop;

import es.jklabs.desktop.gui.Ventana;
import es.jklabs.utilidades.Logger;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;

import javax.swing.*;

/**
 * @author juanky
 */
public final class LoteriaNavidad {

    private static final Logger LOG = Logger.getLogger();

    private LoteriaNavidad() {

    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            LOG.error("Cargar look and field del S.O.", e);
        }
        final JFXPanel fxPanel = new JFXPanel();
        final Ventana vent = new Ventana(fxPanel);
        vent.setVisible(true);
        Platform.runLater(() -> initFX(fxPanel));
    }

    private static void initFX(JFXPanel fxPanel) {
        Scene scene = createScene();
        fxPanel.setScene(scene);
    }

    private static Scene createScene() {
        Group root = new Group();
        return (new Scene(root));
    }

}
