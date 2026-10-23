package org.transportsgt.eventos.payload;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record DatosAlquilerCancelado(
        UUID idAlquiler,
        UUID idUsuarioCliente,
        String nombreCliente,
        String nit,
        UUID idSucursalOrigen,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal precioTotal,
        UUID idPago,
        String motivo,
        Instant canceladoEn
) {
}
