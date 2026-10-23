package org.transportsgt.eventos;

import java.time.Instant;
import java.util.UUID;

/**
 * Sobre común de todo evento (catálogo §1.1). Solo cambia {@code datos}.
 *
 * @param idEvento          clave de idempotencia; único por evento
 * @param tipoEvento        nombre del tipo, p. ej. {@code ViajeProgramado}
 * @param versionEvento     versión del contrato (solo se agregan campos)
 * @param ocurridoEn        hora técnica del evento (no es la fecha de negocio)
 * @param servicioProductor p. ej. {@code servicio-viajes}
 * @param idAgregado        id de la entidad a la que pertenece el evento
 * @param versionAgregado   versión de la entidad (para ignorar eventos viejos o duplicados)
 * @param idCorrelacion     id de la operación de negocio, propagado a los eventos derivados
 * @param datos             payload (ver paquete {@code payload})
 */
public record SobreEvento<T>(
        UUID idEvento,
        String tipoEvento,
        int versionEvento,
        Instant ocurridoEn,
        String servicioProductor,
        UUID idAgregado,
        Long versionAgregado,
        UUID idCorrelacion,
        T datos) {

    /**
     * Protección contra desorden en eventos instantánea (catálogo §1.5): el evento reemplaza la proyección
     * solo si su versión es mayor que la guardada.
     *
     * @param versionGuardada versión actual de la proyección local; {@code null} si aún no existe
     */
    public boolean reemplaza(Long versionGuardada) {
        if (versionGuardada == null) {
            return true;
        }
        return versionAgregado != null && versionAgregado > versionGuardada;
    }
}
