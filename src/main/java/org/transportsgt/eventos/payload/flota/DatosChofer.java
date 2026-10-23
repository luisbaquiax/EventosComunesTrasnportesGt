package org.transportsgt.eventos.payload;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * ChoferRegistrado / ChoferActualizado (misma estructura).
 */
public record DatosChofer(
        UUID idChofer,
        UUID idUsuario,
        UUID idSucursal,
        String nombreCompleto,
        String correo,
        String telefono,
        String numeroLicencia,
        TipoLicencia tipoLicencia,
        LocalDate fechaVencimientoLicencia,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal salarioBaseViaje,
        boolean activo
) {
}
