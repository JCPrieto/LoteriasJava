package es.jklabs.desktop.gui.listener;

import es.jklabs.utilidades.BaseTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResumenMouseListenerTest extends BaseTest {

    private static URI invokeBuildUri(String url) throws Exception {
        Method method = ResumenMouseListener.class.getDeclaredMethod("buildUri", String.class);
        method.setAccessible(true);
        return (URI) method.invoke(null, url);
    }

    @Test
    void buildUriEncodesSpacesAndAccents() throws Exception {
        String url = "https://www.loteriasyapuestas.es/f/loterias/documentos/Lotería Nacional/listas de premios/SM_LISTAOFICIAL.A2025.S102.pdf";

        URI uri = invokeBuildUri(url);

        String rawPath = uri.getRawPath();
        assertTrue(rawPath.contains("%20"));
        String path = uri.getPath();
        assertTrue(path.contains("Lotería Nacional"));
        assertTrue(path.contains("listas de premios"));
    }

    @Test
    void buildUriPreservesQueryAndFragment() throws Exception {
        String url = "https://example.com/mi ruta/archivo.pdf?tipo=lista oficial#seccion";

        URI uri = invokeBuildUri(url);

        assertEquals("/mi%20ruta/archivo.pdf", uri.getRawPath());
        assertEquals("tipo=lista%20oficial", uri.getRawQuery());
        assertEquals("seccion", uri.getRawFragment());
    }
}
