package org.transportsgt.eventos.payload;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * LlegadaRegistrada (registro inmutable): lleva todo lo que Costos necesita.
 */
public record DatosLlegada(
        UUID idViaje,
        TipoViaje tipoViaje,
        UUID idAlquiler,
        UUID idRuta,
        UUID idSucursalOrigen,
        UUID idBus,
        UUID idSucursalBus,
        UUID idChofer,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal salarioBaseChofer,
        OffsetDateTime fechaHoraSalidaReal,
        OffsetDateTime fechaHoraLlegadaReal,
        BigDecimal kilometrajeInicial,
        BigDecimal kilometrajeFinal,
        BigDecimal distanciaRecorridaKm,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal gastoCombustibleTotal,
        UUID idUsuarioRegistro
) {
}
