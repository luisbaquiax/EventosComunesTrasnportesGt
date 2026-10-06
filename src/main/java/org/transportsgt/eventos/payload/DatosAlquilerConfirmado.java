package org.transportsgt.eventos.payload;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * AlquilerConfirmado: contratación confirmada y pagada; trae todo lo necesario para crear el viaje.
 */
public record DatosAlquilerConfirmado(
        UUID idAlquiler,
        UUID idUsuarioCliente,
        String nombreCliente,
        String nit,
        UUID idSucursalOrigen,
        String origen,
        String destino,
        OffsetDateTime fechaHoraSalida,
        OffsetDateTime fechaHoraRetorno,
        OffsetDateTime fechaHoraLlegadaEstimada,
        boolean idaYVuelta,
        int numeroPasajeros,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal precioTotal,
        UUID idPago,
        LocalDate fechaPago
) {
}
