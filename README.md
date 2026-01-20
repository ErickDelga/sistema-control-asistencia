Sistema de Control de Asistencia
Descripción

Sistema backend para la gestión de usuarios, estudiantes y registro de asistencia, desarrollado con Spring Boot, siguiendo el modelo MVC aplicado a servicios REST.

El proyecto se desarrolla de forma incremental según un cronograma académico.

Arquitectura

Arquitectura MVC + capa de servicios:

controller  → Endpoints REST
service     → Lógica de negocio
repository  → Acceso a datos (JPA)
model       → Entidades del sistema


Model: Entidades JPA

View: Respuestas JSON

Controller: Controladores REST

Tecnologías

Java 21 (LTS)

Spring Boot 4.0.1

Spring Data JPA

Spring Security (HTTP Basic)

MySQL

Maven

Funcionalidades implementadas

Gestión de usuarios

Seguridad básica y roles del sistema

CRUD de estudiantes

Registro y consulta básica de asistencia

Seguridad

Autenticación básica con Spring Security

Usuario de prueba para entorno académico

Endpoints protegidos bajo /api/**

Endpoints principales
Estudiantes
POST   /api/estudiantes
GET    /api/estudiantes
DELETE /api/estudiantes/{id}

Asistencia
POST /api/asistencias
GET  /api/asistencias

Estado del proyecto

📌 Avance actual: Semana 8 completada
🚧 En desarrollo

Autor

Erick Delgado