package org.transportsgt.eventos.payload;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * CarteraReembolsada.
 */
public record DatosCarteraReembolsada(
        UUID idMovimiento,
        UUID idCartera,
        UUID idUsuario,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal monto,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal saldoPosterior,
        TipoReferencia tipoReferencia,
        UUID idReferencia,
        String motivo
) {
}
