#!/bin/bash

# Script para iniciar todos los servicios Docker de PetHome
# Autor: PetHome Development Team

set -e

echo "========================================="
echo "PetHome Backend - Starting Docker Services"
echo "========================================="
echo ""

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

print_status() {
    echo -e "${YELLOW}[START]${NC} $1"
}

print_success() {
    echo -e "${GREEN}✓${NC} $1"
}

# Verificar Docker
if ! docker info > /dev/null 2>&1; then
    echo "Error: Docker no está corriendo."
    exit 1
fi

# Verificar si las imágenes existen
if ! docker images | grep -q "pethome"; then
    echo "No se encontraron imágenes de PetHome."
    echo "Ejecutando build primero..."
    ./docker-build.sh
fi

# Modo detached por defecto
DETACH="-d"
if [ "$1" == "--logs" ] || [ "$1" == "-l" ]; then
    DETACH=""
    print_status "Iniciando en modo attached (mostrando logs)"
fi

print_status "Iniciando servicios con Docker Compose..."
echo ""

docker-compose up $DETACH

if [ "$DETACH" == "-d" ]; then
    echo ""
    print_success "Servicios iniciados exitosamente en segundo plano"
    echo ""
    echo "========================================="
    echo "URLs de los servicios:"
    echo "========================================="
    echo "  - Eureka Dashboard:      http://localhost:8761"
    echo "  - API Gateway:           http://localhost:8080"
    echo "  - User Service:          http://localhost:8081"
    echo "  - Veterinarian Service:  http://localhost:8082"
    echo "  - Pet Service:           http://localhost:8083"
    echo "  - Appointment Service:   http://localhost:8084"
    echo ""
    echo "Swagger UIs:"
    echo "  - User Service:          http://localhost:8081/swagger-ui.html"
    echo "  - Veterinarian Service:  http://localhost:8082/swagger-ui.html"
    echo "  - Pet Service:           http://localhost:8083/swagger-ui.html"
    echo "  - Appointment Service:   http://localhost:8084/swagger-ui.html"
    echo ""
    echo "========================================="
    echo "Comandos útiles:"
    echo "========================================="
    echo "  Ver logs:              docker-compose logs -f [servicio]"
    echo "  Ver todos los logs:    docker-compose logs -f"
    echo "  Detener servicios:     ./docker-stop.sh"
    echo "  Estado de servicios:   docker-compose ps"
    echo ""
fi
