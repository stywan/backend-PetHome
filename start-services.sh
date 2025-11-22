#!/bin/bash

# Script para iniciar los microservicios de PetHome en orden
# Autor: PetHome Development Team

echo "========================================="
echo "PetHome Backend - Iniciando Microservicios"
echo "========================================="
echo ""

# Colores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

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

# 1. Config Server
echo -e "${YELLOW}[1/4] Iniciando Config Server...${NC}"
cd config-server
mvn spring-boot:run > ../logs/config-server.log 2>&1 &
CONFIG_PID=$!
cd ..
wait_for_service "Config Server" 8888
echo ""

# 2. Eureka Server
echo -e "${YELLOW}[2/4] Iniciando Eureka Server...${NC}"
cd eureka-server
mvn spring-boot:run > ../logs/eureka-server.log 2>&1 &
EUREKA_PID=$!
cd ..
wait_for_service "Eureka Server" 8761
echo ""

# 3. API Gateway
echo -e "${YELLOW}[3/4] Iniciando API Gateway...${NC}"
cd api-gateway
mvn spring-boot:run > ../logs/api-gateway.log 2>&1 &
GATEWAY_PID=$!
cd ..
wait_for_service "API Gateway" 8080
echo ""

# 4. User Service
echo -e "${YELLOW}[4/7] Iniciando User Service...${NC}"
cd pethome-user-service
mvn spring-boot:run > ../logs/user-service.log 2>&1 &
USER_PID=$!
cd ..
wait_for_service "User Service" 8081
echo ""

# 5. Veterinarian Service
echo -e "${YELLOW}[5/7] Iniciando Veterinarian Service...${NC}"
cd pethome-veterinarian-service
mvn spring-boot:run > ../logs/veterinarian-service.log 2>&1 &
VET_PID=$!
cd ..
wait_for_service "Veterinarian Service" 8082
echo ""

# 6. Pet Service
echo -e "${YELLOW}[6/7] Iniciando Pet Service...${NC}"
cd pethome-pet-service
mvn spring-boot:run > ../logs/pet-service.log 2>&1 &
PET_PID=$!
cd ..
wait_for_service "Pet Service" 8083
echo ""

# 7. Appointment Service
echo -e "${YELLOW}[7/7] Iniciando Appointment Service...${NC}"
cd pethome-appointment-service
mvn spring-boot:run > ../logs/appointment-service.log 2>&1 &
APPOINTMENT_PID=$!
cd ..
wait_for_service "Appointment Service" 8084
echo ""

echo "========================================="
echo -e "${GREEN}✓ Todos los servicios están ejecutándose!${NC}"
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
echo "  - H2 Console (User):     http://localhost:8081/h2-console"
echo "  - H2 Console (Vet):      http://localhost:8082/h2-console"
echo "  - H2 Console (Pet):      http://localhost:8083/h2-console"
echo "  - H2 Console (Appt):     http://localhost:8084/h2-console"
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
echo "  kill $CONFIG_PID $EUREKA_PID $GATEWAY_PID $USER_PID $VET_PID $PET_PID $APPOINTMENT_PID"
echo ""
echo "Logs en: ./logs/"
echo "========================================="
