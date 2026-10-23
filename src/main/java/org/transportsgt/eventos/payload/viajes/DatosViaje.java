package org.transportsgt.eventos.payload;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * ViajeProgramado / ViajeActualizado: instantánea completa del viaje.
 */
public record DatosViaje(
        UUID idViaje,
        TipoViaje tipoViaje,
        EstadoViaje estado,
        UUID idRuta,
        UUID idSucursalOrigen,
        String nombreOrigen,
        String nombreDestino,
        UUID idAlquiler,
        OffsetDateTime fechaHoraSalida,
        OffsetDateTime fechaHoraLlegadaEstimada,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal precioBoleto,
        BusAsignado bus,
        ChoferAsignado chofer
) {
}
