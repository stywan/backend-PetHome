# PetHome Backend - Microservices Architecture

Sistema de microservicios para la plataforma PetHome desarrollado con Spring Boot y Spring Cloud.

## 📋 Arquitectura

El sistema está compuesto por los siguientes microservicios:

### Infraestructura
- **Config Server** (Puerto 8888) - Configuración centralizada
- **Eureka Server** (Puerto 8761) - Service Discovery
- **API Gateway** (Puerto 8080) - Punto de entrada único

### Servicios de Negocio
- **User Service** (Puerto 8081) - Gestión de usuarios y autenticación JWT ✅ COMPLETO
- **Veterinarian Service** (Puerto 8082) - Gestión de veterinarios ✅ COMPLETO
- **Pet Service** (Puerto 8083) - Gestión de mascotas ⏳ EN DESARROLLO
- **Appointment Service** (Puerto 8084) - Gestión de citas ⏳ EN DESARROLLO
- **Payment Service** (Puerto 8085) - Gestión de pagos 📝 PENDIENTE

## 🚀 Tecnologías

- Java 17
- Spring Boot 3.5.5
- Spring Cloud 2025.0.0
- Spring Data JPA
- Spring Security + JWT
- H2 Database (desarrollo)
- Maven
- Lombok

## 📦 Módulos Implementados

### ✅ User Service (COMPLETO)

#### Características:
- ✅ Autenticación JWT
- ✅ Registro de usuarios
- ✅ CRUD completo de usuarios
- ✅ Gestión de direcciones
- ✅ Encriptación de contraseñas con BCrypt
- ✅ Roles (CLIENT, VET, ADMIN)
- ✅ Validaciones con Bean Validation
- ✅ Manejo global de excepciones
- ✅ Integración con Eureka
- ✅ Consola H2 habilitada

#### Endpoints:

**Autenticación** (`/auth`)
- `POST /auth/login` - Login
- `POST /auth/register` - Registro

**Usuarios** (`/users`)
- `GET /users` - Listar todos
- `GET /users/{id}` - Obtener por ID
- `GET /users/email/{email}` - Obtener por email
- `GET /users/role/{role}` - Filtrar por rol
- `PUT /users/{id}` - Actualizar usuario
- `DELETE /users/{id}` - Eliminar usuario

#### Estructura del Código:
```
pethome-user-service/
├── controller/
│   ├── AuthController.java
│   └── UserController.java
├── dto/
│   ├── AddressDTO.java
│   ├── LoginRequestDTO.java
│   ├── LoginResponseDTO.java
│   ├── UserRequestDTO.java
│   ├── UserResponseDTO.java
│   └── UserUpdateDTO.java
├── entity/
│   ├── Address.java
│   └── User.java
├── enums/
│   └── UserRole.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
├── repository/
│   ├── AddressRepository.java
│   └── UserRepository.java
├── security/
│   ├── CustomUserDetailsService.java
│   ├── JwtAuthenticationEntryPoint.java
│   ├── JwtAuthenticationFilter.java
│   └── SecurityConfig.java
├── service/
│   ├── AuthService.java
│   ├── UserService.java
│   └── impl/
│       ├── AuthServiceImpl.java
│       └── UserServiceImpl.java
└── util/
    └── JwtUtil.java
```

## 🔧 Configuración

### Prerequisitos
- Java 17+
- Maven 3.6+

### Variables de Entorno (JWT)
Las configuraciones se encuentran en:
```
config-server/src/main/resources/config/pethome-user-service.yml
```

JWT Secret: `pethome-secret-key-for-jwt-authentication-2024`
JWT Expiration: `86400000` (24 horas)

## ▶️ Cómo Ejecutar

### Orden de Inicio (IMPORTANTE)

1. **Config Server** (primero)
```bash
cd config-server
mvn spring-boot:run
```
Espera a ver: `Started ConfigServerApplication`

2. **Eureka Server** (segundo)
```bash
cd eureka-server
mvn spring-boot:run
```
Espera a ver: `Started EurekaServerApplication`
Dashboard: http://localhost:8761

3. **API Gateway** (tercero)
```bash
cd api-gateway
mvn spring-boot:run
```
Espera a ver: `Started ApiGatewayApplication`

4. **User Service** (cuarto)
```bash
cd pethome-user-service
mvn spring-boot:run
```
Espera a ver: `Started PethomeUserServiceApplication`

### Build Completo
```bash
# Desde la raíz del proyecto
mvn clean install
```

## 🧪 Testing

### User Service - Ejemplos con cURL

#### 1. Registro de Usuario
```bash
curl -X POST http://localhost:8080/api/users/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Pérez",
    "email": "juan@example.com",
    "password": "password123",
    "phone": "+56912345678",
    "role": "CLIENT"
  }'
```

#### 2. Login
```bash
curl -X POST http://localhost:8080/api/users/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "juan@example.com",
    "password": "password123"
  }'
```

Respuesta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "user": {
    "id": 1,
    "name": "Juan Pérez",
    "email": "juan@example.com",
    "role": "CLIENT"
  }
}
```

#### 3. Obtener Usuario (con autenticación)
```bash
curl -X GET http://localhost:8080/api/users/users/1 \
  -H "Authorization: Bearer {TOKEN_AQUI}"
```

## 🗄️ Base de Datos

### H2 Console (Desarrollo)

**User Service:**
- URL: http://localhost:8081/h2-console
- JDBC URL: `jdbc:h2:mem:userdb`
- Usuario: `sa`
- Password: (vacío)

## 📊 Monitoreo

### Eureka Dashboard
http://localhost:8761

### Actuator Endpoints
- User Service: http://localhost:8081/actuator/health
- API Gateway: http://localhost:8080/actuator/health

## 🔐 Seguridad

### JWT Authentication
Todos los endpoints (excepto `/auth/**`) requieren autenticación JWT.

Header requerido:
```
Authorization: Bearer {token}
```

### Roles Implementados
- `CLIENT` - Clientes del sistema
- `VET` - Veterinarios
- `ADMIN` - Administradores

## 🎯 Estado del Proyecto

### Completado ✅
- [x] Config Server
- [x] Eureka Server
- [x] API Gateway
- [x] User Service (100%)
  - [x] Entidades y DTOs
  - [x] Repositorios
  - [x] Servicios
  - [x] Controladores
  - [x] Seguridad JWT
  - [x] Manejo de excepciones

### En Desarrollo ⏳
- [ ] Veterinarian Service
- [ ] Pet Service
- [ ] Appointment Service

### Pendiente 📝
- [ ] Payment Service
- [ ] Tests Unitarios
- [ ] Tests de Integración
- [ ] Documentación Swagger/OpenAPI
- [ ] Docker Compose
- [ ] CI/CD Pipeline

## 📝 Notas de Desarrollo

### CORS
El API Gateway está configurado para permitir requests desde `http://localhost:3000` (React Frontend).

### Logging
Todos los servicios tienen logging configurado en nivel DEBUG para `cl.duoc` durante desarrollo.

### Perfiles
Actualmente en perfil `default`. Para producción, configurar perfiles en Config Server.

## 👥 Equipo de Desarrollo

PetHome Development Team

## 📄 Licencia

Proyecto académico - Duoc UC
