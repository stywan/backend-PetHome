#!/bin/bash

# Script para detener todos los microservicios de PetHome
# Autor: PetHome Development Team

echo "========================================="
echo "PetHome Backend - Deteniendo Microservicios"
echo "========================================="
echo ""

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

# Función para detener un servicio por puerto
stop_service_by_port() {
    local service_name=$1
    local port=$2

    echo "Deteniendo $service_name (puerto $port)..."
    PID=$(lsof -ti:$port)

    if [ -z "$PID" ]; then
        echo -e "${RED}✗ $service_name no está ejecutándose en el puerto $port${NC}"
    else
        kill -9 $PID
        echo -e "${GREEN}✓ $service_name detenido (PID: $PID)${NC}"
    fi
}

# Detener servicios en orden inverso
stop_service_by_port "Appointment Service" 8084
stop_service_by_port "Pet Service" 8083
stop_service_by_port "Veterinarian Service" 8082
stop_service_by_port "User Service" 8081
stop_service_by_port "API Gateway" 8080
stop_service_by_port "Eureka Server" 8761
stop_service_by_port "Config Server" 8888

echo ""
echo "========================================="
echo -e "${GREEN}✓ Todos los servicios han sido detenidos${NC}"
echo "========================================="
