package org.transportsgt.eventos.payload;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * RutaCreada / RutaActualizada (misma estructura).
 */
public record DatosRuta(
        UUID idRuta,
        UUID idSucursalOrigen,
        UUID idSucursalDestino,
        BigDecimal distanciaKm,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal precioBoleto
) {
}
