package es.jklabs.utilidades;

public class Constantes {

    public static final String NOMBRE_APP = "Lotería de Navidad";
    public static final String VERSION = cargarVersion();
    static final String NOMBRE_APP_DOWNLOAD = "LoteriaDeNavidad";
    static final String GITHUB_REPO = "JCPrieto/LoteriasJava";

    private Constantes() {

    }

    private static String cargarVersion() {
        String ruta = "/META-INF/maven/es.jklabs.desktop/LoteriaDeNavidad/pom.properties";
        try (java.io.InputStream in = Constantes.class.getResourceAsStream(ruta)) {
            if (in == null) {
                return "desconocida";
            }
            java.util.Properties props = new java.util.Properties();
            props.load(in);
            String version = props.getProperty("version");
            return version == null || version.isBlank() ? "desconocida" : version;
        } catch (java.io.IOException e) {
            return "desconocida";
        }
    }
}
