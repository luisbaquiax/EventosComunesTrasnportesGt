package org.transportsgt.eventos.payload.cartera;

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
 * PerfilClienteActualizado: instantánea del perfil comercial del cliente.
 * Se publica al crear el cliente (nit nulo) y cada vez que cambian nombre, NIT o activo.
 * El NIT viaja normalizado (ver {@link org.transportsgt.eventos.Nit}).
 */
public record DatosPerfilClienteActualizado(
        UUID idUsuario,
        String nit,
        String nombreCompleto,
        boolean activo
) {
}
