package es.jklabs.utilidades;

import io.github.jcprieto.lib.loteria.enumeradores.EstadoSorteo;

public class UtilidadesEstadoSorteo {

    public static String getHumanReadable(EstadoSorteo estado) {
        return switch (estado) {
            case NO_INICIADO -> Mensajes.getMensaje("sorteo.no.iniciado");
            case EN_PROCESO -> Mensajes.getMensaje("sorteo.en.proceso");
            case TERMINADO_PROVISIONAL -> Mensajes.getMensaje("sorteo.terminado.provisional");
            default -> Mensajes.getMensaje("sorteo.terminado.oficial");
        };
    }
}
