# DIAGRAMAS DE ARQUITECTURA - PETHOME BACKEND

## 1. DIAGRAMA DE ARQUITECTURA GENERAL

```
                          CLIENTE FRONTEND
                         (React - Puerto 3000)
                                 |
                                 | HTTP/REST
                                 v
                    ╔════════════════════════╗
                    ║     API GATEWAY        ║
                    ║    (Puerto 8080)       ║
                    ║  Spring Cloud Gateway  ║
                    ╚════════════════════════╝
                             |
           ┌─────────────────┼─────────────────┐
           |                 |                 |
           v                 v                 v
    ╔════════════════╗ ╔════════════════╗ ╔════════════════╗
    ║  USER SERVICE  ║ ║ VETERINARIAN   ║ ║  PET SERVICE   ║
    ║  (8081)        ║ ║ SERVICE (8082) ║ ║  (8083)        ║
    ║                ║ ║                ║ ║                ║
    ║ - Auth/Login   ║ ║ - Manage Vets  ║ ║ - Manage Pets  ║
    ║ - Users CRUD   ║ ║ - Specialties  ║ ║ - Breeds       ║
    ║ - JWT Tokens   ║ ║ - Availability ║ ║ - Health Info  ║
    ╚════════════════╝ ╚════════════════╝ ╚════════════════╝
           |                 |                 |
           | Feign Clients   | Feign Clients   | Feign Clients
           |                 |                 |
           └─────────────────┼─────────────────┘
                             |
        ┌────────────────────┼────────────────────┐
        |                    |                    |
        v                    v                    v
 ╔════════════════╗  ╔════════════════╗  ╔════════════════╗
 ║ APPOINTMENT    ║  ║ PAYMENT SERVICE║  ║ EUREKA SERVER  ║
 ║ SERVICE (8084) ║  ║ (8085)         ║  ║ (8761)         ║
 ║                ║  ║                ║  ║                ║
 ║ - Manage Appts ║  ║ - Payments     ║  ║ Service        ║
 ║ - Scheduling   ║  ║ - Invoices     ║  ║ Discovery      ║
 ║ - Notifications║  ║ - Reports      ║  ║ & Registry     ║
 ╚════════════════╝  ╚════════════════╝  ╚════════════════╝
                             |
                             |
                    ╔════════════════╗
                    ║  CONFIG SERVER ║
                    ║  (8888)        ║
                    ║  Spring Cloud  ║
                    ║  Config Server ║
                    ╚════════════════╝
                             |
                             |
              ┌──────────────┴──────────────┐
              |                             |
              v                             v
         ╔─────────────╗           ╔─────────────╗
         ║  H2 Database║           ║ Config YAML ║
         ║  (In Memory)║           ║  Files      ║
         ╚─────────────╝           ╚─────────────╝
```

## 2. FLUJO DE AUTENTICACIÓN JWT

```
┌─────────────────────────────────────────────────────────────┐
│                    AUTENTICACIÓN JWT                         │
└─────────────────────────────────────────────────────────────┘

1. LOGIN REQUEST
   CLIENT                                           USER SERVICE
     |                                                    |
     ├─────── POST /api/auth/login ─────────────────>   |
     │        { email, password }                       |
     |                                                   |
     |                                      AuthService.login()
     |                                                   |
     |                                  FindUser(email)
     |                                  ValidatePassword
     |                                  (BCrypt compare)
     |                                                   |
     |        <─── 200 OK ──────────────────────────────┤
     |        { token, tokenType, user }                |
     |                                                   |
     └───► STORE TOKEN LOCALLY

2. API REQUEST WITH TOKEN
   CLIENT                                           SERVICE
     |
     ├─ GET /api/users/1                            |
     │  Header: Authorization: Bearer {TOKEN}       |
     │                                               |
     └──────────────────────────────────────────>   |
                                              JwtAuthenticationFilter
                                                   |
                                         ExtractToken from Header
                                         JwtUtil.validateToken()
                                         Extract Email & Role
                                         Set SecurityContext
                                                   |
                                         Check Authorization
                                                   |
     <────────────────────────────────────────────   |
     { user data }

3. TOKEN STRUCTURE
   ┌─────────────────────────────────────────┐
   │         JWT Token (3 partes)            │
   ├─────────────────────────────────────────┤
   │ HEADER                                  │
   │ {                                       │
   │   "alg": "HS256",                       │
   │   "typ": "JWT"                          │
   │ }                                       │
   ├─────────────────────────────────────────┤
   │ PAYLOAD                                 │
   │ {                                       │
   │   "role": "CLIENT",                     │
   │   "sub": "juan@example.com",            │
   │   "iat": 1699540800,                    │
   │   "exp": 1699627200                     │
   │ }                                       │
   ├─────────────────────────────────────────┤
   │ SIGNATURE                               │
   │ HMACSHA256(header.payload, secret)      │
   └─────────────────────────────────────────┘

4. VALIDACIÓN DE TOKEN
   JwtUtil.validateToken(token, email)
        |
        ├─> extractEmail(token) == email?
        |
        ├─> isTokenExpired(token)?
        |
        └─> returnTrue/False
```

## 3. CONFIGURACIÓN CENTRALIZADA

```
┌─────────────────────────────────────────────────────────────┐
│            CONFIG SERVER (Puerto 8888)                       │
│         Spring Cloud Config Server                           │
└─────────────────────────────────────────────────────────────┘

config-server/src/main/resources/config/
│
├── application.yml
│   ├─ eureka.client
│   ├─ management.endpoints
│   └─ logging.level
│
├── eureka-server.yml
│   ├─ server.port: 8761
│   └─ eureka.client: (disabled)
│
├── api-gateway.yml
│   ├─ server.port: 8080
│   ├─ spring.cloud.gateway.routes
│   │  ├─ user-service-auth
│   │  ├─ user-service
│   │  ├─ veterinarian-service
│   │  ├─ pet-service
│   │  ├─ appointment-service
│   │  └─ payment-service
│   └─ globalcors (CORS settings)
│
├── pethome-user-service.yml
│   ├─ server.port: 8081
│   ├─ spring.datasource (H2)
│   ├─ spring.h2.console
│   ├─ spring.jpa (Hibernate)
│   └─ jwt: (secret, expiration)
│
├── pethome-veterinarian-service.yml
│   ├─ server.port: 8082
│   └─ spring.datasource: jdbc:h2:mem:veterinariandb
│
├── pethome-pet-service.yml
│   ├─ server.port: 8083
│   └─ spring.datasource: jdbc:h2:mem:petdb
│
└── pethome-appointment-service.yml
    ├─ server.port: 8084
    └─ spring.datasource: jdbc:h2:mem:appointmentdb

                      ↓ (Todos los servicios)

    Cada servicio hace request a Config Server
    http://localhost:8888/{service-name}/default
    
    Config Server responde con YAML properties
    Servicios cargan en runtime antes de iniciar
```

## 4. SERVICE DISCOVERY CON EUREKA

```
┌─────────────────────────────────────────────────────────────┐
│          EUREKA SERVER (Puerto 8761)                         │
│         Netflix Service Registry                             │
└─────────────────────────────────────────────────────────────┘

                        ↓ Register
                        
INICIO DE SERVICIOS:

1. Config Server:        NO se registra en Eureka
2. Eureka Server:        NO se registra (es el servidor)
3. API Gateway:          ✓ Registra como "api-gateway"
4. User Service:         ✓ Registra como "pethome-user-service"
5. Veterinarian Service: ✓ Registra como "pethome-veterinarian-service"
6. Pet Service:          ✓ Registra como "pethome-pet-service"
7. Appointment Service:  ✓ Registra como "pethome-appointment-service"
8. Payment Service:      ✓ Registra como "pethome-payment-service"

                        ↓ Discovery

API Gateway usa Eureka para:
- Descubrir ubicación de servicios
- Load balancing (lb://pethome-user-service)
- Health checks periódicos
- Detección de servicios caídos

Feign Clients usan Eureka para:
- Service-to-service communication
- Automatic load balancing
- Retry logic

DASHBOARD: http://localhost:8761
```

## 5. FLUJO DE REQUEST A TRAVÉS DEL GATEWAY

```
CLIENTE REQUEST → API GATEWAY → ROUTERS → PREDICATES → FILTERS → SERVICE

Ejemplo: GET /api/users/1

       ┌──────────────────────────────┐
       │  API Gateway (8080)          │
       └──────────────────────────────┘
                  |
                  |
         ┌────────┴────────┐
         |                 |
    Route Match?      Match /api/users/**?
         |                 |
         |            ✓ YES
         |                 |
         |          ┌──────▼──────────────┐
         |          │ Rewrite Filter     │
         |          │ /api/users/{id}    │
         |          │     ↓              │
         |          │ /users/{id}        │
         |          └──────┬──────────────┘
         |                 |
         |          ┌──────▼──────────────────┐
         |          │ Load Balancer (Eureka) │
         |          │ lb://pethome-user...   │
         |          │                        │
         |          │ Discover service       │
         |          │ from Eureka            │
         |          └──────┬──────────────────┘
         |                 |
         |          ┌──────▼──────────────────────┐
         |          │ User Service (8081)        │
         |          │                            │
         |          │ GET /users/1               │
         |          │                            │
         |          └──────┬──────────────────────┘
         |                 |
         └────────────────►│
                           |
                    RESPONSE JSON
                           |
                           ▼
                      CLIENTE
```

## 6. ESTRUCTURA DE MICROSERVICIO (EJEMPLO: USER SERVICE)

```
pethome-user-service/
│
├── pom.xml
│   └─ Dependencias Spring Boot, Cloud, Security, JWT
│
└── src/main/java/cl/duoc/pethome_user_service/
    │
    ├── PethomeUserServiceApplication.java
    │   └─ @SpringBootApplication
    │      @EnableEurekaClient
    │      @EnableConfigServer
    │
    ├── controller/
    │   ├── AuthController.java
    │   │   ├─ POST /auth/login
    │   │   └─ POST /auth/register
    │   └── UserController.java
    │       ├─ GET /users
    │       ├─ GET /users/{id}
    │       ├─ PUT /users/{id}
    │       └─ DELETE /users/{id}
    │
    ├── service/
    │   ├── AuthService (interface)
    │   │   ├─ login(email, password)
    │   │   └─ register(userRequest)
    │   ├── UserService (interface)
    │   │   ├─ getAllUsers()
    │   │   ├─ getUserById(id)
    │   │   ├─ updateUser(id, userUpdate)
    │   │   └─ deleteUser(id)
    │   └── impl/
    │       ├── AuthServiceImpl.java
    │       │   └─ Implementa lógica autenticación
    │       └── UserServiceImpl.java
    │           └─ Implementa CRUD usuarios
    │
    ├── repository/
    │   ├── UserRepository (JpaRepository)
    │   │   ├─ findByEmail(email)
    │   │   ├─ findByRole(role)
    │   │   └─ custom queries
    │   └── AddressRepository (JpaRepository)
    │
    ├── entity/
    │   ├── User.java (@Entity)
    │   │   ├─ id (PK)
    │   │   ├─ email (unique)
    │   │   ├─ password (encrypted)
    │   │   ├─ name
    │   │   ├─ phone
    │   │   ├─ role (enum: CLIENT, VET, ADMIN)
    │   │   ├─ address (embedded)
    │   │   └─ timestamps (createdAt, updatedAt)
    │   └── Address.java (@Embeddable)
    │       ├─ street
    │       ├─ city
    │       ├─ zipCode
    │       └─ country
    │
    ├── dto/
    │   ├── LoginRequestDTO
    │   │   ├─ email
    │   │   └─ password
    │   ├── LoginResponseDTO
    │   │   ├─ token
    │   │   ├─ tokenType
    │   │   └─ user
    │   ├── UserRequestDTO
    │   ├── UserResponseDTO
    │   └── UserUpdateDTO
    │
    ├── security/
    │   ├── SecurityConfig.java
    │   │   ├─ securityFilterChain()
    │   │   ├─ passwordEncoder() [BCrypt]
    │   │   ├─ authenticationManager()
    │   │   └─ authenticationProvider()
    │   ├── JwtAuthenticationFilter.java
    │   │   ├─ doFilterInternal()
    │   │   └─ extract & validate JWT
    │   ├── JwtAuthenticationEntryPoint.java
    │   │   └─ Handle 401 errors
    │   └── CustomUserDetailsService.java
    │       └─ loadUserByUsername()
    │
    ├── exception/
    │   ├── GlobalExceptionHandler.java
    │   │   ├─ @ControllerAdvice
    │   │   └─ Handle all exceptions
    │   └── ResourceNotFoundException.java
    │
    ├── enums/
    │   └── UserRole.java
    │       ├─ CLIENT
    │       ├─ VET
    │       └─ ADMIN
    │
    └── util/
        └── JwtUtil.java
            ├─ generateToken(email, role)
            ├─ extractEmail(token)
            ├─ extractRole(token)
            ├─ validateToken(token, email)
            ├─ isTokenExpired(token)
            └─ extractAllClaims(token)

        ┌─────────────────┐
        │   H2 Database   │
        │ (In Memory)     │
        │                 │
        │ Tables:         │
        │ - user          │
        │ - address       │
        └─────────────────┘
```

## 7. CICLO DE VIDA DEL REQUEST CON SEGURIDAD JWT

```
1. REQUEST ENTRA AL GATEWAY
   GET /api/users/1
   Header: Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

2. API GATEWAY MAPEA RUTA
   ✓ Match /api/users/**
   ✓ Rewrite a /users/**
   ✓ Busca en Eureka: pethome-user-service
   ✓ Obtiene URL: http://localhost:8081

3. REQUEST LLEGA A USER SERVICE
   GET /users/1
   Header: Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

4. SPRING SECURITY CHAIN
   ├─ JwtAuthenticationFilter ejecuta
   │  ├─ Extract token from header
   │  ├─ Call JwtUtil.validateToken()
   │  ├─ JwtUtil calls extractAllClaims()
   │  ├─ Valida signature con secret
   │  ├─ Valida expiration
   │  ├─ Si OK: Create Authentication object
   │  │   - Principal: email
   │  │   - Authorities: [role]
   │  └─ SetSecurityContext(Authentication)
   │
   ├─ Authorization check
   │  ├─ Endpoint requiere autenticación?
   │  ├─ Si NO: Allow (ej: /auth/**)
   │  └─ Si SI: Continúa si tiene Token válido

5. CONTROLLER MANEJA REQUEST
   @GetMapping("/{id}")
   public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id)
   
   ├─ Obtiene SecurityContext
   ├─ Extrae authenticated user
   ├─ Busca usuario en BD
   ├─ Mapea a DTO
   └─ Retorna 200 OK

6. RESPONSE RETORNA AL CLIENTE
   200 OK
   {
     "id": 1,
     "email": "juan@example.com",
     "name": "Juan",
     "role": "CLIENT"
   }
```

## 8. DATA FLOW - CREAR USUARIO

```
CLIENTE
  |
  └─ POST /api/users/auth/register
     Content-Type: application/json
     {
       "name": "Juan Pérez",
       "email": "juan@example.com",
       "password": "password123",
       "phone": "+56912345678",
       "role": "CLIENT"
     }
     |
     v
API GATEWAY
  ├─ Match /api/auth/**
  ├─ Rewrite a /auth/**
  └─ Forward to pethome-user-service
     |
     v
USER SERVICE - AuthController.register()
  |
  v
AuthService.register(UserRequestDTO)
  |
  ├─ Validate input (email format, etc)
  |
  ├─ Check if user exists
  |  ├─ userRepository.findByEmail(email)
  |  └─ If exists → throw DuplicateException
  |
  ├─ Encode password with BCrypt
  |  └─ passwordEncoder.encode("password123")
  |      = "$2a$10$dXj3SW6G7P50ecc..."
  |
  ├─ Create User entity
  |  ├─ Set email
  |  ├─ Set encryptedPassword
  |  ├─ Set name, phone, role
  |  └─ Create embedded Address
  |
  ├─ Save to repository
  |  └─ userRepository.save(user)
  |      │
  |      └─► H2 Database INSERT
  |          INSERT INTO user (email, password, name, ...)
  |          VALUES (...)
  |
  └─ Return UserResponseDTO
     |
     v
API GATEWAY
  |
  v
CLIENTE
  201 Created
  {
    "id": 1,
    "name": "Juan Pérez",
    "email": "juan@example.com",
    "role": "CLIENT"
  }
```

## 9. MATRIZ DE PUERTOS Y CONEXIONES

```
┌─────────────────────────────────────────────────────────────────┐
│           PUERTO    SERVICIO             PUERTO LOCAL            │
├─────────────────────────────────────────────────────────────────┤
│ 8888  │  Config Server        │ (Configura todos)             │
│ 8761  │  Eureka Server        │ (Registra servicios)          │
│ 8080  │  API Gateway          │ ──┬─────────────────────────┐ │
│ 8081  │  User Service         │ <─┤─────────────────────────┤ │
│ 8082  │  Veterinarian Service │ <─┤─────────────────────────┤ │
│ 8083  │  Pet Service          │ <─┤─────────────────────────┤ │
│ 8084  │  Appointment Service  │ <─┤─────────────────────────┤ │
│ 8085  │  Payment Service      │ <─┤─────────────────────────┤ │
│ 3000  │  React Frontend       │ (Externo)                   │ │
└─────────────────────────────────────────────────────────────────┘

CONEXIONES INTERNAS:
- Todos los servicios se conectan al Config Server (8888)
- Todos los servicios se registran en Eureka (8761)
- API Gateway hace requests a servicios por Eureka discovery
- Servicios comunican entre sí por Feign clients (Eureka discovery)
- CORS habilitado en Gateway para React (3000)
```

## 10. MATRIZ DE RESPONSABILIDADES POR COMPONENTE

```
┌────────────────────────────────────────────────────────────┐
│ COMPONENTE              │ RESPONSABILIDAD                 │
├────────────────────────────────────────────────────────────┤
│ Config Server (8888)    │ Distribución centralizada de    │
│                         │ configuración YAML              │
│                         │ - Propiedades de todos servicios│
│                         │ - JWT secrets                   │
│                         │ - URLs de BD                    │
├────────────────────────────────────────────────────────────┤
│ Eureka Server (8761)    │ Registro y descubrimiento de    │
│                         │ servicios                       │
│                         │ - Health checks                 │
│                         │ - Load balancing                │
│                         │ - Service lookup                │
├────────────────────────────────────────────────────────────┤
│ API Gateway (8080)      │ Punto de entrada único          │
│                         │ - Enrutamiento de requests      │
│                         │ - CORS                          │
│                         │ - Path rewriting                │
│                         │ - Load balancing (Eureka)       │
├────────────────────────────────────────────────────────────┤
│ User Service (8081)     │ Gestión de usuarios             │
│                         │ - Autenticación JWT             │
│                         │ - CRUD usuarios                 │
│                         │ - Gestión de direcciones        │
│                         │ - Validación de credenciales    │
├────────────────────────────────────────────────────────────┤
│ Veterinarian Service    │ Gestión de veterinarios         │
│ (8082)                  │ - CRUD veterinarios             │
│                         │ - Especialidades                │
│                         │ - Disponibilidad                │
├────────────────────────────────────────────────────────────┤
│ Pet Service (8083)      │ Gestión de mascotas             │
│                         │ - CRUD mascotas                 │
│                         │ - Razas, tipos                  │
│                         │ - Historial de salud            │
├────────────────────────────────────────────────────────────┤
│ Appointment Service     │ Gestión de citas                │
│ (8084)                  │ - CRUD citas                    │
│                         │ - Scheduling                    │
│                         │ - Notificaciones                │
│                         │ - Integración con servicios     │
├────────────────────────────────────────────────────────────┤
│ Payment Service (8085)  │ Gestión de pagos (PENDIENTE)    │
│                         │ - Procesamiento de pagos        │
│                         │ - Facturas                      │
│                         │ - Reportes                      │
├────────────────────────────────────────────────────────────┤
│ H2 Database             │ Persistencia de datos            │
│ (In-Memory)             │ - Un BD por servicio            │
│                         │ - DDL: create-drop              │
│                         │ - Ideal para desarrollo         │
└────────────────────────────────────────────────────────────┘
```

