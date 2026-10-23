package org.transportsgt.eventos.payload;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * GastoCombustibleRegistrado.
 */
public record DatosGastoCombustible(
        UUID idGasto,
        UUID idViaje,
        UUID idBus,
        UUID idSucursal,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal monto,
        LocalDate fechaGasto
) {
}
