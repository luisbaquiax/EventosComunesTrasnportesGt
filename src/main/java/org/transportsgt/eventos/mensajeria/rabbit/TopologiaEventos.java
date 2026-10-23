package org.transportsgt.eventos.mensajeria.rabbit;

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
 * Nombres de la topología RabbitMQ.
 */
public final class TopologiaEventos {

    /**
     * Exchange topic único por donde viajan todos los eventos.
     */
    public static final String EXCHANGE = "transporte.eventos";

    /**
     * Exchange (direct) de mensajes fallidos.
     */
    public static final String EXCHANGE_DLX = "transporte.eventos.dlx";

    private TopologiaEventos() {
    }

    /**
     * {@code boletos.viajes.q}: cola del consumidor {@code boletos} para lo que publica {@code viajes}.
     */
    public static String cola(String consumidor, Productor productor) {
        return consumidor + "." + productor.prefijo() + ".q";
    }

    /**
     * {@code boletos.viajes.dlq}.
     */
    public static String colaFallidos(String consumidor, Productor productor) {
        return consumidor + "." + productor.prefijo() + ".dlq";
    }
}
