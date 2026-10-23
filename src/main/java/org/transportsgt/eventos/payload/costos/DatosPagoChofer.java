package org.transportsgt.eventos.payload;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * PagoChoferRegistrado: el salario del chofer cuenta como gasto.
 */
public record DatosPagoChofer(
        UUID idPago,
        UUID idViaje,
        UUID idChofer,
        UUID idSucursal,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal salarioBase,
        BigDecimal multiplicador,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal monto,
        LocalDate fechaPago
) {
}
