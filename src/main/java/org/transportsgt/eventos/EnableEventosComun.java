package org.transportsgt.eventos;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * Activa el outbox, el consumidor idempotente y la topología RabbitMQ en el servicio.
 * Se coloca junto a {@code @SpringBootApplication}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EventosComunConfiguracion.class)
public @interface EnableEventosComun {
}
