package es.jklabs.utilidades;

import io.github.jcprieto.lib.loteria.enumeradores.EstadoSorteo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilidadesEstadoSorteoTest extends BaseTest {

    @Test
    void getHumanReadableDevuelveTextosI18n() {
        assertEquals(Mensajes.getMensaje("sorteo.no.iniciado"),
                UtilidadesEstadoSorteo.getHumanReadable(EstadoSorteo.NO_INICIADO));
        assertEquals(Mensajes.getMensaje("sorteo.en.proceso"),
                UtilidadesEstadoSorteo.getHumanReadable(EstadoSorteo.EN_PROCESO));
        assertEquals(Mensajes.getMensaje("sorteo.terminado.provisional"),
                UtilidadesEstadoSorteo.getHumanReadable(EstadoSorteo.TERMINADO_PROVISIONAL));
        assertEquals(Mensajes.getMensaje("sorteo.terminado.oficial"),
                UtilidadesEstadoSorteo.getHumanReadable(EstadoSorteo.TERMINADO));
    }
}
