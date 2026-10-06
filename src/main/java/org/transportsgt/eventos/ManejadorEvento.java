package org.transportsgt.eventos;

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
