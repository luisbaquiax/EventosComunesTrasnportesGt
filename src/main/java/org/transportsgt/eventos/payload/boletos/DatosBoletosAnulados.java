package org.transportsgt.eventos.payload;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * BoletosAnulados: uno por compra afectada.
 */
public record DatosBoletosAnulados(
        UUID idCompra,
        UUID idViaje,
        String motivo,
        Canal canal,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal montoTotal,
        UUID idUsuarioComprador,
        UUID idPago,
        UUID idSucursalIngreso
) {
}
