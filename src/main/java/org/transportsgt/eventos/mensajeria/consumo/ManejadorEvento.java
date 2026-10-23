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

/**
 * Lo implementa cada servicio por cada evento que consume y lo registra como bean de Spring.
 * La librería crea las colas, enlaces y DLQ a partir de los manejadores registrados.
 *
 * <p>{@link #manejar} se ejecuta dentro de una transacción que ya incluyó el registro en
 * {@code evento_procesado}: si lanza una excepción, ambos se revierten.
 * Un evento duplicado nunca llega aquí.
 *
 * @param <T> clase del payload; debe ser {@code tipo().clasePayload()}
 */
public interface ManejadorEvento<T> {

    TipoEvento tipo();

    void manejar(SobreEvento<T> evento);
}
