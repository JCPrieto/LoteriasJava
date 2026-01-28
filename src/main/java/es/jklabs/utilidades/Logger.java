package es.jklabs.utilidades;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

public class Logger {

    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(Logger.class.getName());
    private static Logger logger;
    private static final int LOG_RETENTION_DAYS = 30;

    private Logger() {
        LocalDate hoy = LocalDate.now();
        FileHandler fh;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        Path logDir = Path.of(System.getProperty("user.home"), ".local", "share", "LoteriaDeNavidad", "logs");
        try {
            Files.createDirectories(logDir);
            limpiarLogsAntiguos(logDir);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Crear directorio logs", e);
        }
        String archivo = logDir.resolve("log_" + hoy.format(dtf) + "_%g.log").toString();
        try {
            fh = new FileHandler(archivo, 1024 * 1024, 5, true);
            LOG.addHandler(fh);
            LOG.setUseParentHandlers(false);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            fh.setLevel(Level.ALL);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Crear archivo logs", e);
        }
    }

    private static void limpiarLogsAntiguos(Path logDir) {
        long cutoff = java.time.Instant.now()
                .minus(java.time.Duration.ofDays(LOG_RETENTION_DAYS))
                .toEpochMilli();
        try (java.nio.file.DirectoryStream<Path> stream = Files.newDirectoryStream(logDir, "log_*.log")) {
            for (Path file : stream) {
                try {
                    long lastModified = Files.getLastModifiedTime(file).toMillis();
                    if (lastModified < cutoff) {
                        Files.deleteIfExists(file);
                    }
                } catch (IOException e) {
                    LOG.log(Level.WARNING, "Eliminar log antiguo " + file, e);
                }
            }
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Listar logs antiguos", e);
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
