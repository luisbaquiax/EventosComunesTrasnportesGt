package org.transportsgt.eventos.mensajeria.excepciones;

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

/**
 * Un {@link ManejadorEvento} la lanza cuando reintentar no sirve (regla de negocio violada, dato imposible).
 * El mensaje va directo a la DLQ, sin reintentos, y la transacción del manejador se revierte.
 */
public class EventoNoRecuperableException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EventoNoRecuperableException(String mensaje) {
        super(mensaje);
    }

    public EventoNoRecuperableException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
