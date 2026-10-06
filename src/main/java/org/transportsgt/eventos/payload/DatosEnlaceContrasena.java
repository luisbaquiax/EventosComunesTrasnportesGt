package org.transportsgt.eventos.payload;

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
