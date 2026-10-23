package org.transportsgt.eventos.payload;

import java.time.Instant;
import java.util.UUID;

/**
 * ViajeEliminado.
 */
public record DatosViajeEliminado(
        UUID idViaje,
        TipoViaje tipoViaje,
        UUID idAlquiler,
        Instant eliminadoEn,
        UUID idUsuarioEliminador
) {
}
