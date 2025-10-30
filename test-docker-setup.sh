#!/bin/bash

# Test script for Docker Compose MySQL Setup
# This script demonstrates the complete MySQL Docker setup
#
# SECURITY NOTE: This script is intended for development/testing purposes.
# For production use, consider using:
# - MySQL configuration files instead of command-line passwords
# - Docker secrets for credential management
# - Vault or other secrets management systems

set -e

# Load environment variables from .env if it exists, otherwise use defaults
if [ -f .env ]; then
    set -a
    source .env
    set +a
fi

# Set defaults if not already set
MYSQL_PASSWORD=${MYSQL_PASSWORD:-change-me}
MYSQL_USER=${MYSQL_USER:-reservation_user}
MYSQL_DATABASE=${MYSQL_DATABASE:-reservations}
MYSQL_PORT=${MYSQL_PORT:-3306}

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
        # Give MySQL a moment to fully initialize after health check passes
        sleep 2
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
if docker compose exec mysql mysqladmin -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" ping 2>&1 | grep -q "mysqld is alive"; then
    echo "✅ MySQL connection successful"
else
    echo "❌ MySQL connection failed"
    exit 1
fi
echo ""

# Show databases
echo "8. Available databases:"
docker compose exec -T mysql mysql -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" -e "SHOW DATABASES;" 2>/dev/null | grep "$MYSQL_DATABASE" && echo "✅ Database '$MYSQL_DATABASE' exists"
echo ""

# Show setup information
echo "📋 Setup Information:"
echo "===================="
echo "Database: $MYSQL_DATABASE"
echo "User: $MYSQL_USER"
echo "Password: ****** (configured via environment)"
echo "Host: localhost"
echo "Port: $MYSQL_PORT"
echo ""

echo "✨ All tests passed! MySQL Docker setup is working correctly."
echo ""
echo "📝 Next steps:"
echo "  - Copy .env.example to .env and customize if needed"
echo "  - Start your Spring Boot application: ./mvnw spring-boot:run"
echo "  - Stop MySQL when done: docker compose down"
echo ""
