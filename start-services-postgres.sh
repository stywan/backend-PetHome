#!/bin/bash

# Script para iniciar los microservicios de PetHome con PostgreSQL (AWS RDS)
# Autor: PetHome Development Team

echo "========================================="
echo "PetHome Backend - Iniciando con PostgreSQL"
echo "========================================="
echo ""

# Colores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Verificar que existe el archivo .env
if [ ! -f ".env" ]; then
    echo -e "${RED}✗ Error: No se encuentra el archivo .env${NC}"
    echo "Crea el archivo .env con las credenciales de la base de datos"
    echo "Puedes usar .env.example como plantilla"
    exit 1
fi

# Cargar variables de entorno
echo -e "${BLUE}Cargando variables de entorno desde .env...${NC}"
export $(cat .env | grep -v '^#' | xargs)
echo -e "${GREEN}✓ Variables cargadas${NC}"
echo ""

# Crear directorio de logs si no existe
mkdir -p logs

# Función para esperar que un servicio esté listo
wait_for_service() {
    local service_name=$1
    local port=$2
    local max_attempts=30
    local attempt=0

    echo -e "${YELLOW}Esperando a que $service_name esté listo (puerto $port)...${NC}"

    while [ $attempt -lt $max_attempts ]; do
        if nc -z localhost $port 2>/dev/null; then
            echo -e "${GREEN}✓ $service_name está listo!${NC}"
            return 0
        fi
        attempt=$((attempt + 1))
        echo -n "."
        sleep 2
    done

    echo -e "${RED}✗ Timeout esperando a $service_name${NC}"
    return 1
}

# Función para verificar conectividad a PostgreSQL
check_postgres() {
    echo -e "${BLUE}Verificando conexión a PostgreSQL...${NC}"
    if command -v psql &> /dev/null; then
        PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -U $DB_USERNAME -d $DB_NAME -c "SELECT 1;" > /dev/null 2>&1
        if [ $? -eq 0 ]; then
            echo -e "${GREEN}✓ Conexión a PostgreSQL exitosa${NC}"
            echo -e "${GREEN}  Host: $DB_HOST${NC}"
            echo -e "${GREEN}  Database: $DB_NAME${NC}"
            return 0
        else
            echo -e "${RED}✗ No se pudo conectar a PostgreSQL${NC}"
            echo "Verifica las credenciales en el archivo .env"
            return 1
        fi
    else
        echo -e "${YELLOW}⚠ psql no instalado, saltando verificación de PostgreSQL${NC}"
        return 0
    fi
}

# Verificar PostgreSQL antes de iniciar servicios
check_postgres
if [ $? -ne 0 ]; then
    echo ""
    echo -e "${YELLOW}¿Deseas continuar de todas formas? (y/n)${NC}"
    read -r response
    if [[ ! "$response" =~ ^[Yy]$ ]]; then
        echo "Saliendo..."
        exit 1
    fi
fi
echo ""

# 1. Config Server
echo -e "${YELLOW}[1/7] Iniciando Config Server...${NC}"
cd config-server
SPRING_PROFILES_ACTIVE=native mvn spring-boot:run > ../logs/config-server.log 2>&1 &
CONFIG_PID=$!
cd ..
wait_for_service "Config Server" 8888
echo ""

# 2. Eureka Server
echo -e "${YELLOW}[2/7] Iniciando Eureka Server...${NC}"
cd eureka-server
mvn spring-boot:run > ../logs/eureka-server.log 2>&1 &
EUREKA_PID=$!
cd ..
wait_for_service "Eureka Server" 8761
echo ""

# 3. API Gateway
echo -e "${YELLOW}[3/7] Iniciando API Gateway...${NC}"
cd api-gateway
mvn spring-boot:run > ../logs/api-gateway.log 2>&1 &
GATEWAY_PID=$!
cd ..
wait_for_service "API Gateway" 8080
echo ""

# 4. User Service (con perfil docker = PostgreSQL)
echo -e "${YELLOW}[4/7] Iniciando User Service (PostgreSQL)...${NC}"
cd pethome-user-service
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=docker" > ../logs/user-service.log 2>&1 &
USER_PID=$!
cd ..
wait_for_service "User Service" 8081
echo ""

# 5. Veterinarian Service (con perfil docker = PostgreSQL)
echo -e "${YELLOW}[5/7] Iniciando Veterinarian Service (PostgreSQL)...${NC}"
cd pethome-veterinarian-service
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=docker" > ../logs/veterinarian-service.log 2>&1 &
VET_PID=$!
cd ..
wait_for_service "Veterinarian Service" 8082
echo ""

# 6. Pet Service (con perfil docker = PostgreSQL)
echo -e "${YELLOW}[6/7] Iniciando Pet Service (PostgreSQL)...${NC}"
cd pethome-pet-service
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=docker" > ../logs/pet-service.log 2>&1 &
PET_PID=$!
cd ..
wait_for_service "Pet Service" 8083
echo ""

# 7. Appointment Service (con perfil docker = PostgreSQL)
echo -e "${YELLOW}[7/7] Iniciando Appointment Service (PostgreSQL)...${NC}"
cd pethome-appointment-service
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=docker" > ../logs/appointment-service.log 2>&1 &
APPOINTMENT_PID=$!
cd ..
wait_for_service "Appointment Service" 8084
echo ""

echo "========================================="
echo -e "${GREEN}✓ Todos los servicios están ejecutándose con PostgreSQL!${NC}"
echo "========================================="
echo ""
echo "URLs de los servicios:"
echo "  - Config Server:         http://localhost:8888"
echo "  - Eureka Server:         http://localhost:8761"
echo "  - API Gateway:           http://localhost:8080"
echo "  - User Service:          http://localhost:8081"
echo "  - Veterinarian Service:  http://localhost:8082"
echo "  - Pet Service:           http://localhost:8083"
echo "  - Appointment Service:   http://localhost:8084"
echo ""
echo -e "${BLUE}Base de datos: PostgreSQL (AWS RDS)${NC}"
echo "  - Host: $DB_HOST"
echo "  - Database: $DB_NAME"
echo ""
echo "PIDs de los procesos:"
echo "  - Config Server:         $CONFIG_PID"
echo "  - Eureka Server:         $EUREKA_PID"
echo "  - API Gateway:           $GATEWAY_PID"
echo "  - User Service:          $USER_PID"
echo "  - Veterinarian Service:  $VET_PID"
echo "  - Pet Service:           $PET_PID"
echo "  - Appointment Service:   $APPOINTMENT_PID"
echo ""
echo "Para detener todos los servicios, ejecuta:"
echo "  ./stop-services.sh"
echo ""
echo "Logs en: ./logs/"
echo "========================================="
