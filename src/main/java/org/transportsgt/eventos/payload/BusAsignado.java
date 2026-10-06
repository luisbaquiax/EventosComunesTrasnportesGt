package org.transportsgt.eventos.payload;

import java.util.UUID;

/**
 * Bus asignado a un viaje (null mientras el viaje esté PENDIENTE_ASIGNACION).
 */
public record BusAsignado(
        UUID idBus,
        String placa,
        int capacidad
) {
}
