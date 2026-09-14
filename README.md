## [Máster en Ingeniería Web por la Universidad Politécnica de Madrid (miw-upm)](http://miw.etsisi.upm.es)

## Arquitectura y Patrones para Aplicaciones Web (APAW)

> Este proyecto es un apoyo docente de la asignatura y un ejemplo práctico del desarrollo de una aplicación Web con
> microservicios

Es un ejemplo de un API Rest completo, basado en Spring Boot, con una arquitectura de 3-capas, y almacenamiento en
bases de datos con JPA soportado por Hibernate y Postgres y con despliegue en AWS

## Tecnologías necesarias

`Java` `Maven` `GitHub` `GitHub Actions` `Sonarcloud` `Slack` `Spring-Boot` `Spring-cloude` `GitHub Packages` `OpenAPI` `JPA`
`PostgreSQL` `Docker` `AWS`

### :gear: Instalación del proyecto

1. Clonar el repositorio en tu equipo, **mediante consola**:

```sh
> cd <folder path>
> git clone https://github.com/miw-upm/apaw-user
```

2. Importar el proyecto mediante **IntelliJ IDEA**
    * **Open**, y seleccionar la carpeta del proyecto.

### :gear: Ejecución en local con IntelliJ

* Ejecutar la clase **Application** con IntelliJ

### :gear: Ejecución en local con Docker

* Crear la red, solo una vez:

```sh
docker network create apawnet
```

* Ver redes:

```sh
docker network ls
```

* Comando para crear imagen y arrancarla en contenedor mediante Docker Compose (Se utiliza el fichero *
  *docker-compose.yml**). Localmente 

```sh
docker compose up --build -d
```

* Necesita de una bases de datos: **url:** `jdbc:postgresql://localhost:5432/apawuserdb`, **username:**`postgres` y *
  *password:** `postgres`. Recordar que la BD **apawuserdb** deben existir previamente.

* Se aporta un fichero `docker-compose-db.yml`que monta Postgres sobre Docker.

```sh
> docker compose -f docker-compose-db.yml -p databases up -d
```

* Para el despliegue de Postgres en AWS, se aporta un deploy (cd-db-postgres-staging), para dispararlo subir la rama
* `postgres`

* Cliente Web: `http://localhost:8081/swagger-ui.html`

