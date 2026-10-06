package org.transportsgt.eventos;

import java.util.UUID;

import org.slf4j.MDC;

/**
 * Propaga el {@code idCorrelacion} de la petición original a los eventos derivados.
 * Un filtro HTTP lo fija con {@link #fijar} al entrar la petición y {@link #limpiar} al salir;
 * un manejador de eventos lo fija con el {@code idCorrelacion} del evento recibido.
 */
public final class ContextoCorrelacion {

    public static final String CLAVE_MDC = "idCorrelacion";

    private ContextoCorrelacion() {
    }

    public static void fijar(UUID idCorrelacion) {
        if (idCorrelacion == null) {
            MDC.remove(CLAVE_MDC);
        } else {
            MDC.put(CLAVE_MDC, idCorrelacion.toString());
        }
    }

    /**
     * Correlación actual, o {@code null} si no hay.
     */
    public static UUID actual() {
        String valor = MDC.get(CLAVE_MDC);
        if (valor == null) {
            return null;
        }
        try {
            return UUID.fromString(valor);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static void limpiar() {
        MDC.remove(CLAVE_MDC);
    }
}
