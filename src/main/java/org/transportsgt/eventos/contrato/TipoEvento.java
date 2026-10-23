package org.transportsgt.eventos.contrato;

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


import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Los 31 tipos de evento del catálogo (catalogo-eventos-es-v3.md). Es la fuente de verdad en código:
 * nombre, productor, llave de ruteo y clase del payload. Si el catálogo cambia, se cambia aquí.
 */
public enum TipoEvento {

    USUARIO_CREADO("UsuarioCreado", Productor.IDENTIDAD, "identidad.usuario.creado", DatosUsuario.class),
    USUARIO_ACTUALIZADO("UsuarioActualizado", Productor.IDENTIDAD, "identidad.usuario.actualizado", DatosUsuario.class),
    SUCURSAL_CREADA("SucursalCreada", Productor.IDENTIDAD, "identidad.sucursal.creada", DatosSucursal.class),
    SUCURSAL_ACTUALIZADA("SucursalActualizada", Productor.IDENTIDAD, "identidad.sucursal.actualizada", DatosSucursal.class),
    ENLACE_CONTRASENA_EMITIDO("EnlaceContrasenaEmitido", Productor.IDENTIDAD, "identidad.usuario.enlace-contrasena-emitido", DatosEnlaceContrasena.class),
    CONTRASENA_CAMBIADA("ContrasenaCambiada", Productor.IDENTIDAD, "identidad.usuario.contrasena-cambiada", DatosContrasenaCambiada.class),
    BUS_REGISTRADO("BusRegistrado", Productor.FLOTA, "flota.bus.registrado", DatosBus.class),
    BUS_ACTUALIZADO("BusActualizado", Productor.FLOTA, "flota.bus.actualizado", DatosBus.class),
    CHOFER_REGISTRADO("ChoferRegistrado", Productor.FLOTA, "flota.chofer.registrado", DatosChofer.class),
    CHOFER_ACTUALIZADO("ChoferActualizado", Productor.FLOTA, "flota.chofer.actualizado", DatosChofer.class),
    RUTA_CREADA("RutaCreada", Productor.VIAJES, "viajes.ruta.creada", DatosRuta.class),
    RUTA_ACTUALIZADA("RutaActualizada", Productor.VIAJES, "viajes.ruta.actualizada", DatosRuta.class),
    RUTA_ELIMINADA("RutaEliminada", Productor.VIAJES, "viajes.ruta.eliminada", DatosRutaEliminada.class),
    VIAJE_PROGRAMADO("ViajeProgramado", Productor.VIAJES, "viajes.viaje.programado", DatosViaje.class),
    VIAJE_ACTUALIZADO("ViajeActualizado", Productor.VIAJES, "viajes.viaje.actualizado", DatosViaje.class),
    VIAJE_ELIMINADO("ViajeEliminado", Productor.VIAJES, "viajes.viaje.eliminado", DatosViajeEliminado.class),
    SALIDA_REGISTRADA("SalidaRegistrada", Productor.VIAJES, "viajes.viaje.salida-registrada", DatosSalida.class),
    LLEGADA_REGISTRADA("LlegadaRegistrada", Productor.VIAJES, "viajes.viaje.llegada-registrada", DatosLlegada.class),
    VENTA_BOLETOS_REGISTRADA("VentaBoletosRegistrada", Productor.BOLETOS, "boletos.venta.registrada", DatosVentaBoletos.class),
    BOLETOS_ANULADOS("BoletosAnulados", Productor.BOLETOS, "boletos.venta.anulada", DatosBoletosAnulados.class),
    CARTERA_RECARGADA("CarteraRecargada", Productor.CARTERA, "cartera.cartera.recargada", DatosCarteraRecargada.class),
    CARTERA_REEMBOLSADA("CarteraReembolsada", Productor.CARTERA, "cartera.cartera.reembolsada", DatosCarteraReembolsada.class),
    PERFIL_CLIENTE_ACTUALIZADO("PerfilClienteActualizado", Productor.CARTERA, "cartera.cliente.actualizado", DatosPerfilClienteActualizado.class),
    ALQUILER_SOLICITADO("AlquilerSolicitado", Productor.ALQUILERES, "alquileres.alquiler.solicitado", DatosAlquilerSolicitado.class),
    ALQUILER_COTIZADO("AlquilerCotizado", Productor.ALQUILERES, "alquileres.alquiler.cotizado", DatosAlquilerCotizado.class),
    ALQUILER_CONFIRMADO("AlquilerConfirmado", Productor.ALQUILERES, "alquileres.alquiler.confirmado", DatosAlquilerConfirmado.class),
    ALQUILER_CANCELADO("AlquilerCancelado", Productor.ALQUILERES, "alquileres.alquiler.cancelado", DatosAlquilerCancelado.class),
    GASTO_COMBUSTIBLE_REGISTRADO("GastoCombustibleRegistrado", Productor.COSTOS, "costos.combustible.registrado", DatosGastoCombustible.class),
    DEPRECIACION_CALCULADA("DepreciacionCalculada", Productor.COSTOS, "costos.depreciacion.calculada", DatosDepreciacion.class),
    GASTO_TALLER_REGISTRADO("GastoTallerRegistrado", Productor.COSTOS, "costos.taller.registrado", DatosGastoTaller.class),
    TARIFA_DEPRECIACION_CAMBIADA("TarifaDepreciacionCambiada", Productor.COSTOS, "costos.depreciacion.tarifa-cambiada", DatosTarifaDepreciacion.class),
    PAGO_CHOFER_REGISTRADO("PagoChoferRegistrado", Productor.COSTOS, "costos.pago-chofer.registrado", DatosPagoChofer.class);

    private static final Map<String, TipoEvento> POR_NOMBRE = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(TipoEvento::nombre, Function.identity()));

    private final String nombre;
    private final Productor productor;
    private final String llaveRuteo;
    private final Class<?> clasePayload;

    TipoEvento(String nombre, Productor productor, String llaveRuteo, Class<?> clasePayload) {
        this.nombre = nombre;
        this.productor = productor;
        this.llaveRuteo = llaveRuteo;
        this.clasePayload = clasePayload;
    }

    /** Valor del campo {@code tipoEvento} del sobre, p. ej. {@code ViajeProgramado}. */
    public String nombre() { return nombre; }

    public Productor productor() { return productor; }

    /** Llave de ruteo {@code {servicio}.{agregado}.{acción}}. */
    public String llaveRuteo() { return llaveRuteo; }

    public Class<?> clasePayload() { return clasePayload; }

    /** Segundo segmento de la llave de ruteo (usuario, viaje, venta...). Se guarda como {@code tipo_agregado}. */
    public String tipoAgregado() { return llaveRuteo.split("\\.")[1]; }

    /** Versión del contrato (solo se agregan campos; un cambio incompatible crea un tipo nuevo). */
    public int versionContrato() { return 1; }

    public static Optional<TipoEvento> deNombre(String nombre) {
        return Optional.ofNullable(POR_NOMBRE.get(nombre));
    }
}
