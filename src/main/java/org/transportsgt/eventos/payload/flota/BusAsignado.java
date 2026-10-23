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
 * Bus asignado a un viaje (null mientras el viaje esté PENDIENTE_ASIGNACION).
 */
public record BusAsignado(
        UUID idBus,
        String placa,
        int capacidad
) {
}
