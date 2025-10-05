#!/bin/bash

# Script to manage the Shoe Shop application

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi
}

# Function to show help
show_help() {
    echo "Shoe Shop Application Management Script"
    echo ""
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  start          Start all services (default)"
    echo "  start-nginx    Start with Nginx frontend"
    echo "  start-proxy    Start with reverse proxy"
    echo "  start-tools    Start with additional tools (phpMyAdmin)"
    echo "  stop           Stop all services"
    echo "  restart        Restart all services"
    echo "  logs           Show logs for all services"
    echo "  logs-backend   Show backend logs"
    echo "  logs-frontend  Show frontend logs"
    echo "  logs-db        Show database logs"
    echo "  status         Show status of all services"
    echo "  clean          Stop and remove all containers, networks, and volumes"
    echo "  build          Build all images"
    echo "  help           Show this help message"
}

# Main script logic
case "${1:-start}" in
    "start")
        print_status "Starting Shoe Shop application..."
        check_docker
        docker-compose up -d
        print_status "Application started successfully!"
        print_status "Frontend: http://localhost:3000"
        print_status "Backend API: http://localhost:8080/api/v1"
        print_status "API Documentation: http://localhost:8080/api/v1/swagger-ui.html"
        ;;
    
    "start-nginx")
        print_status "Starting Shoe Shop application with Nginx frontend..."
        check_docker
        docker-compose --profile nginx up -d
        print_status "Application started successfully!"
        print_status "Frontend (Nginx): http://localhost:80"
        print_status "Backend API: http://localhost:8080/api/v1"
        ;;
    
    "start-proxy")
        print_status "Starting Shoe Shop application with reverse proxy..."
        check_docker
        docker-compose --profile proxy up -d
        print_status "Application started successfully!"
        print_status "Application: http://localhost:80"
        ;;
    
    "start-tools")
        print_status "Starting Shoe Shop application with tools..."
        check_docker
        docker-compose --profile tools up -d
        print_status "Application started successfully!"
        print_status "Frontend: http://localhost:3000"
        print_status "Backend API: http://localhost:8080/api/v1"
        print_status "phpMyAdmin: http://localhost:8081"
        ;;
    
    "stop")
        print_status "Stopping Shoe Shop application..."
        docker-compose down
        print_status "Application stopped successfully!"
        ;;
    
    "restart")
        print_status "Restarting Shoe Shop application..."
        docker-compose restart
        print_status "Application restarted successfully!"
        ;;
    
    "logs")
        docker-compose logs -f
        ;;
    
    "logs-backend")
        docker-compose logs -f backend
        ;;
    
    "logs-frontend")
        docker-compose logs -f frontend
        ;;
    
    "logs-db")
        docker-compose logs -f mysql
        ;;
    
    "status")
        print_status "Checking service status..."
        docker-compose ps
        ;;
    
    "clean")
        print_warning "This will remove all containers, networks, and volumes!"
        read -p "Are you sure? (y/N): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            print_status "Cleaning up..."
            docker-compose down -v --remove-orphans
            docker system prune -f
            print_status "Cleanup completed!"
        else
            print_status "Cleanup cancelled."
        fi
        ;;
    
    "build")
        print_status "Building all images..."
        check_docker
        docker-compose build --no-cache
        print_status "Build completed!"
        ;;
    
    "help"|"-h"|"--help")
        show_help
        ;;
    
    *)
        print_error "Unknown command: $1"
        show_help
        exit 1
        ;;
esac