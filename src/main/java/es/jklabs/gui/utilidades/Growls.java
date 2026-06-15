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
import java.util.function.Predicate;

public class Growls {

    private static final String NOTIFY_SEND = "notify-send";
    private static NotificationDisplayer trayNotificationDisplayer;
    private static BooleanSupplier notifySendAvailableInternal = Growls::isNotifySendAvailableInternal;
    private static DialogDisplayer dialogDisplayer = Growls::showDialogInternal;
    private static CommandExecutor commandExecutor = command -> Runtime.getRuntime().exec(command);
    private static BooleanSupplier windowsInternal = Growls::isWindowsInternal;
    private static TrayInitializer trayInitializer = Growls::initializeTrayInternal;
    private static Predicate<String> executableChecker = path -> Files.isExecutable(Path.of(path));

    private Growls() {

    }

    public static void mostrarInfo(String titulo, String cuerpo) {
        String tituloResuelto = titulo != null ? Mensajes.getMensaje(titulo) : Constantes.NOMBRE_APP;
        String mensaje = Mensajes.getMensaje(cuerpo);
        if (trayNotificationDisplayer != null) {
            trayNotificationDisplayer.show(tituloResuelto, mensaje, TrayIcon.MessageType.INFO);
        } else if (notifySendAvailableInternal.getAsBoolean()) {
            try {
                commandExecutor.execute(new String[]{NOTIFY_SEND, tituloResuelto, mensaje,
                        "--icon=dialog-information"});
            } catch (IOException e) {
                Logger.error(e);
                dialogDisplayer.show(tituloResuelto, mensaje, JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            dialogDisplayer.show(tituloResuelto, mensaje, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void mostrarError(String cuerpo, Exception e) {
        mostrarError(null, cuerpo, e);
    }

    public static void mostrarError(String titulo, String cuerpo, Exception e) {
        String tituloResuelto = titulo != null ? Mensajes.getMensaje(titulo) : Constantes.NOMBRE_APP;
        String mensaje = Mensajes.getError(cuerpo);
        if (trayNotificationDisplayer != null) {
            trayNotificationDisplayer.show(tituloResuelto, mensaje, TrayIcon.MessageType.ERROR);
        } else if (notifySendAvailableInternal.getAsBoolean()) {
            try {
                commandExecutor.execute(new String[]{NOTIFY_SEND, tituloResuelto, mensaje,
                        "--icon=dialog-error"});
            } catch (IOException e2) {
                Logger.error(e2);
                dialogDisplayer.show(tituloResuelto, mensaje, JOptionPane.ERROR_MESSAGE);
            }
        } else {
            dialogDisplayer.show(tituloResuelto, mensaje, JOptionPane.ERROR_MESSAGE);
        }
        Logger.error(cuerpo, e);
    }

    public static void init() {
        trayNotificationDisplayer = null;
        if (windowsInternal.getAsBoolean()) {
            try {
                trayNotificationDisplayer = trayInitializer.initialize();
            } catch (AWTException e) {
                Logger.error("establecer.icono.systray", e);
            }
        }
    }

    static void setNotifySendAvailableForTests(BooleanSupplier supplier) {
        notifySendAvailableInternal = supplier == null ? Growls::isNotifySendAvailableInternal : supplier;
    }

    static void setDialogDisplayerForTests(DialogDisplayer displayer) {
        dialogDisplayer = displayer == null ? Growls::showDialogInternal : displayer;
    }

    static void setCommandExecutorForTests(CommandExecutor executor) {
        commandExecutor = executor == null ? command -> Runtime.getRuntime().exec(command) : executor;
    }

    static void setWindowsForTests(BooleanSupplier supplier) {
        windowsInternal = supplier == null ? Growls::isWindowsInternal : supplier;
    }

    static void setTrayInitializerForTests(TrayInitializer initializer) {
        trayInitializer = initializer == null ? Growls::initializeTrayInternal : initializer;
    }

    static void setTrayNotificationDisplayerForTests(NotificationDisplayer displayer) {
        trayNotificationDisplayer = displayer;
    }

    static void setExecutableCheckerForTests(Predicate<String> checker) {
        executableChecker = checker == null ? path -> Files.isExecutable(Path.of(path)) : checker;
    }

    static void resetTestHooks() {
        notifySendAvailableInternal = Growls::isNotifySendAvailableInternal;
        dialogDisplayer = Growls::showDialogInternal;
        commandExecutor = command -> Runtime.getRuntime().exec(command);
        windowsInternal = Growls::isWindowsInternal;
        trayInitializer = Growls::initializeTrayInternal;
        executableChecker = path -> Files.isExecutable(Path.of(path));
        trayNotificationDisplayer = null;
    }

    private static boolean isNotifySendAvailableInternal() {
        return executableChecker.test("/usr/bin/notify-send") || executableChecker.test("/bin/notify-send");
    }

    private static boolean isWindowsInternal() {
        return System.getProperty("os.name").toLowerCase().startsWith("win");
    }

    private static NotificationDisplayer initializeTrayInternal() throws AWTException {
        SystemTray tray = SystemTray.getSystemTray();
        TrayIcon trayIcon = new TrayIcon(new ImageIcon(Objects.requireNonNull(Growls.class.getClassLoader().getResource
                ("img/icons/line-globe.png"))).getImage(), Constantes.NOMBRE_APP);
        trayIcon.setImageAutoSize(true);
        tray.add(trayIcon);
        return trayIcon::displayMessage;
    }

    private static void showDialogInternal(String titulo, String mensaje, int tipo) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, mensaje, titulo, tipo));
    }

    @FunctionalInterface
    interface DialogDisplayer {
        void show(String titulo, String mensaje, int tipo);
    }

    @FunctionalInterface
    interface NotificationDisplayer {
        void show(String titulo, String mensaje, TrayIcon.MessageType tipo);
    }

    @FunctionalInterface
    interface CommandExecutor {
        void execute(String[] command) throws IOException;
    }

    @FunctionalInterface
    interface TrayInitializer {
        NotificationDisplayer initialize() throws AWTException;
    }
}
