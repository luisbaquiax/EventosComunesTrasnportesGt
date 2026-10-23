package org.transportsgt.eventos.payload;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * TarifaDepreciacionCambiada.
 */
public record DatosTarifaDepreciacion(
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal montoPorKm,
        LocalDate vigenteDesde,
        UUID idUsuarioCambio
) {
}
