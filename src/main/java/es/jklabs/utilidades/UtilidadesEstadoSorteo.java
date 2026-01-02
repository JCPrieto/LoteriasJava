package es.jklabs.utilidades;

import io.github.jcprieto.lib.loteria.enumeradores.EstadoSorteo;

public class UtilidadesEstadoSorteo {

    public static String getHumanReadable(EstadoSorteo estado) {
        return switch (estado) {
            case NO_INICIADO -> "El sorteo aun no ha comenzado";
            case EN_PROCESO -> "El sorteo esta en proceso";
            case TERMINADO_PROVISIONAL -> "El sorteo ha concluido. Los resultados aun son provisionales";
            default -> "El sorteo ha concluido y los resultados se basan el la lista oficial";
        };
    }
}
