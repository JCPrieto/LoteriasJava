package es.jklabs.desktop.constant;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by juanky on 28/09/15.
 */
public class Constant {

    private static final String CONSTANTES = "constant.properties";

    private Constant() {

    }

    public static String getValor(String key) {
        Properties properties = new Properties();
        InputStream inputStream = Constant.class.getClassLoader().getResourceAsStream(CONSTANTES);
        try {
            properties.load(new InputStreamReader(inputStream, "UTF-8"));
            return properties.getProperty(key);
        } catch (IOException e) {
            return key;
        }
    }
}
