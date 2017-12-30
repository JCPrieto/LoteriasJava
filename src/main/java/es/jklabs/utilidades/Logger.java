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
    private final String archivo;

    private Logger() {
        LocalDate hoy = LocalDate.now();
        FileHandler fh;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        archivo = "log_" + hoy.format(dtf) + ".log";
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

    public static Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

    public void info(String mensaje, Throwable e) {
        LOG.log(Level.INFO, mensaje, e);
    }

    public void error(String mensaje, Throwable e) {
        LOG.log(Level.SEVERE, mensaje, e);
    }

    public void aviso(String mensaje, Throwable e) {
        LOG.log(Level.WARNING, mensaje, e);
    }

    public void info(String mensaje) {
        LOG.log(Level.INFO, mensaje);
    }
}
