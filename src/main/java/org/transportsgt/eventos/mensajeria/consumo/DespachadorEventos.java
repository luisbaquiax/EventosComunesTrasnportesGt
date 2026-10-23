package org.transportsgt.eventos.mensajeria.consumo;

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

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Consumidor: lee el mensaje, descarta duplicados con {@code evento_procesado} y llama al {@link ManejadorEvento}
 * correspondiente, todo en UNA transacción (el registro de procesado y el efecto de negocio van juntos).
 *
 * <ul>
 *   <li>Mensaje ilegible o de tipo desconocido, o {@link EventoNoRecuperableException}: va directo a la DLQ,
 *       sin reintentos (el mensaje venenoso nunca bloquea la cola).</li>
 *   <li>Cualquier otra excepción: se propaga y los reintentos con espera creciente del contenedor se encargan
 *       (configurados en {@code application.yml}); al agotarlos el mensaje va a la DLQ.</li>
 * </ul>
 */
public class DespachadorEventos {

    private static final Logger log = LoggerFactory.getLogger(DespachadorEventos.class);

    private final String consumidor;
    private final ConvertidorEventos convertidor;
    private final JdbcClient jdbc;
    private final TransactionTemplate tx;
    private final RabbitTemplate rabbit;
    private final Map<TipoEvento, ManejadorEvento<Object>> manejadores = new EnumMap<>(TipoEvento.class);

    @SuppressWarnings("unchecked")
    public DespachadorEventos(String consumidor, List<ManejadorEvento<?>> lista, ConvertidorEventos convertidor,
                              DataSource dataSource, PlatformTransactionManager transactionManager,
                              RabbitTemplate rabbit) {
        this.consumidor = consumidor;
        this.convertidor = convertidor;
        this.jdbc = JdbcClient.create(dataSource);
        this.tx = new TransactionTemplate(transactionManager);
        this.rabbit = rabbit;
        for (ManejadorEvento<?> manejador : lista) {
            ManejadorEvento<Object> previo = manejadores.put(manejador.tipo(), (ManejadorEvento<Object>) manejador);
            if (previo != null) {
                throw new IllegalStateException("Hay dos manejadores para " + manejador.tipo().nombre()
                        + ": " + previo.getClass().getName() + " y " + manejador.getClass().getName());
            }
        }
    }

    /**
     * @param cola    cola de la que viene el mensaje (para enviarlo a su DLQ si hace falta)
     * @param mensaje mensaje recibido
     */
    public void despachar(String cola, Message mensaje) {
        SobreEvento<Object> sobre;
        try {
            sobre = convertidor.leer(mensaje.getBody());
        } catch (EventoInvalidoException e) {
            enviarAFallidos(cola, mensaje, e.getMessage());
            return;
        }
        TipoEvento tipo = TipoEvento.deNombre(sobre.tipoEvento()).orElseThrow();
        ManejadorEvento<Object> manejador = manejadores.get(tipo);
        if (manejador == null) {
            log.warn("Evento {} ({}) sin manejador en {}: se ignora", sobre.idEvento(), sobre.tipoEvento(), consumidor);
            return;
        }
        ContextoCorrelacion.fijar(sobre.idCorrelacion());
        try {
            tx.executeWithoutResult(estado -> {
                if (!registrarProcesado(sobre.idEvento())) {
                    log.debug("Evento {} ya procesado por {}: se ignora", sobre.idEvento(), consumidor);
                    return;
                }
                manejador.manejar(sobre);
            });
        } catch (EventoNoRecuperableException e) {
            enviarAFallidos(cola, mensaje, e.getMessage());
        } finally {
            ContextoCorrelacion.limpiar();
        }
    }

    /**
     * Inserta en {@code evento_procesado}; {@code false} si ya estaba (evento duplicado).
     */
    private boolean registrarProcesado(UUID idEvento) {
        return jdbc.sql("INSERT INTO evento_procesado (id_evento, consumidor) VALUES (:id, :consumidor) "
                        + "ON CONFLICT DO NOTHING")
                .param("id", idEvento)
                .param("consumidor", consumidor)
                .update() == 1;
    }

    /**
     * Reenvía el mensaje original al exchange de fallidos, con la llave igual al nombre de la cola.
     */
    private void enviarAFallidos(String cola, Message original, String motivo) {
        log.error("Mensaje enviado a la DLQ de {}: {}", cola, motivo);
        Message copia = MessageBuilder.fromMessage(original)
                .setHeader("x-motivo-fallo", motivo == null ? "desconocido" : motivo)
                .build();
        rabbit.send(TopologiaEventos.EXCHANGE_DLX, cola, copia);
    }
}
