package es.jklabs.utilidades;

public class Constantes {

    public static final String NOMBRE_APP = "Lotería de Navidad";
    public static final String VERSION = cargarVersion();
    static final String NOMBRE_APP_DOWNLOAD = "LoteriaDeNavidad";
    static final String GITHUB_REPO = "JCPrieto/LoteriasJava";

    private Constantes() {

    }

    private static String cargarVersion() {
        String version = cargarVersionDesdeProperties();
        if (version != null) {
            return version;
        }
        version = cargarVersionDesdePom();
        return version != null ? version : "desconocida";
    }

    private static String cargarVersionDesdeProperties() {
        String ruta = "/META-INF/maven/es.jklabs.desktop/LoteriaDeNavidad/pom.properties";
        try (java.io.InputStream in = Constantes.class.getResourceAsStream(ruta)) {
            if (in == null) {
                return null;
            }
            java.util.Properties props = new java.util.Properties();
            props.load(in);
            String version = props.getProperty("version");
            return version == null || version.isBlank() ? null : version;
        } catch (java.io.IOException e) {
            return null;
        }
    }

    private static String cargarVersionDesdePom() {
        java.nio.file.Path pom = java.nio.file.Paths.get("pom.xml");
        if (!java.nio.file.Files.isRegularFile(pom)) {
            return null;
        }
        try (java.io.InputStream in = java.nio.file.Files.newInputStream(pom)) {
            javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            org.w3c.dom.Document document = factory.newDocumentBuilder().parse(in);
            javax.xml.xpath.XPath xPath = javax.xml.xpath.XPathFactory.newInstance().newXPath();
            String version = xPath.evaluate("/*[local-name()='project']/*[local-name()='version']", document);
            if (version == null || version.isBlank()) {
                version = xPath.evaluate("/*[local-name()='project']/*[local-name()='parent']/*[local-name()='version']",
                        document);
            }
            return version == null || version.isBlank() ? null : version.trim();
        } catch (Exception e) {
            return null;
        }
    }
}
