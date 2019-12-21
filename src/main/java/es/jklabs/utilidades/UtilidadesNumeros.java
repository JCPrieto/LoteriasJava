package es.jklabs.utilidades;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.jklabs.json.loteria.Numeros;

import java.io.File;
import java.io.IOException;

public class UtilidadesNumeros {

    private UtilidadesNumeros() {

    }

    public static Numeros getNumeros() {
        Numeros numeros;
        ObjectMapper mapper = UtilidadesJSon.getObjectMapper();
        try {
            numeros = mapper.readValue(getArchivo(), Numeros.class);
        } catch (IOException e) {
            numeros = new Numeros();
        }
        return numeros;
    }

    private static File getArchivo() {
        return new File(UtilidadesFichero.HOME +
                UtilidadesFichero.SEPARADOR + UtilidadesFichero.LOTERIAS_FOLDER + UtilidadesFichero.SEPARADOR + "Numeros.json");
    }
}
