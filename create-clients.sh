#!/bin/bash

# Script para crear usuarios con rol CLIENT en el sistema PetHome
# Este script crea clientes (dueños de mascotas) con sus direcciones
# Asegúrate de que User Service esté ejecutándose en http://localhost:8081

USER_SERVICE_URL="http://localhost:8081"

echo "==================================================================="
echo "Creando usuarios con rol CLIENT (Dueños de mascotas)"
echo "==================================================================="
echo ""

# Array para almacenar los IDs de usuarios creados
declare -a USER_IDS

# ============================================================
# Crear 5 usuarios con rol CLIENT
# ============================================================

# Cliente 1: Juan Pérez
echo "1. Creando cliente: Juan Pérez..."
RESPONSE1=$(curl -s -X POST "$USER_SERVICE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Pérez",
    "email": "juan.perez@gmail.com",
    "password": "Cliente123!",
    "phone": "+56987654001",
    "role": "CLIENT",
    "photo": "https://randomuser.me/api/portraits/men/10.jpg"
  }')

USER_ID1=$(echo $RESPONSE1 | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
echo "Cliente creado con ID: $USER_ID1"
echo "$RESPONSE1" | jq '.' 2>/dev/null || echo "$RESPONSE1"
echo ""

# Cliente 2: María López
echo "2. Creando cliente: María López..."
RESPONSE2=$(curl -s -X POST "$USER_SERVICE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "María López",
    "email": "maria.lopez@gmail.com",
    "password": "Cliente123!",
    "phone": "+56987654002",
    "role": "CLIENT",
    "photo": "https://randomuser.me/api/portraits/women/10.jpg"
  }')

USER_ID2=$(echo $RESPONSE2 | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
echo "Cliente creado con ID: $USER_ID2"
echo "$RESPONSE2" | jq '.' 2>/dev/null || echo "$RESPONSE2"
echo ""

# Cliente 3: Pedro Sánchez
echo "3. Creando cliente: Pedro Sánchez..."
RESPONSE3=$(curl -s -X POST "$USER_SERVICE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Pedro Sánchez",
    "email": "pedro.sanchez@gmail.com",
    "password": "Cliente123!",
    "phone": "+56987654003",
    "role": "CLIENT",
    "photo": "https://randomuser.me/api/portraits/men/11.jpg"
  }')

USER_ID3=$(echo $RESPONSE3 | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
echo "Cliente creado con ID: $USER_ID3"
echo "$RESPONSE3" | jq '.' 2>/dev/null || echo "$RESPONSE3"
echo ""

# Cliente 4: Carolina Ramírez
echo "4. Creando cliente: Carolina Ramírez..."
RESPONSE4=$(curl -s -X POST "$USER_SERVICE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Carolina Ramírez",
    "email": "carolina.ramirez@gmail.com",
    "password": "Cliente123!",
    "phone": "+56987654004",
    "role": "CLIENT",
    "photo": "https://randomuser.me/api/portraits/women/11.jpg"
  }')

USER_ID4=$(echo $RESPONSE4 | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
echo "Cliente creado con ID: $USER_ID4"
echo "$RESPONSE4" | jq '.' 2>/dev/null || echo "$RESPONSE4"
echo ""

# Cliente 5: Andrés Torres
echo "5. Creando cliente: Andrés Torres..."
RESPONSE5=$(curl -s -X POST "$USER_SERVICE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Andrés Torres",
    "email": "andres.torres@gmail.com",
    "password": "Cliente123!",
    "phone": "+56987654005",
    "role": "CLIENT",
    "photo": "https://randomuser.me/api/portraits/men/12.jpg"
  }')

USER_ID5=$(echo $RESPONSE5 | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
echo "Cliente creado con ID: $USER_ID5"
echo "$RESPONSE5" | jq '.' 2>/dev/null || echo "$RESPONSE5"
echo ""

# Cliente 6: Valentina Muñoz
echo "6. Creando cliente: Valentina Muñoz..."
RESPONSE6=$(curl -s -X POST "$USER_SERVICE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Valentina Muñoz",
    "email": "valentina.munoz@gmail.com",
    "password": "Cliente123!",
    "phone": "+56987654006",
    "role": "CLIENT",
    "photo": "https://randomuser.me/api/portraits/women/12.jpg"
  }')

USER_ID6=$(echo $RESPONSE6 | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
echo "Cliente creado con ID: $USER_ID6"
echo "$RESPONSE6" | jq '.' 2>/dev/null || echo "$RESPONSE6"
echo ""

# Cliente 7: Roberto Castro
echo "7. Creando cliente: Roberto Castro..."
RESPONSE7=$(curl -s -X POST "$USER_SERVICE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Roberto Castro",
    "email": "roberto.castro@gmail.com",
    "password": "Cliente123!",
    "phone": "+56987654007",
    "role": "CLIENT",
    "photo": "https://randomuser.me/api/portraits/men/13.jpg"
  }')

USER_ID7=$(echo $RESPONSE7 | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
echo "Cliente creado con ID: $USER_ID7"
echo "$RESPONSE7" | jq '.' 2>/dev/null || echo "$RESPONSE7"
echo ""

# Cliente 8: Fernanda Gutiérrez
echo "8. Creando cliente: Fernanda Gutiérrez..."
RESPONSE8=$(curl -s -X POST "$USER_SERVICE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Fernanda Gutiérrez",
    "email": "fernanda.gutierrez@gmail.com",
    "password": "Cliente123!",
    "phone": "+56987654008",
    "role": "CLIENT",
    "photo": "https://randomuser.me/api/portraits/women/13.jpg"
  }')

USER_ID8=$(echo $RESPONSE8 | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
echo "Cliente creado con ID: $USER_ID8"
echo "$RESPONSE8" | jq '.' 2>/dev/null || echo "$RESPONSE8"
echo ""

# Cliente 9: Diego Vargas
echo "9. Creando cliente: Diego Vargas..."
RESPONSE9=$(curl -s -X POST "$USER_SERVICE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Diego Vargas",
    "email": "diego.vargas@gmail.com",
    "password": "Cliente123!",
    "phone": "+56987654009",
    "role": "CLIENT",
    "photo": "https://randomuser.me/api/portraits/men/14.jpg"
  }')

USER_ID9=$(echo $RESPONSE9 | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
echo "Cliente creado con ID: $USER_ID9"
echo "$RESPONSE9" | jq '.' 2>/dev/null || echo "$RESPONSE9"
echo ""

# Cliente 10: Sofía Herrera
echo "10. Creando cliente: Sofía Herrera..."
RESPONSE10=$(curl -s -X POST "$USER_SERVICE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Sofía Herrera",
    "email": "sofia.herrera@gmail.com",
    "password": "Cliente123!",
    "phone": "+56987654010",
    "role": "CLIENT",
    "photo": "https://randomuser.me/api/portraits/women/14.jpg"
  }')

USER_ID10=$(echo $RESPONSE10 | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
echo "Cliente creado con ID: $USER_ID10"
echo "$RESPONSE10" | jq '.' 2>/dev/null || echo "$RESPONSE10"
echo ""

echo "==================================================================="
echo "PASO 1 COMPLETADO: Clientes creados exitosamente"
echo "IDs de usuarios: $USER_ID1, $USER_ID2, $USER_ID3, $USER_ID4, $USER_ID5,"
echo "                 $USER_ID6, $USER_ID7, $USER_ID8, $USER_ID9, $USER_ID10"
echo "==================================================================="
echo ""

# Pequeña pausa para asegurar que los usuarios estén completamente registrados
sleep 2

# ============================================================
# PASO 2: Agregar direcciones a algunos clientes
# ============================================================
echo "PASO 2: Agregando direcciones a los clientes..."
echo "-------------------------------------------------------------------"
echo ""

# Dirección para Juan Pérez (Cliente 1)
if [ ! -z "$USER_ID1" ]; then
  echo "Agregando dirección para Juan Pérez (ID: $USER_ID1)..."
  curl -s -X POST "$USER_SERVICE_URL/users/$USER_ID1/addresses" \
    -H "Content-Type: application/json" \
    -d '{
      "street": "Av. Libertador Bernardo O'\''Higgins 1234",
      "city": "Santiago",
      "neighborhood": "Santiago Centro",
      "zipCode": "8320000",
      "isDefault": true,
      "latitude": -33.4489,
      "longitude": -70.6693
    }' | jq '.' 2>/dev/null || echo "Dirección agregada"
  echo ""
fi

# Dirección para María López (Cliente 2)
if [ ! -z "$USER_ID2" ]; then
  echo "Agregando dirección para María López (ID: $USER_ID2)..."
  curl -s -X POST "$USER_SERVICE_URL/users/$USER_ID2/addresses" \
    -H "Content-Type: application/json" \
    -d '{
      "street": "Av. Providencia 2567",
      "city": "Providencia",
      "neighborhood": "Barrio Italia",
      "zipCode": "7500000",
      "isDefault": true,
      "latitude": -33.4250,
      "longitude": -70.6100
    }' | jq '.' 2>/dev/null || echo "Dirección agregada"
  echo ""
fi

# Dirección para Pedro Sánchez (Cliente 3)
if [ ! -z "$USER_ID3" ]; then
  echo "Agregando dirección para Pedro Sánchez (ID: $USER_ID3)..."
  curl -s -X POST "$USER_SERVICE_URL/users/$USER_ID3/addresses" \
    -H "Content-Type: application/json" \
    -d '{
      "street": "Av. Las Condes 8901",
      "city": "Las Condes",
      "neighborhood": "El Golf",
      "zipCode": "7550000",
      "isDefault": true,
      "latitude": -33.4150,
      "longitude": -70.5800
    }' | jq '.' 2>/dev/null || echo "Dirección agregada"
  echo ""
fi

# Dirección para Carolina Ramírez (Cliente 4)
if [ ! -z "$USER_ID4" ]; then
  echo "Agregando dirección para Carolina Ramírez (ID: $USER_ID4)..."
  curl -s -X POST "$USER_SERVICE_URL/users/$USER_ID4/addresses" \
    -H "Content-Type: application/json" \
    -d '{
      "street": "Av. Vicuña Mackenna 3456",
      "city": "Ñuñoa",
      "neighborhood": "Plaza Ñuñoa",
      "zipCode": "7750000",
      "isDefault": true,
      "latitude": -33.4550,
      "longitude": -70.6000
    }' | jq '.' 2>/dev/null || echo "Dirección agregada"
  echo ""
fi

# Dirección para Andrés Torres (Cliente 5)
if [ ! -z "$USER_ID5" ]; then
  echo "Agregando dirección para Andrés Torres (ID: $USER_ID5)..."
  curl -s -X POST "$USER_SERVICE_URL/users/$USER_ID5/addresses" \
    -H "Content-Type: application/json" \
    -d '{
      "street": "Av. Apoquindo 4567",
      "city": "Las Condes",
      "neighborhood": "Los Dominicos",
      "zipCode": "7600000",
      "isDefault": true,
      "latitude": -33.4050,
      "longitude": -70.5500
    }' | jq '.' 2>/dev/null || echo "Dirección agregada"
  echo ""
fi

# Dirección para Valentina Muñoz (Cliente 6)
if [ ! -z "$USER_ID6" ]; then
  echo "Agregando dirección para Valentina Muñoz (ID: $USER_ID6)..."
  curl -s -X POST "$USER_SERVICE_URL/users/$USER_ID6/addresses" \
    -H "Content-Type: application/json" \
    -d '{
      "street": "Av. Kennedy 5678",
      "city": "Vitacura",
      "neighborhood": "Bicentenario",
      "zipCode": "7630000",
      "isDefault": true,
      "latitude": -33.3850,
      "longitude": -70.5700
    }' | jq '.' 2>/dev/null || echo "Dirección agregada"
  echo ""
fi

# Dirección para Roberto Castro (Cliente 7)
if [ ! -z "$USER_ID7" ]; then
  echo "Agregando dirección para Roberto Castro (ID: $USER_ID7)..."
  curl -s -X POST "$USER_SERVICE_URL/users/$USER_ID7/addresses" \
    -H "Content-Type: application/json" \
    -d '{
      "street": "Av. Grecia 6789",
      "city": "Peñalolén",
      "neighborhood": "Peñalolén Alto",
      "zipCode": "7910000",
      "isDefault": true,
      "latitude": -33.4950,
      "longitude": -70.5400
    }' | jq '.' 2>/dev/null || echo "Dirección agregada"
  echo ""
fi

# Dirección para Fernanda Gutiérrez (Cliente 8)
if [ ! -z "$USER_ID8" ]; then
  echo "Agregando dirección para Fernanda Gutiérrez (ID: $USER_ID8)..."
  curl -s -X POST "$USER_SERVICE_URL/users/$USER_ID8/addresses" \
    -H "Content-Type: application/json" \
    -d '{
      "street": "Av. Irarrázaval 7890",
      "city": "Ñuñoa",
      "neighborhood": "Villa Frei",
      "zipCode": "7800000",
      "isDefault": true,
      "latitude": -33.4650,
      "longitude": -70.6100
    }' | jq '.' 2>/dev/null || echo "Dirección agregada"
  echo ""
fi

# Dirección para Diego Vargas (Cliente 9)
if [ ! -z "$USER_ID9" ]; then
  echo "Agregando dirección para Diego Vargas (ID: $USER_ID9)..."
  curl -s -X POST "$USER_SERVICE_URL/users/$USER_ID9/addresses" \
    -H "Content-Type: application/json" \
    -d '{
      "street": "Av. Santa Rosa 8901",
      "city": "La Pintana",
      "neighborhood": "Santa Rosa",
      "zipCode": "8820000",
      "isDefault": true,
      "latitude": -33.5850,
      "longitude": -70.6350
    }' | jq '.' 2>/dev/null || echo "Dirección agregada"
  echo ""
fi

# Dirección para Sofía Herrera (Cliente 10)
if [ ! -z "$USER_ID10" ]; then
  echo "Agregando dirección para Sofía Herrera (ID: $USER_ID10)..."
  curl -s -X POST "$USER_SERVICE_URL/users/$USER_ID10/addresses" \
    -H "Content-Type: application/json" \
    -d '{
      "street": "Av. Américo Vespucio 9012",
      "city": "Maipú",
      "neighborhood": "Maipú Centro",
      "zipCode": "9250000",
      "isDefault": true,
      "latitude": -33.5150,
      "longitude": -70.7550
    }' | jq '.' 2>/dev/null || echo "Dirección agregada"
  echo ""
fi

echo "==================================================================="
echo "PROCESO COMPLETADO EXITOSAMENTE"
echo "==================================================================="
echo ""
echo "Se han creado:"
echo "  ✓ 10 usuarios con rol CLIENT (Clientes/Dueños de mascotas)"
echo "  ✓ 10 direcciones asociadas a cada cliente"
echo ""
echo "Credenciales de acceso (todas con la misma contraseña):"
echo "  Contraseña: Cliente123!"
echo ""
echo "  1.  juan.perez@gmail.com"
echo "  2.  maria.lopez@gmail.com"
echo "  3.  pedro.sanchez@gmail.com"
echo "  4.  carolina.ramirez@gmail.com"
echo "  5.  andres.torres@gmail.com"
echo "  6.  valentina.munoz@gmail.com"
echo "  7.  roberto.castro@gmail.com"
echo "  8.  fernanda.gutierrez@gmail.com"
echo "  9.  diego.vargas@gmail.com"
echo "  10. sofia.herrera@gmail.com"
echo ""
echo "Para verificar los datos creados, ejecuta:"
echo "  - Usuarios:    curl $USER_SERVICE_URL/users"
echo "  - Direcciones: curl $USER_SERVICE_URL/users/{userId}/addresses"
echo ""
echo "Para hacer login con un cliente, usa:"
echo "  curl -X POST $USER_SERVICE_URL/auth/login \\"
echo "    -H 'Content-Type: application/json' \\"
echo "    -d '{\"email\":\"juan.perez@gmail.com\",\"password\":\"Cliente123!\"}'"
echo ""
echo "==================================================================="
