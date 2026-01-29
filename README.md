Sistema de Control de Asistencia
Descripción

Sistema para la gestión de usuarios, estudiantes y registro de asistencia, desarrollado con Spring Boot, siguiendo el modelo MVC, combinando servicios REST y vistas web.

El proyecto se desarrolla de forma incremental según un cronograma académico, incorporando funcionalidades semana a semana hasta su despliegue final.

Arquitectura

Arquitectura MVC + capa de servicios:

controller

Endpoints REST (/api/**)

Controladores MVC para vistas web

service

Lógica de negocio

repository

Acceso a datos (Spring Data JPA)

model

Entidades del sistema (JPA)

dto

Objetos de transferencia para peticiones específicas

Model: Entidades JPA
View: Respuestas JSON y vistas HTML (Thymeleaf)
Controller: Controladores REST y MVC

Tecnologías

Java 21 (LTS)

Spring Boot 4.0.1

Spring Data JPA

Spring Security (HTTP Basic + Form Login)

Thymeleaf

MySQL

Maven

Funcionalidades implementadas

Gestión de usuarios

Seguridad básica y roles del sistema

CRUD de estudiantes

Registro de asistencia

Consultas de asistencia por:

Fecha

Grado

Estudiante (Semana 9)

Interfaz web con Thymeleaf (Semana 10):

Login personalizado

Menú principal

Vistas para estudiantes y asistencia

Integración de vistas con servicios existentes

Seguridad

Autenticación básica con Spring Security para endpoints REST

Autenticación por formulario para vistas web

Usuario de prueba para entorno académico

Endpoints protegidos bajo /api/**

Acceso a vistas restringido a usuarios autenticados

Endpoints principales (API REST)
Estudiantes

POST /api/estudiantes

GET /api/estudiantes

PUT /api/estudiantes/{id}

DELETE /api/estudiantes/{id}

Asistencia

POST /api/asistencias

GET /api/asistencias

GET /api/asistencias/grado/{grado}

GET /api/asistencias/fecha/{fecha}

GET /api/asistencias/estudiante/{id}

Vistas web principales

GET /login → Pantalla de autenticación

GET / → Home del sistema

GET /menu → Menú principal

GET /estudiantes → Gestión visual de estudiantes

GET /asistencias → Registro y consulta de asistencia

Estado del proyecto

📌 Avance actual: Semana 10 completada
🚀 Proyecto en evolución según cronograma académico, orientado a un sistema real de control de asistencia institucional.

Autor

Erick Delgado