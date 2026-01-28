package es.jklabs.gui.utilidades;

import es.jklabs.utilidades.Constantes;
import es.jklabs.utilidades.Logger;
import es.jklabs.utilidades.Mensajes;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.BooleanSupplier;

public class Growls {

    private static final String NOTIFY_SEND = "notify-send";
    private static TrayIcon trayIcon;
    private static BooleanSupplier NOTIFY_SEND_AVAILABLE = Growls::isNotifySendAvailableInternal;
    private static DialogDisplayer DIALOG_DISPLAYER = Growls::showDialogInternal;

    private Growls() {

    }

    public static void mostrarInfo(String titulo, String cuerpo) {
        if (trayIcon != null) {
            trayIcon.displayMessage(titulo != null ? Mensajes.getMensaje(titulo) : null, Mensajes.getMensaje(cuerpo), TrayIcon.MessageType.INFO);
        } else if (NOTIFY_SEND_AVAILABLE.getAsBoolean()) {
            try {
                Runtime.getRuntime().exec(new String[]{NOTIFY_SEND,
                        titulo != null ? Mensajes.getMensaje(titulo) : Constantes.NOMBRE_APP,
                        Mensajes.getMensaje(cuerpo),
                        "--icon=dialog-information"});
            } catch (IOException e) {
                Logger.error(e);
                DIALOG_DISPLAYER.show(titulo != null ? Mensajes.getMensaje(titulo) : Constantes.NOMBRE_APP,
                        Mensajes.getMensaje(cuerpo), JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            DIALOG_DISPLAYER.show(titulo != null ? Mensajes.getMensaje(titulo) : Constantes.NOMBRE_APP,
                    Mensajes.getMensaje(cuerpo), JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void mostrarError(String cuerpo, Exception e) {
        mostrarError(null, cuerpo, e);
    }

    public static void mostrarError(String titulo, String cuerpo, Exception e) {
        if (trayIcon != null) {
            trayIcon.displayMessage(titulo != null ? Mensajes.getMensaje(titulo) : null, Mensajes.getError(cuerpo), TrayIcon.MessageType.ERROR);
        } else if (NOTIFY_SEND_AVAILABLE.getAsBoolean()) {
            try {
                Runtime.getRuntime().exec(new String[]{NOTIFY_SEND,
                        titulo != null ? Mensajes.getMensaje(titulo) : Constantes.NOMBRE_APP,
                        Mensajes.getError(cuerpo),
                        "--icon=dialog-error"});
            } catch (IOException e2) {
                Logger.error(e2);
                DIALOG_DISPLAYER.show(titulo != null ? Mensajes.getMensaje(titulo) : Constantes.NOMBRE_APP,
                        Mensajes.getError(cuerpo), JOptionPane.ERROR_MESSAGE);
            }
        } else {
            DIALOG_DISPLAYER.show(titulo != null ? Mensajes.getMensaje(titulo) : Constantes.NOMBRE_APP,
                    Mensajes.getError(cuerpo), JOptionPane.ERROR_MESSAGE);
        }
        Logger.error(cuerpo, e);
    }

    public static void init() {
        trayIcon = null;
        if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
            SystemTray tray = SystemTray.getSystemTray();
            trayIcon = new TrayIcon(new ImageIcon(Objects.requireNonNull(Growls.class.getClassLoader().getResource
                    ("img/icons/line-globe.png"))).getImage(), Constantes.NOMBRE_APP);
            trayIcon.setImageAutoSize(true);
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                Logger.error("establecer.icono.systray", e);
            }
        }
    }

    static void setNotifySendAvailableForTests(BooleanSupplier supplier) {
        NOTIFY_SEND_AVAILABLE = supplier == null ? Growls::isNotifySendAvailableInternal : supplier;
    }

    static void setDialogDisplayerForTests(DialogDisplayer displayer) {
        DIALOG_DISPLAYER = displayer == null ? Growls::showDialogInternal : displayer;
    }

    static void setTrayIconForTests(TrayIcon trayIcon) {
        Growls.trayIcon = trayIcon;
    }

    static void resetTestHooks() {
        NOTIFY_SEND_AVAILABLE = Growls::isNotifySendAvailableInternal;
        DIALOG_DISPLAYER = Growls::showDialogInternal;
        trayIcon = null;
    }

    private static boolean isNotifySendAvailableInternal() {
        return isExecutable("/usr/bin/notify-send") || isExecutable("/bin/notify-send");
    }

    private static boolean isExecutable(String path) {
        return Files.isExecutable(Path.of(path));
    }

    private static void showDialogInternal(String titulo, String mensaje, int tipo) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, mensaje, titulo, tipo));
    }

    @FunctionalInterface
    interface DialogDisplayer {
        void show(String titulo, String mensaje, int tipo);
    }
}
