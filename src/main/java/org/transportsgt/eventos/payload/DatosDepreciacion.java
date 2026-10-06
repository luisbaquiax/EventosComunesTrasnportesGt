package org.transportsgt.eventos.payload;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DepreciacionCalculada.
 */
public record DatosDepreciacion(
        UUID idDepreciacion,
        UUID idViaje,
        UUID idBus,
        UUID idSucursal,
        BigDecimal distanciaKm,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal montoPorKm,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal monto,
        LocalDate fechaCalculo
) {
}
