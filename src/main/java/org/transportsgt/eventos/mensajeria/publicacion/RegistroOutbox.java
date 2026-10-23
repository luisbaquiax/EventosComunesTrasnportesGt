package org.transportsgt.eventos;

import java.sql.Types;
import java.util.UUID;
import javax.sql.DataSource;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Productor: guarda el evento en {@code outbox_evento} dentro de la MISMA transacción que el cambio de negocio.
 * Un {@link PublicadorOutbox} lo envía después a RabbitMQ. Garantiza que no haya cambio sin evento.
 *
 * <pre>{@code
 * @Transactional
 * public void programar(...) {
 *     viaje = repositorio.save(...);
 *     outbox.registrar(TipoEvento.VIAJE_PROGRAMADO, viaje.getId(), viaje.getVersion(), datos);
 * }
 * }</pre>
 */
public class RegistroOutbox {

    private static final String INSERTAR = """
            INSERT INTO outbox_evento
                (id, tipo_agregado, id_agregado, tipo_evento, llave_ruteo, version_agregado, id_correlacion, datos)
            VALUES
                (:id, :tipoAgregado, :idAgregado, :tipoEvento, :llaveRuteo, :versionAgregado, :idCorrelacion,
                 CAST(:datos AS jsonb))
            """;

    private final JdbcClient jdbc;
    private final ConvertidorEventos convertidor;

    public RegistroOutbox(DataSource dataSource, ConvertidorEventos convertidor) {
        this.jdbc = JdbcClient.create(dataSource);
        this.convertidor = convertidor;
    }

    /**
     * Usa el {@code idCorrelacion} del contexto actual (ver {@link ContextoCorrelacion}).
     */
    public UUID registrar(TipoEvento tipo, UUID idAgregado, Long versionAgregado, Object datos) {
        return registrar(tipo, idAgregado, versionAgregado, ContextoCorrelacion.actual(), datos);
    }

    /**
     * @return el {@code idEvento} generado
     * @throws IllegalStateException    si no hay una transacción activa (el outbox solo tiene sentido dentro de una)
     * @throws IllegalArgumentException si {@code datos} no es del tipo de payload del evento
     */
    public UUID registrar(TipoEvento tipo, UUID idAgregado, Long versionAgregado, UUID idCorrelacion, Object datos) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new IllegalStateException(
                    "RegistroOutbox.registrar debe llamarse dentro de la transacción del cambio de negocio");
        }
        if (!tipo.clasePayload().isInstance(datos)) {
            throw new IllegalArgumentException("El payload de " + tipo.nombre() + " debe ser "
                    + tipo.clasePayload().getSimpleName() + " y no "
                    + (datos == null ? "null" : datos.getClass().getSimpleName()));
        }
        UUID idEvento = UUID.randomUUID();
        jdbc.sql(INSERTAR)
                .param("id", idEvento)
                .param("tipoAgregado", tipo.tipoAgregado())
                .param("idAgregado", idAgregado)
                .param("tipoEvento", tipo.nombre())
                .param("llaveRuteo", tipo.llaveRuteo())
                .param("versionAgregado", versionAgregado, Types.BIGINT)
                .param("idCorrelacion", idCorrelacion, Types.OTHER)
                .param("datos", convertidor.aJson(datos))
                .update();
        return idEvento;
    }
}
