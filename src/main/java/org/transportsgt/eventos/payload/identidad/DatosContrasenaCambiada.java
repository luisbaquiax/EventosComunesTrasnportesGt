package org.transportsgt.eventos.payload;

import java.time.Instant;
import java.util.UUID;

/**
 * ContrasenaCambiada.
 */
public record DatosContrasenaCambiada(
        UUID idUsuario,
        String correo,
        String nombreCompleto,
        MotivoContrasena motivo,
        Instant cambiadaEn
) {
}
