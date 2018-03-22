package es.jklabs.gui.utilidades;

import es.jklabs.desktop.gui.Ventana;
import es.jklabs.utilidades.Logger;

import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class Growls {

    private static final Logger LOG = Logger.getLogger();
    private static ResourceBundle mensajes = ResourceBundle.getBundle("i18n/mensajes", Locale.getDefault());
    private static ResourceBundle errores = ResourceBundle.getBundle("i18n/errores", Locale.getDefault());

    public static void mostrarInfo(Ventana parent, String titulo, String cuerpo) {
        parent.getTrayIcon().displayMessage(titulo != null ? mensajes.getString(titulo) : null, mensajes.getString
                (cuerpo), TrayIcon.MessageType.INFO);
    }

    public static void mostrarError(Ventana parent, String cuerpo, Exception e) {
        mostrarError(parent, null, cuerpo, e);
    }

    public static void mostrarError(Ventana parent, String titulo, String cuerpo, Exception e) {
        parent.getTrayIcon().displayMessage(titulo != null ? mensajes.getString(titulo) : null, errores.getString
                (cuerpo), TrayIcon.MessageType.ERROR);
        LOG.error(cuerpo, e);
    }
}
