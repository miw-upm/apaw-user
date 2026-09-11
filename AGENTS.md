# Guía de arquitectura y contribución — APAW User

Guía para `apaw-user`, ejercicio docente de Arquitectura y Patrones para Aplicaciones Web.
Revisada contra el código del repositorio el 2026-09-11.

## Alcance y criterios

- Mantener una arquitectura de tres capas, sencilla y adecuada al ejercicio.
- Los permisos y la autorización por roles quedan expresamente fuera del alcance. 
- La presencia de Spring Security y del enum `Role` no implica que haya que implementar autorización.
- No introducir arquitectura hexagonal, interfaces de servicio, modelos duplicados ni módulos adicionales sin una necesidad concreta.
- `DEBE` indica una regla de contribución; `DEBERÍA`, una recomendación. Los apartados de estado actual describen la implementación y no obligan a perpetuarla.
- Las mejoras propuestas no se consideran implementadas ni autorizan refactorizaciones ajenas a la tarea.

## Estructura y dependencias

Paquete base: `es.upm.miw.apaw`.

```text
Application
config/
  EurekaConfig
  LoggingFilter
  SecurityConfiguration
  SeederForDev
resources/
  UserResource
  SystemResource
  VersionBadgeGenerator
  dtos/
    UserDto
    ApplicationInfoDto
    Validations
  exceptionshandler/
    ApiExceptionHandler
services/
  UserService
  criteria/
    UserFindCriteria
  exceptions/
    ApiException, ClientBusinessException, ErrorMessage, ...
infrastructure/
  data/
    daos/
      UserRepository
    models/
      User, Role, Province
  support/       (actualmente solo un archivo de reserva)
  clientshttp/   (actualmente solo un archivo de reserva)
```

Flujo principal:

```text
HTTP → Resource → Service → Repository → PostgreSQL
          ↕           ↕
         DTO     entidad JPA
```

- Los recursos DEBEN delegar las operaciones de negocio en servicios.
- Los servicios DEBEN trabajar con entidades y criterios, sin depender de `resources.dtos`.
- Los servicios acceden directamente a repositorios Spring Data JPA.
- `User` es simultáneamente entidad persistente y modelo usado por el servicio; no existe un modelo de dominio separado.
- La conversión DTO ↔ entidad DEBE permanecer en `resources.dtos`.
- No asumir la existencia de gateway, librería commons u otros servicios no incluidos en este repositorio.

## Recursos HTTP

- Usar `@RestController`, sufijo `Resource` y constantes para rutas.
- Usar inyección por constructor para colaboradores; `@RequiredArgsConstructor` es la convención de `UserResource` y `UserService`.
- Validar cuerpos de entrada con `@Valid` y las restricciones del DTO.
- Recibir criterios de búsqueda con `@ModelAttribute`.
- No devolver entidades JPA directamente como respuesta HTTP.

Contrato actual de usuarios:

| Operación | Método del recurso | Respuesta de éxito |
| --- | --- | --- |
| `POST /users` | `create` | 200 sin cuerpo |
| `GET /users/{id}` | `read` | 200 con `UserDto` |
| `GET /users` | `find` | 200 con lista de resúmenes `UserDto` |
| `DELETE /users/{id}` | `delete` | 200 sin cuerpo |

`SystemResource` expone `/system` y `/system/version-badge`.
Los cambios de contrato HTTP DEBEN acompañarse de la revisión de los tests funcionales afectados.

## DTOs y criterios

- DTOs actuales: `UserDto` y `ApplicationInfoDto`; no crear DTOs o convenciones de otros proyectos por analogía.
- `UserDto` sirve como entrada y salida. `id` y `registrationDate` son `READ_ONLY`; `password` es `WRITE_ONLY`.
- El mapeo actual usa `BeanUtils.copyProperties`, constructor desde `User` y `toDomain()`.
- `toSummary()` construye un DTO con id, móvil, nombre, apellidos y email.
- Actualmente `UserResource.create()` llama a `UserDto.doDefault()`, que asigna `CUSTOMER` y activo cuando faltan. No describir estos valores como invariantes ya garantizadas por el servicio.
- Los criterios DEBEN ubicarse en `services.criteria`, con sufijo `FindCriteria` y sin anotaciones de serialización HTTP.
- `UserFindCriteria` contiene `active`, `mobile` y `billable`, todos opcionales.

## Servicios y modelo

- Usar `@Service`, sufijo `Service` y métodos con nombres como `create`, `read`, `find` y `delete`.
- Encapsular comprobaciones de negocio en métodos privados cuando facilite la lectura.
- `create(User)` comprueba el móvil, asigna UUID y fecha de registro, genera una contraseña si es nula y guarda la entidad.
- El móvil existente provoca `ClientBusinessException`.
- `read(UUID)` lanza `NotFoundException` cuando no encuentra el usuario.
- `delete(UUID)` delega directamente en `deleteById`; no tiene comprobación explícita de existencia.
- `find()` devuelve `Stream<User>` a partir de resultados materializados del repositorio. Filtra `billable` en memoria; actualmente no hay paginación.
- `User.isBillable()` comprueba que estén presentes los datos necesarios para facturación.
- No existen límites transaccionales explícitos en `UserService`. Si una tarea introduce operaciones que deban ser atómicas, definir su transacción en el servicio; no asumir que varias llamadas al repositorio comparten una transacción.

## Persistencia

- Usar Spring Data JPA, Hibernate y PostgreSQL; H2 se usa en tests.
- Los repositorios DEBEN ubicarse en `infrastructure.data.daos` y extender `JpaRepository`.
- `UserRepository` usa `User` y `UUID`, con consultas derivadas por móvil, activo y roles.
- Los modelos DEBEN ubicarse en `infrastructure.data.models`.
- `User` usa `@Entity`, `@Table(name = "miwUser")` y `@Id` de Jakarta Persistence.
- El móvil tiene `@Column(unique = true, nullable = false)`; los enums se almacenan con `EnumType.STRING`.
- No usar reglas de MongoDB, `@Document`, `MongoTemplate` o `@DBRef` en esta aplicación.
- `spring.jpa.open-in-view` está desactivado. No depender de carga diferida desde el controlador.

## Excepciones y respuestas de error

- Las excepciones locales y `ErrorMessage` se ubican actualmente en `services.exceptions`.
- La traducción HTTP DEBE centralizarse en `resources.exceptionshandler.ApiExceptionHandler`.
- `ErrorMessage` contiene `error`, `message` y `cause`.
- `ApiException` contiene detalle y detalle de causa; `ClientBusinessException` extiende directamente `RuntimeException`.
- Mantener `ClientBusinessException` y `ConflictException` → 409 para los casos gestionados como conflicto.
- Mantener `NotFoundException` y `NoResourceFoundException` → 404.
- `ResponseStatusException` tiene un manejador separado que conserva `getStatusCode()` y `getHeaders()` mediante `ResponseEntity`. No asignarle un estado fijo.
- El manejador actual también agrupa validación y otras excepciones bajo 400, y tiene una captura general con 500.
- `HttpRequestMethodNotSupportedException` sigue agrupada bajo 400 en el código actual: esto describe el estado existente, no una regla para nuevos manejadores.
- Al cambiar una traducción, comprobar la excepción concreta y el estado esperado. Distinguir fallos observados de ejemplos hipotéticos.

## Configuración e infraestructura

- `config` contiene configuración técnica e inicialización.
- `EurekaConfig` declara un `RestTemplate` con `@LoadBalanced` y tiempos de conexión y lectura.
- Hay dependencia de Eureka y OpenFeign, y `@EnableFeignClients` en `Application`; actualmente no hay clientes Feign implementados.
- Si se añaden clientes HTTP, ubicarlos en `infrastructure.clientshttp` y aislar allí los detalles de comunicación.
- Reservar `infrastructure.support` para utilidades técnicas internas, sin mezclar clientes HTTP ni reglas de negocio.
- `LoggingFilter` registra peticiones y respuestas; no interpretar `WRITE_ONLY` como protección del contenido de los logs.
- `SecurityConfiguration` configura sesiones sin estado, desactiva CSRF y habilita seguridad de métodos, pero no define reglas de permisos. Mantener el alcance docente indicado arriba.
- `Application` excluye `ErrorMvcAutoConfiguration`.

## Perfiles y datos iniciales

- `application.yml` define puerto 8081 y perfil predeterminado `dev`.
- `application-dev.yml` configura PostgreSQL y `ddl-auto: update`.
- `application-test.yml` configura H2 en memoria y desactiva Eureka.
- `SeederForDev` DEBE permanecer limitado a `dev` y `test`, con `ApplicationRunner` y lógica en `run()`.
- Actualmente el seeder borra todos los usuarios y vuelve a cargarlos al arrancar. Tenerlo en cuenta antes de ejecutar la aplicación contra una base con datos que deban conservarse.
- Mantener los UUID fijos del seeder mientras los tests dependan de ellos.

## Tests y verificación

- `*Test`: pruebas ejecutadas por Surefire; actualmente `ApplicationTest` carga el contexto.
- `*IT`: integración de repositorio y servicio con `@SpringBootTest` y perfil `test`.
- `*FT`: pruebas HTTP con `RANDOM_PORT`, perfil `test` y `org.springframework.test.web.servlet.client.RestTestClient`.
- Para comprobar 409, usar `.expectStatus().isEqualTo(HttpStatus.CONFLICT)`; no existe `.isConflict()` en el cliente utilizado.
- Cubrir los casos de éxito y error afectados por el cambio. Los permisos quedan fuera del ejercicio.
- Los nuevos tests que muten datos compartidos DEBERÍAN limpiar sus datos o aislar su estado; hay tests de creación actuales sin limpieza explícita.
- Usar JDK 21, conforme al objetivo de Maven y al entorno de CI.
- `mvn test` ejecuta Surefire; `mvn verify` incluye las ejecuciones Failsafe de `*IT` y `*FT` y su verificación final.
- Para cambios exclusivamente documentales, revisar contenido y diff; no es necesario ejecutar la aplicación.

## Tecnología y despliegue

Versiones observadas en `pom.xml` (consultarlo antes de modificar dependencias):

- Java 21.
- Spring Boot 4.1.0.
- Spring Cloud 2025.1.2, gestionado mediante BOM.
- Springdoc OpenAPI 3.0.3.
- Maven, Lombok, Jakarta Validation, Spring MVC y Spring Data JPA.
- WebFlux está declarado con alcance de test; la aplicación HTTP es Spring MVC.

La CI de `.github/workflows/ci.yml` ejecuta `mvn verify`, CodeQL y SonarCloud.
El Dockerfile construye con Maven y ejecuta con JRE 21 y usuario sin privilegios.
`docker-compose.yml` publica 8081 y utiliza la red externa `apawnet`.
El healthcheck del Dockerfile apunta actualmente a 8080, distinto del puerto 8081 configurado en la aplicación; no tomarlo como referencia de puerto sin revisar esa discrepancia.

## Límites de las contribuciones

- No introducir DTOs en servicios ni acceso directo al repositorio desde recursos de negocio.
- No implementar permisos como parte de una revisión general.
- No cambiar contratos, valores por defecto o comportamiento de borrado incidentalmente.
- Mantener esta guía sincronizada cuando cambien la estructura o las decisiones arquitectónicas.
