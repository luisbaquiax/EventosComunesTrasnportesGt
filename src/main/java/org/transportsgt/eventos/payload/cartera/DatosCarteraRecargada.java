package org.transportsgt.eventos.payload;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * CarteraRecargada.
 */
public record DatosCarteraRecargada(
        UUID idMovimiento,
        UUID idCartera,
        UUID idUsuario,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal monto,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal saldoPosterior,
        LocalDate fechaRecarga
) {
}
