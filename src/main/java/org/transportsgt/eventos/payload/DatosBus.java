package org.transportsgt.eventos.payload;

import java.util.UUID;

/**
 * BusRegistrado / BusActualizado (misma estructura).
 */
public record DatosBus(
        UUID idBus,
        UUID idSucursal,
        String placa,
        String marca,
        String modelo,
        int anioFabricacion,
        int capacidadPasajeros,
        EstadoOperativo estadoOperativo,
        boolean activo
) {
}
