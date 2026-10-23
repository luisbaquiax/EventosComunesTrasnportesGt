package org.transportsgt.eventos.payload;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * AlquilerSolicitado.
 */
public record DatosAlquilerSolicitado(
        UUID idAlquiler,
        UUID idUsuarioCliente,
        String nombreCliente,
        String nit,
        UUID idSucursalOrigen,
        String origen,
        String destino,
        OffsetDateTime fechaHoraSalida,
        OffsetDateTime fechaHoraRetorno,
        int numeroPasajeros,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal precioEstimado
) {
}
