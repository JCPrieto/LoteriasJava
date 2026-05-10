package es.jklabs.utilidades;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UtilidadesGitHubReleasesTest extends BaseTest {

    @AfterEach
    void resetHooks() {
        UtilidadesGitHubReleases.resetTestHooks();
    }

    private static void setJsonResponse(int status, String body) {
        UtilidadesGitHubReleases.setConnectionFactoryForTests(uri -> new TestHttpURLConnection(status, body, null));
    }

    private static JsonNode parseJson(String json) {
        return invokePrivate("parsearJson", new Class<?>[]{String.class}, json);
    }

    @SuppressWarnings("unchecked")
    private static <T> T invokePrivate(String methodName, Class<?>[] parameterTypes, Object... args) {
        try {
            Method method = UtilidadesGitHubReleases.class.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return (T) method.invoke(null, args);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new AssertionError(e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            if (cause instanceof Error error) {
                throw error;
            }
            throw sneakyThrow(cause);
        }
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

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> RuntimeException sneakyThrow(Throwable throwable) throws E {
        throw (E) throwable;
    }

    private static Object invokeRecordAccessor(Object target, String methodName) {
        try {
            Method method = target.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            return method.invoke(target);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new AssertionError(e);
        } catch (InvocationTargetException e) {
            throw new AssertionError(e.getCause());
        }
    }

    @Test
    void existeNuevaVersionDevuelveFalseSinReleaseOVersion() throws IOException {
        UtilidadesGitHubReleases.setReleaseProviderForTests(() -> null);
        assertFalse(UtilidadesGitHubReleases.existeNuevaVersion());

        UtilidadesGitHubReleases.setReleaseProviderForTests(() -> new UtilidadesGitHubReleases.ReleaseInfo(
                null, null, null, null));
        assertFalse(UtilidadesGitHubReleases.existeNuevaVersion());
    }

    @Test
    void existeNuevaVersionComparaVersionesNormalizadas() throws IOException {
        UtilidadesGitHubReleases.setReleaseProviderForTests(() -> new UtilidadesGitHubReleases.ReleaseInfo(
                "999.0.0", null, null, null));
        assertTrue(UtilidadesGitHubReleases.existeNuevaVersion());

        UtilidadesGitHubReleases.setReleaseProviderForTests(() -> new UtilidadesGitHubReleases.ReleaseInfo(
                "0.0.1", null, null, null));
        assertFalse(UtilidadesGitHubReleases.existeNuevaVersion());

        UtilidadesGitHubReleases.setReleaseProviderForTests(() -> new UtilidadesGitHubReleases.ReleaseInfo(
                Constantes.VERSION, null, null, null));
        assertFalse(UtilidadesGitHubReleases.existeNuevaVersion());
    }

    @Test
    void settersDeTestRestauranValoresPorDefectoConNull() {
        UtilidadesGitHubReleases.setReleaseProviderForTests(null);
        UtilidadesGitHubReleases.setBrowserOpenerForTests(null);
        UtilidadesGitHubReleases.setConnectionFactoryForTests(null);

        assertDoesNotThrow(UtilidadesGitHubReleases::resetTestHooks);
    }

    @Test
    void abrirNuevaVersionSinUrlNoAbre() {
        UtilidadesGitHubReleases.ReleaseInfo release = new UtilidadesGitHubReleases.ReleaseInfo(
                "2.0.0", null, null, null);
        UtilidadesGitHubReleases.setReleaseProviderForTests(() -> release);

        AtomicReference<URI> opened = new AtomicReference<>();
        UtilidadesGitHubReleases.setBrowserOpenerForTests(new TestBrowserOpener(opened, true));

        UtilidadesGitHubReleases.abrirNuevaVersionEnNavegador();

        assertNull(opened.get());
    }

    @Test
    void abrirNuevaVersionConErrorDelProviderNoAbre() {
        UtilidadesGitHubReleases.setReleaseProviderForTests(() -> {
            throw new IOException("fallo");
        });

        AtomicReference<URI> opened = new AtomicReference<>();
        UtilidadesGitHubReleases.setBrowserOpenerForTests(new TestBrowserOpener(opened, true));

        UtilidadesGitHubReleases.abrirNuevaVersionEnNavegador();

        assertNull(opened.get());
    }

    @Test
    void abrirNuevaVersionConErrorDelNavegadorNoPropagaExcepcion() {
        UtilidadesGitHubReleases.ReleaseInfo release = new UtilidadesGitHubReleases.ReleaseInfo(
                "2.0.0", "LoteriaDeNavidad-2.0.0.zip", "https://example.com/app.zip", "https://example.com/release");
        UtilidadesGitHubReleases.setReleaseProviderForTests(() -> release);
        UtilidadesGitHubReleases.setBrowserOpenerForTests(new UtilidadesGitHubReleases.BrowserOpener() {
            @Override
            public boolean isSupported() {
                return true;
            }

            @Override
            public void open(URI uri) throws IOException {
                throw new IOException("fallo navegador");
            }
        });

        assertDoesNotThrow(UtilidadesGitHubReleases::abrirNuevaVersionEnNavegador);
    }

    @Test
    void obtenerUltimaReleaseDevuelveNullConRespuestaVaciaJsonInvalidoOTagAusente() {
        setJsonResponse(200, "");
        assertNull(invokePrivate("obtenerUltimaRelease", new Class<?>[]{}));

        setJsonResponse(200, "{");
        assertNull(invokePrivate("obtenerUltimaRelease", new Class<?>[]{}));

        setJsonResponse(200, "{\"html_url\":\"https://example.com/release\"}");
        assertNull(invokePrivate("obtenerUltimaRelease", new Class<?>[]{}));
    }

    @Test
    void obtenerUltimaReleaseConstruyeReleaseSinAssetSiNoHayZip() {
        setJsonResponse(200, """
                {
                  "tag_name": "v2.0.0",
                  "html_url": "https://example.com/release",
                  "assets": [{"name":"app.dmg","browser_download_url":"https://example.com/app.dmg"}]
                }
                """);

        UtilidadesGitHubReleases.ReleaseInfo release = invokePrivate("obtenerUltimaRelease", new Class<?>[]{});

        assertEquals("2.0.0", release.version());
        assertNull(release.assetName());
        assertNull(release.downloadUrl());
        assertEquals("https://example.com/release", release.releaseUrl());
    }

    @Test
    void obtenerUltimaReleaseSeleccionaAssetPreferido() {
        setJsonResponse(200, """
                {
                  "tag_name": "V2.0.0",
                  "html_url": "https://example.com/release",
                  "assets": [
                    {"name":"fallback.zip","browser_download_url":"https://example.com/fallback.zip"},
                    {"name":"LoteriaDeNavidad-2.0.0.zip","browser_download_url":"https://example.com/app.zip"}
                  ]
                }
                """);

        UtilidadesGitHubReleases.ReleaseInfo release = invokePrivate("obtenerUltimaRelease", new Class<?>[]{});

        assertEquals("2.0.0", release.version());
        assertEquals("LoteriaDeNavidad-2.0.0.zip", release.assetName());
        assertEquals("https://example.com/app.zip", release.downloadUrl());
        assertEquals("https://example.com/release", release.releaseUrl());
    }

    @Test
    void leerUrlDevuelveBodyConStatusCorrectoYLanzaConErrorHttp() {
        setJsonResponse(200, "{\"ok\":true}");
        assertEquals("{\"ok\":true}", invokePrivate("leerUrl", new Class<?>[]{}));

        UtilidadesGitHubReleases.setConnectionFactoryForTests(uri -> new TestHttpURLConnection(
                500, null, "{\"message\":\"error\"}"));

        IOException exception = assertThrows(IOException.class,
                () -> invokePrivate("leerUrl", new Class<?>[]{}));
        assertEquals("HTTP 500: {\"message\":\"error\"}", exception.getMessage());

        UtilidadesGitHubReleases.setConnectionFactoryForTests(uri -> new TestHttpURLConnection(
                100, null, null));
        exception = assertThrows(IOException.class, () -> invokePrivate("leerUrl", new Class<?>[]{}));
        assertEquals("HTTP 100: ", exception.getMessage());
    }

    @Test
    void normalizarVersionQuitaPrefijoV() {
        assertNull(invokePrivate("normalizarVersion", new Class<?>[]{String.class}, new Object[]{null}));
        assertEquals("1.2.3", invokePrivate("normalizarVersion", new Class<?>[]{String.class}, "v1.2.3"));
        assertEquals("1.2.3", invokePrivate("normalizarVersion", new Class<?>[]{String.class}, "V1.2.3"));
        assertEquals("release-1", invokePrivate("normalizarVersion", new Class<?>[]{String.class}, "release-1"));
    }

    @Test
    void seleccionarAssetDevuelveNullConAssetsInvalidos() {
        assertNull(invokePrivate("seleccionarAsset", new Class<?>[]{JsonNode.class, String.class}, null, "1.0.0"));
        JsonNode objectNode = parseJson("{\"assets\":[]}");
        assertNull(invokePrivate("seleccionarAsset", new Class<?>[]{JsonNode.class, String.class}, objectNode, "1.0.0"));
    }

    @Test
    void seleccionarAssetPrefiereZipExactoSobreFallback() {
        JsonNode assets = parseJson("""
                [
                  {"name":"otro.zip","browser_download_url":"https://example.com/otro.zip"},
                  {"name":"LoteriaDeNavidad-2.0.0.zip","browser_download_url":"https://example.com/app.zip"}
                ]
                """);

        Object asset = invokePrivate("seleccionarAsset", new Class<?>[]{JsonNode.class, String.class}, assets, "2.0.0");

        assertEquals("LoteriaDeNavidad-2.0.0.zip", invokeRecordAccessor(asset, "name"));
        assertEquals("https://example.com/app.zip", invokeRecordAccessor(asset, "downloadUrl"));
    }

    @Test
    void seleccionarAssetUsaPrimerZipSiNoHayPreferido() {
        JsonNode assets = parseJson("""
                [
                  {"name":"app.dmg","browser_download_url":"https://example.com/app.dmg"},
                  {"name":"fallback.zip","browser_download_url":"https://example.com/fallback.zip"},
                  {"name":"segundo.zip","browser_download_url":"https://example.com/segundo.zip"}
                ]
                """);

        Object asset = invokePrivate("seleccionarAsset", new Class<?>[]{JsonNode.class, String.class}, assets, "2.0.0");

        assertEquals("fallback.zip", invokeRecordAccessor(asset, "name"));
        assertEquals("https://example.com/fallback.zip", invokeRecordAccessor(asset, "downloadUrl"));
    }

    @Test
    void seleccionarAssetIgnoraAssetsSinNombreOUrl() {
        JsonNode assets = parseJson("""
                [
                  {"name":"sin-url.zip"},
                  {"browser_download_url":"https://example.com/sin-nombre.zip"},
                  {"name":"","browser_download_url":"https://example.com/vacio.zip"},
                  {"name":"valido.zip","browser_download_url":"https://example.com/valido.zip"}
                ]
                """);

        Object asset = invokePrivate("seleccionarAsset", new Class<?>[]{JsonNode.class, String.class}, assets, "2.0.0");

        assertEquals("valido.zip", invokeRecordAccessor(asset, "name"));
    }

    @Test
    void diferenteVersionReconoceMayorMenorIgualYPartesFaltantes() {
        assertFalse((Boolean) invokePrivate("diferenteVersion", new Class<?>[]{String.class}, new Object[]{null}));
        assertFalse((Boolean) invokePrivate("diferenteVersion", new Class<?>[]{String.class}, Constantes.VERSION));
        assertFalse((Boolean) invokePrivate("diferenteVersion", new Class<?>[]{String.class}, "0.0.1"));
        assertTrue((Boolean) invokePrivate("diferenteVersion", new Class<?>[]{String.class}, "999"));
        assertTrue((Boolean) invokePrivate("diferenteVersion", new Class<?>[]{String.class}, "999.0.0-beta"));
        assertFalse((Boolean) invokePrivate("diferenteVersion", new Class<?>[]{String.class}, "2.7"));
        assertTrue((Boolean) invokePrivate("diferenteVersion", new Class<?>[]{String.class}, "2.7.7.1"));
    }

    @Test
    void parseVersionConviertePartesNoNumericasEnCero() {
        assertArrayEquals(new int[]{1, 2, 3}, invokePrivate("parseVersion", new Class<?>[]{String.class}, "1.2.3"));
        assertArrayEquals(new int[]{1, 0, 3}, invokePrivate("parseVersion", new Class<?>[]{String.class}, "1.beta.3"));
        assertArrayEquals(new int[]{1, 2}, invokePrivate("parseVersion", new Class<?>[]{String.class}, "1.2."));
        assertArrayEquals(new int[]{0}, invokePrivate("parseVersion", new Class<?>[]{String.class}, "999999999999999999999999"));
    }

    @Test
    void leerStreamDevuelveVacioConNullYConcatenaLineas() throws IOException {
        assertEquals("", invokePrivate("leerStream", new Class<?>[]{java.io.InputStream.class}, new Object[]{null}));

        ByteArrayInputStream in = new ByteArrayInputStream("linea1\nlinea2".getBytes(StandardCharsets.UTF_8));
        assertEquals("linea1linea2", invokePrivate("leerStream", new Class<?>[]{java.io.InputStream.class}, in));
    }

    @Test
    void parsearJsonDevuelveNullConJsonInvalido() {
        assertNull(invokePrivate("parsearJson", new Class<?>[]{String.class}, "{"));
        assertNotNull(invokePrivate("parsearJson", new Class<?>[]{String.class}, "{\"tag_name\":\"v1.0.0\"}"));
    }

    @Test
    void obtenerTextoValidaNodoCampoYContenido() {
        JsonNode node = parseJson("{\"texto\":\"valor\",\"nulo\":null,\"vacio\":\"\"}");

        assertNull(invokePrivate("obtenerTexto", new Class<?>[]{JsonNode.class, String.class}, null, "texto"));
        assertNull(invokePrivate("obtenerTexto", new Class<?>[]{JsonNode.class, String.class}, node, null));
        assertNull(invokePrivate("obtenerTexto", new Class<?>[]{JsonNode.class, String.class}, node, "ausente"));
        assertNull(invokePrivate("obtenerTexto", new Class<?>[]{JsonNode.class, String.class}, node, "nulo"));
        assertNull(invokePrivate("obtenerTexto", new Class<?>[]{JsonNode.class, String.class}, node, "vacio"));
        assertEquals("valor", invokePrivate("obtenerTexto", new Class<?>[]{JsonNode.class, String.class}, node, "texto"));

        JsonNode mockNode = mock(JsonNode.class);
        JsonNode mockValue = mock(JsonNode.class);
        when(mockNode.get("sinTexto")).thenReturn(mockValue);
        when(mockValue.asText()).thenReturn(null);
        assertNull(invokePrivate("obtenerTexto", new Class<?>[]{JsonNode.class, String.class}, mockNode, "sinTexto"));
    }

    @Test
    void parseLeadingNumberValidaEntradasLimite() {
        assertEquals(0, (int) invokePrivate("parseLeadingNumber", new Class<?>[]{String.class}, new Object[]{null}));
        assertEquals(0, (int) invokePrivate("parseLeadingNumber", new Class<?>[]{String.class}, ""));
        assertEquals(0, (int) invokePrivate("parseLeadingNumber", new Class<?>[]{String.class}, "beta"));
        assertEquals(12, (int) invokePrivate("parseLeadingNumber", new Class<?>[]{String.class}, "12-beta"));
        assertEquals(0, (int) invokePrivate("parseLeadingNumber", new Class<?>[]{String.class}, "999999999999999999999999"));
    }

    private static final class TestHttpURLConnection extends HttpURLConnection {
        private final String inputBody;
        private final String errorBody;

        private TestHttpURLConnection(int status, String inputBody, String errorBody) throws IOException {
            super(new URL("https://example.com"));
            this.responseCode = status;
            this.inputBody = inputBody;
            this.errorBody = errorBody;
        }

        @Override
        public void disconnect() {

        }

        @Override
        public boolean usingProxy() {
            return false;
        }

        @Override
        public void connect() {

        }

        @Override
        public InputStream getInputStream() {
            return stream(inputBody);
        }

        @Override
        public InputStream getErrorStream() {
            return stream(errorBody);
        }

        private InputStream stream(String body) {
            if (body == null) {
                return null;
            }
            return new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
        }
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
