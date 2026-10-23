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

import static org.assertj.core.api.Assertions.assertThat;

class NitTest {

    @Test
    void normalizaMayusculasGuionesYEspacios() {
        assertThat(Nit.normalizar("1234567-k")).isEqualTo("1234567K");
        assertThat(Nit.normalizar("  12 34-567 K ")).isEqualTo("1234567K");
        assertThat(Nit.normalizar("c/f")).isEqualTo("CF");
    }

    @Test
    void nuloOVacioDevuelveNulo() {
        assertThat(Nit.normalizar(null)).isNull();
        assertThat(Nit.normalizar("  - ")).isNull();
    }

    @Test
    void esIdempotente() {
        String una = Nit.normalizar("1234567-k");
        assertThat(Nit.normalizar(una)).isEqualTo(una);
    }

    @Test
    void consumidorFinalSeDetectaConCualquierEscritura() {
        assertThat(Nit.esConsumidorFinal("cf")).isTrue();
        assertThat(Nit.esConsumidorFinal("C/F")).isTrue();
        assertThat(Nit.esConsumidorFinal("1234567K")).isFalse();
    }

    @Test
    void unNitDeClienteNoPuedeSerCfNiTenerFormatoInvalido() {
        assertThat(Nit.esNitDeCliente("1234567-K")).isTrue();
        assertThat(Nit.esNitDeCliente("CF")).isFalse();
        assertThat(Nit.esNitDeCliente("1")).isFalse();
        assertThat(Nit.esNitDeCliente("12#45")).isFalse();
        assertThat(Nit.esNitDeCliente(null)).isFalse();
    }
}
