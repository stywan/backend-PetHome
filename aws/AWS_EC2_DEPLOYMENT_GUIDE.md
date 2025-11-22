# Guía de Despliegue en AWS EC2

Esta guía te llevará paso a paso para desplegar tu aplicación PetHome Backend en Amazon EC2.

## Tabla de Contenidos

1. [Requisitos Previos](#requisitos-previos)
2. [Checklist Pre-Despliegue](#checklist-pre-despliegue)
3. [Paso 1: Crear Base de Datos RDS PostgreSQL](#paso-1-crear-base-de-datos-rds-postgresql)
4. [Paso 2: Crear y Configurar Instancia EC2](#paso-2-crear-y-configurar-instancia-ec2)
5. [Paso 3: Configurar Security Groups](#paso-3-configurar-security-groups)
6. [Paso 4: Instalar Docker en EC2](#paso-4-instalar-docker-en-ec2)
7. [Paso 5: Desplegar la Aplicación](#paso-5-desplegar-la-aplicación)
8. [Paso 6: Verificar el Despliegue](#paso-6-verificar-el-despliegue)
9. [Integración con Frontend](#integración-con-frontend)
10. [Configuración de Dominio y HTTPS](#configuración-de-dominio-y-https)
11. [Consideraciones de Seguridad para Producción](#consideraciones-de-seguridad-para-producción)
12. [Troubleshooting](#troubleshooting)
13. [Costos Estimados](#costos-estimados)

---

## Requisitos Previos

- Cuenta de AWS activa
- AWS CLI instalado (opcional pero recomendado)
- Tu proyecto PetHome Backend en tu máquina local
- Conocimientos básicos de terminal/SSH
- Frontend React desplegado o listo para desplegar

---

## Checklist Pre-Despliegue

Antes de comenzar el despliegue, asegúrate de completar estos pasos:

### Configuración del Backend

- [ ] **Cambiar `ddl-auto` en archivos `-docker.yml`**
  - Ubicación: `config-server/src/main/resources/config/*-docker.yml`
  - Cambiar de `create-drop` a `update` o `validate`
  - **CRÍTICO**: `create-drop` borra todos los datos cada vez que se reinicia el servicio

  ```yaml
  # ANTES (borra datos al reiniciar)
  jpa:
    hibernate:
      ddl-auto: create-drop

  # DESPUÉS (mantiene datos)
  jpa:
    hibernate:
      ddl-auto: update  # o 'validate' para producción estricta
  ```

- [ ] **Cambiar JWT Secret Key**
  - No usar el secret por defecto en producción
  - Generar uno seguro: `openssl rand -base64 64`
  - Actualizar en `.env` y en archivos de configuración

- [ ] **Configurar CORS para tu frontend**
  - Agregar todos los dominios desde donde se accederá al API
  - Ver sección [Integración con Frontend](#integración-con-frontend)

- [ ] **Revisar credenciales de base de datos**
  - No commitear contraseñas reales en el repositorio
  - Usar variables de entorno

### Configuración del Frontend

- [ ] **Actualizar URL del API**
  - Cambiar de `localhost:8080` a la IP/dominio de EC2
  - Ejemplo: `http://YOUR_EC2_IP:8080/api`

- [ ] **Configurar variables de entorno del frontend**
  ```env
  REACT_APP_API_URL=http://YOUR_EC2_IP:8080/api
  # o con dominio
  REACT_APP_API_URL=https://api.pethome.com/api
  ```

### Archivos a Modificar

| Archivo | Cambio Requerido |
|---------|------------------|
| `config-server/.../pethome-user-service-docker.yml` | `ddl-auto: update`, JWT secret |
| `config-server/.../pethome-veterinarian-service-docker.yml` | `ddl-auto: update` |
| `config-server/.../pethome-pet-service-docker.yml` | `ddl-auto: update` |
| `config-server/.../pethome-appointment-service-docker.yml` | `ddl-auto: update` |
| `config-server/.../api-gateway-docker.yml` | CORS origins |
| `.env` (en EC2) | Todas las credenciales |
| Frontend `.env` | API URL |

---

## Paso 1: Crear Base de Datos RDS PostgreSQL

> **Nota**: Si ya tienes tu RDS creada (`dbpethome.c58egkiimr78.us-east-1.rds.amazonaws.com`), puedes saltar al [Paso 2](#paso-2-crear-y-configurar-instancia-ec2).

### 1.1 Crear instancia RDS

1. Ve a la consola de AWS → **RDS** → **Create database**
2. Selecciona:
   - **Engine**: PostgreSQL
   - **Version**: PostgreSQL 15.x o superior
   - **Templates**: Free tier (para desarrollo) o Production
   - **DB instance identifier**: `pethome-db`
   - **Master username**: `pethome_admin`
   - **Master password**: Crea una contraseña segura (guárdala!)
   - **DB instance class**:
     - Free tier: `db.t3.micro` o `db.t4g.micro`
     - Producción: `db.t3.small` o superior
   - **Storage**: 20 GB (General Purpose SSD)
   - **Public access**: No (para seguridad)
   - **VPC**: Default VPC
   - **Database name**: `pethome`

3. Click en **Create database**
4. Espera 5-10 minutos mientras se crea la instancia
5. Una vez creada, anota el **Endpoint** (algo como: `pethome-db.xxxxx.us-east-1.rds.amazonaws.com`)

### 1.2 Configurar Security Group de RDS

1. Ve a tu instancia RDS → Tab **Connectivity & security**
2. Click en el Security Group
3. Click en **Edit inbound rules**
4. Agrega una regla:
   - **Type**: PostgreSQL
   - **Port**: 5432
   - **Source**: Custom → Aquí pondrás el Security Group de tu EC2 (lo haremos después)

---

## Paso 2: Crear y Configurar Instancia EC2

### 2.1 Lanzar instancia EC2

1. Ve a la consola de AWS → **EC2** → **Launch Instance**
2. Configura:
   - **Name**: `pethome-backend-server`
   - **AMI**: Amazon Linux 2023 (recomendado) o Ubuntu Server 22.04
   - **Instance type**:
     - Desarrollo: `t2.medium` (4GB RAM mínimo)
     - Producción: `t3.large` o superior
   - **Key pair**: Crea o selecciona un key pair existente (GUARDA EL .pem FILE!)
   - **Network settings**:
     - VPC: Mismo VPC que tu RDS
     - Auto-assign public IP: Enable
   - **Configure storage**:
     - 30 GB (General Purpose SSD)

3. Click en **Launch instance**
4. Anota la **Public IPv4 address** de tu instancia

### 2.2 Conectar a tu instancia EC2

Desde tu terminal local:

```bash
# Cambiar permisos del key file
chmod 400 /path/to/your-key.pem

# Conectar via SSH
ssh -i /path/to/your-key.pem ec2-user@YOUR_EC2_PUBLIC_IP
```

Si usas Ubuntu en lugar de Amazon Linux:
```bash
ssh -i /path/to/your-key.pem ubuntu@YOUR_EC2_PUBLIC_IP
```

---

## Paso 3: Configurar Security Groups

### 3.1 Security Group de EC2

1. Ve a tu instancia EC2 → Tab **Security**
2. Click en el Security Group
3. Click en **Edit inbound rules**
4. Agrega las siguientes reglas:

| Type | Port | Source | Description |
|------|------|--------|-------------|
| SSH | 22 | My IP | SSH access |
| Custom TCP | 8080 | 0.0.0.0/0 | API Gateway (ÚNICO PUERTO PÚBLICO) |
| Custom TCP | 8761 | My IP | Eureka Dashboard (solo para debug) |

**IMPORTANTE para Producción**:
- Solo exponer el puerto **8080** (API Gateway) al público
- Los puertos 8081-8084 (microservicios) NO deben ser públicos
- El puerto 8888 (Config Server) NUNCA debe ser público
- El puerto 8761 (Eureka) solo para debugging desde tu IP

### 3.2 Actualizar Security Group de RDS

1. Vuelve al Security Group de RDS
2. En la regla PostgreSQL que creamos antes:
   - **Source**: Selecciona el Security Group de tu EC2
   - Esto permite que solo tu EC2 pueda conectarse a la base de datos

---

## Paso 4: Instalar Docker en EC2

### Opción A: Usando el script automatizado (RECOMENDADO)

Desde tu **máquina local**, sube y ejecuta el script:

```bash
# 1. Subir el script a EC2
scp -i /path/to/your-key.pem aws/setup-ec2.sh ec2-user@YOUR_EC2_PUBLIC_IP:/tmp/

# 2. SSH a EC2
ssh -i /path/to/your-key.pem ec2-user@YOUR_EC2_PUBLIC_IP

# 3. Ejecutar el script
chmod +x /tmp/setup-ec2.sh
/tmp/setup-ec2.sh

# 4. IMPORTANTE: Logout y vuelve a conectar para que los permisos de Docker tomen efecto
exit

# 5. Volver a conectar
ssh -i /path/to/your-key.pem ec2-user@YOUR_EC2_PUBLIC_IP
```

### Opción B: Instalación manual

Si prefieres instalar manualmente:

```bash
# Para Amazon Linux 2023
sudo yum update -y
sudo yum install -y docker git
sudo service docker start
sudo usermod -a -G docker ec2-user

# Instalar Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Enable Docker on boot
sudo systemctl enable docker

# Logout y volver a conectar
exit
```

Vuelve a conectar via SSH y verifica:
```bash
docker --version
docker-compose --version
```

---

## Paso 5: Desplegar la Aplicación

### 5.1 Opción A: Despliegue Automatizado desde tu Máquina Local

1. Edita el script `aws/deploy-to-ec2.sh` en tu máquina local:
   ```bash
   EC2_HOST="YOUR_EC2_PUBLIC_IP"
   EC2_KEY="/path/to/your-key.pem"
   ```

2. Ejecuta el script:
   ```bash
   chmod +x aws/deploy-to-ec2.sh
   ./aws/deploy-to-ec2.sh
   ```

### 5.2 Opción B: Despliegue Manual

#### Desde tu máquina local:

```bash
# Comprimir el proyecto
tar -czf pethome-backend.tar.gz \
    --exclude='.git' \
    --exclude='target' \
    --exclude='.idea' \
    --exclude='logs' \
    .

# Subir a EC2
scp -i /path/to/your-key.pem pethome-backend.tar.gz ec2-user@YOUR_EC2_PUBLIC_IP:/opt/

# SSH a EC2
ssh -i /path/to/your-key.pem ec2-user@YOUR_EC2_PUBLIC_IP
```

#### En la instancia EC2:

```bash
# Crear directorio y descomprimir
sudo mkdir -p /opt/pethome
cd /opt/pethome
sudo tar -xzf ../pethome-backend.tar.gz
sudo chown -R ec2-user:ec2-user /opt/pethome

# Dar permisos de ejecución a los scripts
chmod +x *.sh
chmod +x aws/*.sh
```

### 5.3 Configurar Variables de Entorno

```bash
cd /opt/pethome

# Copiar el ejemplo de configuración
cp aws/.env.ec2.example .env

# Editar con tus credenciales
nano .env
```

Actualiza estos valores en el archivo `.env`:

```bash
# Tu endpoint de RDS (ya lo tienes)
DB_HOST=dbpethome.c58egkiimr78.us-east-1.rds.amazonaws.com
DB_PASSWORD=0zMGiIYDmCf0g94iS0ex

# Tu IP pública de EC2
GATEWAY_URL=http://YOUR_EC2_PUBLIC_IP:8080

# Un JWT secret seguro (genera uno nuevo para producción)
JWT_SECRET=$(openssl rand -base64 64)

# IMPORTANTE: Tus dominios frontend (ver siguiente sección)
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://tu-frontend.com,https://tu-frontend.com
```

### 5.4 Construir las Imágenes Docker

```bash
cd /opt/pethome

# Esto tomará varios minutos la primera vez
./docker-build.sh
```

### 5.5 Iniciar los Servicios

```bash
./docker-start.sh

# Monitorear los logs
./docker-logs.sh
```

---

## Paso 6: Verificar el Despliegue

### 6.1 Verificar que todos los contenedores estén corriendo

```bash
docker-compose ps
```

Deberías ver todos los servicios como `Up` y `healthy`:
```
NAME                          STATUS
pethome-config-server         Up (healthy)
pethome-eureka-server         Up (healthy)
pethome-api-gateway           Up (healthy)
pethome-user-service          Up (healthy)
pethome-veterinarian-service  Up (healthy)
pethome-pet-service           Up (healthy)
pethome-appointment-service   Up (healthy)
```

### 6.2 Verificar endpoints

Desde tu navegador o usando curl:

```bash
# API Gateway Health
curl http://YOUR_EC2_PUBLIC_IP:8080/actuator/health

# Test de autenticación
curl -X POST http://YOUR_EC2_PUBLIC_IP:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"password"}'
```

### 6.3 Eureka Dashboard (solo desde tu IP)

Si configuraste el puerto 8761 para tu IP:
```
http://YOUR_EC2_PUBLIC_IP:8761
```

---

## Integración con Frontend

Esta sección es **CRÍTICA** para que tu frontend React funcione correctamente con el backend desplegado.

### Configuración de CORS

El API Gateway maneja las políticas CORS. Debes configurar todos los orígenes desde donde se accederá al API.

#### 1. Actualizar api-gateway-docker.yml

Ubicación: `config-server/src/main/resources/config/api-gateway-docker.yml`

```yaml
spring:
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowed-origins:
              # Desarrollo local
              - http://localhost:3000
              - http://127.0.0.1:3000

              # Frontend desplegado (AGREGAR TUS DOMINIOS)
              - http://tu-dominio-frontend.com
              - https://tu-dominio-frontend.com

              # Si usas Vercel, Netlify, etc.
              - https://pethome-frontend.vercel.app
              - https://pethome.netlify.app

              # Si el frontend está en S3/CloudFront
              - https://d1234567890.cloudfront.net

            allowed-methods:
              - GET
              - POST
              - PUT
              - DELETE
              - OPTIONS
              - PATCH
            allowed-headers: "*"
            allow-credentials: true
            max-age: 3600
```

#### 2. Actualizar .env en EC2

```bash
# En /opt/pethome/.env
CORS_ALLOWED_ORIGINS=http://localhost:3000,https://tu-frontend.com,https://pethome.vercel.app
```

### Configuración del Frontend

#### 1. Variables de entorno del frontend

Crear/actualizar `.env` en tu proyecto React:

```env
# Desarrollo local
REACT_APP_API_URL=http://localhost:8080/api

# Producción (crear .env.production)
REACT_APP_API_URL=http://YOUR_EC2_IP:8080/api

# O con dominio personalizado
REACT_APP_API_URL=https://api.pethome.com/api
```

#### 2. Configuración de Axios/Fetch

Ejemplo de configuración de Axios:

```javascript
// src/services/api.js
import axios from 'axios';

const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para agregar JWT token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Interceptor para manejar errores
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expirado o inválido
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

#### 3. Manejo de autenticación

```javascript
// src/services/auth.js
import api from './api';

export const login = async (email, password) => {
  const response = await api.post('/auth/login', { email, password });
  const { token, user } = response.data;

  // Guardar token
  localStorage.setItem('token', token);

  return user;
};

export const logout = () => {
  localStorage.removeItem('token');
};

export const isAuthenticated = () => {
  return !!localStorage.getItem('token');
};
```

### Opciones de Despliegue del Frontend

#### Opción 1: Vercel (Recomendado para React)

```bash
# Instalar Vercel CLI
npm i -g vercel

# Desplegar
vercel

# Configurar variable de entorno en Vercel Dashboard
REACT_APP_API_URL=http://YOUR_EC2_IP:8080/api
```

#### Opción 2: AWS S3 + CloudFront

```bash
# Build del frontend
npm run build

# Subir a S3
aws s3 sync build/ s3://tu-bucket-frontend --delete

# Configurar CloudFront para servir desde S3
```

#### Opción 3: Netlify

```bash
# Build command: npm run build
# Publish directory: build
# Environment variable: REACT_APP_API_URL
```

### Problemas Comunes de CORS

#### Error: "Access-Control-Allow-Origin"

```
Access to XMLHttpRequest at 'http://EC2_IP:8080/api/users'
from origin 'http://localhost:3000' has been blocked by CORS policy
```

**Solución**:
1. Verificar que el origen está en `allowed-origins`
2. Reconstruir y reiniciar el API Gateway:
   ```bash
   docker-compose restart api-gateway
   ```

#### Error: "Preflight request doesn't pass"

**Solución**:
- Asegurar que `OPTIONS` está en `allowed-methods`
- Verificar que `allow-credentials: true` si envías cookies/auth

#### Error: "Network Error" en Axios

**Posibles causas**:
1. EC2 no accesible (verificar Security Group)
2. Servicios no corriendo (`docker-compose ps`)
3. URL incorrecta en frontend

---

## Configuración de Dominio y HTTPS

### Usar un dominio con Route 53

1. Registrar o transferir dominio a Route 53
2. Crear Hosted Zone
3. Crear Record Set:
   - **Name**: `api.pethome.com`
   - **Type**: A
   - **Value**: IP de tu EC2

### Configurar HTTPS con Let's Encrypt

Para producción, es **OBLIGATORIO** usar HTTPS.

#### Opción 1: Nginx como Reverse Proxy (Recomendado)

```bash
# En EC2
sudo yum install -y nginx certbot python3-certbot-nginx

# Configurar Nginx
sudo nano /etc/nginx/conf.d/pethome.conf
```

```nginx
server {
    listen 80;
    server_name api.pethome.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
    }
}
```

```bash
# Iniciar Nginx
sudo systemctl start nginx
sudo systemctl enable nginx

# Obtener certificado SSL
sudo certbot --nginx -d api.pethome.com
```

#### Opción 2: AWS Application Load Balancer + ACM

1. Solicitar certificado en AWS Certificate Manager
2. Crear Application Load Balancer
3. Configurar listener HTTPS (443) con el certificado
4. Target Group apuntando a tu EC2:8080

### Actualizar Frontend para HTTPS

```env
REACT_APP_API_URL=https://api.pethome.com/api
```

---

## Consideraciones de Seguridad para Producción

### Cambios OBLIGATORIOS antes de ir a producción

#### 1. Cambiar ddl-auto (YA MENCIONADO)

```yaml
# En todos los archivos *-docker.yml
jpa:
  hibernate:
    ddl-auto: validate  # o 'update'
```

#### 2. JWT Secret Key

```bash
# Generar secret seguro
openssl rand -base64 64

# Resultado ejemplo:
# K7gNU3sdo+OL0wNhqoVWhr3g6s1xYv72ol/pe/Unols=...

# Actualizar en .env
JWT_SECRET=tu-secret-generado-aqui
```

#### 3. Credenciales de Base de Datos

- Cambiar contraseña por defecto de RDS
- Rotar credenciales periódicamente
- Considerar usar AWS Secrets Manager

```bash
# Cambiar password en RDS
aws rds modify-db-instance \
    --db-instance-identifier pethome-db \
    --master-user-password nueva-password-segura
```

#### 4. Security Groups Restrictivos

```
EC2 Inbound Rules (PRODUCCIÓN):
- SSH (22): Solo tu IP
- HTTPS (443): 0.0.0.0/0 (si usas Nginx/ALB)
- HTTP (80): 0.0.0.0/0 (redirect a HTTPS)

NO exponer:
- 8080 directamente (usar Nginx/ALB)
- 8081-8084 (microservicios internos)
- 8761 (Eureka)
- 8888 (Config Server)
```

#### 5. Habilitar Logs y Monitoreo

```bash
# Instalar CloudWatch Agent
sudo yum install -y amazon-cloudwatch-agent

# Configurar alertas para:
- CPU > 80%
- Memoria > 85%
- Disco > 90%
- Error rate en logs
```

#### 6. Backups Automáticos

**RDS:**
- Habilitar automated backups (7-30 días retention)
- Crear snapshots antes de cambios importantes

**EC2:**
- Crear AMI de la configuración funcional
- Snapshots programados del EBS

### Checklist de Seguridad

- [ ] HTTPS configurado
- [ ] JWT secret cambiado
- [ ] Password de RDS cambiado
- [ ] ddl-auto en `update` o `validate`
- [ ] Security Groups restrictivos
- [ ] Logs habilitados
- [ ] Backups configurados
- [ ] Monitoreo activo
- [ ] Rate limiting implementado (opcional pero recomendado)

---

## Troubleshooting

### Problema: Los servicios no se inician

```bash
# Ver logs de un servicio específico
docker logs pethome-user-service

# Ver logs en tiempo real
docker logs -f pethome-user-service

# Reiniciar un servicio específico
docker-compose restart user-service
```

### Problema: No puedo conectar a la base de datos

1. Verifica que el Security Group de RDS permita conexiones desde el Security Group de EC2
2. Verifica que el endpoint en `.env` sea correcto
3. Prueba la conexión:
   ```bash
   docker exec -it pethome-user-service bash
   apt-get update && apt-get install -y postgresql-client
   psql -h YOUR_RDS_ENDPOINT -U pethome_admin -d pethome
   ```

### Problema: "Out of memory" en EC2

Tu instancia necesita más RAM. Considera:
1. Upgrade a `t2.medium` o `t3.medium`
2. O agregar swap:
   ```bash
   sudo dd if=/dev/zero of=/swapfile bs=1G count=4
   sudo chmod 600 /swapfile
   sudo mkswap /swapfile
   sudo swapon /swapfile
   echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
   ```

### Problema: Los contenedores se reinician constantemente

```bash
# Ver health checks
docker inspect pethome-user-service | grep -A 10 Health

# Ver si hay problemas de red
docker network ls
docker network inspect pethome-network
```

### Problema: CORS errors desde frontend

1. Verificar que el origen está en la lista de `allowed-origins`
2. Reconstruir el API Gateway:
   ```bash
   docker-compose build api-gateway
   docker-compose up -d api-gateway
   ```
3. Verificar en browser DevTools → Network → Headers

### Problema: Frontend no puede conectar al backend

1. Verificar variable de entorno `REACT_APP_API_URL`
2. Verificar Security Group de EC2 (puerto 8080 abierto)
3. Verificar que el servicio esté corriendo: `curl http://EC2_IP:8080/actuator/health`
4. Verificar CORS configuration

### Problema: Token JWT expirado/inválido

1. Verificar que el JWT_SECRET sea el mismo en todos los servicios
2. Verificar que el token no haya expirado (24 horas por defecto)
3. Verificar formato del header: `Authorization: Bearer <token>`

---

## Comandos Útiles

### Gestión de servicios

```bash
# Ver estado
docker-compose ps

# Ver logs de todos los servicios
docker-compose logs -f

# Ver logs de un servicio específico
docker logs -f pethome-user-service

# Reiniciar todos los servicios
docker-compose restart

# Reiniciar un servicio específico
docker-compose restart user-service

# Detener todo
docker-compose down

# Detener y eliminar volúmenes
docker-compose down -v

# Reconstruir y reiniciar
./docker-stop.sh
./docker-build.sh
./docker-start.sh
```

### Acceder a un contenedor

```bash
# Abrir shell en un contenedor
docker exec -it pethome-user-service bash

# Ver variables de entorno de un contenedor
docker exec pethome-user-service env
```

### Limpieza

```bash
# Eliminar contenedores detenidos
docker container prune

# Eliminar imágenes no usadas
docker image prune

# Eliminar todo (CUIDADO!)
docker system prune -a
```

### Base de datos

```bash
# Conectar a PostgreSQL desde EC2
docker exec -it pethome-user-service bash
psql -h $DB_HOST -U $DB_USERNAME -d $DB_NAME

# Ver tablas
\dt

# Ver datos de usuarios
SELECT * FROM users;
```

---

## Costos Estimados AWS

### Escenario Desarrollo/Testing

| Recurso | Tipo | Costo Mensual (aprox) |
|---------|------|----------------------|
| EC2 | t2.medium (4GB RAM) | $30/mes |
| RDS | db.t3.micro | $15/mes |
| Storage (EBS) | 50 GB | $5/mes |
| Data Transfer | 10 GB/mes | $1/mes |
| **TOTAL** | | **~$51/mes** |

### Escenario Producción (tráfico bajo-medio)

| Recurso | Tipo | Costo Mensual (aprox) |
|---------|------|----------------------|
| EC2 | t3.large (8GB RAM) | $60/mes |
| RDS | db.t3.small | $30/mes |
| Storage (EBS) | 100 GB | $10/mes |
| Data Transfer | 50 GB/mes | $5/mes |
| Route 53 | Hosted Zone | $0.50/mes |
| ACM | Certificate | Gratis |
| **TOTAL** | | **~$106/mes** |

**Nota**: Estos son costos aproximados. Usa la [AWS Pricing Calculator](https://calculator.aws/) para estimaciones precisas.

---

## Optimizaciones Adicionales

### 1. CI/CD con GitHub Actions

```yaml
# .github/workflows/deploy.yml
name: Deploy to EC2

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Deploy to EC2
        uses: appleboy/ssh-action@master
        with:
          host: ${{ secrets.EC2_HOST }}
          username: ec2-user
          key: ${{ secrets.EC2_SSH_KEY }}
          script: |
            cd /opt/pethome
            git pull
            ./docker-build.sh
            ./docker-start.sh
```

### 2. Monitoreo con CloudWatch

```bash
# Instalar CloudWatch Agent en EC2
sudo yum install -y amazon-cloudwatch-agent
```

### 3. Auto Scaling (Avanzado)

- Crear AMI de tu EC2 configurada
- Crear Launch Template
- Crear Auto Scaling Group
- Agregar Application Load Balancer

---

## Resumen de Pasos Rápidos

Para un despliegue rápido, sigue estos pasos:

1. **Preparar código**:
   - Cambiar `ddl-auto: update` en archivos `-docker.yml`
   - Actualizar CORS con dominios del frontend

2. **Configurar EC2**:
   ```bash
   ssh -i key.pem ec2-user@EC2_IP
   /tmp/setup-ec2.sh
   ```

3. **Desplegar**:
   ```bash
   ./aws/deploy-to-ec2.sh
   ssh -i key.pem ec2-user@EC2_IP
   cd /opt/pethome
   nano .env  # Configurar credenciales
   ./docker-build.sh
   ./docker-start.sh
   ```

4. **Configurar Frontend**:
   ```env
   REACT_APP_API_URL=http://EC2_IP:8080/api
   ```

5. **Verificar**:
   ```bash
   curl http://EC2_IP:8080/actuator/health
   ```

---

## Soporte

Si encuentras problemas:

1. Revisa los logs: `./docker-logs.sh`
2. Verifica la sección de [Troubleshooting](#troubleshooting)
3. Revisa la documentación de AWS
4. Contacta al equipo de desarrollo

---

## Referencias

- [AWS EC2 Documentation](https://docs.aws.amazon.com/ec2/)
- [AWS RDS PostgreSQL Documentation](https://docs.aws.amazon.com/rds/)
- [Docker Documentation](https://docs.docker.com/)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Let's Encrypt Documentation](https://letsencrypt.org/docs/)
- [Nginx Documentation](https://nginx.org/en/docs/)
