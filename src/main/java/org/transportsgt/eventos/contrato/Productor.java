package org.transportsgt.eventos.contrato;

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
 * Servicios que publican eventos. {@code prefijo} es el primer segmento de la llave de ruteo.
 */
public enum Productor {
    IDENTIDAD("identidad", "servicio-identidad"),
    FLOTA("flota", "servicio-flota"),
    VIAJES("viajes", "servicio-viajes"),
    BOLETOS("boletos", "servicio-boletos"),
    CARTERA("cartera", "servicio-clientes"),// Clientes y Cartera publica con el prefijo "cartera"
    ALQUILERES("alquileres", "servicio-alquileres"),
    COSTOS("costos", "servicio-costos");

    private final String prefijo;
    private final String nombreServicio;

    Productor(String prefijo, String nombreServicio) {
        this.prefijo = prefijo;
        this.nombreServicio = nombreServicio;
    }

    public String prefijo() {
        return prefijo;
    }

    /**
     * Valor del campo {@code servicioProductor} del sobre.
     */
    public String nombreServicio() {
        return nombreServicio;
    }
}
