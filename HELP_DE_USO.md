# eventos-comun

Librería compartida de los microservicios de la plataforma de transporte. Contiene el **contrato de eventos** y la infraestructura de mensajería, para que ningún servicio la reescriba:

| Qué | Dónde |
|---|---|
| Sobre común del evento | `SobreEvento<T>` |
| Los 32 tipos de evento (nombre, productor, llave de ruteo, payload) | `TipoEvento`, `Productor` |
| Normalización del NIT (`Nit.normalizar()`, `CF` reservado al mostrador) | `Nit` |
| Payloads (records) y enumeraciones del catálogo | paquete `payload` |
| Outbox transaccional (guardar + publicar + limpiar) | `RegistroOutbox`, `PublicadorOutbox` |
| Consumidor idempotente con reintentos y DLQ | `ManejadorEvento`, `DespachadorEventos` |
| Exchanges, colas, enlaces y DLQ de RabbitMQ | se crean solos a partir de los manejadores |

Fuente de verdad del contrato: `catalogo-eventos-es-v4.md`. Si un payload cambia, se cambia primero allí y luego aquí.

## 1. Usarla en un microservicio

`pom.xml` del servicio:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.TU_USUARIO</groupId>
    <artifactId>eventos-comun</artifactId>
    <version>1.1.0</version>
</dependency>
```

El servicio debe tener `spring-boot-starter-amqp`, `spring-boot-starter-data-jpa` (o `-jdbc`) y Jackson (`spring-boot-starter-webmvc` ya lo trae). La librería los declara `provided`: no impone versiones.

Clase principal:

```java
@SpringBootApplication
@EnableEventosComun
public class BoletosApplication { ... }
```

`application.properties` (mínimo; ver `application-eventos.properties` en este repo para la versión completa con reintentos):

```properties
# nombre corto: prefijo de las colas y valor de evento_procesado.consumidor
eventos.servicio=boletos
```

Tablas: `outbox_evento` y `evento_procesado` van en la migración Flyway de cada servicio (definidas en `modelo-datos-postgresql-v4.md`, «Tablas comunes»).

## 2. Publicar un evento (productor)

Siempre dentro de la transacción del cambio de negocio:

```java
@Service
@RequiredArgsConstructor
public class ViajeService {
    private final ViajeRepository viajes;
    private final RegistroOutbox outbox;

    @Transactional
    public Viaje programar(...) {
        Viaje v = viajes.save(...);
        outbox.registrar(TipoEvento.VIAJE_PROGRAMADO, v.getId(), v.getVersion(), toDatos(v));   // DatosViaje
        return v;
    }
}
```

- `registrar` falla si no hay transacción activa y si el payload no es el del tipo.
- El `idCorrelacion` se toma de `ContextoCorrelacion` (un filtro HTTP lo fija al entrar la petición; los manejadores de eventos lo reciben del evento consumido). También hay una sobrecarga que lo recibe explícito.
- `PublicadorOutbox` (automático, cada segundo) publica los pendientes y marca `publicado_en`; a las 03:30 borra los publicados con más de 24 h.

## 3. Consumir un evento

Un bean por evento. No hay que escribir `@RabbitListener` ni declarar colas:

```java
@Component
@RequiredArgsConstructor
class ViajeProgramadoManejador implements ManejadorEvento<DatosViaje> {
    private final ViajeRefRepository refs;

    @Override public TipoEvento tipo() { return TipoEvento.VIAJE_PROGRAMADO; }

    @Override
    public void manejar(SobreEvento<DatosViaje> evento) {            // ya está en una transacción
        Long versionGuardada = refs.versionDe(evento.datos().idViaje());   // null si no existe
        if (!evento.reemplaza(versionGuardada)) return;              // evento viejo o duplicado de la proyección
        refs.upsert(evento.datos(), evento.versionAgregado());
    }
}
```

La librería, por cada **productor** distinto entre tus manejadores, crea la cola `{servicio}.{productor}.q` enlazada **solo** con las llaves de los eventos que manejas, y su DLQ `{servicio}.{productor}.dlq`.

| Situación | Resultado |
|---|---|
| Evento duplicado (`evento_procesado`) | Se ignora, sin llamar al manejador |
| El manejador lanza una excepción cualquiera | Se revierte todo y se reintenta con espera creciente; al agotarlos → DLQ |
| El manejador lanza `EventoNoRecuperableException` | Directo a la DLQ, sin reintentos |
| JSON inválido, tipo desconocido o sin `datos` | Directo a la DLQ, sin reintentos |
| Evento sin manejador en este servicio | Se ignora con un aviso en el log |

## 4. Publicarla en JitPack

1. Sube **solo esta carpeta** como un repositorio **público** de GitHub (por ejemplo `eventos-comun`). JitPack compila desde la raíz del repo y `jitpack.yml` ya fija Java 21.
2. En GitHub: *Releases → Create a new release* con el tag `1.1.0`.
3. Entra a `https://jitpack.io/#TU_USUARIO/eventos-comun`, busca `1.1.0` y pulsa **Get it**; el *Log* muestra la compilación.
4. Las coordenadas son `com.github.TU_USUARIO:eventos-comun:1.1.0` (JitPack ignora el `groupId` del pom).
5. Un tag publicado no se modifica: para cambiar algo se publica `1.0.1` y se sube la versión en cada servicio. Para desarrollar rápido puede usarse `main-SNAPSHOT`, pero no lo dejes así en la entrega.
6. Antes de la entrega, compila una vez cada versión que usarás para que JitPack la tenga en caché.

Plan B (sin JitPack): `mvn install` en esta carpeta y usar `org.transportsgt:eventos-comun:1.1.0` desde el `~/.m2` local. En Docker/Cloud Run no sirve, porque el contenedor no tiene tu `.m2`.

## 5. Pruebas

```bash
mvn test
```

`TipoEventoTest` verifica los 32 tipos (nombres y llaves únicos, cantidad por productor) y `ConvertidorEventosTest` verifica ida y vuelta del sobre, dinero como texto, campos desconocidos y mensajes inválidos.

## 6. Límites conocidos

- **Entrega al menos una vez:** un evento puede llegar repetido; por eso todo manejador pasa por `evento_procesado`.
- **Sin confirmaciones del publicador** (*publisher confirms*): el outbox marca `publicado_en` al entregar el mensaje a RabbitMQ. Si CloudAMQP lo pierde antes de persistirlo no hay reintento. Mejora opcional: `spring.rabbitmq.publisher-confirm-type=correlated` y esperar la confirmación.
- **Fechas con zona:** `OffsetDateTime` conserva el mismo instante al leerse, pero Jackson puede normalizar la zona horaria. Convierte a `America/Guatemala` solo para mostrar.
- Los importes viajan como texto con dos decimales (`"170.00"`); crea los `BigDecimal` con `setScale(2)`.
- El outbox necesita la instancia siempre encendida (Cloud Run: `--min-instances 1 --no-cpu-throttling`).
- No se registra nunca el payload de `EnlaceContrasenaEmitido` (su `toString()` oculta el token).
