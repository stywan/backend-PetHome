#!/bin/bash

###############################################################################
# EC2 Setup Script for PetHome Backend
# This script installs Docker, Docker Compose, and sets up the environment
# Run this script on your EC2 instance after SSH connection
###############################################################################

set -e

echo "=========================================="
echo "PetHome Backend - EC2 Setup Script"
echo "=========================================="

# Update system packages
echo ""
echo "Step 1: Updating system packages..."
sudo yum update -y

# Install Docker
echo ""
echo "Step 2: Installing Docker..."
sudo yum install -y docker
sudo service docker start
sudo usermod -a -G docker ec2-user

# Install Docker Compose
echo ""
echo "Step 3: Installing Docker Compose..."
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Install Git
echo ""
echo "Step 4: Installing Git..."
sudo yum install -y git

# Create application directory
echo ""
echo "Step 5: Creating application directory..."
sudo mkdir -p /opt/pethome
sudo chown ec2-user:ec2-user /opt/pethome

# Enable Docker to start on boot
echo ""
echo "Step 6: Enabling Docker to start on boot..."
sudo systemctl enable docker

# Verify installations
echo ""
echo "=========================================="
echo "Installation Complete!"
echo "=========================================="
echo "Docker version:"
docker --version
echo ""
echo "Docker Compose version:"
docker-compose --version
echo ""
echo "Git version:"
git --version
echo ""
echo "=========================================="
echo "IMPORTANT: You need to log out and log back in"
echo "for Docker permissions to take effect!"
echo "=========================================="
echo ""
echo "After re-login, upload your project files to:"
echo "/opt/pethome"
echo ""
