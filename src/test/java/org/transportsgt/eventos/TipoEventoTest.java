package org.transportsgt.eventos;

import org.transportsgt.eventos.contrato.*;
import org.transportsgt.eventos.configuracion.*;
import org.transportsgt.eventos.contexto.*;
import org.transportsgt.eventos.soporte.*;
import org.transportsgt.eventos.mensajeria.consumo.*;
import org.transportsgt.eventos.mensajeria.excepciones.*;
import org.transportsgt.eventos.mensajeria.publicacion.*;
import org.transportsgt.eventos.mensajeria.rabbit.*;
import org.transportsgt.eventos.payload.identidad.*;
import org.transportsgt.eventos.payload.flota.*;
import org.transportsgt.eventos.payload.viajes.*;
import org.transportsgt.eventos.payload.boletos.*;
import org.transportsgt.eventos.payload.cartera.*;
import org.transportsgt.eventos.payload.alquileres.*;
import org.transportsgt.eventos.payload.costos.*;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class TipoEventoTest {

    @Test
    void hay32TiposDeEvento() {
        assertThat(TipoEvento.values()).hasSize(32);
    }

    @Test
    void nombresYLlavesDeRuteoSonUnicos() {
        Set<String> nombres = Arrays.stream(TipoEvento.values()).map(TipoEvento::nombre).collect(Collectors.toSet());
        Set<String> llaves = Arrays.stream(TipoEvento.values()).map(TipoEvento::llaveRuteo).collect(Collectors.toSet());
        assertThat(nombres).hasSize(32);
        assertThat(llaves).hasSize(32);
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
        assertThat(contar(Productor.CARTERA)).isEqualTo(3);
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
