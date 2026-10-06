package org.transportsgt.eventos.payload;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * SalidaRegistrada (registro inmutable).
 */
public record DatosSalida(
        UUID idViaje,
        UUID idBus,
        UUID idChofer,
        OffsetDateTime fechaHoraSalidaReal,
        BigDecimal kilometrajeInicial,
        UUID idUsuarioRegistro
) {
}
