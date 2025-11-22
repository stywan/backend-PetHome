# PetHome Backend - Guía de Docker

Esta guía explica cómo ejecutar el sistema PetHome Backend completamente dockerizado usando Docker Compose.

## Tabla de Contenidos

- [Requisitos Previos](#requisitos-previos)
- [Inicio Rápido](#inicio-rápido)
- [Arquitectura Docker](#arquitectura-docker)
- [Scripts Disponibles](#scripts-disponibles)
- [Configuración](#configuración)
- [URLs de Acceso](#urls-de-acceso)
- [Comandos Útiles](#comandos-útiles)
- [Troubleshooting](#troubleshooting)

---

## Requisitos Previos

### Software Requerido

- **Docker Desktop** 4.x o superior
  - [Descargar para Mac](https://www.docker.com/products/docker-desktop)
  - [Descargar para Windows](https://www.docker.com/products/docker-desktop)
- **Docker Compose** v2.x (incluido en Docker Desktop)
- Al menos **8GB de RAM** asignados a Docker
- Al menos **20GB de espacio en disco**

### Verificar Instalación

```bash
# Verificar Docker
docker --version
# Debería mostrar: Docker version 24.x.x o superior

# Verificar Docker Compose
docker-compose --version
# Debería mostrar: Docker Compose version v2.x.x o superior

# Verificar que Docker está corriendo
docker info
```

---

## Inicio Rápido

### Opción 1: Scripts Automatizados (Recomendado)

```bash
# 1. Construir todas las imágenes
./docker-build.sh

# 2. Iniciar todos los servicios
./docker-start.sh

# 3. Ver logs (opcional)
./docker-logs.sh all -f

# 4. Detener servicios cuando termines
./docker-stop.sh
```

### Opción 2: Docker Compose Manual

```bash
# 1. Construir imágenes
docker-compose build

# 2. Iniciar servicios
docker-compose up -d

# 3. Ver logs
docker-compose logs -f

# 4. Detener servicios
docker-compose down
```

---

## Arquitectura Docker

### Servicios y Puertos

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| config-server | 8888 | Configuración centralizada |
| eureka-server | 8761 | Service Discovery |
| api-gateway | 8080 | API Gateway / Punto de entrada |
| user-service | 8081 | Gestión de usuarios + JWT |
| veterinarian-service | 8082 | Gestión de veterinarios |
| pet-service | 8083 | Gestión de mascotas |
| appointment-service | 8084 | Gestión de citas |

### Red Docker

Todos los servicios se ejecutan en una red bridge llamada `pethome-network`, permitiendo comunicación entre contenedores usando nombres de servicio.

### Orden de Inicio

Los servicios se inician en el siguiente orden (gestionado automáticamente por `depends_on` y `healthcheck`):

1. **config-server** (primero, es la base)
2. **eureka-server** (depende de Config Server)
3. **api-gateway** (depende de Config + Eureka)
4. **Servicios de negocio** en paralelo (todos dependen de Config + Eureka)

### Health Checks

Cada servicio tiene configurado un health check que verifica:
- Intervalo: cada 30 segundos
- Timeout: 10 segundos
- Reintentos: 5
- Start period: 60 segundos (tiempo de gracia al iniciar)

---

## Scripts Disponibles

### `docker-build.sh`

Construye las imágenes Docker de todos los servicios.

```bash
# Construir todas las imágenes
./docker-build.sh

# Construir solo un servicio específico
./docker-build.sh user-service

# Ver ayuda
./docker-build.sh --help
```

**Características:**
- Build multi-stage optimizado
- Muestra progreso y tiempo total
- Valida que Docker esté corriendo

### `docker-start.sh`

Inicia todos los servicios en contenedores Docker.

```bash
# Iniciar en modo detached (segundo plano)
./docker-start.sh

# Iniciar y ver logs en tiempo real
./docker-start.sh --logs
```

**Características:**
- Inicia servicios en orden correcto
- Muestra URLs de acceso
- Verifica imágenes antes de iniciar

### `docker-stop.sh`

Detiene los servicios Docker.

```bash
# Detener servicios (mantiene volúmenes)
./docker-stop.sh

# Detener y limpiar todo (contenedores + volúmenes + redes)
./docker-stop.sh --clean

# Detener, limpiar todo + eliminar imágenes
./docker-stop.sh --clean --images
```

### `docker-logs.sh`

Visualiza logs de los servicios.

```bash
# Ver logs de un servicio específico
./docker-logs.sh user-service

# Ver logs en tiempo real (follow)
./docker-logs.sh user-service -f

# Ver últimas 100 líneas
./docker-logs.sh eureka-server -n 100

# Ver logs de todos los servicios
./docker-logs.sh all -f
```

---

## Configuración

### Variables de Entorno

Los servicios usan el perfil `docker` automáticamente, que carga configuraciones desde:
```
config-server/src/main/resources/config/{servicio}-docker.yml
```

### Base de Datos

Por defecto, los servicios usan **H2 in-memory** en modo Docker para desarrollo.

Para usar **PostgreSQL** en producción, los archivos `-docker.yml` ya están configurados. Solo necesitas:

1. Descomentar el servicio PostgreSQL en `docker-compose.yml`
2. Actualizar las variables de entorno en los servicios

### JWT Configuration

El User Service usa las siguientes configuraciones JWT en Docker:

```yaml
jwt:
  secret: ${JWT_SECRET:pethome-secret-key-for-jwt-authentication-2024-production-secure}
  expiration: ${JWT_EXPIRATION:86400000}
```

---

## URLs de Acceso

### Dashboards y Monitoreo

| Servicio | URL |
|----------|-----|
| Eureka Dashboard | http://localhost:8761 |
| API Gateway Health | http://localhost:8080/actuator/health |

### Swagger UIs

| Servicio | URL |
|----------|-----|
| User Service | http://localhost:8081/swagger-ui.html |
| Veterinarian Service | http://localhost:8082/swagger-ui.html |
| Pet Service | http://localhost:8083/swagger-ui.html |
| Appointment Service | http://localhost:8084/swagger-ui.html |

### API Endpoints (vía Gateway)

| Endpoint | URL Base |
|----------|----------|
| Autenticación | http://localhost:8080/api/auth |
| Usuarios | http://localhost:8080/api/users |
| Veterinarios | http://localhost:8080/api/veterinarians |
| Mascotas | http://localhost:8080/api/pets |
| Citas | http://localhost:8080/api/appointments |

---

## Comandos Útiles

### Gestión de Contenedores

```bash
# Ver estado de todos los servicios
docker-compose ps

# Ver logs de todos los servicios
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f user-service

# Reiniciar un servicio
docker-compose restart user-service

# Reconstruir y reiniciar un servicio
docker-compose up -d --build user-service

# Ejecutar comando en un contenedor
docker-compose exec user-service sh

# Ver recursos usados
docker stats
```

### Limpieza y Mantenimiento

```bash
# Limpiar contenedores detenidos
docker container prune

# Limpiar imágenes sin usar
docker image prune

# Limpiar todo (¡CUIDADO!)
docker system prune -a

# Ver espacio usado por Docker
docker system df
```

### Debugging

```bash
# Inspeccionar un contenedor
docker inspect pethome-user-service

# Ver logs de últimas 100 líneas
docker-compose logs --tail=100 user-service

# Ver configuración activa de docker-compose
docker-compose config

# Ver redes
docker network ls
docker network inspect pethome-network
```

---

## Troubleshooting

### Problema: "Port is already allocated"

**Error:** `Bind for 0.0.0.0:8080 failed: port is already allocated`

**Solución:**
```bash
# Ver qué está usando el puerto
lsof -i :8080

# Detener el proceso o cambiar el puerto en docker-compose.yml
# Luego reiniciar
./docker-stop.sh
./docker-start.sh
```

### Problema: Servicio no inicia o está "unhealthy"

**Diagnóstico:**
```bash
# Ver logs del servicio
./docker-logs.sh [servicio] -f

# Ver estado de health check
docker-compose ps

# Inspeccionar contenedor
docker inspect pethome-[servicio]
```

**Soluciones comunes:**
1. Aumentar memoria asignada a Docker (Settings > Resources)
2. Esperar más tiempo (algunos servicios tardan en iniciar)
3. Verificar que Config Server y Eureka estén healthy primero

### Problema: "Config Server connection refused"

**Error:** `Connection refused: config-server:8888`

**Solución:**
```bash
# Config Server debe iniciar primero y estar healthy
docker-compose up -d config-server
# Esperar hasta que esté healthy
docker-compose ps

# Luego iniciar los demás
docker-compose up -d
```

### Problema: Build falla con error de Maven

**Error:** `Could not resolve dependencies`

**Solución:**
```bash
# Limpiar build anterior
./docker-stop.sh --clean --images

# Reconstruir desde cero
./docker-build.sh
```

### Problema: Cambios en código no se reflejan

**Solución:**
```bash
# Necesitas reconstruir la imagen
./docker-stop.sh
./docker-build.sh [servicio]
./docker-start.sh
```

### Problema: Servicio no se registra en Eureka

**Diagnóstico:**
```bash
# Verificar logs del servicio
./docker-logs.sh [servicio] -f | grep -i eureka

# Verificar Eureka dashboard
# Abrir: http://localhost:8761
```

**Soluciones:**
1. Verificar que Eureka Server esté running y healthy
2. Verificar configuración de red (todos deben estar en pethome-network)
3. Reiniciar el servicio problemático

---

## Diferencias entre Local y Docker

| Aspecto | Local (./start-services.sh) | Docker (./docker-start.sh) |
|---------|----------------------------|----------------------------|
| Base de datos | H2 in-memory | H2 in-memory (configurable a PostgreSQL) |
| Configuración | `application.yml` | `{servicio}-docker.yml` |
| Comunicación | localhost:puerto | nombre-servicio:puerto |
| Logs | `./logs/*.log` | `docker-compose logs` |
| Reinicio | Manual | Automático (`restart: unless-stopped`) |
| Dependencias | Manual (scripts) | Automático (`depends_on` + `healthcheck`) |

---

## Mejores Prácticas

### Desarrollo

1. **Usar logs en tiempo real** durante desarrollo:
   ```bash
   ./docker-logs.sh all -f
   ```

2. **Reconstruir solo el servicio que cambió**:
   ```bash
   ./docker-build.sh user-service
   docker-compose up -d --no-deps user-service
   ```

3. **Verificar health de servicios antes de testear**:
   ```bash
   docker-compose ps
   ```

### Producción

1. **Usar PostgreSQL en lugar de H2**
2. **Configurar límites de recursos** en docker-compose.yml:
   ```yaml
   deploy:
     resources:
       limits:
         cpus: '1'
         memory: 512M
   ```

3. **Usar volúmenes para logs persistentes**
4. **Configurar monitoreo externo** (Prometheus, Grafana)
5. **Usar Docker Swarm o Kubernetes** para orquestación avanzada

---

## Soporte

Para problemas o preguntas:
1. Revisar logs: `./docker-logs.sh [servicio] -f`
2. Ver esta guía de troubleshooting
3. Consultar documentación de Docker: https://docs.docker.com

---

**Última actualización:** 2024
**Autor:** PetHome Development Team
