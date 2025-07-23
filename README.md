## [Máster en Ingeniería Web por la Universidad Politécnica de Madrid (miw-upm)](http://miw.etsisi.upm.es)

## Arquitectura y Patrones para Aplicaciones Web (APAW)
> Este proyecto es un apoyo docente de la asignatura y un ejemplo práctico del desarrollo de una aplicación Web siguiendo una Arquitectura por capas

Es un ejemplo de un API Rest completo, basado en Spring Boot, con una arquitectura de tres capas, y almacenamiento en
bases de datos con JPA soportado por Hibernate y Postgres.
La seguridad esta basada en OAuth2 y OpenId Connect, desarrollando ambos procesos en el API

## Tecnologías necesarias
`Java` `Maven` `GitHub` `GitHub Actions` `Sonarcloud` `Slack` `Spring-Boot` `GitHub Packages` `OpenAPI` `JPA` `PostgreSQL` `Docker` 

### Estado del código
[![DevOps](https://github.com/miw-upm/apaw-user/actions/workflows/continuous-integration.yml/badge.svg)](https://github.com/miw-upm/apaw-user/actions/workflows/continuous-integration.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=es.upm.miw%3Aapaw-user&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=es.upm.miw%3Aapaw-user)
[![Render broken](https://apaw-user-latest.onrender.com/version-badge)](https://apaw-user-latest.onrender.com/swagger-ui.html)

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
docker network create devopsNet
```
* Ver redes:
```sh
docker network ls
```
* Comando Docker para crear imagen y arrancar contenedor con la imagen ( :warning: **incluir el punto final** ):
```sh
docker build -t apaw-user:latest .
docker run -d --name apaw-user1  -p 8081:8081 apaw-user
```

* Comando para crear imagen y arrancarla en contenedor mediante docker compose (Se utiliza el fichero **docker-compose.yml**)
```sh
docker compose up --build -d
```

* Necesita de una bases de datos: **url:** `jdbc:postgresql://localhost:5432/apawuserdb`, **username:**`postgres` y *
  *password:** `postgres`. Recordar que la BD **apawuserdb** deben existir previamente.

* Se aporta un fichero `docker-compose-db.yml`que monta 2 motores de BD sobre Docker: Postgres y MongoDB.

```sh
> docker compose -f docker-compose-db.yml -p databases up -d
```

* Cliente Web: `http://localhost:8081/swagger-ui.html`

