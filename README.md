# 📘 Sistema de Control de Asistencia

## 📖 Descripción

Sistema web para la gestión de usuarios, estudiantes y registro de asistencia escolar, desarrollado con **Spring Boot**, siguiendo el patrón de arquitectura **MVC + Service Layer**, combinando servicios REST y vistas web con Thymeleaf.

El proyecto se desarrolla de forma incremental según un cronograma académico, incorporando funcionalidades semana a semana hasta su consolidación final.

---

# 🏗 Arquitectura

Arquitectura basada en el patrón:

**MVC + Capa de Servicios**

### 📂 Estructura del Proyecto

- **controller**
    - Endpoints REST (`/api/**`)
    - Controladores MVC para vistas web
- **service**
    - Lógica de negocio
    - Validaciones del sistema
- **repository**
    - Acceso a datos mediante Spring Data JPA
- **model**
    - Entidades del sistema (JPA)
- **dto**
    - Objetos de transferencia de datos para peticiones específicas
- **templates**
    - Vistas HTML con Thymeleaf
- **static**
    - Archivos JS y CSS externos

### 📌 Aplicación del patrón MVC

- **Model** → Entidades JPA
- **View** → Respuestas JSON y vistas HTML (Thymeleaf)
- **Controller** → Controladores REST y MVC

---

# 🛠 Tecnologías Utilizadas

- Java 21 (LTS)
- Spring Boot 4.0.1
- Spring Data JPA
- Spring Security (HTTP Basic + Form Login)
- Thymeleaf
- MySQL
- Maven
- Bootstrap (CDN)
- JavaScript externo para validaciones

---

# 🚀 Funcionalidades Implementadas

## 👤 Gestión de Usuarios

- Registro y autenticación de usuarios
- Encriptación de contraseñas
- Roles del sistema:
    - ADMIN
    - DOCENTE
    - RECTORIA
- Restricción de acceso por rol

---

## 🎓 Gestión de Estudiantes

- Crear estudiante
- Listar estudiantes
- Actualizar estudiante
- Eliminar estudiante
- Validaciones backend
- Validaciones JavaScript externas (Semana 11)
- Integración completa con vistas Thymeleaf

---

## 📋 Gestión de Asistencias

- Registro de asistencia por estudiante
- Estados:
    - Presente
    - Ausente
    - Tarde
- Consultas por:
    - Fecha
    - Grado
    - Estudiante
- Validación para evitar registros duplicados por fecha
- Mejoras en flujo de registro
- Resumen y optimización de consultas

---

# 🔐 Seguridad

- Autenticación HTTP Basic para endpoints REST
- Autenticación por formulario para vistas web
- Protección de rutas `/api/**`
- Acceso a vistas restringido a usuarios autenticados
- Manejo de roles con Spring Security
- Usuario de prueba para entorno académico

---

# 🌐 Interfaz Web (Thymeleaf)

Implementada desde Semana 10:

- Login personalizado
- Menú principal
- Vistas para estudiantes
- Vistas para asistencia
- Formularios MVC completos
- Integración de servicios existentes con interfaz gráfica

---

# 🔌 Endpoints REST Principales

## 📘 Estudiantes

- `POST /api/estudiantes`
- `GET /api/estudiantes`
- `PUT /api/estudiantes/{id}`
- `DELETE /api/estudiantes/{id}`

## 📋 Asistencias

- `POST /api/asistencias`
- `GET /api/asistencias`
- `GET /api/asistencias/grado/{grado}`
- `GET /api/asistencias/fecha/{fecha}`
- `GET /api/asistencias/estudiante/{id}`

---

# 🖥 Vistas Web Principales

- `GET /login` → Pantalla de autenticación
- `GET /` → Home del sistema
- `GET /menu` → Menú principal
- `GET /estudiantes` → Gestión visual de estudiantes
- `GET /asistencias` → Registro y consulta de asistencia

---

# 📈 Evolución del Proyecto

### 🔹 Semanas 3 – 8
- Estructura MVC
- Modelado de entidades JPA
- CRUD de usuarios
- Seguridad básica
- CRUD de estudiantes
- Registro inicial de asistencia

### 🔹 Semana 9
- Filtros avanzados de asistencia
- Consulta por estudiante
- Ajustes de seguridad

### 🔹 Semana 10
- Integración de vistas Thymeleaf
- Login personalizado
- Organización de arquitectura
- Documentación inicial

### 🔹 Semana 11
- Formularios MVC completos
- Validaciones JavaScript externas
- Separación clara entre controladores REST y MVC

### 🔹 Semana 12
- Optimización de lógica de negocio
- Mejoras en flujo de registro de asistencia
- Organización y limpieza de servicios

### 🔹 Semana 13
- Validación para evitar asistencia duplicada por fecha
- Mejoras estructurales en controladores
- Estabilidad general del sistema

---

# ▶️ Cómo Ejecutar el Proyecto

1. Clonar el repositorio:
