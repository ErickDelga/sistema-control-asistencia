# Sistema de Control de Asistencia Escolar

Aplicación web desarrollada con **Spring Boot** para gestionar la asistencia de estudiantes por **año, tipo de bachillerato y sección**.

El sistema permite a los docentes registrar asistencia y a la administración consultar información y reportes de forma organizada.

---

# Funcionalidades principales

- Gestión de usuarios del sistema
- Registro y administración de estudiantes
- Creación y gestión de clases
- Registro de asistencia por clase
- Dashboard con estadísticas de asistencia
- Consulta de reportes
- Importación masiva de estudiantes mediante archivos CSV
- Fotos opcionales para estudiantes

---

# Roles del sistema

## Administrador
- Gestiona usuarios
- Acceso completo al sistema

## Rectoría
- Gestiona estudiantes y clases
- Consulta reportes y asistencias

## Docente
- Crea clases
- Registra asistencia
- Gestiona estudiantes
- Puede importar estudiantes mediante CSV
- Solo visualiza las clases que ha creado

## Secretaría
- Consulta estudiantes, clases y asistencias
- Acceso a reportes

---

# Tecnologías utilizadas

## Backend
- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA

## Frontend
- Thymeleaf
- Bootstrap
- Chart.js

## Base de datos
- MySQL

## Herramientas
- Maven
- Git
- GitHub
- IntelliJ IDEA

---

# Ejecución del proyecto

## 1. Crear la base de datos

```sql
CREATE DATABASE asistencia_db;
```

## 2. Configurar application.properties

Ubicación:

```
src/main/resources/application.properties
```

Ejemplo de configuración:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/asistencia_db
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## 3. Ejecutar la aplicación

Desde IntelliJ ejecutar la clase:

```
AsistenciaApplication
```

O desde la terminal:

```bash
mvn spring-boot:run
```

La aplicación estará disponible en:

```
http://localhost:8080
```

---

# Importación de estudiantes por CSV

El sistema permite cargar estudiantes mediante archivos CSV con el siguiente formato:

```
nombreCompleto,anio,tipoBachillerato,seccion
Juan Pérez,PRIMERO,GENERAL,A
Ana López,SEGUNDO,TECNICO,B
Carlos Martínez,TERCERO,SOFTWARE,A
```

---

# Autor

Proyecto académico desarrollado para la gestión de asistencia en instituciones educativas.