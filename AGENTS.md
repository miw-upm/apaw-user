# Guía de estilo y arquitectura

Documento normativo para contribuir en `apaw-user`, ejercicio docente de Arquitectura y Patrones para Aplicaciones Web.

El proyecto sigue una arquitectura por capas con Spring Boot y persistencia JPA. Las reglas establecen responsabilidades,
convenciones y criterios para ampliar la aplicación. Los permisos quedan fuera del alcance del ejercicio.

## Criterios de corrección

Cada criterio puede descontar puntos. La letra marca la penalización orientativa:

- a = -0.2
- b = -0.4
- c = -0.6
- d = -0.8
- e = -1
- f = -1.5
- g = -2
- h = -2.5
- i = -3
- j = -5
- K = -10

Coste de incumplir: cuando una sección indique un coste común, solo se etiqueta la regla que se sale de ese coste.

## Niveles de regla

- `DEBE`: obligatorio.
- `NO DEBE`: prohibido.
- `DEBERÍA`: recomendado, salvo razón técnica explícita.
- `PUEDE`: opcional.

## Estructura y dependencias

```text
es.upm.miw.apaw/
  Application
  config/
  resources/
    dtos/
    exceptionshandler/
  services/
    criteria/
    exceptions/
  infrastructure/
    data/
      daos/
      models/
    clientshttp/
    support/
```

Capas principales:

- `resources`: entrada y salida HTTP, DTOs, validaciones de entrada y traducción HTTP de excepciones.
- `services`: casos de uso, coordinación de operaciones, criterios y excepciones de negocio.
- `infrastructure`: persistencia JPA, clientes HTTP salientes y soporte técnico.
- `config`: configuración Spring e inicializadores.

Reglas de dependencia:

- DEBE mantenerse el flujo `resources → services → infrastructure`.
- `resources` DEBE delegar los casos de uso en `services`.
- `services` PUEDE depender directamente de los repositorios JPA de `infrastructure.data.daos`.
- `services` NO DEBE depender de DTOs ni componentes de `resources`.
- `infrastructure` NO DEBE depender de `resources`.
- Las entidades JPA NO DEBEN conocer DTOs, resources, services ni repositories.
- NO DEBE duplicarse el modelo en una entidad JPA y un dominio separado sin una necesidad arquitectónica explícita.

## Convenciones de nombres (b)

- Clases e interfaces: DEBE usar PascalCase (`User`, `UserService`, `UserFindCriteria`).
- Métodos y variables: DEBE usar camelCase (`registrationDate`, `findByMobile`).
- Constantes: DEBE usar mayúsculas con guion bajo (`USER_ID`, `USERS`).
- Paquetes: DEBE usar minúsculas y jerarquías separadas por puntos (`infrastructure.data.daos`).
- Enums: DEBE usar PascalCase para el tipo y mayúsculas para sus valores (`Role.CUSTOMER`).
- Predicados: DEBE usar prefijos `is`, `has` o `can` (`isBillable`, `hasMobile`).
- DEBE mantener una convención uniforme entre predicados relacionados, aunque alguna llamada utilice negación.
- Los nombres DEBEN ser descriptivos y estar en inglés.
- DEBERÍA evitar abreviaturas, salvo las habituales (`id`, `url`, `dto`, `dao`).
- NO DEBE usar prefijos de tipo ni notación húngara (`strName`, `iCount`).

## Estilo de código (a)

- DEBE usar `this.` para acceder a atributos y métodos propios de instancia.
- DEBE seguir el formato y estilo de las clases vecinas.
- NO DEBE usar `System.out.print` ni `System.out.println`; usar logging.
- DEBE usar `@Log4j2` o `LogManager.getLogger()` para logging.
- DEBERÍA elegir el nivel de log según la severidad del mensaje.
- DEBERÍA aclarar el código con nombres y métodos expresivos antes de añadir comentarios explicativos.
- (b) Los mensajes de negocio DEBEN incluir el dato causante cuando ayude a identificar el error, sin incluir secretos.
- NO DEBE cambiar de estilo de forma incidental al ampliar una clase.

## Límites de tamaño (b-c)

Son umbrales de revisión. El criterio principal es responsabilidad única y legibilidad.

- DEBERÍA mantener un máximo de 3 parámetros por método; agrupar filtros relacionados en un `FindCriteria`.
- DEBERÍA mantener un máximo de 20 líneas por método.
- DEBERÍA mantener complejidad ciclomática máxima de 8 y un máximo de 2 niveles de anidamiento.
- DEBERÍA mantener un máximo de 20 métodos públicos y 6 dependencias inyectadas por clase.
- DEBERÍA mantener un máximo de 250 líneas por clase y 120 caracteres por línea.
- DEBERÍA mantener un máximo de 8 atributos, salvo entidades y DTOs con más campos reales del recurso.
- Los tests y seeders PUEDEN superar los umbrales cuando dividirlos perjudique su claridad.
- NO DEBE crear clases, DTOs o abstracciones únicamente para cumplir una cifra.

## Resources HTTP (e-f)

- DEBE usar `@RestController` y sufijo `Resource`.
- DEBE declarar las rutas mediante constantes y mantener coherencia entre las operaciones del recurso.
- DEBE usar inyección por constructor con `@RequiredArgsConstructor` para sus colaboradores.
- PUEDE usar `@Value` para propiedades simples de configuración.
- DEBE recibir cuerpos de negocio mediante DTOs de `resources.dtos`.
- DEBE validar la entrada con `@Valid` cuando el DTO declare restricciones.
- DEBERÍA recibir filtros con `@ModelAttribute` y clases `FindCriteria`.
- DEBE convertir DTOs a entidades antes de llamar al servicio y entidades a DTOs antes de responder.
- NO DEBE exponer entidades JPA directamente como respuesta HTTP.
- NO DEBE consultar repositorios ni contener decisiones de negocio.
- Cada endpoint DEBERÍA delegar en un único servicio principal.
- NO DEBE aplicar valores por defecto de creación; el servicio decide cuándo se inicializa el modelo.
- NO DEBE añadir permisos como parte del desarrollo ordinario de este ejercicio.

## DTOs (e-f)

- DEBE ubicarse en `resources.dtos`, con sufijo `Dto`.
- DEBE representar los datos de intercambio HTTP y declarar sus restricciones Jakarta Validation.
- DEBE mantener la conversión DTO ↔ entidad en la propia clase mediante constructor y `toDomain()`.
- DEBERÍA usar `BeanUtils.copyProperties`, Lombok y builders siguiendo el estilo existente.
- DEBE priorizar un DTO general por recurso cuando permita expresar sus operaciones con claridad.
- PUEDE usar `READ_ONLY` y `WRITE_ONLY` para diferencias entre entrada y salida.
- PUEDE usar `toSummary()` para una respuesta reducida del mismo recurso.
- NO DEBE crear variantes de creación, actualización, lectura y resumen automáticamente.
- Otro DTO DEBE responder a una necesidad concreta de contrato que no quede clara con el DTO general.
- PUEDE usar records para datos simples e inmutables; NO DEBE convertir DTOs amplios a records por uniformidad.
- NO DEBE consultar la base de datos, contener reglas de negocio ni decidir valores por defecto de creación.
- NO DEBE ubicarse en `services` ni ser conocido por entidades o repositorios.

## Criteria (c-d)

- DEBE ubicarse en `services.criteria`, con sufijo `FindCriteria`.
- DEBE expresar filtros de búsqueda, no detalles de presentación ni consultas a base de datos.
- DEBERÍA ofrecer predicados de presencia (`hasMobile`, `hasActive`, `hasBillable`) para simplificar el servicio.
- NO DEBE contener anotaciones de serialización HTTP.
- Al añadir un filtro, DEBE definirse el significado de su ausencia y probar coincidencias y exclusiones.
- NO DEBE introducir una clase diferente por cada combinación de filtros.

## Services (e-f)

- DEBE usar `@Service`, sufijo `Service` e inyección por constructor.
- DEBE implementar casos de uso sin mantener estado mutable propio de una petición.
- DEBE trabajar con entidades y criterios, no con DTOs ni respuestas HTTP.
- DEBE coordinar repositorios y las reglas que requieran consultar otros datos, como unicidad del móvil.
- DEBE decidir cuándo aplicar el comportamiento del modelo: `create()` llama a `user.doDefault()`.
- DEBE controlar la identidad y fecha asignadas durante la creación.
- DEBE lanzar excepciones de `services.exceptions` para los errores de negocio o aplicación.
- DEBE lanzar `NotFoundException` en lecturas y actualizaciones de recursos inexistentes.
- El borrado DEBE tolerar que el recurso no exista, salvo requisito explícito contrario.
- DEBERÍA mantener nombres consistentes: `create`, `read`, `readByMobile`, `update`, `delete`, `find`.
- DEBERÍA encapsular comprobaciones en métodos privados (`assertXxx`, `validateXxx`).
- DEBE delimitar en el servicio la transacción cuando varias operaciones del caso de uso deban ser atómicas.
- NO DEBE depender de controllers, `ResponseEntity`, `RestTestClient` ni detalles de presentación.

## Persistencia JPA (e-f)

- DEBE usar repositorios Spring Data `JpaRepository` en `infrastructure.data.daos`.
- DEBE nombrar cada repositorio `{Entity}Repository` y parametrizarlo con su entidad y tipo de identidad.
- DEBERÍA usar consultas derivadas para operaciones simples (`findByMobile`, `existsByMobile`).
- PUEDE usar `@Query`, `Specification` o repositorios custom cuando una consulta lo necesite.
- DEBE mantener SQL y detalles de persistencia dentro de la infraestructura.
- NO DEBE incluir decisiones del caso de uso en el repositorio.
- Los servicios PUEDEN combinar consultas con predicados del modelo que dependan de su estado.
- DEBE mantener coherencia entre una regla del modelo y su traducción a consulta cuando se necesite filtrar en la BD.
- NO DEBE añadir paginación o mecanismos de consulta más complejos sin un requisito o una limitación comprobada.

## Entidades JPA (e-f)

- DEBE ubicarse en `infrastructure.data.models`, usar `@Entity` y nombrarse sin sufijo.
- DEBE marcar la identidad con `jakarta.persistence.Id`.
- DEBERÍA declarar restricciones de unicidad y obligatoriedad en columnas cuando formen parte de la integridad.
- DEBE persistir enums mediante `@Enumerated(EnumType.STRING)`.
- DEBE usar relaciones JPA cuando el modelo requiera relaciones entre entidades.
- PUEDE usar Lombok conforme al estilo del proyecto.
- DEBE encapsular el comportamiento derivado de sus campos, como `isBillable()`.
- DEBE encapsular los valores por defecto en `doDefault()` cuando formen parte de su inicialización.
- `doDefault()` DEBE completar valores ausentes sin sobrescribir los proporcionados.
- La llamada a `doDefault()` DEBE decidirla el servicio que conoce la operación.
- NO DEBE inicializar contraseñas o valores de creación en el constructor vacío usado por JPA para cargar entidades.
- NO DEBE consultar repositorios ni depender de services, DTOs, resources o configuración.

## Identidad de entidades (f)

- DEBE usar un atributo técnico `id` de tipo `UUID`, sin significado de negocio.
- NO DEBE usar móvil, email u otra clave natural como identidad técnica.
- DEBE asignar la identidad al crear, antes de persistir.
- PUEDE usar UUID fijos en el seeder para referencias estables en tests.
- Las restricciones de unicidad DEBEN respaldarse en base de datos cuando sean necesarias para la integridad.
- NO DEBE confundir una comprobación previa de existencia con la garantía de una restricción única.

## Infrastructure support (c-d)

- DEBE ubicarse en `infrastructure.support`.
- DEBE contener capacidades técnicas internas reutilizables.
- PUEDE envolver una librería externa para aislar sus detalles cuando sea necesario.
- NO DEBE contener reglas de negocio ni clientes HTTP salientes.

## Infrastructure clients HTTP (c-d)

- DEBE ubicarse en `infrastructure.clientshttp`.
- DEBE encapsular comunicaciones HTTP salientes y sus detalles de protocolo.
- El servicio DEBE decidir cuándo invocar al cliente dentro del caso de uso.
- PUEDE agrupar clientes en subpaquetes cuando aumenten las integraciones.
- NO DEBE contener controllers HTTP de entrada ni decisiones de negocio.

## Excepciones y errores (e-f)

- DEBE centralizar la traducción HTTP en `resources.exceptionshandler.ApiExceptionHandler`.
- DEBE usar `services.exceptions` para excepciones de negocio y aplicación.
- DEBE devolver el formato `ErrorMessage` cuando haya cuerpo de error.
- Los errores conocidos DEBEN tener tratamiento explícito.
- En este ejercicio, la captura general de `Exception.class` con 500 DEBE reservarse para errores imprevistos.
- NO DEBE enviar una excepción identificada al manejador general como sustituto de decidir su tratamiento.
- El estado de un manejador específico DEBE elegirse según el significado del error y el contrato de la API.
- DEBE conservar el estado y las cabeceras de `ResponseStatusException`.
- Los servicios NO DEBEN capturar excepciones técnicas si no aportan una decisión de negocio.
- Si un manejador agrupa excepciones técnicas, DEBE tenerse en cuenta que pueden representar distintas causas.
- NO DEBE atribuir toda violación de integridad a un móvil duplicado sin comprobar su causa.
- Los errores imprevistos DEBEN registrarse con nivel `error`.
- DEBE actualizar los tests afectados cuando cambie un mapeo HTTP.

## Configuración e inicializadores (e-f)

- DEBE ubicarse en `config` y usar `@Configuration` cuando declare beans.
- DEBE reservarse para configuración técnica y construcción de componentes, sin reglas de negocio.
- Los inicializadores DEBEN implementar `ApplicationRunner` y ejecutar su lógica en `run(...)`.
- `SeederForDev` DEBE limitarse a los perfiles `dev` y `test`.
- DEBE mantener estables las referencias del seeder utilizadas por los tests.
- NO DEBE modificar datos existentes del seeder de manera que cambie el significado de esas referencias.
- PUEDE ampliar el seeder con nuevos datos sin rehacer los tests anteriores.

## Tests (e-f)

Convenciones:

- `*Test`: unitarios o pruebas específicas de contexto, según corresponda.
- `*IT`: integración de servicios y repositorios.
- `*FT`: funcionales HTTP.

Reglas:

- DEBE leer y seguir el estilo de los tests de la clase antes de ampliarla.
- DEBE usar métodos `@Test` independientes, nombres `testXxx`, builders y AssertJ.
- DEBE usar `assertThatThrownBy` para comprobar excepciones del servicio.
- NO DEBE introducir tests parametrizados, condicionales ni estilos alternativos al ampliar estas clases.
- DEBE usar `@SpringBootTest` y `@ActiveProfiles("test")` cuando se levante el contexto para integración o HTTP.
- Los tests HTTP DEBEN usar `RANDOM_PORT` y `RestTestClient`.
- Las respuestas de usuarios DEBEN comprobarse con `expectBody(UserDto.class)` o `expectBody(UserDto[].class)` y AssertJ.
- DEBE cubrir casos de éxito y error en la capa responsable: negocio en servicios; contrato HTTP en recursos.
- DEBE añadir tests del repositorio cuando se incorporen consultas que necesiten verificación propia.
- DEBE apoyarse en constantes del seeder sin asumir que representan todos los datos existentes.
- **Ampliar el seeder NO DEBE provocar fallos en los tests anteriores.**
- Las búsquedas generales DEBEN comprobar pertenencia o exclusión, sin exigir tamaños totales ni listas completas.
- PUEDE comprobar un único resultado al buscar por un dato único y una lista vacía para una búsqueda sin coincidencias.
- NO DEBE depender de un orden de resultados que el contrato no establezca.
- PUEDE crear usuarios adicionales y dejarlos en la base durante la ejecución; NO DEBE añadir limpieza por defecto.
- Los tests de borrado DEBEN crear su propio usuario, preservando las referencias del seeder.
- Si otro test necesita modificar un dato sembrado, DEBE restaurarlo o aislar el cambio.
- NO DEBE depender del orden de ejecución de los tests. Cada test DEBE preparar sus datos adicionales.

## Tecnología y build

- DEBE usar Java 21 y Maven, conforme al objetivo del proyecto.
- DEBE mantener Spring MVC, Spring Data JPA, PostgreSQL y H2 para los tests.
- DEBE consultar `pom.xml` antes de modificar versiones y dependencias.
- PUEDE usar Lombok: `@Data`, `@Builder`, `@RequiredArgsConstructor`, `@Log4j2`, `@NoArgsConstructor`, `@AllArgsConstructor`.
- DEBERÍA verificar los cambios relevantes con `mvn verify`, que incluye integración y funcionales.
- DEBE distinguir las comprobaciones estáticas de los tests realmente ejecutados.
- Los cambios exclusivamente documentales PUEDEN verificarse mediante revisión de contenido y diff.

## Docker y perfiles (c-d)

- `application.yml` DEBE contener configuración común y los perfiles, las diferencias por entorno.
- Direcciones y parámetros de conexión DEBEN configurarse fuera del código de negocio.
- Los puertos, redes, URLs y healthchecks DEBEN ser coherentes entre Spring y Docker Compose.
- NO DEBE usar `localhost` para referirse a otro contenedor; DEBE configurar una dirección accesible desde el cliente.
- NO DEBE incorporar configuración de despliegue o permisos como parte de una tarea ajena a ese alcance.

## Antipatrones prohibidos

- DTOs en servicios o DTOs nuevos por cada variante menor de un recurso.
- Entidades JPA expuestas directamente desde resources.
- Acceso a repositorios desde resources de negocio.
- Entidades que conozcan DTOs, services o repositorios.
- Reglas de negocio en configuración, soporte técnico o clientes HTTP.
- Inicialización de creación desde el DTO o durante una lectura de JPA.
- Tests que dependan del tamaño del seeder o del orden de ejecución.
- Refactorizaciones generales, cambios de contrato o nuevas abstracciones sin relación con la tarea solicitada.

## Otros

- El código nuevo DEBE usar nombres en inglés. La documentación normativa PUEDE estar en español.
- DEBE revisar el código vigente antes de emitir recomendaciones; no basarse únicamente en esta guía.
- Las mejoras propuestas DEBEN indicar un problema concreto, su consecuencia y la solución.
- NO DEBE presentar hipótesis como fallos comprobados ni ampliar el ejercicio por escenarios especulativos.
- DEBE mantener la documentación y los tests coherentes cuando cambie un contrato o decisión arquitectónica.
