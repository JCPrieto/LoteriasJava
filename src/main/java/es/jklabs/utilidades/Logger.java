package es.jklabs.utilidades;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

public class Logger {

    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(Logger.class.getName());
    private static Logger logger;

    private Logger() {
        LocalDate hoy = LocalDate.now();
        FileHandler fh;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        String archivo = "log_" + hoy.format(dtf) + ".log";
        try {
            fh = new FileHandler(archivo, true);
            LOG.addHandler(fh);
            LOG.setUseParentHandlers(false);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            fh.setLevel(Level.ALL);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Crear archivo logs", e);
        }
    }

    public static void init() {
        if (logger == null) {
            logger = new Logger();
        }
    }

    public static void error(Exception e) {
        LOG.log(Level.SEVERE, null, e);
    }

    public static void error(String mensaje, Throwable e) {
        LOG.log(Level.SEVERE, Mensajes.getError(mensaje), e);
    }

    static void info(String mensaje) {
        LOG.log(Level.INFO, mensaje);
    }
}
