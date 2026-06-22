package es.jklabs.gui.utilidades;

import com.sshtools.twoslices.Toast;
import com.sshtools.twoslices.ToastType;
import es.jklabs.utilidades.Constantes;
import es.jklabs.utilidades.Logger;
import es.jklabs.utilidades.Mensajes;

import javax.swing.*;

public class Growls {

    private static DialogDisplayer dialogDisplayer = Growls::showDialogInternal;
    private static ToastSender toastSender = Growls::showToastInternal;

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
        // two-slices selecciona el backend disponible de forma perezosa al mostrar cada notificacion.
    }

    static void setDialogDisplayerForTests(DialogDisplayer displayer) {
        dialogDisplayer = displayer == null ? Growls::showDialogInternal : displayer;
    }

    static void setToastSenderForTests(ToastSender sender) {
        toastSender = sender == null ? Growls::showToastInternal : sender;
    }

    static void resetTestHooks() {
        dialogDisplayer = Growls::showDialogInternal;
        toastSender = Growls::showToastInternal;
    }

    private static void showDialogInternal(String titulo, String mensaje, int tipo) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, mensaje, titulo, tipo));
    }

    private static void mostrarToast(String titulo, String mensaje, ToastType toastType, int dialogType) {
        try {
            toastSender.show(toastType, titulo, mensaje);
        } catch (RuntimeException e) {
            Logger.error(e);
            dialogDisplayer.show(titulo, mensaje, dialogType);
        }
    }

    private static void showToastInternal(ToastType tipo, String titulo, String mensaje) {
        Toast.toast(tipo, titulo, mensaje);
    }

    @FunctionalInterface
    interface DialogDisplayer {
        void show(String titulo, String mensaje, int tipo);
    }

    @FunctionalInterface
    interface ToastSender {
        void show(ToastType tipo, String titulo, String mensaje);
    }
}
