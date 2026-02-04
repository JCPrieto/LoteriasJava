package es.jklabs.utilidades;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

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
    void abrirNuevaVersionUsaReleaseUrlSiExiste() {
        UtilidadesGitHubReleases.ReleaseInfo release = new UtilidadesGitHubReleases.ReleaseInfo(
                "2.0.0", "LoteriaDeNavidad-2.0.0.zip", "https://example.com/app.zip", "https://example.com/release");
        UtilidadesGitHubReleases.setReleaseProviderForTests(() -> release);

        AtomicReference<URI> opened = new AtomicReference<>();
        UtilidadesGitHubReleases.setBrowserOpenerForTests(new TestBrowserOpener(opened, true));

        UtilidadesGitHubReleases.abrirNuevaVersionEnNavegador();

        assertEquals(URI.create("https://example.com/release"), opened.get());
    }

    @Test
    void abrirNuevaVersionUsaDownloadUrlSiNoHayReleaseUrl() {
        UtilidadesGitHubReleases.ReleaseInfo release = new UtilidadesGitHubReleases.ReleaseInfo(
                "2.0.0", "LoteriaDeNavidad-2.0.0.zip", "https://example.com/app.zip", null);
        UtilidadesGitHubReleases.setReleaseProviderForTests(() -> release);

        AtomicReference<URI> opened = new AtomicReference<>();
        UtilidadesGitHubReleases.setBrowserOpenerForTests(new TestBrowserOpener(opened, true));

        UtilidadesGitHubReleases.abrirNuevaVersionEnNavegador();

        assertEquals(URI.create("https://example.com/app.zip"), opened.get());
    }

    @Test
    void abrirNuevaVersionSinReleaseNoAbre() {
        UtilidadesGitHubReleases.setReleaseProviderForTests(() -> null);

        AtomicReference<URI> opened = new AtomicReference<>();
        UtilidadesGitHubReleases.setBrowserOpenerForTests(new TestBrowserOpener(opened, true));

        UtilidadesGitHubReleases.abrirNuevaVersionEnNavegador();

        assertNull(opened.get());
    }

    @Test
    void abrirNuevaVersionNoSoportadoNoAbre() {
        UtilidadesGitHubReleases.ReleaseInfo release = new UtilidadesGitHubReleases.ReleaseInfo(
                "2.0.0", "LoteriaDeNavidad-2.0.0.zip", "https://example.com/app.zip", "https://example.com/release");
        UtilidadesGitHubReleases.setReleaseProviderForTests(() -> release);

        AtomicReference<URI> opened = new AtomicReference<>();
        UtilidadesGitHubReleases.setBrowserOpenerForTests(new TestBrowserOpener(opened, false));

        UtilidadesGitHubReleases.abrirNuevaVersionEnNavegador();

        assertNull(opened.get());
    }

    @Test
    void abrirNuevaVersionSoportadoMarcaApertura() {
        UtilidadesGitHubReleases.ReleaseInfo release = new UtilidadesGitHubReleases.ReleaseInfo(
                "2.0.0", "LoteriaDeNavidad-2.0.0.zip", "https://example.com/app.zip", "https://example.com/release");
        UtilidadesGitHubReleases.setReleaseProviderForTests(() -> release);

        AtomicBoolean opened = new AtomicBoolean(false);
        UtilidadesGitHubReleases.setBrowserOpenerForTests(new TestBrowserOpener(opened, true));

        UtilidadesGitHubReleases.abrirNuevaVersionEnNavegador();

        assertTrue(opened.get());
    }

    private static final class TestBrowserOpener implements UtilidadesGitHubReleases.BrowserOpener {
        private final AtomicReference<URI> opened;
        private final AtomicBoolean openedFlag;
        private final boolean supported;

        private TestBrowserOpener(AtomicReference<URI> opened, boolean supported) {
            this.opened = opened;
            this.openedFlag = null;
            this.supported = supported;
        }

        private TestBrowserOpener(AtomicBoolean openedFlag, boolean supported) {
            this.opened = null;
            this.openedFlag = openedFlag;
            this.supported = supported;
        }

        @Override
        public boolean isSupported() {
            return supported;
        }

        @Override
        public void open(URI uri) {
            if (opened != null) {
                opened.set(uri);
            }
            if (openedFlag != null) {
                openedFlag.set(true);
            }
        }
    }
}
