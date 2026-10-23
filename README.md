# EventosComunesTrasnportesGt

Librería compartida de los microservicios de la plataforma de transporte.

## Organización del código

El código fuente está agrupado físicamente por responsabilidad. Las declaraciones
`package` se mantienen sin cambios para que los microservicios actuales conserven
sus imports; por eso estas carpetas sirven para orientar la navegación, pero no
representan paquetes Java nuevos.

| Carpeta | Responsabilidad |
|---|---|
| `contrato` | Sobre, catálogo de eventos, productores y conversión JSON |
| `payload/identidad`, `payload/flota`, `payload/viajes`, etc. | Payloads y enumeraciones agrupados por dominio de negocio |
| `mensajeria` | Outbox, consumo idempotente, RabbitMQ y excepciones |
| `configuracion` | Activación y configuración automática de la librería |
| `contexto` | Correlación de peticiones y eventos |
| `soporte` | Utilidades transversales, como la normalización del NIT |

La separación es por capacidad compartida, no por microservicio: un servicio puede producir eventos de un dominio y consumir eventos de varios dominios.
