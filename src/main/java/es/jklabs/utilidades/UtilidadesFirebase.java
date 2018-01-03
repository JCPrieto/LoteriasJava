package es.jklabs.utilidades;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import es.jklabs.desktop.constant.Constant;
import es.jklabs.desktop.gui.Ventana;
import es.jklabs.json.firebase.Aplicacion;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.AccessDeniedException;
import java.nio.file.Paths;
import java.util.Objects;

public class UtilidadesFirebase {

    private static final Logger LOG = Logger.getLogger();

    private UtilidadesFirebase() {

    }

    public static boolean existeNuevaVersion() {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("https://curriculum-a2a80.firebaseio.com/aplicaciones/Loteria%20de%20Navidad" +
                ".json");
        try {
            HttpResponse response = client.execute(request);
            if (Objects.equals(response.getStatusLine().getStatusCode(), HttpResponseCode.OK)) {
                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));
                ObjectMapper mapper = new ObjectMapper();
                Aplicacion aplicacion = mapper.readValue(rd, Aplicacion.class);
                return diferenteVersion(aplicacion.getUltimaVersion(), Constant.getValor("version"));
            }
        } catch (IOException e) {
            LOG.error("Consultar nueva version en la aplicacion", e);
        }
        return false;
    }

    private static boolean diferenteVersion(String serverVersion, String appVersion) {
        String[] sv = serverVersion.split("\\.");
        String[] av = appVersion.split("\\.");
        return Integer.parseInt(sv[0]) > Integer.parseInt(av[0]) || Integer.parseInt(sv[0]) == Integer.parseInt(av[0]) && (Integer.parseInt(sv[1]) > Integer.parseInt(av[1]) || Integer.parseInt(sv[1]) == Integer.parseInt(av[1]) && Integer.parseInt(sv[2]) > Integer.parseInt(av[2]));
    }

    public static void descargaNuevaVersion(Ventana ventana) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int retorno = fc.showSaveDialog(ventana);
        if (retorno == JFileChooser.APPROVE_OPTION) {
            File directorio = fc.getSelectedFile();
            try {
                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseOptions options = new FirebaseOptions.Builder()
                            .setCredentials(GoogleCredentials.fromStream(ventana.getClass().getClassLoader()
                                    .getResourceAsStream
                                            ("json/curriculum-a2a80-firebase-adminsdk-17wyo-de15a29f7c.json")))
                            .setStorageBucket(Constant.getValor("storage.bucket"))
                            .build();
                    FirebaseApp.initializeApp(options);
                }
                Bucket bucket = StorageClient.getInstance().bucket();
                Storage storage = bucket.getStorage();
                Aplicacion app = getAplicacion();
                if (app != null) {
                    Blob blob = storage.get(Constant.getValor("storage.bucket"), getNombreApp(app), Storage
                            .BlobGetOption.fields(Storage.BlobField.SIZE));
                    blob.downloadTo(Paths.get(directorio.getPath() + System.getProperty("file.separator") + getNombreApp(app)));
                    actualizarNumDescargas();
                    ventana.getTrayIcon().displayMessage(null, "Nueva version descargada completamente", TrayIcon
                            .MessageType.INFO);
                } else {
                    LOG.info("Error de lectura de la BBDD");
                }
            } catch (AccessDeniedException e) {
                ventana.getTrayIcon().displayMessage(null, "No tiene permisos para descargar para escribir la ruta " +
                        "indicada", TrayIcon.MessageType.ERROR);
                descargaNuevaVersion(ventana);
            } catch (IOException e) {
                LOG.error("Descargar nueva version", e);
            }
        }
    }

    private static void actualizarNumDescargas() throws IOException {
        Aplicacion app = getAplicacion();
        if (app != null) {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPut put = new HttpPut("https://curriculum-a2a80.firebaseio.com/aplicaciones/Loteria%20de%20Navidad/numDescargas.json");
            StringEntity params = new StringEntity(Integer.toString(app.getNumDescargas() + 1), "UTF-8");
            params.setContentType("application/json");
            put.addHeader("content-type", "application/json");
            put.setEntity(params);
            client.execute(put);
        } else {
            LOG.info("Error de lectura de la BBDD");
        }
    }

    private static String getNombreApp(Aplicacion app) throws IOException {
        return Constant.getValor("nombre.app.dowload") + "-" + app.getUltimaVersion() + ".zip";
    }

    private static Aplicacion getAplicacion() {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("https://curriculum-a2a80.firebaseio.com/aplicaciones/Loteria%20de%20Navidad.json");
        try {
            HttpResponse response = client.execute(request);
            if (Objects.equals(response.getStatusLine().getStatusCode(), HttpResponseCode.OK)) {
                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(rd, Aplicacion.class);
            }
        } catch (IOException e) {
            LOG.error("Consultar nueva version en la aplicacion", e);
        }
        return null;
    }
}
