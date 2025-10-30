#!/bin/bash

# Test script for Docker Compose MySQL Setup
# This script demonstrates the complete MySQL Docker setup

set -e

echo "🐳 Docker Compose MySQL Setup Test"
echo "===================================="
echo ""

# Check Docker is available
echo "1. Checking Docker availability..."
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed"
    exit 1
fi
echo "✅ Docker is available: $(docker --version)"
echo ""

# Check Docker Compose is available
echo "2. Checking Docker Compose availability..."
if ! docker compose version &> /dev/null; then
    echo "❌ Docker Compose is not available"
    exit 1
fi
echo "✅ Docker Compose is available: $(docker compose version)"
echo ""

# Validate docker-compose.yml
echo "3. Validating docker-compose.yml..."
if docker compose config > /dev/null 2>&1; then
    echo "✅ docker-compose.yml is valid"
else
    echo "❌ docker-compose.yml has errors"
    exit 1
fi
echo ""

# Start containers
echo "4. Starting MySQL container..."
docker compose up -d
echo "✅ Container started"
echo ""

# Wait for MySQL to be ready
echo "5. Waiting for MySQL to be healthy..."
max_attempts=30
attempt=0
while [ $attempt -lt $max_attempts ]; do
    if docker compose ps | grep -q "healthy"; then
        echo "✅ MySQL is healthy"
        break
    fi
    attempt=$((attempt + 1))
    if [ $attempt -eq $max_attempts ]; then
        echo "❌ MySQL did not become healthy in time"
        docker compose logs mysql
        exit 1
    fi
    sleep 1
done
echo ""

# Check container status
echo "6. Container status:"
docker compose ps
echo ""

# Test MySQL connection
echo "7. Testing MySQL connection..."
if docker compose exec mysql mysqladmin -u reservation_user -pchange-me ping 2>&1 | grep -q "mysqld is alive"; then
    echo "✅ MySQL connection successful"
else
    echo "❌ MySQL connection failed"
    exit 1
fi
echo ""

# Show databases
echo "8. Available databases:"
docker compose exec -T mysql mysql -u reservation_user -pchange-me -e "SHOW DATABASES;" 2>/dev/null | grep reservations && echo "✅ Database 'reservations' exists"
echo ""

# Show setup information
echo "📋 Setup Information:"
echo "===================="
echo "Database: reservations"
echo "User: reservation_user"
echo "Password: change-me (change in .env file)"
echo "Host: localhost"
echo "Port: 3306"
echo ""

echo "✨ All tests passed! MySQL Docker setup is working correctly."
echo ""
echo "📝 Next steps:"
echo "  - Copy .env.example to .env and customize if needed"
echo "  - Start your Spring Boot application: ./mvnw spring-boot:run"
echo "  - Stop MySQL when done: docker compose down"
echo ""
