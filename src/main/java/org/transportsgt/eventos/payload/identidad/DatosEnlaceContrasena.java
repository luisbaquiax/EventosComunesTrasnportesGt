package org.transportsgt.eventos.payload.identidad;

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

import java.time.Instant;
import java.util.UUID;

/**
 * EnlaceContrasenaEmitido. Lleva un SECRETO (token): toString() lo oculta; nunca registrar este payload.
 */
public record DatosEnlaceContrasena(
        UUID idUsuario,
        String correo,
        String nombreCompleto,
        PropositoContrasena proposito,
        String token,
        Instant expiraEn
) {
    /**
     * Oculta el token: este payload nunca debe aparecer en los logs.
     */
    @Override
    public String toString() {
        return "DatosEnlaceContrasena[idUsuario=" + idUsuario + ", proposito=" + proposito + ", token=***]";
    }
}
