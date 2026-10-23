package org.transportsgt.eventos.payload;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Línea de un gasto de taller.
 */
public record LineaTaller(
        UUID idLinea,
        UUID idCategoria,
        String nombreCategoria,
        String descripcion,
        TipoLinea tipo,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal monto
) {
}
