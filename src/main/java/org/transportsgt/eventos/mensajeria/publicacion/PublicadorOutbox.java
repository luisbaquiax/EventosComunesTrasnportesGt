package org.transportsgt.eventos;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import tools.jackson.databind.JsonNode;

/**
 * Poller del outbox: lee los eventos pendientes, los publica en el exchange {@code transporte.eventos} y los
 * marca como publicados. Entrega <b>al menos una vez</b>: si el servicio cae entre enviar y marcar, el evento
 * se reenvía y el consumidor lo descarta por {@code evento_procesado}.
 *
 * <p>{@code FOR UPDATE SKIP LOCKED} permite varias instancias del mismo servicio sin publicar duplicados.
 * Las filas ya publicadas se borran con {@link #limpiarPublicados()} (por defecto, las de más de 24 h).
 *
 * <p>Requiere que la instancia esté siempre encendida (en Cloud Run: {@code --min-instances 1 --no-cpu-throttling}).
 */
public class PublicadorOutbox {

    private static final Logger log = LoggerFactory.getLogger(PublicadorOutbox.class);
    private static final int INTENTOS_ALERTA = 10;

    private static final String PENDIENTES = """
            SELECT id, tipo_evento, llave_ruteo, id_agregado, version_agregado, id_correlacion,
                   CAST(datos AS text) AS datos, creado_en
              FROM outbox_evento
             WHERE publicado_en IS NULL
             ORDER BY creado_en, id
             LIMIT :lote
               FOR UPDATE SKIP LOCKED
            """;

    private record Fila(UUID id, String tipoEvento, String llaveRuteo, UUID idAgregado, Long versionAgregado,
                        UUID idCorrelacion, String datos, OffsetDateTime creadoEn) {
    }

    private final JdbcClient jdbc;
    private final RabbitTemplate rabbit;
    private final ConvertidorEventos convertidor;
    private final TransactionTemplate tx;
    private final int lote;
    private final int horasRetencion;

    public PublicadorOutbox(DataSource dataSource, RabbitTemplate rabbit, ConvertidorEventos convertidor,
                            PlatformTransactionManager transactionManager, int lote, int horasRetencion) {
        this.jdbc = JdbcClient.create(dataSource);
        this.rabbit = rabbit;
        this.convertidor = convertidor;
        this.tx = new TransactionTemplate(transactionManager);
        this.lote = lote;
        this.horasRetencion = horasRetencion;
    }

    @Scheduled(fixedDelayString = "${eventos.outbox.intervalo-ms:1000}")
    public void publicarPendientes() {
        try {
            tx.executeWithoutResult(estado -> publicarLote());
        } catch (RuntimeException e) {
            // La base de datos o RabbitMQ pueden estar caídos un rato: se reintenta en el siguiente ciclo.
            log.warn("No se pudo procesar el outbox: {}", e.toString());
        }
    }

    private void publicarLote() {
        List<Fila> filas = jdbc.sql(PENDIENTES)
                .param("lote", lote)
                .query((rs, n) -> new Fila(
                        rs.getObject("id", UUID.class),
                        rs.getString("tipo_evento"),
                        rs.getString("llave_ruteo"),
                        rs.getObject("id_agregado", UUID.class),
                        rs.getObject("version_agregado", Long.class),
                        rs.getObject("id_correlacion", UUID.class),
                        rs.getString("datos"),
                        rs.getObject("creado_en", OffsetDateTime.class)))
                .list();
        for (Fila fila : filas) {
            try {
                rabbit.send(TopologiaEventos.EXCHANGE, fila.llaveRuteo(), construirMensaje(fila));
                jdbc.sql("UPDATE outbox_evento SET publicado_en = now(), intentos = intentos + 1 WHERE id = :id")
                        .param("id", fila.id())
                        .update();
            } catch (RuntimeException e) {
                // Se detiene el lote para conservar el orden; el resto se intenta en el siguiente ciclo.
                int intentos = jdbc.sql("UPDATE outbox_evento SET intentos = intentos + 1 WHERE id = :id RETURNING intentos")
                        .param("id", fila.id())
                        .query(Integer.class)
                        .single();
                if (intentos >= INTENTOS_ALERTA) {
                    log.error("El evento {} ({}) lleva {} intentos sin publicarse", fila.id(), fila.tipoEvento(), intentos, e);
                } else {
                    log.warn("No se pudo publicar el evento {} ({}): {}", fila.id(), fila.tipoEvento(), e.toString());
                }
                return;
            }
        }
        if (!filas.isEmpty()) {
            log.debug("Outbox: {} evento(s) publicados", filas.size());
        }
    }

    private Message construirMensaje(Fila fila) {
        TipoEvento tipo = TipoEvento.deNombre(fila.tipoEvento())
                .orElseThrow(() -> new IllegalStateException("Tipo de evento desconocido en el outbox: " + fila.tipoEvento()));
        JsonNode datos = convertidor.leerArbol(fila.datos());
        SobreEvento<JsonNode> sobre = new SobreEvento<>(
                fila.id(),
                tipo.nombre(),
                tipo.versionContrato(),
                fila.creadoEn().toInstant(),
                tipo.productor().nombreServicio(),
                fila.idAgregado(),
                fila.versionAgregado(),
                fila.idCorrelacion(),
                datos);
        return MessageBuilder.withBody(convertidor.serializar(sobre))
                .setContentType("application/json")
                .setContentEncoding("UTF-8")
                .setMessageId(fila.id().toString())
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .setHeader("tipoEvento", tipo.nombre())
                .build();
    }

    /**
     * Borra del outbox los eventos ya publicados con más de {@code horasRetencion} horas.
     */
    @Scheduled(cron = "${eventos.outbox.limpieza-cron:0 30 3 * * *}")
    public void limpiarPublicados() {
        try {
            int borrados = jdbc.sql("DELETE FROM outbox_evento WHERE publicado_en < now() - make_interval(hours => :horas)")
                    .param("horas", horasRetencion)
                    .update();
            if (borrados > 0) {
                log.info("Outbox: {} evento(s) publicados eliminados", borrados);
            }
        } catch (RuntimeException e) {
            log.warn("No se pudo limpiar el outbox: {}", e.toString());
        }
    }
}
