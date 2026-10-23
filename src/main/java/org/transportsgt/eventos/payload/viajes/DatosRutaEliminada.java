package org.transportsgt.eventos.payload;

import java.time.Instant;
import java.util.UUID;

/**
 * RutaEliminada.
 */
public record DatosRutaEliminada(
        UUID idRuta,
        Instant eliminadaEn
) {
}
