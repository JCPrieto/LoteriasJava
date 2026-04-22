package es.jklabs.utilidades;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.junit.jupiter.api.Assertions.*;

class LoggerTest extends BaseTest {

    private static final String USER_HOME = "user.home";
    private String originalUserHome;

    @AfterEach
    void restoreLoggerState() throws Exception {
        resetLogger();
        if (originalUserHome == null) {
            System.clearProperty(USER_HOME);
        } else {
            System.setProperty(USER_HOME, originalUserHome);
        }
    }

    @Test
    void initCreaDirectorioYEliminaLogsAntiguos(@TempDir Path tempDir) throws Exception {
        prepareUserHome(tempDir);
        Path logDir = getLogDir(tempDir);
        Files.createDirectories(logDir);
        Path oldLog = Files.writeString(logDir.resolve("log_old.log"), "old");
        Path recentLog = Files.writeString(logDir.resolve("log_recent.log"), "recent");
        Files.setLastModifiedTime(oldLog, FileTime.from(Instant.now().minus(Duration.ofDays(31))));
        Files.setLastModifiedTime(recentLog, FileTime.from(Instant.now().minus(Duration.ofDays(5))));

        Logger.init();
        Logger.info("mensaje de prueba");
        flushLoggerHandlers();

        assertTrue(Files.isDirectory(logDir));
        assertFalse(Files.exists(oldLog));
        assertTrue(Files.exists(recentLog));
        assertTrue(listLogFiles(logDir).stream().anyMatch(path -> path.getFileName().toString().startsWith("log_")));
    }

    @Test
    void initSoloConfiguraHandlerUnaVez(@TempDir Path tempDir) throws Exception {
        prepareUserHome(tempDir);

        Logger.init();
        int handlersAfterFirstInit = getLoggerInstance().getHandlers().length;

        Logger.init();
        int handlersAfterSecondInit = getLoggerInstance().getHandlers().length;

        assertEquals(1, handlersAfterFirstInit);
        assertEquals(handlersAfterFirstInit, handlersAfterSecondInit);
    }

    @Test
    void errorYInfoPublicanMensajesEnLogger() throws Exception {
        java.util.logging.Logger julLogger = getLoggerInstance();
        TestHandler handler = new TestHandler();
        julLogger.addHandler(handler);
        julLogger.setUseParentHandlers(false);
        julLogger.setLevel(Level.ALL);

        Exception exception = new IllegalStateException("fallo");
        Logger.error(exception);
        Logger.error("consultar.nueva.version", exception);
        Logger.info("mensaje informativo");

        assertEquals(3, handler.records.size());
        assertEquals(Level.SEVERE, handler.records.get(0).getLevel());
        assertEquals(exception, handler.records.get(0).getThrown());
        assertEquals(Mensajes.getError("consultar.nueva.version"), handler.records.get(1).getMessage());
        assertEquals(exception, handler.records.get(1).getThrown());
        assertEquals(Level.INFO, handler.records.get(2).getLevel());
        assertEquals("mensaje informativo", handler.records.get(2).getMessage());
    }

    private void prepareUserHome(Path tempDir) throws Exception {
        originalUserHome = System.getProperty(USER_HOME);
        System.setProperty(USER_HOME, tempDir.toString());
        resetLogger();
    }

    private void resetLogger() throws Exception {
        java.util.logging.Logger julLogger = getLoggerInstance();
        for (Handler handler : julLogger.getHandlers()) {
            handler.close();
            julLogger.removeHandler(handler);
        }
        julLogger.setUseParentHandlers(false);
        julLogger.setLevel(Level.OFF);

        Field loggerField = Logger.class.getDeclaredField("logger");
        loggerField.setAccessible(true);
        loggerField.set(null, null);
    }

    private java.util.logging.Logger getLoggerInstance() throws Exception {
        Field logField = Logger.class.getDeclaredField("LOG");
        logField.setAccessible(true);
        return (java.util.logging.Logger) logField.get(null);
    }

    private void flushLoggerHandlers() throws Exception {
        for (Handler handler : getLoggerInstance().getHandlers()) {
            handler.flush();
        }
    }

    private Path getLogDir(Path tempDir) {
        return tempDir.resolve(".local").resolve("share").resolve("LoteriaDeNavidad").resolve("logs");
    }

    private List<Path> listLogFiles(Path logDir) throws IOException {
        try (var stream = Files.list(logDir)) {
            return stream.toList();
        }
    }

    private static final class TestHandler extends Handler {
        private final java.util.ArrayList<LogRecord> records = new java.util.ArrayList<>();

        @Override
        public void publish(LogRecord record) {
            records.add(record);
        }

        @Override
        public void flush() {
            //
        }

        @Override
        public void close() {
            //
        }
    }
}
