package org.transportsgt.eventos.payload;

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
