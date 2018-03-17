package es.jklabs.utilidades;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import com.google.firebase.database.*;
import es.jklabs.desktop.constant.Constant;
import es.jklabs.desktop.gui.Ventana;
import es.jklabs.gui.utilidades.Growls;
import es.jklabs.json.firebase.Aplicacion;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class UtilidadesFirebase {

    private static final Logger LOG = Logger.getLogger();
    private static final String REFERENCE = "aplicaciones/Loteria de Navidad";

    private UtilidadesFirebase() {

    }

    public static boolean existeNuevaVersion() throws IOException, InterruptedException {
        instanciarFirebase();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(REFERENCE);
        Aplicacion app = getAplicacion(ref);
        return diferenteVersion(app.getUltimaVersion(), Constant.getValor("version"));
    }

    private static void instanciarFirebase() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(UtilidadesFirebase.class.getClassLoader()
                            .getResourceAsStream
                                    ("json/curriculum-a2a80-firebase-adminsdk-17wyo-de15a29f7c.json")))
                    .setStorageBucket(Constant.getValor("storage.bucket")).setDatabaseUrl
                            ("https://curriculum-a2a80.firebaseio.com").build();
            FirebaseApp.initializeApp(options);
        }
    }

    private static boolean diferenteVersion(String serverVersion, String appVersion) {
        String[] sv = serverVersion.split("\\.");
        String[] av = appVersion.split("\\.");
        return Integer.parseInt(sv[0]) > Integer.parseInt(av[0]) || Integer.parseInt(sv[0]) == Integer.parseInt(av[0]) && (Integer.parseInt(sv[1]) > Integer.parseInt(av[1]) || Integer.parseInt(sv[1]) == Integer.parseInt(av[1]) && Integer.parseInt(sv[2]) > Integer.parseInt(av[2]));
    }

    public static void descargaNuevaVersion(Ventana ventana) throws InterruptedException {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int retorno = fc.showSaveDialog(ventana);
        if (retorno == JFileChooser.APPROVE_OPTION) {
            File directorio = fc.getSelectedFile();
            try {
                instanciarFirebase();
                Bucket bucket = StorageClient.getInstance().bucket();
                Storage storage = bucket.getStorage();
                Aplicacion app = getAplicacion(FirebaseDatabase.getInstance().getReference(REFERENCE));
                if (app.getUltimaVersion() != null) {
                    Blob blob = storage.get(Constant.getValor("storage.bucket"), getNombreApp(app), Storage
                            .BlobGetOption.fields(Storage.BlobField.SIZE));
                    blob.downloadTo(Paths.get(directorio.getPath() + System.getProperty("file.separator") + getNombreApp(app)));
                    actualizarNumDescargas();
                    Growls.mostrarInfo(ventana, null, "nueva.version.descargada");
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

    private static void actualizarNumDescargas() throws IOException, InterruptedException {
        instanciarFirebase();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(REFERENCE);
        Aplicacion app = getAplicacion(ref);
        Map<String, Object> map = new HashMap<>();
        map.put("numDescargas", (app.getNumDescargas() + 1));
        ref.updateChildrenAsync(map);
    }

    private static String getNombreApp(Aplicacion app) {
        return Constant.getValor("nombre.app.dowload") + "-" + app.getUltimaVersion() + ".zip";
    }

    private static Aplicacion getAplicacion(DatabaseReference ref) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Aplicacion app = new Aplicacion();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Aplicacion snap = snapshot.getValue(Aplicacion.class);
                app.setNumDescargas(snap.getNumDescargas());
                app.setUltimaVersion(snap.getUltimaVersion());
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                LOG.info(error.getMessage());
                latch.countDown();
            }
        });
        latch.await();
        return app;
    }
}
