package es.jklabs.gui.utilidades;

import com.sshtools.twoslices.Toast;
import com.sshtools.twoslices.ToastType;
import com.sshtools.twoslices.ToasterFactory;
import com.sshtools.twoslices.ToasterSettings;
import es.jklabs.utilidades.Constantes;
import es.jklabs.utilidades.Logger;
import es.jklabs.utilidades.Mensajes;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Locale;
import java.util.Set;

public class Growls {

    private static final String APP_ICON_NAME = "loteriadenavidad";
    private static final String APP_DATA_DIR_NAME = ".loteriadenavidad";
    private static final String DBUS_TOASTER_CLASS = "com.sshtools.twoslices.impl.DBUSNotifyToaster";
    private static DialogDisplayer dialogDisplayer = Growls::showDialogInternal;
    private static ToastSender toastSender = Growls::showToastInternal;
    private static SettingsConfigurer settingsConfigurer = Growls::configureSettingsInternal;
    private static boolean settingsConfigured;
    private static Path appIconPath;
    private static Path appIconDirectory;

    private Growls() {

    }

    public static void mostrarInfo(String titulo, String cuerpo) {
        String tituloResuelto = titulo != null ? Mensajes.getMensaje(titulo) : Constantes.NOMBRE_APP;
        String mensaje = Mensajes.getMensaje(cuerpo);
        mostrarToast(tituloResuelto, mensaje, ToastType.INFO, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void mostrarError(String cuerpo, Exception e) {
        mostrarError(null, cuerpo, e);
    }

    public static void mostrarError(String titulo, String cuerpo, Exception e) {
        String tituloResuelto = titulo != null ? Mensajes.getMensaje(titulo) : Constantes.NOMBRE_APP;
        String mensaje = Mensajes.getError(cuerpo);
        mostrarToast(tituloResuelto, mensaje, ToastType.ERROR, JOptionPane.ERROR_MESSAGE);
        Logger.error(cuerpo, e);
    }

    public static void init() {
        configureSettings();
    }

    static void setDialogDisplayerForTests(DialogDisplayer displayer) {
        dialogDisplayer = displayer == null ? Growls::showDialogInternal : displayer;
    }

    static void setToastSenderForTests(ToastSender sender) {
        toastSender = sender == null ? Growls::showToastInternal : sender;
    }

    static void setSettingsConfigurerForTests(SettingsConfigurer configurer) {
        settingsConfigurer = configurer == null ? Growls::configureSettingsInternal : configurer;
        settingsConfigured = false;
    }

    static void resetTestHooks() {
        dialogDisplayer = Growls::showDialogInternal;
        toastSender = Growls::showToastInternal;
        settingsConfigurer = Growls::configureSettingsInternal;
        settingsConfigured = false;
        appIconPath = null;
        appIconDirectory = null;
    }

    static void setAppIconDirectoryForTests(Path directory) {
        appIconDirectory = directory;
        appIconPath = null;
    }

    private static void showDialogInternal(String titulo, String mensaje, int tipo) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, mensaje, titulo, tipo));
    }

    private static void mostrarToast(String titulo, String mensaje, ToastType toastType, int dialogType) {
        try {
            configureSettings();
            toastSender.show(toastType, titulo, mensaje, getToastIcon());
        } catch (RuntimeException e) {
            Logger.error(e);
            dialogDisplayer.show(titulo, mensaje, dialogType);
        }
    }

    private static void showToastInternal(ToastType tipo, String titulo, String mensaje, String icono) {
        Toast.toast(tipo, icono, titulo, mensaje);
    }

    private static synchronized void configureSettings() {
        if (!settingsConfigured) {
            settingsConfigurer.configure();
            settingsConfigured = true;
        }
    }

    private static void configureSettingsInternal() {
        ToasterSettings settings = new ToasterSettings()
                .setAppName(Constantes.NOMBRE_APP)
                .setDefaultImage(getAppIconUrl());
        if (isLinux()) {
            settings.setPreferredToasterClassName(DBUS_TOASTER_CLASS);
        }
        ToasterFactory.setSettings(settings);
        ToasterFactory.setFactory(null);
    }

    private static URL getAppIconUrl() {
        return Growls.class.getClassLoader().getResource("img/icons/app/icon.png");
    }

    private static synchronized String getToastIcon() {
        if (appIconPath != null) {
            return appIconPath.toAbsolutePath().toString();
        }
        URL iconUrl = getAppIconUrl();
        if (iconUrl == null) {
            return APP_ICON_NAME;
        }
        try (InputStream in = iconUrl.openStream()) {
            Path iconDirectory = prepareAppIconDirectory();
            Path iconPath = iconDirectory.resolve(APP_ICON_NAME + ".png");
            if (Files.isSymbolicLink(iconPath)) {
                throw new IOException("Toast icon path must not be a symbolic link: " + iconPath);
            }
            Files.copy(in, iconPath, StandardCopyOption.REPLACE_EXISTING);
            appIconPath = iconPath;
            return iconPath.toAbsolutePath().toString();
        } catch (IOException e) {
            Logger.error(e);
            return APP_ICON_NAME;
        }
    }

    private static Path getAppIconDirectory() {
        if (appIconDirectory != null) {
            return appIconDirectory;
        }
        return Paths.get(System.getProperty("user.home", "."), APP_DATA_DIR_NAME);
    }

    private static Path prepareAppIconDirectory() throws IOException {
        Path iconDirectory = getAppIconDirectory();
        if (Files.isSymbolicLink(iconDirectory)) {
            throw new IOException("Toast icon directory must not be a symbolic link: " + iconDirectory);
        }
        Files.createDirectories(iconDirectory);
        restrictDirectoryPermissions(iconDirectory);
        return iconDirectory;
    }

    private static void restrictDirectoryPermissions(Path directory) throws IOException {
        if (Files.getFileStore(directory).supportsFileAttributeView("posix")) {
            Files.setPosixFilePermissions(directory, Set.of(
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_WRITE,
                    PosixFilePermission.OWNER_EXECUTE));
        }
    }

    private static boolean isLinux() {
        return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("linux");
    }

    @FunctionalInterface
    interface DialogDisplayer {
        void show(String titulo, String mensaje, int tipo);
    }

    @FunctionalInterface
    interface ToastSender {
        void show(ToastType tipo, String titulo, String mensaje, String icono);
    }

    @FunctionalInterface
    interface SettingsConfigurer {
        void configure();
    }
}
