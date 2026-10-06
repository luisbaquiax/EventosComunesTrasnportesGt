package org.transportsgt.eventos;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class TipoEventoTest {

    @Test
    void hay31TiposDeEvento() {
        assertThat(TipoEvento.values()).hasSize(31);
    }

    @Test
    void nombresYLlavesDeRuteoSonUnicos() {
        Set<String> nombres = Arrays.stream(TipoEvento.values()).map(TipoEvento::nombre).collect(Collectors.toSet());
        Set<String> llaves = Arrays.stream(TipoEvento.values()).map(TipoEvento::llaveRuteo).collect(Collectors.toSet());
        assertThat(nombres).hasSize(31);
        assertThat(llaves).hasSize(31);
    }

    @Test
    void laLlaveEmpiezaConElPrefijoDelProductorYTieneTresSegmentos() {
        for (TipoEvento tipo : TipoEvento.values()) {
            assertThat(tipo.llaveRuteo()).startsWith(tipo.productor().prefijo() + ".");
            assertThat(tipo.llaveRuteo().split("\\.")).hasSize(3);
        }
    }

    @Test
    void cantidadPorProductorCoincideConElCatalogo() {
        assertThat(contar(Productor.IDENTIDAD)).isEqualTo(6);
        assertThat(contar(Productor.FLOTA)).isEqualTo(4);
        assertThat(contar(Productor.VIAJES)).isEqualTo(8);
        assertThat(contar(Productor.BOLETOS)).isEqualTo(2);
        assertThat(contar(Productor.CARTERA)).isEqualTo(2);
        assertThat(contar(Productor.ALQUILERES)).isEqualTo(4);
        assertThat(contar(Productor.COSTOS)).isEqualTo(5);
    }

    @Test
    void tipoAgregadoEsElSegundoSegmento() {
        assertThat(TipoEvento.VIAJE_PROGRAMADO.tipoAgregado()).isEqualTo("viaje");
        assertThat(TipoEvento.ENLACE_CONTRASENA_EMITIDO.tipoAgregado()).isEqualTo("usuario");
    }

    @Test
    void sePuedeBuscarPorNombre() {
        assertThat(TipoEvento.deNombre("ViajeProgramado")).contains(TipoEvento.VIAJE_PROGRAMADO);
        assertThat(TipoEvento.deNombre("NoExiste")).isEmpty();
    }

    private static long contar(Productor productor) {
        return Arrays.stream(TipoEvento.values()).filter(t -> t.productor() == productor).count();
    }
}
