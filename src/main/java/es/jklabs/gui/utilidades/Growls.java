package es.jklabs.gui.utilidades;

import com.sshtools.twoslices.*;
import es.jklabs.utilidades.Constantes;
import es.jklabs.utilidades.Logger;
import es.jklabs.utilidades.Mensajes;

import javax.swing.*;
import java.net.URL;
import java.util.Locale;

public class Growls {

    private static final String DBUS_TOASTER_CLASS = "com.sshtools.twoslices.impl.DBUSNotifyToaster";
    private static DesktopNotifier notifier = new TwoSlicesDesktopNotifier();
    private static DialogDisplayer dialogDisplayer = Growls::showDialogInternal;
    private static URL notificationIcon = getNotificationIcon();
    private static boolean initialized;

    private Growls() {

    }

    public static void init() {
        notificationIcon = getNotificationIcon();
        ToasterSettings settings = new ToasterSettings()
                .setAppName(Constantes.NOMBRE_APP);
        if (notificationIcon != null) {
            settings.setDefaultImage(notificationIcon);
        }
        if (isLinux()) {
            settings.setPreferredToasterClassName(DBUS_TOASTER_CLASS);
        }
        ToasterFactory.setSettings(settings);
        ToasterFactory.setFactory(null);
        initialized = true;
    }

    public static void mostrarInfo(String titulo, String cuerpo) {
        String tituloResuelto = titulo != null ? Mensajes.getMensaje(titulo) : Constantes.NOMBRE_APP;
        mostrarGrowl(tituloResuelto, Mensajes.getMensaje(cuerpo), NotificationType.INFO);
    }

    public static void mostrarError(String cuerpo, Exception e) {
        mostrarError(null, cuerpo, e);
    }

    public static void mostrarError(String titulo, String cuerpo, Exception e) {
        String tituloResuelto = titulo != null ? Mensajes.getMensaje(titulo) : Constantes.NOMBRE_APP;
        String mensaje = Mensajes.getError(cuerpo);
        mostrarGrowl(tituloResuelto, mensaje, NotificationType.ERROR);
        Logger.error(cuerpo, e);
    }

    private static void mostrarGrowl(String titulo, String cuerpo, NotificationType type) {
        try {
            ensureInitialized();
            notifier.show(titulo, cuerpo, type, notificationIcon);
        } catch (RuntimeException e) {
            Logger.error(e);
            dialogDisplayer.show(titulo, cuerpo, type.optionPaneMessageType());
        }
    }

    private static void ensureInitialized() {
        if (!initialized) {
            init();
        }
    }

    private static URL getNotificationIcon() {
        return Growls.class.getResource("/img/icons/app/icon.png");
    }

    private static void showDialogInternal(String titulo, String mensaje, int tipo) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, mensaje, titulo, tipo));
    }

    static void setNotifierForTests(DesktopNotifier notifier) {
        Growls.notifier = notifier == null ? new TwoSlicesDesktopNotifier() : notifier;
    }

    static void setDialogDisplayerForTests(DialogDisplayer displayer) {
        dialogDisplayer = displayer == null ? Growls::showDialogInternal : displayer;
    }

    static void resetTestHooks() {
        notifier = new TwoSlicesDesktopNotifier();
        dialogDisplayer = Growls::showDialogInternal;
        notificationIcon = getNotificationIcon();
        initialized = false;
    }

    private static boolean isLinux() {
        return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("linux");
    }

    enum NotificationType {
        INFO(ToastType.INFO, JOptionPane.INFORMATION_MESSAGE),
        ERROR(ToastType.ERROR, JOptionPane.ERROR_MESSAGE);

        private final ToastType toastType;
        private final int optionPaneMessageType;

        NotificationType(ToastType toastType, int optionPaneMessageType) {
            this.toastType = toastType;
            this.optionPaneMessageType = optionPaneMessageType;
        }

        private ToastType toastType() {
            return toastType;
        }

        private int optionPaneMessageType() {
            return optionPaneMessageType;
        }
    }

    @FunctionalInterface
    interface DesktopNotifier {
        void show(String title, String body, NotificationType type, URL icon);
    }

    @FunctionalInterface
    interface DialogDisplayer {
        void show(String titulo, String mensaje, int tipo);
    }

    private static class TwoSlicesDesktopNotifier implements DesktopNotifier {
        @Override
        public void show(String title, String body, NotificationType type, URL icon) {
            ToastBuilder builder = Toast.builder()
                    .type(type.toastType())
                    .title(title)
                    .content(body);
            if (icon != null) {
                builder.icon(icon);
            }
            builder.toast();
        }
    }
}
