package org.transportsgt.eventos;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Configuración de la librería. Se activa con {@link EnableEventosComun} en la clase principal del servicio.
 *
 * <p>Propiedades:
 * <ul>
 *   <li>{@code eventos.servicio} (obligatoria): nombre corto del servicio, p. ej. {@code boletos}. Es el prefijo de
 *       sus colas y el valor de {@code evento_procesado.consumidor}.</li>
 *   <li>{@code eventos.outbox.intervalo-ms} (1000), {@code eventos.outbox.lote} (50),
 *       {@code eventos.outbox.retencion-horas} (24), {@code eventos.outbox.limpieza-cron}.</li>
 * </ul>
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Configuration(proxyBeanMethods = false)
@EnableScheduling
@EnableRabbit
public class EventosComunConfiguracion {

    @Bean
    public ConvertidorEventos convertidorEventos() {
        return new ConvertidorEventos();
    }

    @Bean
    public RegistroOutbox registroOutbox(DataSource dataSource, ConvertidorEventos convertidor) {
        return new RegistroOutbox(dataSource, convertidor);
    }

    @Bean
    public PublicadorOutbox publicadorOutbox(DataSource dataSource, RabbitTemplate rabbit,
                                             ConvertidorEventos convertidor,
                                             PlatformTransactionManager transactionManager,
                                             @Value("${eventos.outbox.lote:50}") int lote,
                                             @Value("${eventos.outbox.retencion-horas:24}") int horasRetencion) {
        return new PublicadorOutbox(dataSource, rabbit, convertidor, transactionManager, lote, horasRetencion);
    }

    /**
     * Exchanges, colas (una por consumidor y productor), enlaces y DLQ, según los manejadores registrados.
     */
    @Bean
    public Declarables topologiaEventos(ObjectProvider<ManejadorEvento<?>> manejadores,
                                        @Value("${eventos.servicio}") String servicio) {
        List<Declarable> declarables = new ArrayList<>();
        TopicExchange exchange = new TopicExchange(TopologiaEventos.EXCHANGE, true, false);
        DirectExchange dlx = new DirectExchange(TopologiaEventos.EXCHANGE_DLX, true, false);
        declarables.add(exchange);
        declarables.add(dlx);

        for (Map.Entry<Productor, Set<TipoEvento>> grupo : agruparPorProductor(manejadores.stream().toList()).entrySet()) {
            String nombreCola = TopologiaEventos.cola(servicio, grupo.getKey());
            String nombreDlq = TopologiaEventos.colaFallidos(servicio, grupo.getKey());
            Queue cola = QueueBuilder.durable(nombreCola)
                    .withArgument("x-dead-letter-exchange", TopologiaEventos.EXCHANGE_DLX)
                    .withArgument("x-dead-letter-routing-key", nombreCola)
                    .build();
            Queue dlq = QueueBuilder.durable(nombreDlq).build();
            declarables.add(cola);
            declarables.add(dlq);
            declarables.add(BindingBuilder.bind(dlq).to(dlx).with(nombreCola));
            for (TipoEvento tipo : grupo.getValue()) {
                Binding enlace = BindingBuilder.bind(cola).to(exchange).with(tipo.llaveRuteo());
                declarables.add(enlace);
            }
        }
        return new Declarables(declarables);
    }

    @Bean
    public DespachadorEventos despachadorEventos(ObjectProvider<ManejadorEvento<?>> manejadores,
                                                 ConvertidorEventos convertidor, DataSource dataSource,
                                                 PlatformTransactionManager transactionManager,
                                                 RabbitTemplate rabbit,
                                                 @Value("${eventos.servicio}") String servicio) {
        return new DespachadorEventos(servicio, manejadores.stream().toList(), convertidor, dataSource,
                transactionManager, rabbit);
    }

    /**
     * Registra un escucha por cada cola creada; así no hay que escribir {@code @RabbitListener} en los servicios.
     */
    @Bean
    public RabbitListenerConfigurer escuchasEventos(ObjectProvider<ManejadorEvento<?>> manejadores,
                                                    DespachadorEventos despachador,
                                                    @Value("${eventos.servicio}") String servicio) {
        return (RabbitListenerEndpointRegistrar registrar) -> {
            for (Productor productor : agruparPorProductor(manejadores.stream().toList()).keySet()) {
                String cola = TopologiaEventos.cola(servicio, productor);
                SimpleRabbitListenerEndpoint endpoint = new SimpleRabbitListenerEndpoint();
                endpoint.setId("eventos-" + cola);
                endpoint.setQueueNames(cola);
                endpoint.setMessageListener(mensaje -> despachador.despachar(cola, mensaje));
                registrar.registerEndpoint(endpoint);
            }
        };
    }

    private static Map<Productor, Set<TipoEvento>> agruparPorProductor(List<ManejadorEvento<?>> manejadores) {
        Map<Productor, Set<TipoEvento>> grupos = new EnumMap<>(Productor.class);
        for (ManejadorEvento<?> manejador : manejadores) {
            grupos.computeIfAbsent(manejador.tipo().productor(), p -> new LinkedHashSet<>()).add(manejador.tipo());
        }
        return grupos;
    }
}
