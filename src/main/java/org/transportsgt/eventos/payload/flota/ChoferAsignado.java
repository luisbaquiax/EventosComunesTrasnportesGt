package org.transportsgt.eventos.payload;

import java.util.UUID;

/**
 * Chofer asignado a un viaje (null mientras el viaje esté PENDIENTE_ASIGNACION).
 */
public record ChoferAsignado(
        UUID idChofer,
        String nombreCompleto
) {
}
