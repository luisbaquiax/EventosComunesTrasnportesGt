package org.transportsgt.eventos.contexto;

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
