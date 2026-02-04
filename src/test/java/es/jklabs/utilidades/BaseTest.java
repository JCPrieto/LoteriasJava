package es.jklabs.utilidades;

import org.junit.jupiter.api.BeforeAll;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;

public abstract class BaseTest {

    @BeforeAll
    static void configureTestEnvironment() {
        System.setProperty("java.awt.headless", "true");
        LogManager.getLogManager().reset();
        java.util.logging.Logger.getLogger("").setLevel(Level.OFF);
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Logger.class.getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);
        for (Handler handler : logger.getHandlers()) {
            handler.setLevel(Level.OFF);
            logger.removeHandler(handler);
        }
    }
}
