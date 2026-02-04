package es.jklabs.utilidades;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.jklabs.gui.utilidades.Growls;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class UtilidadesGitHubReleases {

    private static final String LATEST_RELEASE_URL = "https://api.github.com/repos/" + Constantes.GITHUB_REPO
            + "/releases/latest";
    private static final String USER_AGENT = "LoteriaDeNavidad/" + Constantes.VERSION;
    private static final int TIMEOUT_MILLIS = 15000;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static ReleaseProvider RELEASE_PROVIDER = UtilidadesGitHubReleases::obtenerUltimaRelease;
    private static BrowserOpener BROWSER_OPENER = new DesktopBrowserOpener();

    private UtilidadesGitHubReleases() {

    }

    public static boolean existeNuevaVersion() throws IOException {
        ReleaseInfo release = obtenerUltimaRelease();
        if (release == null || release.version() == null) {
            return false;
        }
        return diferenteVersion(release.version());
    }

    public static void abrirNuevaVersionEnNavegador() {
        try {
            ReleaseInfo release = RELEASE_PROVIDER.obtener();
            if (release == null) {
                Logger.info("Error de lectura de la release");
                return;
            }
            String url = release.releaseUrl() != null ? release.releaseUrl() : release.downloadUrl();
            if (url == null) {
                Logger.info("Error de lectura de la release");
                return;
            }
            if (!BROWSER_OPENER.isSupported()) {
                Growls.mostrarError("abrir.nueva.version", new UnsupportedOperationException("Desktop browse not supported"));
                return;
            }
            Growls.mostrarInfo(null, "nueva.version.abrir");
            BROWSER_OPENER.open(URI.create(url));
        } catch (IOException e) {
            Logger.error("Abrir nueva version", e);
            Growls.mostrarError("abrir.nueva.version", e);
        }
    }

    static void setReleaseProviderForTests(ReleaseProvider provider) {
        RELEASE_PROVIDER = provider == null ? UtilidadesGitHubReleases::obtenerUltimaRelease : provider;
    }

    static void setBrowserOpenerForTests(BrowserOpener opener) {
        BROWSER_OPENER = opener == null ? new DesktopBrowserOpener() : opener;
    }

    static void resetTestHooks() {
        RELEASE_PROVIDER = UtilidadesGitHubReleases::obtenerUltimaRelease;
        BROWSER_OPENER = new DesktopBrowserOpener();
    }

    private static ReleaseInfo obtenerUltimaRelease() throws IOException {
        String json = leerUrl();
        if (json.isEmpty()) {
            return null;
        }
        JsonNode root = parsearJson(json);
        if (root == null) {
            return null;
        }
        String tag = obtenerTexto(root, "tag_name");
        if (tag == null) {
            return null;
        }
        String releaseUrl = obtenerTexto(root, "html_url");
        String version = normalizarVersion(tag);
        ReleaseAsset asset = seleccionarAsset(root.path("assets"), version);
        if (asset == null) {
            return new ReleaseInfo(version, null, null, releaseUrl);
        }
        return new ReleaseInfo(version, asset.name(), asset.downloadUrl(), releaseUrl);
    }

    private static String normalizarVersion(String tag) {
        if (tag == null) {
            return null;
        }
        if (tag.startsWith("v") || tag.startsWith("V")) {
            return tag.substring(1);
        }
        return tag;
    }

    private static ReleaseAsset seleccionarAsset(JsonNode assets, String version) {
        if (assets == null || !assets.isArray()) {
            return null;
        }
        ReleaseAsset fallback = null;
        String preferido = Constantes.NOMBRE_APP_DOWNLOAD + "-" + version + ".zip";
        for (JsonNode asset : assets) {
            String name = obtenerTexto(asset, "name");
            String url = obtenerTexto(asset, "browser_download_url");
            if (name == null || url == null) {
                continue;
            }
            if (preferido.equals(name)) {
                return new ReleaseAsset(name, url);
            }
            if (fallback == null && name.endsWith(".zip")) {
                fallback = new ReleaseAsset(name, url);
            }
        }
        return fallback;
    }

    private static boolean diferenteVersion(String serverVersion) {
        if (serverVersion == null) {
            return false;
        }
        int[] server = parseVersion(serverVersion);
        int[] actual = parseVersion(Constantes.VERSION);
        int max = Math.max(server.length, actual.length);
        for (int i = 0; i < max; i++) {
            int sv = i < server.length ? server[i] : 0;
            int av = i < actual.length ? actual[i] : 0;
            if (sv != av) {
                return sv > av;
            }
        }
        return false;
    }

    private static int[] parseVersion(String version) {
        String[] parts = version.split("\\.");
        int[] numbers = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            numbers[i] = parseLeadingNumber(parts[i]);
        }
        return numbers;
    }

    private static String leerUrl() throws IOException {
        URI uri = URI.create(UtilidadesGitHubReleases.LATEST_RELEASE_URL);
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestProperty("Accept", "application/vnd.github+json");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setConnectTimeout(TIMEOUT_MILLIS);
        connection.setReadTimeout(TIMEOUT_MILLIS);
        int status = connection.getResponseCode();
        String body;
        if (status >= 200 && status < 300) {
            body = leerStream(connection.getInputStream());
        } else {
            body = leerStream(connection.getErrorStream());
            throw new IOException("HTTP " + status + ": " + body);
        }
        connection.disconnect();
        return body;
    }

    private static String leerStream(InputStream in) throws IOException {
        if (in == null) {
            return "";
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    private static JsonNode parsearJson(String json) {
        try {
            return OBJECT_MAPPER.readTree(json);
        } catch (IOException e) {
            Logger.error("parsear.github.release", e);
            return null;
        }
    }

    private static String obtenerTexto(JsonNode node, String field) {
        if (node == null || field == null) {
            return null;
        }
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            return null;
        }
        String text = value.asText();
        return text == null || text.isEmpty() ? null : text;
    }

    private static int parseLeadingNumber(String part) {
        if (part == null || part.isEmpty()) {
            return 0;
        }
        int end = 0;
        while (end < part.length() && Character.isDigit(part.charAt(end))) {
            end++;
        }
        if (end == 0) {
            return 0;
        }
        try {
            return Integer.parseInt(part.substring(0, end));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @FunctionalInterface
    interface ReleaseProvider {
        ReleaseInfo obtener() throws IOException;
    }

    interface BrowserOpener {
        boolean isSupported();

        void open(URI uri) throws IOException;
    }

    private static final class DesktopBrowserOpener implements BrowserOpener {
        @Override
        public boolean isSupported() {
            return Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE);
        }

        @Override
        public void open(URI uri) throws IOException {
            Desktop.getDesktop().browse(uri);
        }
    }

    record ReleaseInfo(String version, String assetName, String downloadUrl, String releaseUrl) {
    }

    private record ReleaseAsset(String name, String downloadUrl) {
    }
}
