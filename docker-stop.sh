#!/bin/bash

# Script para detener todos los servicios Docker de PetHome
# Autor: PetHome Development Team

echo "========================================="
echo "PetHome Backend - Stopping Docker Services"
echo "========================================="
echo ""

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

print_status() {
    echo -e "${YELLOW}[STOP]${NC} $1"
}

print_success() {
    echo -e "${GREEN}✓${NC} $1"
}

# Opción para limpiar todo
if [ "$1" == "--clean" ] || [ "$1" == "-c" ]; then
    print_status "Deteniendo y limpiando servicios (contenedores, volúmenes y redes)..."
    docker-compose down -v --remove-orphans
    print_success "Servicios detenidos y limpiados completamente"

    # Opcional: limpiar imágenes también
    if [ "$2" == "--images" ]; then
        print_status "Eliminando imágenes de PetHome..."
        docker images | grep pethome | awk '{print $3}' | xargs -r docker rmi -f
        print_success "Imágenes eliminadas"
    fi
else
    print_status "Deteniendo servicios..."
    docker-compose down
    print_success "Servicios detenidos"
fi

echo ""
echo "========================================="
echo "Opciones de limpieza:"
echo "========================================="
echo "  Limpiar todo:              ./docker-stop.sh --clean"
echo "  Limpiar todo + imágenes:   ./docker-stop.sh --clean --images"
echo ""
