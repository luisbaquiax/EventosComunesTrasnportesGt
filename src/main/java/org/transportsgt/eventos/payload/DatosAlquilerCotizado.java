package org.transportsgt.eventos.payload;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * AlquilerCotizado.
 */
public record DatosAlquilerCotizado(
        UUID idAlquiler,
        UUID idUsuarioCliente,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal precioFinal,
        OffsetDateTime fechaHoraLlegadaEstimada,
        UUID idUsuarioCotizador
) {
}
