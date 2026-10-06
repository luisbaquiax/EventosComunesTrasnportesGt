package org.transportsgt.eventos.payload;

import java.util.UUID;

/**
 * UsuarioCreado / UsuarioActualizado (misma estructura).
 */
public record DatosUsuario(
        UUID idUsuario,
        String correo,
        String nombreCompleto,
        Rol rol,
        UUID idSucursal,
        boolean activo
) {
}
