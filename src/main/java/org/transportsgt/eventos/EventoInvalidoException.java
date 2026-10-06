package org.transportsgt.eventos;

/**
 * El mensaje no se puede interpretar (JSON inválido, tipo desconocido, payload vacío).
 */
public class EventoInvalidoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EventoInvalidoException(String mensaje) {
        super(mensaje);
    }

    public EventoInvalidoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
