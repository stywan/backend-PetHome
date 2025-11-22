#!/bin/bash

# Script para crear 5 usuarios con rol VET y sus veterinarios asociados en el sistema PetHome
# Este script debe ejecutarse después de que todos los servicios estén funcionando
# Asegúrate de que:
# - User Service esté ejecutándose en http://localhost:8081
# - Veterinarian Service esté ejecutándose en http://localhost:8082

USER_SERVICE_URL="http://localhost:8081"
VET_SERVICE_URL="http://localhost:8082/veterinarians"

echo "==================================================================="
echo "Creando 5 usuarios con rol VET y sus veterinarios asociados"
echo "==================================================================="
echo ""

# Array para almacenar los IDs de usuarios creados
declare -a USER_IDS

# ============================================================
# PASO 1: Crear 5 usuarios con rol VET
# ============================================================
echo "PASO 1: Creando usuarios con rol VET..."
echo "-------------------------------------------------------------------"
echo ""

# Usuario 1: Dr. Carlos Rodríguez
echo "1. Creando usuario: Dr. Carlos Rodríguez..."
RESPONSE1=$(curl -s -X POST "$USER_SERVICE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dr. Carlos Rodríguez",
    "email": "carlos.rodriguez@pethome.cl",
    "password": "VetPass123!",
    "phone": "+56912345001",
    "role": "VET",
    "photo": "https://randomuser.me/api/portraits/men/1.jpg"
  }')

USER_ID1=$(echo $RESPONSE1 | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
echo "Usuario creado con ID: $USER_ID1"
echo "$RESPONSE1" | jq '.' 2>/dev/null || echo "$RESPONSE1"
echo ""

# Usuario 2: Dra. María González
echo "2. Creando usuario: Dra. María González..."
RESPONSE2=$(curl -s -X POST "$USER_SERVICE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dra. María González",
    "email": "maria.gonzalez@pethome.cl",
    "password": "VetPass123!",
    "phone": "+56912345002",
    "role": "VET",
    "photo": "https://randomuser.me/api/portraits/women/2.jpg"
  }')

USER_ID2=$(echo $RESPONSE2 | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
echo "Usuario creado con ID: $USER_ID2"
echo "$RESPONSE2" | jq '.' 2>/dev/null || echo "$RESPONSE2"
echo ""

# Usuario 3: Dr. Jorge Fernández
echo "3. Creando usuario: Dr. Jorge Fernández..."
RESPONSE3=$(curl -s -X POST "$USER_SERVICE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dr. Jorge Fernández",
    "email": "jorge.fernandez@pethome.cl",
    "password": "VetPass123!",
    "phone": "+56912345003",
    "role": "VET",
    "photo": "https://randomuser.me/api/portraits/men/3.jpg"
  }')

USER_ID3=$(echo $RESPONSE3 | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
echo "Usuario creado con ID: $USER_ID3"
echo "$RESPONSE3" | jq '.' 2>/dev/null || echo "$RESPONSE3"
echo ""

# Usuario 4: Dra. Ana Patricia Silva
echo "4. Creando usuario: Dra. Ana Patricia Silva..."
RESPONSE4=$(curl -s -X POST "$USER_SERVICE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dra. Ana Patricia Silva",
    "email": "ana.silva@pethome.cl",
    "password": "VetPass123!",
    "phone": "+56912345004",
    "role": "VET",
    "photo": "https://randomuser.me/api/portraits/women/4.jpg"
  }')

USER_ID4=$(echo $RESPONSE4 | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
echo "Usuario creado con ID: $USER_ID4"
echo "$RESPONSE4" | jq '.' 2>/dev/null || echo "$RESPONSE4"
echo ""

# Usuario 5: Dr. Ricardo Morales
echo "5. Creando usuario: Dr. Ricardo Morales..."
RESPONSE5=$(curl -s -X POST "$USER_SERVICE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dr. Ricardo Morales",
    "email": "ricardo.morales@pethome.cl",
    "password": "VetPass123!",
    "phone": "+56912345005",
    "role": "VET",
    "photo": "https://randomuser.me/api/portraits/men/5.jpg"
  }')

USER_ID5=$(echo $RESPONSE5 | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
echo "Usuario creado con ID: $USER_ID5"
echo "$RESPONSE5" | jq '.' 2>/dev/null || echo "$RESPONSE5"
echo ""

echo "==================================================================="
echo "PASO 1 COMPLETADO: Usuarios creados exitosamente"
echo "IDs de usuarios: $USER_ID1, $USER_ID2, $USER_ID3, $USER_ID4, $USER_ID5"
echo "==================================================================="
echo ""

# Pequeña pausa para asegurar que los usuarios estén completamente registrados
sleep 2

# ============================================================
# PASO 2: Crear 5 veterinarios asociados a los usuarios
# ============================================================
echo "PASO 2: Creando perfiles de veterinarios..."
echo "-------------------------------------------------------------------"
echo ""

# Veterinario 1: Dr. Carlos Rodríguez (Medicina General)
echo "1. Creando veterinario: Dr. Carlos Rodríguez (Medicina General)..."
curl -s -X POST "$VET_SERVICE_URL" \
  -H "Content-Type: application/json" \
  -d "{
    \"userId\": $USER_ID1,
    \"specialty\": \"Medicina General\",
    \"licenseNumber\": \"VET-2024-001\",
    \"clinicName\": \"Clínica Veterinaria San Martín\",
    \"clinicAddress\": \"Av. Providencia 1234, Santiago\",
    \"workingHours\": \"Lunes a Viernes: 9:00-18:00, Sábados: 9:00-13:00\",
    \"bio\": \"Veterinario con más de 15 años de experiencia en medicina general para mascotas. Especializado en diagnóstico y tratamiento de enfermedades comunes en perros y gatos.\",
    \"experienceYears\": 15,
    \"photo\": \"https://randomuser.me/api/portraits/men/1.jpg\"
  }" | jq '.' 2>/dev/null || echo "Veterinario 1 creado"
echo ""

# Veterinario 2: Dra. María González (Cirugía)
echo "2. Creando veterinario: Dra. María González (Cirugía)..."
curl -s -X POST "$VET_SERVICE_URL" \
  -H "Content-Type: application/json" \
  -d "{
    \"userId\": $USER_ID2,
    \"specialty\": \"Cirugía\",
    \"licenseNumber\": \"VET-2024-002\",
    \"clinicName\": \"Centro Quirúrgico PetCare\",
    \"clinicAddress\": \"Av. Apoquindo 5678, Las Condes\",
    \"workingHours\": \"Lunes a Viernes: 8:00-17:00\",
    \"bio\": \"Cirujana veterinaria certificada con experiencia en cirugías de alta complejidad. Especializada en ortopedia y cirugía de tejidos blandos.\",
    \"experienceYears\": 12,
    \"photo\": \"https://randomuser.me/api/portraits/women/2.jpg\"
  }" | jq '.' 2>/dev/null || echo "Veterinario 2 creado"
echo ""

# Veterinario 3: Dr. Jorge Fernández (Dermatología)
echo "3. Creando veterinario: Dr. Jorge Fernández (Dermatología)..."
curl -s -X POST "$VET_SERVICE_URL" \
  -H "Content-Type: application/json" \
  -d "{
    \"userId\": $USER_ID3,
    \"specialty\": \"Dermatología\",
    \"licenseNumber\": \"VET-2024-003\",
    \"clinicName\": \"DermaVet Especialidades\",
    \"clinicAddress\": \"Av. Las Condes 8901, Santiago\",
    \"workingHours\": \"Martes a Sábado: 10:00-19:00\",
    \"bio\": \"Dermatólogo veterinario especializado en enfermedades de la piel, alergias y problemas capilares en mascotas. Certificado en dermatología veterinaria avanzada.\",
    \"experienceYears\": 8,
    \"photo\": \"https://randomuser.me/api/portraits/men/3.jpg\"
  }" | jq '.' 2>/dev/null || echo "Veterinario 3 creado"
echo ""

# Veterinario 4: Dra. Ana Patricia Silva (Cardiología)
echo "4. Creando veterinario: Dra. Ana Patricia Silva (Cardiología)..."
curl -s -X POST "$VET_SERVICE_URL" \
  -H "Content-Type: application/json" \
  -d "{
    \"userId\": $USER_ID4,
    \"specialty\": \"Cardiología\",
    \"licenseNumber\": \"VET-2024-004\",
    \"clinicName\": \"CardioVet Centro Especializado\",
    \"clinicAddress\": \"Av. Vitacura 3456, Vitacura\",
    \"workingHours\": \"Lunes a Viernes: 9:30-18:30\",
    \"bio\": \"Cardióloga veterinaria con especialización en enfermedades cardiovasculares en perros y gatos. Experiencia en ecocardiografía y tratamiento de insuficiencia cardíaca.\",
    \"experienceYears\": 10,
    \"photo\": \"https://randomuser.me/api/portraits/women/4.jpg\"
  }" | jq '.' 2>/dev/null || echo "Veterinario 4 creado"
echo ""

# Veterinario 5: Dr. Ricardo Morales (Animales Exóticos)
echo "5. Creando veterinario: Dr. Ricardo Morales (Animales Exóticos)..."
curl -s -X POST "$VET_SERVICE_URL" \
  -H "Content-Type: application/json" \
  -d "{
    \"userId\": $USER_ID5,
    \"specialty\": \"Animales Exóticos\",
    \"licenseNumber\": \"VET-2024-005\",
    \"clinicName\": \"Exovet Clínica Especializada\",
    \"clinicAddress\": \"Av. Kennedy 7890, Las Condes\",
    \"workingHours\": \"Lunes a Viernes: 10:00-19:00, Sábados: 10:00-14:00\",
    \"bio\": \"Veterinario especializado en el cuidado de animales exóticos: aves, reptiles, pequeños mamíferos y fauna silvestre. Certificado internacional en medicina de animales no convencionales.\",
    \"experienceYears\": 7,
    \"photo\": \"https://randomuser.me/api/portraits/men/5.jpg\"
  }" | jq '.' 2>/dev/null || echo "Veterinario 5 creado"
echo ""

echo "==================================================================="
echo "PROCESO COMPLETADO EXITOSAMENTE"
echo "==================================================================="
echo ""
echo "Se han creado:"
echo "  ✓ 5 usuarios con rol VET"
echo "  ✓ 5 perfiles de veterinarios asociados"
echo ""
echo "Credenciales de acceso (todas con la misma contraseña):"
echo "  Contraseña: VetPass123!"
echo ""
echo "  1. carlos.rodriguez@pethome.cl  - Medicina General"
echo "  2. maria.gonzalez@pethome.cl    - Cirugía"
echo "  3. jorge.fernandez@pethome.cl   - Dermatología"
echo "  4. ana.silva@pethome.cl         - Cardiología"
echo "  5. ricardo.morales@pethome.cl   - Animales Exóticos"
echo ""
echo "Para verificar los datos creados, ejecuta:"
echo "  - Usuarios:       curl $USER_SERVICE_URL/users"
echo "  - Veterinarios:   curl $VET_SERVICE_URL"
echo ""
echo "Para hacer login con un veterinario, usa:"
echo "  curl -X POST $USER_SERVICE_URL/auth/login \\"
echo "    -H 'Content-Type: application/json' \\"
echo "    -d '{\"email\":\"carlos.rodriguez@pethome.cl\",\"password\":\"VetPass123!\"}'"
echo ""
echo "==================================================================="
