package org.transportsgt.eventos.payload.flota;

import org.transportsgt.eventos.contrato.*;
import org.transportsgt.eventos.configuracion.*;
import org.transportsgt.eventos.contexto.*;
import org.transportsgt.eventos.soporte.*;
import org.transportsgt.eventos.mensajeria.consumo.*;
import org.transportsgt.eventos.mensajeria.excepciones.*;
import org.transportsgt.eventos.mensajeria.publicacion.*;
import org.transportsgt.eventos.mensajeria.rabbit.*;
import org.transportsgt.eventos.payload.identidad.*;
import org.transportsgt.eventos.payload.flota.*;
import org.transportsgt.eventos.payload.viajes.*;
import org.transportsgt.eventos.payload.boletos.*;
import org.transportsgt.eventos.payload.cartera.*;
import org.transportsgt.eventos.payload.alquileres.*;
import org.transportsgt.eventos.payload.costos.*;

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
