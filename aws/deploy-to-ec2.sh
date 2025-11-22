#!/bin/bash

###############################################################################
# Deploy PetHome Backend to EC2
# This script packages and uploads your application to EC2
# Run this from your LOCAL machine
###############################################################################

set -e

# Configuration - EDIT THESE VALUES
EC2_HOST="your-ec2-public-ip"           # Example: 54.123.45.67
EC2_USER="ec2-user"
EC2_KEY="path/to/your-key.pem"          # Example: ~/Downloads/my-key.pem
REMOTE_DIR="/opt/pethome"

echo "=========================================="
echo "PetHome Backend - EC2 Deployment Script"
echo "=========================================="

# Validate configuration
if [ "$EC2_HOST" == "your-ec2-public-ip" ]; then
    echo "ERROR: Please edit this script and set your EC2_HOST"
    exit 1
fi

if [ "$EC2_KEY" == "path/to/your-key.pem" ]; then
    echo "ERROR: Please edit this script and set your EC2_KEY path"
    exit 1
fi

if [ ! -f "$EC2_KEY" ]; then
    echo "ERROR: Key file not found: $EC2_KEY"
    exit 1
fi

# Check if we're in the project root
if [ ! -f "docker-compose.yml" ]; then
    echo "ERROR: Please run this script from the pethome-backend directory"
    exit 1
fi

echo ""
echo "Step 1: Creating deployment package..."
echo "Excluding unnecessary files..."

# Create temporary directory for clean deployment
TEMP_DIR=$(mktemp -d)
echo "Temporary directory: $TEMP_DIR"

# Copy only necessary files
rsync -av --progress \
    --exclude='.git' \
    --exclude='target' \
    --exclude='.idea' \
    --exclude='logs' \
    --exclude='.DS_Store' \
    --exclude='*.log' \
    --exclude='.env' \
    ./ "$TEMP_DIR/"

echo ""
echo "Step 2: Copying .env.example to .env for EC2..."
cp .env.example "$TEMP_DIR/.env"

echo ""
echo "Step 3: Uploading files to EC2..."
echo "Target: $EC2_USER@$EC2_HOST:$REMOTE_DIR"

# Create remote directory if it doesn't exist
ssh -i "$EC2_KEY" "$EC2_USER@$EC2_HOST" "mkdir -p $REMOTE_DIR"

# Upload files using rsync
rsync -avz --progress \
    -e "ssh -i $EC2_KEY" \
    "$TEMP_DIR/" \
    "$EC2_USER@$EC2_HOST:$REMOTE_DIR/"

# Clean up temp directory
rm -rf "$TEMP_DIR"

echo ""
echo "Step 4: Setting correct permissions on EC2..."
ssh -i "$EC2_KEY" "$EC2_USER@$EC2_HOST" << 'ENDSSH'
    cd /opt/pethome
    chmod +x *.sh
    chmod +x aws/*.sh
ENDSSH

echo ""
echo "=========================================="
echo "Upload Complete!"
echo "=========================================="
echo ""
echo "Next steps:"
echo "1. SSH into your EC2 instance:"
echo "   ssh -i $EC2_KEY $EC2_USER@$EC2_HOST"
echo ""
echo "2. Navigate to the application directory:"
echo "   cd $REMOTE_DIR"
echo ""
echo "3. Edit .env file with your database credentials:"
echo "   nano .env"
echo ""
echo "4. Build and start the application:"
echo "   ./docker-build.sh"
echo "   ./docker-start.sh"
echo ""
echo "5. Check service status:"
echo "   docker-compose ps"
echo ""
