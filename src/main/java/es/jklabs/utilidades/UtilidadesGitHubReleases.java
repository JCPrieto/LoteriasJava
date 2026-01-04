package es.jklabs.utilidades;

import es.jklabs.desktop.gui.Ventana;
import es.jklabs.gui.utilidades.Growls;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilidadesGitHubReleases {

    private static final String LATEST_RELEASE_URL = "https://api.github.com/repos/" + Constantes.GITHUB_REPO
            + "/releases/latest";
    private static final String USER_AGENT = "LoteriaDeNavidad/" + Constantes.VERSION;

    private UtilidadesGitHubReleases() {

    }

    public static boolean existeNuevaVersion() throws IOException {
        ReleaseInfo release = obtenerUltimaRelease();
        if (release == null || release.version() == null) {
            return false;
        }
        return diferenteVersion(release.version());
    }

    public static void descargaNuevaVersion(Ventana ventana) throws InterruptedException {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int retorno = fc.showSaveDialog(ventana);
        if (retorno == JFileChooser.APPROVE_OPTION) {
            File directorio = fc.getSelectedFile();
            try {
                ReleaseInfo release = obtenerUltimaRelease();
                if (release == null || release.downloadUrl() == null) {
                    Logger.info("Error de lectura de la release");
                    return;
                }
                Path destino = Paths.get(directorio.getPath() + FileSystems.getDefault().getSeparator()
                        + release.assetName());
                descargarArchivo(release.downloadUrl(), destino);
                Growls.mostrarInfo(null, "nueva.version.descargada");
            } catch (AccessDeniedException e) {
                Growls.mostrarError("path.sin.permiso.escritura", e);
                descargaNuevaVersion(ventana);
            } catch (IOException e) {
                Logger.error("Descargar nueva version", e);
            }
        }
    }

    private static ReleaseInfo obtenerUltimaRelease() throws IOException {
        String json = leerUrl();
        if (json.isEmpty()) {
            return null;
        }
        String tag = extraerCampo(json, "\"tag_name\"\\s*:\\s*\"([^\"]+)\"");
        if (tag == null) {
            return null;
        }
        String version = normalizarVersion(tag);
        ReleaseAsset asset = seleccionarAsset(json, version);
        if (asset == null) {
            return new ReleaseInfo(version, null, null);
        }
        return new ReleaseInfo(version, asset.name(), asset.downloadUrl());
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

    private static ReleaseAsset seleccionarAsset(String json, String version) {
        String assetsBlock = extraerCampo(json, "\"assets\"\\s*:\\s*\\[(.*?)]");
        if (assetsBlock == null) {
            return null;
        }
        Pattern assetPattern = Pattern.compile("\"name\"\\s*:\\s*\"([^\"]+)\".*?\"browser_download_url\"\\s*:"
                + "\\s*\"([^\"]+)\"", Pattern.DOTALL);
        Matcher assetMatcher = assetPattern.matcher(assetsBlock);
        ReleaseAsset fallback = null;
        String preferido = Constantes.NOMBRE_APP_DOWNLOAD + "-" + version + ".zip";
        while (assetMatcher.find()) {
            String name = assetMatcher.group(1);
            String url = assetMatcher.group(2);
            if (preferido.equals(name)) {
                return new ReleaseAsset(name, url);
            }
            if (fallback == null && name.endsWith(".zip")) {
                fallback = new ReleaseAsset(name, url);
            }
        }
        return fallback;
    }

    private static String extraerCampo(String json, String patron) {
        Pattern pattern = Pattern.compile(patron, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static void descargarArchivo(String url, Path destino) throws IOException {
        URI uri = URI.create(url);
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setInstanceFollowRedirects(true);
        try (InputStream in = connection.getInputStream()) {
            Files.copy(in, destino, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            connection.disconnect();
        }
    }

    private static String leerUrl() throws IOException {
        URI uri = URI.create(UtilidadesGitHubReleases.LATEST_RELEASE_URL);
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestProperty("Accept", "application/vnd.github+json");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        try (InputStream in = connection.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } finally {
            connection.disconnect();
        }
    }

    private static boolean diferenteVersion(String serverVersion) {
        String[] sv = serverVersion.split("\\.");
        String[] av = Constantes.VERSION.split("\\.");
        return Integer.parseInt(sv[0]) > Integer.parseInt(av[0]) || Integer.parseInt(sv[0]) == Integer.parseInt(av[0])
                && (Integer.parseInt(sv[1]) > Integer.parseInt(av[1]) || Integer.parseInt(sv[1]) == Integer
                .parseInt(av[1]) && Integer.parseInt(sv[2]) > Integer.parseInt(av[2]));
    }

    private record ReleaseInfo(String version, String assetName, String downloadUrl) {
    }

    private record ReleaseAsset(String name, String downloadUrl) {
    }
}
