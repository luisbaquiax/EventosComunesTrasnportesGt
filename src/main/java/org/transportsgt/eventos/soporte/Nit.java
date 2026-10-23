package org.transportsgt.eventos.soporte;

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

import java.util.regex.Pattern;

/**
 * Normalización única del NIT para todos los servicios, de modo que el historial por NIT
 * coincida entre Boletos, Alquileres y Clientes y Cartera.
 *
 * <p>Forma normalizada: mayúsculas, sin espacios, guiones ni diagonales ({@code 1234567-k} → {@code 1234567K}).
 * {@code CF} significa consumidor final: solo existe en ventas y alquileres de mostrador;
 * un perfil de cliente no puede tener NIT {@code CF}.
 */
public final class Nit {

    /** Consumidor final. */
    public static final String CONSUMIDOR_FINAL = "CF";

    private static final Pattern FORMA_VALIDA = Pattern.compile("^[0-9A-Z]{2,20}$");

    private Nit() {
    }

    /** Devuelve el NIT normalizado, o {@code null} si el texto es nulo o queda vacío. */
    public static String normalizar(String nit) {
        if (nit == null) {
            return null;
        }
        String limpio = nit.replaceAll("[\\s\\-/]", "").toUpperCase(java.util.Locale.ROOT);
        return limpio.isEmpty() ? null : limpio;
    }

    /** {@code true} si el NIT (ya normalizado o no) corresponde a consumidor final ({@code CF}). */
    public static boolean esConsumidorFinal(String nit) {
        return CONSUMIDOR_FINAL.equals(normalizar(nit));
    }

    /** {@code true} si, una vez normalizado, tiene entre 2 y 20 caracteres alfanuméricos. */
    public static boolean tieneFormatoValido(String nit) {
        String n = normalizar(nit);
        return n != null && FORMA_VALIDA.matcher(n).matches();
    }

    /** {@code true} si sirve como NIT de un perfil de cliente: formato válido y distinto de {@code CF}. */
    public static boolean esNitDeCliente(String nit) {
        return tieneFormatoValido(nit) && !esConsumidorFinal(nit);
    }
}
