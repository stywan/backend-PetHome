# REPORTE EXHAUSTIVO: PROYECTO PETHOME-BACKEND

## RESUMEN EJECUTIVO

**PetHome Backend** es una arquitectura de **microservicios basada en Spring Boot y Spring Cloud** desarrollada en **Java 17**. Es un sistema completo de gestión de servicios veterinarios con autenticación JWT, service discovery y API Gateway.

**Ubicación:** `/Users/stywan1/Downloads/pethome-backend`
**Tamaño Total:** 1.9 MB
**Archivos Principales:** 134 archivos (Java, YAML, XML)
**Estado:** Proyecto académico activo en desarrollo (Duoc UC)

---

## 1. TIPO DE APLICACIÓN & TECNOLOGÍAS

### Tipo de Proyecto
- **Arquitectura:** Microservicios distribuidos
- **Framework:** Spring Boot 3.5.5
- **Lenguaje:** Java 17
- **Build Tool:** Maven 3.6+
- **Patrón:** Backend para aplicación web (React Frontend en puerto 3000)

### Stack Tecnológico

#### Core
- **Spring Boot:** 3.5.5
- **Spring Cloud:** 2025.0.0
- **Java:** 17

#### Componentes Cloud
- **Spring Cloud Config Server:** Configuración centralizada (puerto 8888)
- **Netflix Eureka:** Service Discovery (puerto 8761)
- **Spring Cloud Gateway:** API Gateway (puerto 8080)
- **Spring Cloud OpenFeign:** Comunicación inter-servicios
- **Spring Cloud Bootstrap:** Bootstrap properties

#### Seguridad & Autenticación
- **Spring Security:** Framework de seguridad
- **JWT (JJWT 0.11.5):** JSON Web Tokens para autenticación stateless
- **BCrypt:** Encriptación de contraseñas

#### Data
- **Spring Data JPA:** ORM y acceso a datos
- **Hibernate:** JPA implementation
- **H2 Database:** Base de datos en memoria (desarrollo)

#### Otros
- **Lombok:** Reducción de boilerplate code
- **Bean Validation:** Validación de entrada
- **Spring Boot Actuator:** Monitoreo y health checks
- **Netty:** Servidor web reactivo (Gateway)

---

## 2. ESTRUCTURA DE CARPETAS Y ARCHIVOS PRINCIPALES

### Árbol Completo del Proyecto

```
pethome-backend/
├── README.md                                    (Documentación principal)
├── pom.xml                                      (POM padre - Multi-módulo)
│
├── config-server/                               (Módulo 1: Configuración centralizada)
│   ├── pom.xml
│   ├── src/main/java/cl/duoc/config_server/
│   │   └── ConfigServerApplication.java         (Main)
│   └── src/main/resources/
│       ├── application.yml                      (Config local - puerto 8888)
│       └── config/                              (Archivos de configuración YAML)
│           ├── application.yml                  (Config común)
│           ├── eureka-server.yml
│           ├── api-gateway.yml
│           ├── pethome-user-service.yml
│           ├── pethome-veterinarian-service.yml
│           ├── pethome-pet-service.yml
│           └── pethome-appointment-service.yml
│
├── eureka-server/                               (Módulo 2: Service Discovery)
│   ├── pom.xml
│   ├── src/main/java/cl/duoc/eureka_server/
│   │   └── EurekaServerApplication.java         (Main - puerto 8761)
│   └── src/main/resources/
│       └── application.yml
│
├── api-gateway/                                 (Módulo 3: API Gateway)
│   ├── pom.xml
│   ├── src/main/java/cl/duoc/api_gateway/
│   │   └── ApiGatewayApplication.java           (Main - puerto 8080)
│   └── src/main/resources/
│       └── application.yml
│
├── pethome-user-service/                        (Módulo 4: Gestión de usuarios)
│   ├── pom.xml
│   ├── src/main/java/cl/duoc/pethome_user_service/
│   │   ├── PethomeUserServiceApplication.java   (Main - puerto 8081)
│   │   ├── controller/
│   │   │   ├── AuthController.java              (POST /auth/login, /auth/register)
│   │   │   └── UserController.java              (CRUD usuarios)
│   │   ├── service/
│   │   │   ├── AuthService.java (interface)
│   │   │   ├── UserService.java (interface)
│   │   │   └── impl/
│   │   │       ├── AuthServiceImpl.java
│   │   │       └── UserServiceImpl.java
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   └── AddressRepository.java
│   │   ├── entity/
│   │   │   ├── User.java                        (JPA Entity)
│   │   │   └── Address.java                     (Embeddable)
│   │   ├── dto/
│   │   │   ├── LoginRequestDTO.java
│   │   │   ├── LoginResponseDTO.java
│   │   │   ├── UserRequestDTO.java
│   │   │   ├── UserResponseDTO.java
│   │   │   ├── UserUpdateDTO.java
│   │   │   └── AddressDTO.java
│   │   ├── security/
│   │   │   ├── SecurityConfig.java              (JWT + Spring Security)
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   ├── JwtAuthenticationEntryPoint.java
│   │   │   └── CustomUserDetailsService.java
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── ResourceNotFoundException.java
│   │   ├── enums/
│   │   │   └── UserRole.java                    (CLIENT, VET, ADMIN)
│   │   └── util/
│   │       └── JwtUtil.java                     (JWT generation/validation)
│   └── src/main/resources/
│       ├── application.yml
│       └── application.properties
│
├── pethome-veterinarian-service/                (Módulo 5: Gestión veterinarios)
│   ├── pom.xml
│   └── src/main/java/cl/duoc/pethome_veterinarian_service/
│       ├── PethomeVeterinarianServiceApplication.java  (Main - puerto 8082)
│       └── [Estructura similar a user-service]
│
├── pethome-pet-service/                         (Módulo 6: Gestión mascotas)
│   ├── pom.xml
│   └── src/main/java/cl/duoc/pethome_pet_service/
│       ├── PetServiceApplication.java           (Main - puerto 8083)
│       └── [Estructura similar a user-service]
│
├── pethome-appointment-service/                 (Módulo 7: Gestión citas)
│   ├── pom.xml
│   └── src/main/java/cl/duoc/pethome_appointment_service/
│       ├── AppointmentServiceApplication.java   (Main - puerto 8084)
│       └── [Estructura similar a user-service]
│
├── pethome-payment-service/                     (Módulo 8: Gestión pagos - PENDIENTE)
│   ├── pom.xml
│   └── src/main/java/cl/duoc/pethome_payment_service/
│       └── [Skeleton solamente]
│
├── start-services.sh                            (Script para iniciar todos los servicios)
├── stop-services.sh                             (Script para detener todos los servicios)
├── create-users-and-veterinarians.sh           (Script de datos iniciales)
├── create-clients.sh                            (Script de datos iniciales)
│
└── logs/                                        (Directorio de logs generado)
    ├── config-server.log
    ├── eureka-server.log
    ├── api-gateway.log
    ├── user-service.log
    ├── veterinarian-service.log
    ├── pet-service.log
    └── appointment-service.log
```

---

## 3. DEPENDENCIAS PRINCIPALES

### Dependencia Maven Padre (pom.xml raíz)

```xml
Parent: org.springframework.boot:spring-boot-starter-parent:3.5.5
Spring Cloud Version: 2025.0.0
Java Version: 17
```

### Dependencias Comunes (Todos los Servicios)

```
Spring Boot Starters:
├── spring-boot-starter-data-jpa
├── spring-boot-starter-validation
├── spring-boot-starter-web
├── spring-boot-starter-security (excepto Gateway)
├── spring-boot-starter-actuator
└── spring-boot-starter-test (scope: test)

Spring Cloud:
├── spring-cloud-starter-netflix-eureka-client
├── spring-cloud-starter-config
├── spring-cloud-starter-openfeign (Vet, Pet, Appointment)
├── spring-cloud-starter-bootstrap (Pet service)
└── spring-cloud-starter-gateway (Gateway solamente)

Base de Datos:
└── h2 (runtime)

Seguridad/JWT:
├── jjwt-api:0.11.5
├── jjwt-impl:0.11.5 (runtime)
└── jjwt-jackson:0.11.5 (runtime)

Otros:
└── lombok
```

### Dependencias Específicas por Servicio

**User Service (COMPLETO):**
- Spring Security
- JWT Library
- Actuator

**Veterinarian Service (EN DESARROLLO):**
- Spring Cloud OpenFeign (para llamadas a otros servicios)

**Pet Service (EN DESARROLLO):**
- Spring Cloud OpenFeign
- Spring Cloud Bootstrap

**Appointment Service (EN DESARROLLO):**
- Spring Cloud OpenFeign
- Feign Client Configuration

**Payment Service (PENDIENTE):**
- Solo spring-boot-starter (skeleton)

---

## 4. CONFIGURACIÓN DE BASE DE DATOS

### Tipo de Base de Datos
**H2 Database** - Base de datos en memoria para desarrollo

### Configuración por Servicio

#### User Service (puerto 8081)
```yaml
datasource:
  url: jdbc:h2:mem:userdb
  driver-class-name: org.h2.Driver
  username: sa
  password: (vacío)
h2:
  console:
    enabled: true
    path: /h2-console
jpa:
  hibernate:
    ddl-auto: create-drop        # Crea/recrea tablas en cada inicio
  show-sql: true
  properties:
    hibernate:
      format_sql: true
      dialect: org.hibernate.dialect.H2Dialect
```

#### Veterinarian Service (puerto 8082)
```yaml
datasource.url: jdbc:h2:mem:veterinariandb
```

#### Pet Service (puerto 8083)
```yaml
datasource.url: jdbc:h2:mem:petdb
```

#### Appointment Service (puerto 8084)
```yaml
datasource.url: jdbc:h2:mem:appointmentdb
```

### Acceso H2 Console (Desarrollo)
```
User Service:     http://localhost:8081/h2-console
Veterinarian:     http://localhost:8082/h2-console
Pet Service:      http://localhost:8083/h2-console
Appointment:      http://localhost:8084/h2-console

Credenciales:
  JDBC URL: Ver configuración anterior
  Usuario: sa
  Password: (dejar vacío)
```

### Nota Importante
- DDL-AUTO en **create-drop**: Tabla se borra y recrea en cada inicio
- Ideal para desarrollo, NO para producción
- Para Docker, se debería cambiar a **validate** o **update** con BD persistente (PostgreSQL/MySQL)

---

## 5. VARIABLES DE ENTORNO & CONFIGURACIÓN

### No hay archivos .env
El proyecto **NO utiliza archivos .env** tradicionales. La configuración se centraliza en **Config Server**.

### Configuración Centralizada (Config Server - Puerto 8888)

**Ubicación:** `/config-server/src/main/resources/config/`

#### application.yml (Configuración común para todos)
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    fetch-registry: true
    register-with-eureka: true
  instance:
    prefer-ip-address: true

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always

logging:
  level:
    root: INFO
    cl.duoc: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
```

#### eureka-server.yml
```yaml
server:
  port: 8761
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
  server:
    enable-self-preservation: false
```

#### api-gateway.yml
```yaml
server:
  port: 8080
spring:
  cloud:
    gateway:
      routes: [7 rutas configuradas]
      globalcors: [CORS para http://localhost:3000]
```

#### pethome-user-service.yml
```yaml
server:
  port: 8081
jwt:
  secret: pethome-secret-key-for-jwt-authentication-2024
  expiration: 86400000  # 24 horas en milisegundos
spring:
  datasource:
    url: jdbc:h2:mem:userdb
    username: sa
    password: (vacío)
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

### Variables de Entorno Críticas

```
JWT_SECRET=pethome-secret-key-for-jwt-authentication-2024
JWT_EXPIRATION=86400000

CONFIG_SERVER_URL=http://localhost:8888
EUREKA_SERVER_URL=http://localhost:8761/eureka/
```

### Configuración por Perfil
- **Perfil actual:** default (desarrollo)
- **Para producción:** Implementar perfiles (prod) en Config Server

---

## 6. SCRIPTS DISPONIBLES

### 1. start-services.sh
```bash
chmod +x start-services.sh
./start-services.sh
```

**Funcionalidad:**
- Inicia 7 servicios en orden correcto
- Espera a que cada servicio esté listo antes de iniciar el siguiente
- Crea logs en `./logs/` para cada servicio
- Retorna PIDs de procesos
- Tiempo estimado de startup: ~2-3 minutos

**Orden de inicio (IMPORTANTE):**
1. Config Server (8888)
2. Eureka Server (8761)
3. API Gateway (8080)
4. User Service (8081)
5. Veterinarian Service (8082)
6. Pet Service (8083)
7. Appointment Service (8084)

### 2. stop-services.sh
```bash
chmod +x stop-services.sh
./stop-services.sh
```

**Funcionalidad:**
- Detiene todos los servicios por puerto
- Mata procesos en orden inverso
- Colorized output (rojo/verde)

### 3. create-users-and-veterinarians.sh
```bash
chmod +x create-users-and-veterinarians.sh
./create-users-and-veterinarians.sh
```

**Funcionalidad:**
- Crea datos iniciales de prueba
- Usuarios de ejemplo
- Veterinarios de ejemplo

### 4. create-clients.sh
```bash
chmod +x create-clients.sh
./create-clients.sh
```

**Funcionalidad:**
- Crea clientes iniciales
- Datos de prueba para desarrollo

### Maven Scripts

```bash
# Build completo
mvn clean install

# Ejecutar Config Server
cd config-server && mvn spring-boot:run

# Ejecutar Eureka Server
cd eureka-server && mvn spring-boot:run

# Ejecutar API Gateway
cd api-gateway && mvn spring-boot:run

# Ejecutar User Service
cd pethome-user-service && mvn spring-boot:run

# Build sin tests
mvn clean install -DskipTests

# Generar JAR (por módulo)
mvn clean package
```

---

## 7. CONFIGURACIÓN DOCKER

### Situación Actual
**NO hay configuración Docker** en el proyecto (ningún Dockerfile ni docker-compose.yml)

### Archivos que se NECESITAN crear:

```
pethome-backend/
├── Dockerfile.config-server
├── Dockerfile.eureka-server
├── Dockerfile.api-gateway
├── Dockerfile.pethome-user-service
├── Dockerfile.pethome-veterinarian-service
├── Dockerfile.pethome-pet-service
├── Dockerfile.pethome-appointment-service
├── Dockerfile.pethome-payment-service
├── docker-compose.yml
└── .dockerignore
```

### Recomendaciones para Dockerización

1. **Imagen Base:** `openjdk:17-slim` o `eclipse-temurin:17-jdk-slim`
2. **Multi-stage builds:** Para optimizar tamaño
3. **Health Checks:** Aprovechar `/actuator/health`
4. **Redes Docker:** Crear red personalizada para comunicación intra-contenedores
5. **Volúmenes:** Para logs persistentes
6. **Variables de Entorno:** Parametrizar puertos y configuración
7. **Orden de startup:** Usar depends_on con health checks
8. **BD Persistente:** Migrar a PostgreSQL/MySQL en contenedor separado

---

## 8. PUERTOS Y SERVICIOS

### Mapa de Puertos

```
PORT    SERVICE                      STATUS              PROTOCOLO
────────────────────────────────────────────────────────────────
8888    Config Server               REQUERIDO PRIMERO   HTTP
8761    Eureka Server (Dashboard)   REQUERIDO SEGUNDO   HTTP
8080    API Gateway                 REQUERIDO TERCERO   HTTP
8081    User Service                COMPLETO (100%)     HTTP
8082    Veterinarian Service        EN DESARROLLO       HTTP
8083    Pet Service                 EN DESARROLLO       HTTP
8084    Appointment Service         EN DESARROLLO       HTTP
8085    Payment Service             PENDIENTE           HTTP
3000    Frontend React              (externo)           HTTP
```

### Endpoints Principales

#### API Gateway (puerto 8080) - Punto de entrada único
```
POST   /api/auth/login              -> pethome-user-service
POST   /api/auth/register           -> pethome-user-service
GET    /api/users                   -> pethome-user-service
GET    /api/users/{id}              -> pethome-user-service
PUT    /api/users/{id}              -> pethome-user-service
DELETE /api/users/{id}              -> pethome-user-service
GET    /api/veterinarians           -> pethome-veterinarian-service
GET    /api/pets                    -> pethome-pet-service
GET    /api/appointments            -> pethome-appointment-service
GET    /api/payments                -> pethome-payment-service
```

#### Eureka Dashboard
```
http://localhost:8761

Muestra:
- Servicios registrados
- Instancias por servicio
- Estado de salud
- Información de replicación
```

#### Actuator Endpoints (Todos los servicios)
```
GET /actuator/health              Estado del servicio
GET /actuator/info                Información del servicio
GET /actuator/metrics             Métricas
GET /actuator/env                 Variables de entorno
```

#### H2 Console (Desarrollo - Todos los servicios)
```
Ubicación: http://localhost:{PUERTO}/h2-console

User Service:         http://localhost:8081/h2-console
Veterinarian Service: http://localhost:8082/h2-console
Pet Service:          http://localhost:8083/h2-console
Appointment Service:  http://localhost:8084/h2-console
```

---

## 9. ARCHIVOS DE CONFIGURACIÓN PRINCIPALES

### Configuración de Seguridad (User Service)

**SecurityConfig.java** - Configuración Spring Security con JWT
```
- CSRF deshabilitado (API REST stateless)
- Session Management: STATELESS (para JWT)
- Autorización:
  * /auth/** - PÚBLICO
  * /h2-console/** - PÚBLICO (desarrollo)
  * /actuator/** - PÚBLICO
  * /error - PÚBLICO
  * Resto - REQUERIDA AUTENTICACIÓN JWT
- Password Encoder: BCryptPasswordEncoder
- Authentication Provider: DaoAuthenticationProvider
- JWT Filter: JwtAuthenticationFilter
- Exception Handler: JwtAuthenticationEntryPoint
```

**JwtUtil.java** - Utilidad JWT
```
- Generación de tokens (email + rol)
- Extracción de claims
- Validación de tokens
- Verificación de expiración
- Algoritmo: HS256 (HMAC SHA-256)
```

### Gateway Configuration (api-gateway.yml)

```yaml
7 rutas configuradas con:
1. user-service-auth     /api/auth/**
2. user-service          /api/users/**
3. veterinarian-service  /api/veterinarians/**
4. pet-service           /api/pets/**
5. appointment-service   /api/appointments/**,/api/services/**
6. payment-service       /api/payments/**

CORS Habilitado:
- Origins: localhost:3000, 127.0.0.1:3000, 192.168.1.86:3000, 0.0.0.0:3000
- Methods: GET, POST, PUT, DELETE, OPTIONS, PATCH
- Headers: * (todos)
- Credentials: true
```

### Logging Configuration

```yaml
Nivel de Log:
  root: INFO
  cl.duoc: DEBUG (namespace custom para el proyecto)

Patrón: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
```

---

## 10. SEGURIDAD & AUTENTICACIÓN

### Autenticación JWT (Implementada)

#### Flujo de Autenticación
```
1. Usuario envía email + password a POST /auth/login
2. AuthService valida credenciales (BCrypt comparison)
3. Si válido, genera JWT token:
   - Subject: email
   - Claims: { "role": "CLIENT|VET|ADMIN" }
   - Expiration: 24 horas (86400000 ms)
   - Algoritmo: HS256
4. Retorna token al cliente
5. Cliente envía token en header: Authorization: Bearer {TOKEN}
6. JwtAuthenticationFilter valida el token en cada request
7. Si válido, usuario autenticado; si no, 401 Unauthorized
```

#### Componentes de Seguridad

**1. SecurityConfig.java**
```java
- Configuración de Spring Security
- Cadena de filtros (filter chain)
- Endpoints públicos vs protegidos
- Password encoding (BCrypt)
- Authentication manager
```

**2. JwtUtil.java**
```java
- Generar tokens con claims personalizados
- Extraer email y rol del token
- Validar token contra email
- Verificar expiración
- Manejo de excepciones (ExpiredJwtException, etc.)
```

**3. JwtAuthenticationFilter.java**
```java
- Intercepta cada request
- Extrae token del header Authorization: Bearer
- Valida token con JwtUtil
- Establece Authentication en SecurityContext
- Propaga excepciones a JwtAuthenticationEntryPoint
```

**4. JwtAuthenticationEntryPoint.java**
```java
- Maneja excepciones de autenticación
- Retorna 401 Unauthorized con mensaje en JSON
```

**5. CustomUserDetailsService.java**
```java
- Implementa UserDetailsService
- Carga usuario desde BD
- Mapea a Spring Security UserDetails
- Usado por DaoAuthenticationProvider
```

### Roles Implementados

```
CLIENT  - Clientes regulares del sistema
VET     - Veterinarios
ADMIN   - Administradores del sistema
```

### Encriptación de Contraseñas

```
Algoritmo: BCrypt
Salt: Automático (incluido en hash)
Strength: 10 (default)
```

**Ejemplo de hash BCrypt:**
```
plaintext:  password123
hash:       $2a$10$dXj3SW6G7P50ecc.tz...
```

### Endpoints Públicos vs Protegidos

**PÚBLICOS (sin autenticación):**
```
POST   /auth/login      - Login de usuario
POST   /auth/register   - Registro de usuario
GET    /h2-console/**   - H2 Console (solo desarrollo)
GET    /actuator/**     - Actuator endpoints (monitoreo)
GET    /error           - Página de error
```

**PROTEGIDOS (requieren JWT):**
```
GET    /users           - Listar usuarios
GET    /users/{id}      - Obtener usuario
PUT    /users/{id}      - Actualizar usuario
DELETE /users/{id}      - Eliminar usuario
GET    /users/email/{email}
GET    /users/role/{role}
[Todos los endpoints de otros servicios]
```

### Token JWT Ejemplo

```
Header:
{
  "alg": "HS256",
  "typ": "JWT"
}

Payload:
{
  "role": "CLIENT",
  "sub": "juan@example.com",
  "iat": 1699540800,
  "exp": 1699627200
}

Signature:
HMACSHA256(
  base64(header) + "." + base64(payload),
  "pethome-secret-key-for-jwt-authentication-2024"
)
```

### Validaciones de Entrada

**Spring Validation (Bean Validation)**
```
@NotBlank        - Campo no vacío
@Email           - Debe ser email válido
@NotNull         - No puede ser nulo
@Size            - Tamaño específico
@Pattern         - Expresión regular
```

### Manejo de Excepciones

**GlobalExceptionHandler.java**
```
- ResourceNotFoundException   -> 404 Not Found
- ValidationException         -> 400 Bad Request
- DuplicateEntryException    -> 409 Conflict
- UnauthorizedException      -> 401 Unauthorized
```

---

## RESUMEN DE ESTADO DEL PROYECTO

### Completado (100%)
- Config Server (Puerto 8888)
- Eureka Server (Puerto 8761)
- API Gateway (Puerto 8080)
- User Service (Puerto 8081)
  - Autenticación JWT completa
  - CRUD de usuarios
  - Gestión de direcciones
  - BCrypt password encoding
  - 3 roles (CLIENT, VET, ADMIN)

### En Desarrollo (Arquitectura lista)
- Veterinarian Service (Puerto 8082)
- Pet Service (Puerto 8083)
- Appointment Service (Puerto 8084)

### Pendiente
- Payment Service (Puerto 8085) - skeleton solamente
- Tests Unitarios
- Tests de Integración
- Documentación Swagger/OpenAPI
- Docker & docker-compose
- CI/CD Pipeline

---

## RECOMENDACIONES PARA DOCKERIZACIÓN

### 1. Crear Dockerfiles Base

```dockerfile
# Dockerfile base para servicios Java
FROM eclipse-temurin:17-jdk-slim AS builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-slim
EXPOSE 8080
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 2. Docker-Compose

```yaml
version: '3.9'
services:
  config-server:
    build:
      context: .
      dockerfile: Dockerfile.config-server
    ports:
      - "8888:8888"
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8888/actuator/health"]
      interval: 10s
      timeout: 5s
      retries: 5

  eureka-server:
    depends_on:
      config-server:
        condition: service_healthy
    # ... resto de config

  api-gateway:
    depends_on:
      eureka-server:
        condition: service_healthy
    # ... resto de config

  # Servicios de negocio
  user-service:
  veterinarian-service:
  pet-service:
  appointment-service:
  payment-service:

  # BD (opcional - si se migra de H2 en memoria)
  postgres:
    image: postgres:16-alpine
    volumes:
      - postgres_data:/var/lib/postgresql/data
    environment:
      POSTGRES_PASSWORD: pethome
      POSTGRES_DB: pethome_db

volumes:
  postgres_data:

networks:
  pethome-network:
    driver: bridge
```

### 3. Cambios en Configuración para Producción

```yaml
# application-prod.yml

spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/pethome_db
    username: ${DB_USER}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: validate  # NO create-drop en producción
    show-sql: false

eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/

jwt:
  secret: ${JWT_SECRET}  # Pasar como variable de entorno
  expiration: ${JWT_EXPIRATION}

logging:
  level:
    root: WARN
    cl.duoc: INFO
```

### 4. Variables de Entorno para Docker

```
JWT_SECRET=tu-secret-super-seguro-en-produccion
JWT_EXPIRATION=86400000
DB_USER=pethome
DB_PASSWORD=tu-password-seguro
CONFIG_SERVER_URL=http://config-server:8888
EUREKA_SERVER_URL=http://eureka-server:8761/eureka/
SPRING_PROFILES_ACTIVE=prod
```

---

## FLUJO DE INICIALIZACIÓN

### Orden Correcto de Inicio
```
1. Config Server (8888)
   ├─ Lee archivos YAML de ./config/
   ├─ Exposición: http://localhost:8888
   └─ Esperado: "Started ConfigServerApplication"

2. Eureka Server (8761) [Espera Config Server]
   ├─ Se registra como cliente de Config Server
   ├─ Dashboard: http://localhost:8761
   └─ Esperado: "Started EurekaServerApplication"

3. API Gateway (8080) [Espera Eureka]
   ├─ Se registra en Eureka como "api-gateway"
   ├─ Configuración de rutas desde Config Server
   └─ Esperado: "Started ApiGatewayApplication"

4. User Service (8081) [Espera API Gateway]
   ├─ Se registra en Eureka como "pethome-user-service"
   ├─ Obtiene config de Config Server
   ├─ Crea BD H2 en memoria
   └─ Esperado: "Started PethomeUserServiceApplication"

5-7. Otros servicios (8082-8084)
   └─ Mismo patrón que User Service
```

### Validación de Startup

```bash
# Verificar Config Server
curl http://localhost:8888/pethome-user-service/default

# Verificar Eureka
curl http://localhost:8761/eureka/apps

# Verificar API Gateway
curl http://localhost:8080/actuator/health

# Verificar User Service
curl http://localhost:8081/actuator/health

# Verificar JWT (login)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

---

## ARCHIVO .gitignore

```
target/
.idea/
*.iml
*.class
.DS_Store
logs/
*.log
maven_home/
.m2/
.vscode/
```

---

## NOTAS IMPORTANTES PARA DOCKERIZACIÓN

1. **Secretos:** No hardcodear jwt.secret en Dockerfile
2. **Salud:** Usar /actuator/health para health checks
3. **Dependencias:** Usar depends_on con condition: service_healthy
4. **Redes:** Usar red Docker para comunicación intra-contenedores
5. **Puertos:** Mapear correctamente puerto externo:puerto interno
6. **Variables:** Usar docker-compose.env o secrets
7. **BD:** Migrar de H2 en memoria a PostgreSQL/MySQL persistente
8. **Logs:** Mapear /logs a volumen Docker
9. **TTL:** Configurar apropiadament timeouts en Feign/Eureka
10. **Profiles:** Crear application-docker.yml con configuración específica

---

## CONCLUSIÓN

PetHome Backend es una **arquitectura de microservicios bien estructurada** con:
- Patrón completo de cloud native (Config Server, Eureka, Gateway)
- Autenticación JWT segura
- User Service completamente funcional
- Base lista para otros servicios

**Lo que falta para producción:**
- Docker & docker-compose
- BD relacional persistente (PostgreSQL)
- Swagger/OpenAPI documentation
- Tests unitarios e integración
- Logging centralizado (ELK Stack)
- Monitoreo avanzado (Prometheus, Grafana)
- CI/CD pipeline (Jenkins, GitLab CI, GitHub Actions)

