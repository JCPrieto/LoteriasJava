package es.jklabs.utilidades;

public class Constantes {

    public static final String NOMBRE_APP = "Lotería de Navidad";
    public static final String VERSION = cargarVersion();
    static final String NOMBRE_APP_DOWNLOAD = "LoteriaDeNavidad";
    static final String GITHUB_REPO = "JCPrieto/LoteriasJava";

    private Constantes() {

    }

    private static String cargarVersion() {
        return cargarVersion(Constantes::cargarVersionDesdeProperties, Constantes::cargarVersionDesdePom);
    }

    static String cargarVersion(VersionSupplier propertiesSupplier, VersionSupplier pomSupplier) {
        String version = propertiesSupplier.get();
        if (version != null) {
            return version;
        }
        version = pomSupplier.get();
        return version != null ? version : "desconocida";
    }

    private static String cargarVersionDesdeProperties() {
        String ruta = "/META-INF/maven/es.jklabs.desktop/LoteriaDeNavidad/pom.properties";
        return cargarVersionDesdeProperties(() -> Constantes.class.getResourceAsStream(ruta));
    }

    static String cargarVersionDesdeProperties(InputStreamSupplier supplier) {
        java.io.InputStream in;
        try {
            in = supplier.get();
        } catch (java.io.IOException e) {
            return null;
        }
        if (in == null) {
            return null;
        }
        try {
            java.util.Properties props = new java.util.Properties();
            props.load(in);
            String version = normalizarVersion(props.getProperty("version"));
            return cerrar(in) ? version : null;
        } catch (java.io.IOException e) {
            cerrar(in);
            return null;
        }
    }

    private static String cargarVersionDesdePom() {
        return cargarVersionDesdePom(java.nio.file.Paths.get("pom.xml"));
    }

    static String cargarVersionDesdePom(java.nio.file.Path pom) {
        if (!java.nio.file.Files.isRegularFile(pom)) {
            return null;
        }
        try (java.io.InputStream in = java.nio.file.Files.newInputStream(pom)) {
            javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            factory.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setAttribute(javax.xml.XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(javax.xml.XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
            factory.setNamespaceAware(true);
            javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new org.xml.sax.helpers.DefaultHandler());
            org.w3c.dom.Document document = builder.parse(in);
            javax.xml.xpath.XPath xPath = javax.xml.xpath.XPathFactory.newInstance().newXPath();
            String version = xPath.evaluate("/*[local-name()='project']/*[local-name()='version']", document);
            if (normalizarVersion(version) == null) {
                version = xPath.evaluate("/*[local-name()='project']/*[local-name()='parent']/*[local-name()='version']",
                        document);
            }
            return normalizarVersion(version);
        } catch (Exception e) {
            return null;
        }
    }

    static String normalizarVersion(String version) {
        return version == null || version.isBlank() ? null : version.trim();
    }

    static boolean cerrar(java.io.InputStream in) {
        try {
            in.close();
            return true;
        } catch (java.io.IOException e) {
            return false;
        }
    }

    interface VersionSupplier {

        String get();
    }

    interface InputStreamSupplier {

        java.io.InputStream get() throws java.io.IOException;
    }
}
