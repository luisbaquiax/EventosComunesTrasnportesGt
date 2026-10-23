package org.transportsgt.eventos.payload;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * VentaBoletosRegistrada: una compra = un evento con varios asientos.
 */
public record DatosVentaBoletos(
        UUID idCompra,
        UUID idViaje,
        UUID idRuta,
        Canal canal,
        List<Integer> asientos,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal precioUnitario,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal montoTotal,
        UUID idUsuarioComprador,
        String nitComprador,
        String nombreComprador,
        UUID idPago,
        LocalDate fechaPago,
        UUID idSucursalIngreso,
        UUID idUsuarioVendedor
) {
}
