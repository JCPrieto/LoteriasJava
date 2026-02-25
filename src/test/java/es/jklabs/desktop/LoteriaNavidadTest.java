package es.jklabs.desktop;

import es.jklabs.desktop.gui.Ventana;
import es.jklabs.gui.utilidades.Growls;
import es.jklabs.utilidades.BaseTest;
import es.jklabs.utilidades.Logger;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class LoteriaNavidadTest extends BaseTest {

    @Test
    void mainInicializaYMuestraVentana() {
        AtomicReference<Runnable> runnableRef = new AtomicReference<>();
        try (MockedStatic<Logger> loggerMock = mockStatic(Logger.class);
             MockedStatic<SwingUtilities> swingMock = mockStatic(SwingUtilities.class);
             MockedStatic<Growls> growlsMock = mockStatic(Growls.class);
             MockedStatic<UIManager> uiManagerMock = mockStatic(UIManager.class);
             MockedConstruction<Ventana> ventanaMock = mockConstruction(Ventana.class)) {

            swingMock.when(() -> SwingUtilities.invokeLater(any(Runnable.class))).thenAnswer(invocation -> {
                runnableRef.set(invocation.getArgument(0));
                return null;
            });
            uiManagerMock.when(UIManager::getSystemLookAndFeelClassName).thenReturn("laf.test");

            LoteriaNavidad.main(new String[0]);

            loggerMock.verify(Logger::init, times(1));
            swingMock.verify(() -> SwingUtilities.invokeLater(any(Runnable.class)), times(1));
            assertNotNull(runnableRef.get());

            runnableRef.get().run();

            growlsMock.verify(Growls::init, times(1));
            uiManagerMock.verify(UIManager::getSystemLookAndFeelClassName, times(1));
            uiManagerMock.verify(() -> UIManager.setLookAndFeel("laf.test"), times(1));
            verify(ventanaMock.constructed().getFirst()).setVisible(true);
        }
    }

    @Test
    void mainRegistraErrorSiFallaLookAndFeel() {
        AtomicReference<Runnable> runnableRef = new AtomicReference<>();
        try (MockedStatic<Logger> loggerMock = mockStatic(Logger.class);
             MockedStatic<SwingUtilities> swingMock = mockStatic(SwingUtilities.class);
             MockedStatic<Growls> growlsMock = mockStatic(Growls.class);
             MockedStatic<UIManager> uiManagerMock = mockStatic(UIManager.class);
             MockedConstruction<Ventana> ventanaMock = mockConstruction(Ventana.class)) {

            swingMock.when(() -> SwingUtilities.invokeLater(any(Runnable.class))).thenAnswer(invocation -> {
                runnableRef.set(invocation.getArgument(0));
                return null;
            });
            uiManagerMock.when(UIManager::getSystemLookAndFeelClassName).thenReturn("laf.test");
            uiManagerMock.when(() -> UIManager.setLookAndFeel("laf.test"))
                    .thenThrow(new UnsupportedLookAndFeelException("fallo"));

            LoteriaNavidad.main(new String[0]);

            assertNotNull(runnableRef.get());
            runnableRef.get().run();

            growlsMock.verify(Growls::init, times(1));
            loggerMock.verify(() -> Logger.error(eq("Cargar look and field del S.O."),
                    any(UnsupportedLookAndFeelException.class)), times(1));
            org.junit.jupiter.api.Assertions.assertEquals(0, ventanaMock.constructed().size());
        }
    }
}
