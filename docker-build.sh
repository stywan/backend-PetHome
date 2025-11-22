#!/bin/bash

# Script para construir todas las imágenes Docker de PetHome
# Autor: PetHome Development Team

set -e  # Exit on error

echo "========================================="
echo "PetHome Backend - Building Docker Images"
echo "========================================="
echo ""

# Colores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Función para mostrar mensajes
print_status() {
    echo -e "${YELLOW}[BUILD]${NC} $1"
}

print_success() {
    echo -e "${GREEN}✓${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

# Verificar que Docker está corriendo
if ! docker info > /dev/null 2>&1; then
    print_error "Docker no está corriendo. Por favor inicia Docker Desktop."
    exit 1
fi

# Opción para build individual o completo
if [ "$1" == "--help" ] || [ "$1" == "-h" ]; then
    echo "Uso: $0 [servicio]"
    echo ""
    echo "Servicios disponibles:"
    echo "  config-server"
    echo "  eureka-server"
    echo "  api-gateway"
    echo "  user-service"
    echo "  veterinarian-service"
    echo "  pet-service"
    echo "  appointment-service"
    echo ""
    echo "Si no se especifica servicio, se construyen todas las imágenes."
    exit 0
fi

# Timestamp de inicio
START_TIME=$(date +%s)

# Si se especifica un servicio, construir solo ese
if [ -n "$1" ]; then
    print_status "Construyendo imagen para $1..."
    docker-compose build "$1"
    print_success "Imagen $1 construida exitosamente"
else
    # Construir todas las imágenes
    print_status "Construyendo todas las imágenes Docker..."
    echo ""

    # Lista de servicios en orden
    services=(
        "config-server"
        "eureka-server"
        "api-gateway"
        "user-service"
        "veterinarian-service"
        "pet-service"
        "appointment-service"
    )

    total=${#services[@]}
    current=0

    for service in "${services[@]}"; do
        current=$((current + 1))
        print_status "[$current/$total] Construyendo $service..."

        if docker-compose build "$service"; then
            print_success "$service construido exitosamente"
        else
            print_error "Error construyendo $service"
            exit 1
        fi
        echo ""
    done
fi

# Timestamp de fin
END_TIME=$(date +%s)
DURATION=$((END_TIME - START_TIME))

echo "========================================="
echo -e "${GREEN}✓ Build completado exitosamente${NC}"
echo "Tiempo total: ${DURATION} segundos"
echo "========================================="
echo ""
echo "Para iniciar los servicios, ejecuta:"
echo "  ./docker-start.sh"
echo ""
