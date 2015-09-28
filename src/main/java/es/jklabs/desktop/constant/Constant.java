package es.jklabs.desktop.constant;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by juanky on 28/09/15.
 */
public class Constant {

    private static final String CONSTANTES = "constant.properties";

    public static String getValor(String key) throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = Constant.class.getClassLoader().getResourceAsStream(CONSTANTES);
        properties.load(inputStream);
        return properties.getProperty(key);
    }
}
