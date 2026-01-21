package es.jklabs.utilidades;

import es.jklabs.desktop.gui.Ventana;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import javax.swing.*;
import java.io.File;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class UtilidadesGitHubReleasesTest {

    @BeforeAll
    static void setHeadless() {
        System.setProperty("java.awt.headless", "true");
    }

    @AfterEach
    void resetHooks() {
        UtilidadesGitHubReleases.resetTestHooks();
    }

    @Test
    void descargaNuevaVersionDescargaEnDestino(@TempDir Path tempDir) throws Exception {
        JFileChooser chooser = Mockito.mock(JFileChooser.class);
        Mockito.when(chooser.showSaveDialog(Mockito.any())).thenReturn(JFileChooser.APPROVE_OPTION);
        Mockito.when(chooser.getSelectedFile()).thenReturn(tempDir.toFile());
        UtilidadesGitHubReleases.setFileChooserSupplierForTests(() -> chooser);

        UtilidadesGitHubReleases.ReleaseInfo release = new UtilidadesGitHubReleases.ReleaseInfo(
                "2.0.0", "LoteriaDeNavidad-2.0.0.zip", "https://example.com/app.zip");
        UtilidadesGitHubReleases.setReleaseProviderForTests(() -> release);

        AtomicReference<String> url = new AtomicReference<>();
        AtomicReference<Path> destino = new AtomicReference<>();
        UtilidadesGitHubReleases.setDownloaderForTests((downloadUrl, path) -> {
            url.set(downloadUrl);
            destino.set(path);
        });

        Ventana ventana = Mockito.mock(Ventana.class);
        UtilidadesGitHubReleases.descargaNuevaVersion(ventana);

        assertEquals("https://example.com/app.zip", url.get());
        assertEquals(tempDir.resolve("LoteriaDeNavidad-2.0.0.zip"), destino.get());
    }

    @Test
    void descargaNuevaVersionSinPermisosNoReintenta(@TempDir Path tempDir) throws Exception {
        JFileChooser chooser = Mockito.mock(JFileChooser.class);
        Mockito.when(chooser.showSaveDialog(Mockito.any())).thenReturn(JFileChooser.APPROVE_OPTION);
        Mockito.when(chooser.getSelectedFile()).thenReturn(tempDir.toFile());
        UtilidadesGitHubReleases.setFileChooserSupplierForTests(() -> chooser);

        UtilidadesGitHubReleases.ReleaseInfo release = new UtilidadesGitHubReleases.ReleaseInfo(
                "2.0.0", "LoteriaDeNavidad-2.0.0.zip", "https://example.com/app.zip");
        UtilidadesGitHubReleases.setReleaseProviderForTests(() -> release);

        AtomicInteger calls = new AtomicInteger();
        UtilidadesGitHubReleases.setDownloaderForTests((downloadUrl, path) -> {
            calls.incrementAndGet();
            throw new AccessDeniedException(path.toString());
        });

        Ventana ventana = Mockito.mock(Ventana.class);
        UtilidadesGitHubReleases.descargaNuevaVersion(ventana);

        assertEquals(1, calls.get());
    }

    @Test
    void descargaNuevaVersionCanceladaNoConsultaRelease() throws Exception {
        JFileChooser chooser = Mockito.mock(JFileChooser.class);
        Mockito.when(chooser.showSaveDialog(Mockito.any())).thenReturn(JFileChooser.CANCEL_OPTION);
        UtilidadesGitHubReleases.setFileChooserSupplierForTests(() -> chooser);

        AtomicBoolean releaseConsultada = new AtomicBoolean(false);
        UtilidadesGitHubReleases.setReleaseProviderForTests(() -> {
            releaseConsultada.set(true);
            return null;
        });

        AtomicBoolean descargado = new AtomicBoolean(false);
        UtilidadesGitHubReleases.setDownloaderForTests((downloadUrl, path) -> descargado.set(true));

        Ventana ventana = Mockito.mock(Ventana.class);
        UtilidadesGitHubReleases.descargaNuevaVersion(ventana);

        assertFalse(releaseConsultada.get());
        assertFalse(descargado.get());
    }

    @Test
    void descargaNuevaVersionSinUrlNoDescarga(@TempDir Path tempDir) throws Exception {
        JFileChooser chooser = Mockito.mock(JFileChooser.class);
        Mockito.when(chooser.showSaveDialog(Mockito.any())).thenReturn(JFileChooser.APPROVE_OPTION);
        Mockito.when(chooser.getSelectedFile()).thenReturn(new File(tempDir.toFile(), "destino"));
        UtilidadesGitHubReleases.setFileChooserSupplierForTests(() -> chooser);

        UtilidadesGitHubReleases.ReleaseInfo release = new UtilidadesGitHubReleases.ReleaseInfo("2.0.0", "app.zip", null);
        UtilidadesGitHubReleases.setReleaseProviderForTests(() -> release);

        AtomicBoolean descargado = new AtomicBoolean(false);
        UtilidadesGitHubReleases.setDownloaderForTests((downloadUrl, path) -> descargado.set(true));

        Ventana ventana = Mockito.mock(Ventana.class);
        UtilidadesGitHubReleases.descargaNuevaVersion(ventana);

        assertFalse(descargado.get());
    }
}
