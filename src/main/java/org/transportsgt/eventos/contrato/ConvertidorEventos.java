package org.transportsgt.eventos.contrato;

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

import java.time.Instant;
import java.util.UUID;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Serializa y lee el sobre. Usa su propio {@link JsonMapper} (Jackson 3) para que el contrato no dependa
 * de la configuración del {@code ObjectMapper} de cada servicio. Los campos desconocidos se ignoran:
 * así un productor puede agregar campos sin romper a los consumidores (compatibilidad hacia atrás).
 */
public class ConvertidorEventos {

    /**
     * Sobre tal como viaja por el cable, con {@code datos} aún sin interpretar.
     */
    public record Crudo(
            UUID idEvento,
            String tipoEvento,
            Integer versionEvento,
            Instant ocurridoEn,
            String servicioProductor,
            UUID idAgregado,
            Long versionAgregado,
            UUID idCorrelacion,
            JsonNode datos) {
    }

    private final JsonMapper mapper;

    public ConvertidorEventos() {
        this(crearMapper());
    }

    public ConvertidorEventos(JsonMapper mapper) {
        this.mapper = mapper;
    }

    public static JsonMapper crearMapper() {
        return JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }

    /**
     * JSON de un payload (se guarda así en {@code outbox_evento.datos}).
     */
    public String aJson(Object datos) {
        return mapper.writeValueAsString(datos);
    }

    public JsonNode leerArbol(String json) {
        return mapper.readTree(json);
    }

    public byte[] serializar(SobreEvento<?> sobre) {
        return mapper.writeValueAsBytes(sobre);
    }

    /**
     * Lee un mensaje y convierte {@code datos} a la clase de payload de su tipo.
     *
     * @throws EventoInvalidoException si el JSON es inválido, el tipo no existe o {@code datos} falta
     */
    public SobreEvento<Object> leer(byte[] cuerpo) {
        Crudo crudo;
        try {
            crudo = mapper.readValue(cuerpo, Crudo.class);
        } catch (RuntimeException e) {
            throw new EventoInvalidoException("JSON inválido: " + e.getMessage(), e);
        }
        if (crudo == null || crudo.idEvento() == null || crudo.tipoEvento() == null) {
            throw new EventoInvalidoException("Falta idEvento o tipoEvento en el sobre");
        }
        TipoEvento tipo = TipoEvento.deNombre(crudo.tipoEvento())
                .orElseThrow(() -> new EventoInvalidoException("Tipo de evento desconocido: " + crudo.tipoEvento()));
        if (crudo.datos() == null || crudo.datos().isNull()) {
            throw new EventoInvalidoException("El evento " + crudo.idEvento() + " no trae datos");
        }
        Object datos;
        try {
            datos = mapper.treeToValue(crudo.datos(), tipo.clasePayload());
        } catch (RuntimeException e) {
            throw new EventoInvalidoException("Payload inválido para " + crudo.tipoEvento() + ": " + e.getMessage(), e);
        }
        int versionEvento = crudo.versionEvento() != null ? crudo.versionEvento() : 1;   // por omisión, contrato v1
        return new SobreEvento<>(crudo.idEvento(), crudo.tipoEvento(), versionEvento, crudo.ocurridoEn(),
                crudo.servicioProductor(), crudo.idAgregado(), crudo.versionAgregado(), crudo.idCorrelacion(), datos);
    }
}