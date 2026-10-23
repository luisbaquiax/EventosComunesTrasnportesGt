package org.transportsgt.eventos;

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
