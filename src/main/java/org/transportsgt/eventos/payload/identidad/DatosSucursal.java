package org.transportsgt.eventos.payload;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * SucursalCreada / SucursalActualizada (misma estructura).
 */
public record DatosSucursal(
        UUID idSucursal,
        String nombre,
        String departamento,
        String direccion,
        BigDecimal latitud,
        BigDecimal longitud,
        boolean activa
) {
}
