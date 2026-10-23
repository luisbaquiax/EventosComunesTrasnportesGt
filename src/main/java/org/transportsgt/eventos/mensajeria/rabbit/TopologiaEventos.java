package org.transportsgt.eventos;

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
