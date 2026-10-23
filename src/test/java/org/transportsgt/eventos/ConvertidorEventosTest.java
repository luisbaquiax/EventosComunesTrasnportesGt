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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class ConvertidorEventosTest {

    private final ConvertidorEventos convertidor = new ConvertidorEventos();

    private static SobreEvento<Object> sobre(TipoEvento tipo, Object datos) {
        return new SobreEvento<>(UUID.randomUUID(), tipo.nombre(), 1, Instant.parse("2026-10-20T14:32:10Z"),
                tipo.productor().nombreServicio(), UUID.randomUUID(), 3L, UUID.randomUUID(), datos);
    }

    @Test
    void ventaDeBoletosIdaYVuelta() {
        DatosVentaBoletos datos = new DatosVentaBoletos(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                Canal.EN_LINEA, List.of(12, 13), new BigDecimal("85.00"), new BigDecimal("170.00"),
                UUID.randomUUID(), "CF", "Ana", null, LocalDate.of(2026, 10, 20), UUID.randomUUID(), null);
        SobreEvento<Object> original = sobre(TipoEvento.VENTA_BOLETOS_REGISTRADA, datos);

        SobreEvento<Object> leido = convertidor.leer(convertidor.serializar(original));

        assertThat(leido.idEvento()).isEqualTo(original.idEvento());
        assertThat(leido.versionAgregado()).isEqualTo(3L);
        assertThat(leido.datos()).isEqualTo(datos);
    }

    @Test
    void elDineroViajaComoTextoConDosDecimales() {
        DatosVentaBoletos datos = new DatosVentaBoletos(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                Canal.MOSTRADOR, List.of(1), new BigDecimal("85.00"), new BigDecimal("85.00"),
                null, "CF", "Cliente", null, LocalDate.of(2026, 10, 20), UUID.randomUUID(), UUID.randomUUID());
        String json = new String(convertidor.serializar(sobre(TipoEvento.VENTA_BOLETOS_REGISTRADA, datos)),
                StandardCharsets.UTF_8);
        assertThat(json).contains("\"montoTotal\":\"85.00\"");
        assertThat(json).contains("\"fechaPago\":\"2026-10-20\"");
        assertThat(json).contains("\"ocurridoEn\":\"2026-10-20T14:32:10Z\"");
    }

    @Test
    void viajeConBusNuloMientrasEstaPendienteDeAsignacion() {
        DatosViaje datos = new DatosViaje(UUID.randomUUID(), TipoViaje.ALQUILER_PRIVADO, EstadoViaje.PENDIENTE_ASIGNACION,
                null, UUID.randomUUID(), "Xela", "Cobán", UUID.randomUUID(),
                OffsetDateTime.parse("2026-11-10T07:00:00-06:00"), OffsetDateTime.parse("2026-11-12T20:00:00-06:00"),
                null, null, null);
        SobreEvento<Object> leido = convertidor.leer(convertidor.serializar(sobre(TipoEvento.VIAJE_PROGRAMADO, datos)));
        DatosViaje viaje = (DatosViaje) leido.datos();
        assertThat(viaje.bus()).isNull();
        assertThat(viaje.estado()).isEqualTo(EstadoViaje.PENDIENTE_ASIGNACION);
        // misma instantánea aunque la zona horaria pueda normalizarse
        assertThat(viaje.fechaHoraSalida().toInstant()).isEqualTo(datos.fechaHoraSalida().toInstant());
    }

    @Test
    void ignoraCamposDesconocidosParaCompatibilidadHaciaAtras() {
        String json = """
                {"idEvento":"%s","tipoEvento":"ViajeProgramado","versionEvento":2,"ocurridoEn":"2026-10-20T14:32:10Z",
                 "servicioProductor":"servicio-viajes","idAgregado":"%s","versionAgregado":1,"idCorrelacion":null,
                 "datos":{"idViaje":"%s","tipoViaje":"REGULAR","estado":"PROGRAMADO","campoNuevo":"x",
                          "bus":{"idBus":"%s","placa":"C123ABC","capacidad":45,"otro":1}}}
                """.formatted(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        SobreEvento<Object> leido = convertidor.leer(json.getBytes(StandardCharsets.UTF_8));
        BusAsignado bus = ((DatosViaje) leido.datos()).bus();
        assertThat(bus.capacidad()).isEqualTo(45);
    }

    @Test
    void jsonInvalidoTipoDesconocidoOSinDatosSonEventoInvalido() {
        assertThatThrownBy(() -> convertidor.leer("no es json".getBytes(StandardCharsets.UTF_8)))
                .isInstanceOf(EventoInvalidoException.class);
        String desconocido = """
                {"idEvento":"%s","tipoEvento":"Inventado","datos":{}}""".formatted(UUID.randomUUID());
        assertThatThrownBy(() -> convertidor.leer(desconocido.getBytes(StandardCharsets.UTF_8)))
                .isInstanceOf(EventoInvalidoException.class).hasMessageContaining("desconocido");
        String sinDatos = """
                {"idEvento":"%s","tipoEvento":"BusRegistrado"}""".formatted(UUID.randomUUID());
        assertThatThrownBy(() -> convertidor.leer(sinDatos.getBytes(StandardCharsets.UTF_8)))
                .isInstanceOf(EventoInvalidoException.class).hasMessageContaining("datos");
    }

    @Test
    void elTokenDeContrasenaNoApareceEnToString() {
        DatosEnlaceContrasena datos = new DatosEnlaceContrasena(UUID.randomUUID(), "a@b.com", "Ana",
                PropositoContrasena.RESTABLECIMIENTO, "TOKEN-SECRETO", Instant.now());
        assertThat(datos.toString()).doesNotContain("TOKEN-SECRETO");
    }

    @Test
    void reemplazaSoloSiLaVersionEsMayor() {
        SobreEvento<Object> v3 = sobre(TipoEvento.BUS_ACTUALIZADO, new Object());
        assertThat(v3.reemplaza(null)).isTrue();
        assertThat(v3.reemplaza(2L)).isTrue();
        assertThat(v3.reemplaza(3L)).isFalse();
        assertThat(v3.reemplaza(4L)).isFalse();
    }
}
