package es.jklabs.utilidades;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UtilidadesFecha {

    public static String getHumanReadable(LocalDateTime time) {
        return time.format(DateTimeFormatter.ofPattern("dd-MM-yy HH:mm"));
    }
}
