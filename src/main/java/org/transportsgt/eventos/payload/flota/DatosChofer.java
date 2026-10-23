package org.transportsgt.eventos.payload.flota;

import org.transportsgt.eventos.contrato.*;
import org.transportsgt.eventos.configuracion.*;
import org.transportsgt.eventos.contexto.*;
import org.transportsgt.eventos.soporte.*;
import org.transportsgt.eventos.mensajeria.consumo.*;
import org.transportsgt.eventos.mensajeria.excepciones.*;
import org.transportsgt.eventos.mensajeria.publicacion.*;
import org.transportsgt.eventos.mensajeria.rabbit.*;
import org.transportsgt.eventos.payload.identidad.*;
import org.transportsgt.eventos.payload.flota.*;
import org.transportsgt.eventos.payload.viajes.*;
import org.transportsgt.eventos.payload.boletos.*;
import org.transportsgt.eventos.payload.cartera.*;
import org.transportsgt.eventos.payload.alquileres.*;
import org.transportsgt.eventos.payload.costos.*;

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
