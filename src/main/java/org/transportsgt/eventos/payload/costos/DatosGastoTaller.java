package org.transportsgt.eventos.payload;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * GastoTallerRegistrado.
 */
public record DatosGastoTaller(
        UUID idGasto,
        UUID idBus,
        UUID idSucursal,
        LocalDate fechaServicio,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal total,
        List<LineaTaller> lineas
) {
}
