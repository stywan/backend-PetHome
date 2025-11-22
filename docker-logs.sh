#!/bin/bash

# Script para ver logs de los servicios Docker de PetHome
# Autor: PetHome Development Team

# Colores
YELLOW='\033[1;33m'
NC='\033[0m'

if [ "$1" == "--help" ] || [ "$1" == "-h" ] || [ -z "$1" ]; then
    echo "========================================="
    echo "PetHome Backend - Docker Logs Viewer"
    echo "========================================="
    echo ""
    echo "Uso: $0 [servicio] [opciones]"
    echo ""
    echo "Servicios disponibles:"
    echo "  config-server"
    echo "  eureka-server"
    echo "  api-gateway"
    echo "  user-service"
    echo "  veterinarian-service"
    echo "  pet-service"
    echo "  appointment-service"
    echo "  all (todos los servicios)"
    echo ""
    echo "Opciones:"
    echo "  -f    Seguir logs en tiempo real (follow)"
    echo "  -n N  Mostrar últimas N líneas"
    echo ""
    echo "Ejemplos:"
    echo "  $0 user-service -f          # Ver logs de user-service en tiempo real"
    echo "  $0 all -f                   # Ver todos los logs"
    echo "  $0 eureka-server -n 100     # Ver últimas 100 líneas"
    echo ""
    exit 0
fi

SERVICE=$1
shift

if [ "$SERVICE" == "all" ]; then
    echo -e "${YELLOW}Mostrando logs de todos los servicios...${NC}"
    docker-compose logs "$@"
else
    echo -e "${YELLOW}Mostrando logs de $SERVICE...${NC}"
    docker-compose logs "$@" "$SERVICE"
fi
