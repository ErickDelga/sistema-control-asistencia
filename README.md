SSistema de Control de Asistencia
Descripción

Sistema backend para la gestión de usuarios, estudiantes y registro de asistencia, desarrollado con Spring Boot, siguiendo el modelo MVC aplicado a servicios REST.

El proyecto se desarrolla de forma incremental según un cronograma académico.

Arquitectura

Arquitectura MVC + capa de servicios:

controller → Endpoints REST

service → Lógica de negocio

repository → Acceso a datos (JPA)

model → Entidades del sistema

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

Registro de asistencia

Consultas de asistencia por fecha, grado y estudiante (Semana 9)

Seguridad

Autenticación básica con Spring Security

Usuario de prueba para entorno académico

Endpoints protegidos bajo /api/**

Endpoints principales
Estudiantes
POST   /api/estudiantes
GET    /api/estudiantes
PUT    /api/estudiantes/{id}
DELETE /api/estudiantes/{id}

Asistencia
POST /api/asistencias
GET  /api/asistencias
GET  /api/asistencias/grado/{grado}
GET  /api/asistencias/fecha/{fecha}
GET  /api/asistencias/estudiante/{id}

Estado del proyecto

📌 Avance actual: Semana 9 completada
🚀 Proyecto en evolución según cronograma académico

Autor

Erick Delgado