package org.transportsgt.eventos;

/**
 * Servicios que publican eventos. {@code prefijo} es el primer segmento de la llave de ruteo.
 */
public enum Productor {
    IDENTIDAD("identidad", "servicio-identidad"),
    FLOTA("flota", "servicio-flota"),
    VIAJES("viajes", "servicio-viajes"),
    BOLETOS("boletos", "servicio-boletos"),
    CARTERA("cartera", "servicio-clientes"),// Clientes y Cartera publica con el prefijo "cartera"
    ALQUILERES("alquileres", "servicio-alquileres"),
    COSTOS("costos", "servicio-costos");

    private final String prefijo;
    private final String nombreServicio;

    Productor(String prefijo, String nombreServicio) {
        this.prefijo = prefijo;
        this.nombreServicio = nombreServicio;
    }

    public String prefijo() {
        return prefijo;
    }

    /**
     * Valor del campo {@code servicioProductor} del sobre.
     */
    public String nombreServicio() {
        return nombreServicio;
    }
}
