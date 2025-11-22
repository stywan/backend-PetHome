# Análisis Completo del Proyecto PetHome Backend

## Información General

| Propiedad | Valor |
|-----------|-------|
| **Tipo de Proyecto** | Arquitectura de Microservicios |
| **Framework Principal** | Spring Boot 3.5.5 + Spring Cloud 2025.0.0 |
| **Lenguaje** | Java 17 |
| **Build Tool** | Maven 3.6+ |
| **Tamaño Total** | 1.9 MB |
| **Estado** | Proyecto Académico en Desarrollo (Duoc UC) |

---

## Estructura del Proyecto

```
pethome-backend/
├── config-server/                    (Config Server - Puerto 8888)
├── eureka-server/                    (Service Discovery - Puerto 8761)
├── api-gateway/                      (API Gateway - Puerto 8080)
├── pethome-user-service/             (Gestión de Usuarios - Puerto 8081) ✅
├── pethome-veterinarian-service/     (Gestión de Veterinarios - Puerto 8082) ⏳
├── pethome-pet-service/              (Gestión de Mascotas - Puerto 8083) ⏳
├── pethome-appointment-service/      (Citas - Puerto 8084) ⏳
├── pethome-payment-service/          (Pagos - Puerto 8085) 📝
├── docker-compose.yml
├── pom.xml
├── README.md
├── ARCHITECTURE_DIAGRAMS.md
├── EXHAUSTIVE_ANALYSIS.md
└── DOCKER.md
```

### Estado de los Servicios

| Servicio | Puerto | Estado |
|----------|--------|--------|
| Config Server | 8888 | ✅ Completo |
| Eureka Server | 8761 | ✅ Completo |
| API Gateway | 8080 | ✅ Completo |
| User Service | 8081 | ✅ Completo (100%) |
| Veterinarian Service | 8082 | ⏳ En Desarrollo |
| Pet Service | 8083 | ⏳ En Desarrollo |
| Appointment Service | 8084 | ⏳ En Desarrollo |
| Payment Service | 8085 | 📝 Pendiente |

---

## Stack Tecnológico

### Core

- **Spring Boot:** 3.5.5
- **Spring Cloud:** 2025.0.0
- **Java:** 17
- **Maven:** 3.6+

### Componentes de Spring Cloud

| Componente | Propósito |
|------------|-----------|
| Spring Cloud Config Server | Gestión centralizada de configuración |
| Netflix Eureka | Descubrimiento y registro de servicios |
| Spring Cloud Gateway | API Gateway con balanceo de carga |
| Spring Cloud OpenFeign | Cliente REST declarativo |
| Spring Cloud Bootstrap | Carga de configuración bootstrap |

### Seguridad y Autenticación

- **Spring Security:** Framework de seguridad
- **JWT (JJWT 0.11.5):** Tokens de autenticación
  - Algoritmo: HS256
  - Expiración: 24 horas
- **BCrypt:** Encriptación de contraseñas

### Base de Datos

- **PostgreSQL en AWS RDS:** Base de datos de producción
  - Host: `dbpethome.c58egkiimr78.us-east-1.rds.amazonaws.com`
  - Puerto: 5432
  - Base de datos: `pethome`
  - Usuario: `pethome_admin`
- **H2:** Base de datos en memoria (desarrollo local)
- **Spring Data JPA:** ORM
- **Hibernate:** Implementación JPA con PostgreSQLDialect

### Librerías Adicionales

- **Lombok:** Reducción de código boilerplate
- **Bean Validation:** Validación de inputs
- **Spring Boot Actuator:** Health checks y métricas
- **Swagger/OpenAPI 2.7.0:** Documentación de API
- **Netty:** Servidor web reactivo (API Gateway)

---

## Descripción de Microservicios

### 1. Config Server (Puerto 8888)

**Propósito:** Gestión centralizada de configuración

**Características:**
- Almacena archivos YAML de configuración
- Permite configuración dinámica sin redespliegue
- Soporta múltiples perfiles (local, docker, prod)

**Ubicación de configuraciones:** `config-server/src/main/resources/config/`

---

### 2. Eureka Server (Puerto 8761)

**Propósito:** Descubrimiento y registro de servicios

**Características:**
- Registro y descubrimiento de servicios
- Health checks
- Dashboard: `http://localhost:8761`

---

### 3. API Gateway (Puerto 8080)

**Propósito:** Punto de entrada único para todas las peticiones

**Características:**
- Gestión de rutas con balanceo de carga
- Configuración CORS para frontend React
- Filtrado de peticiones/respuestas
- Reescritura de paths

**Configuración de Rutas:**

| Ruta | Servicio Destino |
|------|------------------|
| `/api/auth/**` | User Service |
| `/api/users/**` | User Service |
| `/api/veterinarians/**` | Veterinarian Service |
| `/api/pets/**` | Pet Service |
| `/api/appointments/**` | Appointment Service |
| `/api/payments/**` | Payment Service |

---

### 4. User Service (Puerto 8081) ✅

**Propósito:** Gestión de usuarios y autenticación

**Características:**
- Registro y login con JWT
- CRUD de usuarios
- Gestión de direcciones
- Roles: CLIENT, VET, ADMIN
- Encriptación BCrypt
- Validación de inputs

**Modelos de Datos:**

**User:**
- id, name, email (único), password (encriptado), phone, photo
- role (CLIENT, VET, ADMIN)
- createdAt, updatedAt
- Relación: One-to-many con Address

**Address:**
- id, user_id (FK), street, city, state, zipCode
- createdAt, updatedAt

**Endpoints:**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/auth/login` | Login de usuario |
| POST | `/auth/register` | Registro de usuario |
| GET | `/users` | Obtener todos los usuarios |
| GET | `/users/{id}` | Obtener usuario por ID |
| GET | `/users/email/{email}` | Obtener usuario por email |
| GET | `/users/role/{role}` | Obtener usuarios por rol |
| PUT | `/users/{id}` | Actualizar usuario |
| DELETE | `/users/{id}` | Eliminar usuario |

---

### 5. Veterinarian Service (Puerto 8082) ⏳

**Propósito:** Gestión de perfiles de veterinarios

**Características:**
- Registro y gestión de perfiles
- Gestión de especialidades y licencias
- Disponibilidad y horarios
- Sistema de calificación

**Modelos de Datos:**

**Veterinarian:**
- id, userId (FK), specialty, licenseNumber (único)
- clinicName, clinicAddress, workingHours, bio, photo
- isAvailable, rating, experienceYears
- createdAt, updatedAt
- Relación: One-to-many con VeterinarianSchedule

**VeterinarianSchedule:**
- id, veterinarian_id (FK)
- dayOfWeek (MON-SUN), startTime, endTime
- isActive

**Endpoints:**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/veterinarians` | Crear veterinario |
| GET | `/veterinarians` | Obtener todos |
| GET | `/veterinarians/{id}` | Obtener por ID |
| GET | `/veterinarians/user/{userId}` | Obtener por userId |
| GET | `/veterinarians/available` | Obtener disponibles |
| GET | `/veterinarians/specialty/{specialty}` | Obtener por especialidad |
| GET | `/veterinarians/search` | Buscar veterinarios |
| PUT | `/veterinarians/{id}` | Actualizar |
| DELETE | `/veterinarians/{id}` | Eliminar |
| PUT | `/veterinarians/{id}/availability` | Cambiar disponibilidad |

---

### 6. Pet Service (Puerto 8083) ⏳

**Propósito:** Gestión de mascotas e historial médico

**Características:**
- Registro y gestión de mascotas
- Historial médico
- Condiciones de salud y alergias
- Gestión de dueños

**Modelos de Datos:**

**Pet:**
- id, name, species, breed, birthDate, gender, color
- weight, medicalConditions, allergies
- ownerId (FK), isActive
- createdAt, updatedAt
- Relación: One-to-many con MedicalRecord

**MedicalRecord:**
- id, pet_id (FK), veterinarianId (FK)
- visitDate, recordType (CHECKUP, VACCINATION, SURGERY, TREATMENT, EMERGENCY, OTHER)
- diagnosis, treatment, medications, notes
- weight, temperature, heartRate, cost
- nextVisitDate, createdAt, updatedAt

**Endpoints:**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/pets` | Crear mascota |
| GET | `/pets` | Obtener todas |
| GET | `/pets/{id}` | Obtener por ID |
| GET | `/pets/owner/{ownerId}` | Obtener por dueño |
| GET | `/pets/owner/{ownerId}/active` | Obtener activas |
| GET | `/pets/species/{species}` | Obtener por especie |
| GET | `/pets/search?name={name}` | Buscar por nombre |
| PUT | `/pets/{id}` | Actualizar |
| DELETE | `/pets/{id}` | Eliminar |
| POST | `/pets/{petId}/medical-records` | Agregar historial médico |
| GET | `/pets/{petId}/medical-records` | Obtener historial médico |

---

### 7. Appointment Service (Puerto 8084) ⏳

**Propósito:** Gestión de citas veterinarias

**Características:**
- Creación y programación de citas
- Gestión de estados (pending, confirmed, completed, cancelled)
- Registro de diagnóstico y prescripción
- Manejo de cancelaciones

**Modelo de Datos:**

**Appointment:**
- id, clientId, veterinarianId, petId, serviceId
- date, time, duration, address
- status (pending|confirmed|completed|cancelled)
- notes, diagnosis, prescription
- completedAt, cancellationReason, cancelledAt
- createdAt, updatedAt

**Endpoints:**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/appointments` | Crear cita |
| GET | `/appointments` | Obtener todas |
| GET | `/appointments/{id}` | Obtener por ID |
| GET | `/appointments/client/{clientId}` | Obtener por cliente |
| GET | `/appointments/vet/{veterinarianId}` | Obtener por veterinario |
| GET | `/appointments/pet/{petId}` | Obtener por mascota |
| GET | `/appointments/date/{date}` | Obtener por fecha |
| GET | `/appointments/status/{status}` | Obtener por estado |
| GET | `/appointments/client/{clientId}/upcoming` | Obtener próximas |
| PUT | `/appointments/{id}` | Actualizar |
| PUT | `/appointments/{id}/complete` | Marcar completada |
| PUT | `/appointments/{id}/cancel` | Cancelar |
| DELETE | `/appointments/{id}` | Eliminar |

---

### 8. Payment Service (Puerto 8085) 📝

**Propósito:** Procesamiento de pagos y facturación

**Estado:** Solo existe el esqueleto de la aplicación

**Características Planificadas:**
- Procesamiento de pagos
- Gestión de facturas
- Reportes de pagos
- Historial de facturación

---

## Resumen de Endpoints API

**URL Base:** `http://localhost:8080/api` (a través de API Gateway)

### Endpoints Públicos (Sin Autenticación)

- `POST /auth/login` - Login de usuario
- `POST /auth/register` - Registro de usuario
- `GET /actuator/health` - Health check
- `GET /h2-console` - Consola H2 (solo desarrollo)

### Endpoints Protegidos (Requieren JWT)

- `/users/**` - Gestión de usuarios
- `/veterinarians/**` - Gestión de veterinarios
- `/pets/**` - Gestión de mascotas
- `/appointments/**` - Gestión de citas
- `/payments/**` - Gestión de pagos

**Formato de Request:**
```
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

---

## Configuración

### Archivos de Configuración

**Ubicación:** `config-server/src/main/resources/config/`

- `application.yml` - Configuración común
- `pethome-user-service.yml` - Config local con H2
- `pethome-veterinarian-service.yml` - Config local con H2
- `pethome-pet-service.yml` - Config local con H2
- `pethome-appointment-service.yml` - Config local con H2
- `eureka-server.yml`
- `api-gateway.yml`

**Archivos con profile `docker` (PostgreSQL/RDS):**
- `pethome-user-service-docker.yml`
- `pethome-veterinarian-service-docker.yml`
- `pethome-pet-service-docker.yml`
- `pethome-appointment-service-docker.yml`
- `eureka-server-docker.yml`
- `api-gateway-docker.yml`

> **Nota:** Los archivos con terminación `-docker` conectan a PostgreSQL en AWS RDS. El nombre "docker" es simplemente el nombre del profile de Spring Cloud Config, no significa que solo funcionen con Docker. Se activan con `--spring.profiles.active=docker`.

### Variables de Entorno (.env)

```bash
DB_HOST=your-rds-endpoint.region.rds.amazonaws.com
DB_PORT=5432
DB_NAME=pethome
DB_USERNAME=pethome_admin
DB_PASSWORD=your-database-password

JWT_SECRET=your-super-secure-jwt-secret-key-here-change-this
JWT_EXPIRATION=86400000

EUREKA_SERVER_URL=http://localhost:8761/eureka
CONFIG_SERVER_URL=http://localhost:8888
GATEWAY_URL=http://localhost:8080

CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:8080
SPRING_PROFILES_ACTIVE=dev
```

### Orden de Inicio de Servicios

1. Config Server (8888) - Debe iniciar primero
2. Eureka Server (8761) - Requiere Config Server
3. API Gateway (8080) - Requiere Config Server y Eureka
4. User Service (8081) - Requiere Config Server y Eureka
5. Veterinarian Service (8082) - Orden opcional
6. Pet Service (8083) - Orden opcional
7. Appointment Service (8084) - Orden opcional

---

## Esquemas de Base de Datos

### User Service

**Tabla: users**

| Columna | Tipo | Restricciones |
|---------|------|---------------|
| id | BIGINT | PK, Auto-increment |
| name | VARCHAR(255) | NOT NULL |
| email | VARCHAR(255) | UNIQUE, NOT NULL |
| password | VARCHAR(255) | NOT NULL (BCrypt) |
| phone | VARCHAR(20) | |
| role | VARCHAR(20) | NOT NULL |
| photo | VARCHAR(500) | |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | |

**Tabla: addresses**

| Columna | Tipo | Restricciones |
|---------|------|---------------|
| id | BIGINT | PK |
| user_id | BIGINT | FK NOT NULL |
| street | VARCHAR(255) | |
| city | VARCHAR(100) | |
| state | VARCHAR(50) | |
| zip_code | VARCHAR(20) | |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | |

### Veterinarian Service

**Tabla: veterinarians**

| Columna | Tipo | Restricciones |
|---------|------|---------------|
| id | BIGINT | PK |
| user_id | BIGINT | UNIQUE, NOT NULL |
| specialty | VARCHAR(255) | NOT NULL |
| license_number | VARCHAR(255) | UNIQUE, NOT NULL |
| clinic_name | VARCHAR(255) | |
| clinic_address | VARCHAR(500) | |
| working_hours | VARCHAR(255) | |
| bio | TEXT | |
| is_available | BOOLEAN | NOT NULL |
| rating | DECIMAL(3,2) | NOT NULL |
| experience_years | INT | |
| photo | VARCHAR(500) | |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | |

**Tabla: veterinarian_schedules**

| Columna | Tipo | Restricciones |
|---------|------|---------------|
| id | BIGINT | PK |
| veterinarian_id | BIGINT | FK, NOT NULL |
| day_of_week | VARCHAR(10) | NOT NULL |
| start_time | TIME | NOT NULL |
| end_time | TIME | NOT NULL |
| is_active | BOOLEAN | NOT NULL |

### Pet Service

**Tabla: pets**

| Columna | Tipo | Restricciones |
|---------|------|---------------|
| id | BIGINT | PK |
| name | VARCHAR(100) | NOT NULL |
| species | VARCHAR(50) | NOT NULL |
| breed | VARCHAR(50) | |
| birth_date | DATE | NOT NULL |
| gender | VARCHAR(10) | NOT NULL |
| color | VARCHAR(50) | |
| weight | DECIMAL(5,2) | |
| medical_conditions | TEXT | |
| allergies | TEXT | |
| owner_id | BIGINT | NOT NULL |
| is_active | BOOLEAN | NOT NULL |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | |

**Tabla: medical_records**

| Columna | Tipo | Restricciones |
|---------|------|---------------|
| id | BIGINT | PK |
| pet_id | BIGINT | FK, NOT NULL |
| veterinarian_id | BIGINT | NOT NULL |
| visit_date | DATE | NOT NULL |
| record_type | VARCHAR(20) | NOT NULL |
| diagnosis | TEXT | NOT NULL |
| treatment | TEXT | |
| medications | TEXT | |
| notes | TEXT | |
| weight | DECIMAL(5,2) | |
| temperature | DECIMAL(4,2) | |
| heart_rate | INT | |
| cost | DECIMAL(10,2) | |
| next_visit_date | DATE | |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | |

### Appointment Service

**Tabla: appointments**

| Columna | Tipo | Restricciones |
|---------|------|---------------|
| id | BIGINT | PK |
| client_id | BIGINT | NOT NULL |
| veterinarian_id | BIGINT | NOT NULL |
| pet_id | BIGINT | NOT NULL |
| service_id | BIGINT | NOT NULL |
| date | DATE | NOT NULL |
| time | TIME | NOT NULL |
| duration | VARCHAR(20) | |
| address | VARCHAR(500) | |
| status | VARCHAR(20) | NOT NULL |
| notes | VARCHAR(1000) | |
| diagnosis | VARCHAR(2000) | |
| prescription | VARCHAR(2000) | |
| completed_at | TIMESTAMP | |
| cancellation_reason | VARCHAR(500) | |
| cancelled_at | TIMESTAMP | |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | |

---

## Testing

### Framework de Testing

- **JUnit 5** (Spring Boot Test)
- **Ubicación:** `src/test/java`

### Archivos de Test Existentes

```
config-server/src/test/java/.../ConfigServerApplicationTests.java
pethome-user-service/src/test/java/.../PethomeUserServiceApplicationTests.java
pethome-veterinarian-service/src/test/java/.../PethomeVeterinarianServiceApplicationTests.java
pethome-pet-service/src/test/java/.../PethomePetServiceApplicationTests.java
pethome-appointment-service/src/test/java/.../PethomeAppointmentServiceApplicationTests.java
pethome-payment-service/src/test/java/.../PethomePaymentServiceApplicationTests.java
```

**Estado:** ⏳ Solo existen tests básicos de contexto de aplicación. Tests unitarios e integración pendientes.

---

## Seguridad

### Implementación JWT

**Configuración del Token:**
- Algoritmo: HS256 (HMAC-SHA256)
- Expiración: 86400000ms (24 horas)
- Secret por defecto: `pethome-secret-key-for-jwt-authentication-2024`

**Claims del Token:**
- `sub` (subject): Email del usuario
- `role`: Rol del usuario
- `iat`: Timestamp de creación
- `exp`: Timestamp de expiración

### Endpoints Públicos vs Protegidos

**Públicos (Sin autenticación):**
- `/auth/**`
- `/h2-console/**`
- `/actuator/**`
- `/v3/api-docs/**`
- `/swagger-ui/**`

**Protegidos (Requieren JWT):**
- Todos los demás endpoints

### Configuración CORS (API Gateway)

```yaml
allowed-origins:
  - http://localhost:3000
  - http://127.0.0.1:3000
  - http://192.168.1.86:3000
  - http://0.0.0.0:3000
allowed-methods: GET, POST, PUT, DELETE, OPTIONS, PATCH
allowed-headers: *
max-age: 3600
credentials: true
```

### Buenas Prácticas Implementadas

- ✅ Encriptación de contraseñas con BCrypt
- ✅ Autenticación stateless con JWT
- ✅ CORS configurado correctamente
- ✅ CSRF deshabilitado para REST API
- ✅ Gestión de sesiones stateless
- ✅ Consola H2 restringida (solo desarrollo)
- ✅ Manejo de excepciones de seguridad
- ✅ Validación de inputs

### Recomendaciones para Producción

- ⚠️ Cambiar secret key de JWT (actualmente usa key por defecto)
- ⚠️ Cambiar `ddl-auto: create-drop` a `validate` o `update` en archivos `-docker.yml` (actualmente borra datos al reiniciar)
- ⚠️ Implementar HTTPS/TLS
- ⚠️ Agregar rate limiting
- ⚠️ Implementar refresh tokens
- ⚠️ Agregar logging de auditoría
- ✅ PostgreSQL en AWS RDS ya implementado

---

## Scripts de Automatización

### Scripts Disponibles

| Script | Descripción |
|--------|-------------|
| `start-services.sh` | Inicio automático de todos los servicios |
| `stop-services.sh` | Apagado graceful de servicios |
| `docker-build.sh` | Build de imágenes Docker |
| `docker-start.sh` | Inicio de servicios via Docker |
| `docker-stop.sh` | Detener servicios Docker |
| `docker-logs.sh` | Ver logs de Docker |
| `start-services-postgres.sh` | Iniciar con PostgreSQL |
| `create-users-and-veterinarians.sh` | Crear datos de prueba |
| `create-clients.sh` | Crear usuarios de prueba |

---

## Soporte Docker

### Dockerfiles Disponibles

- config-server
- eureka-server
- api-gateway
- user-service
- veterinarian-service
- pet-service
- appointment-service

### docker-compose.yml

Configurado con:
- Dependencias entre servicios
- Health checks
- Variables de entorno
- Red personalizada
- Mapeo de volúmenes

---

## Monitoreo y Salud

### Endpoints de Actuator

- `/actuator/health` - Estado de salud
- `/actuator/metrics` - Métricas

### Dashboards

- Eureka: `http://localhost:8761`
- H2 Console (dev): `http://localhost:{port}/h2-console`

---

## Resumen Final

### Fortalezas del Proyecto

- ✅ Arquitectura de microservicios bien estructurada
- ✅ Separación clara de responsabilidades
- ✅ Implementación completa de seguridad con JWT
- ✅ Gestión centralizada de configuración
- ✅ Service discovery con Eureka
- ✅ API Gateway para routing y CORS
- ✅ Documentación del código
- ✅ Soporte Docker para containerización
- ✅ Código limpio usando Lombok
- ✅ Manejo apropiado de excepciones y validación

### Estado Actual

| Componente | Estado |
|------------|--------|
| User Service | 100% Completo |
| Servicios de Veterinario y Mascotas | En Desarrollo |
| Appointment Service | En Desarrollo |
| Payment Service | Pendiente |
| Tests | Mínimos (solo placeholders) |
| Despliegue en Producción | Requiere configuración adicional |

### Listo Para

- ✅ Desarrollo y testing local
- ✅ Containerización con Docker
- ✅ PostgreSQL en AWS RDS (ya implementado)
- ✅ Desarrollo de servicios adicionales
- ✅ Integración con frontend React (puerto 3000)

### Trabajo Pendiente

- 📝 Completar Payment Service
- 📝 Tests unitarios e integración
- 📝 Documentación Swagger/OpenAPI
- 📝 Diagramas de secuencia
- 📝 Guías de despliegue en producción
- 📝 Contributing guidelines
- 📝 Cambiar `ddl-auto: create-drop` a `validate` en producción

---

*Análisis generado el 21 de Noviembre de 2025*
