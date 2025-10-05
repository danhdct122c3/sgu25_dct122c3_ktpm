# PowerShell script to manage the Shoe Shop application

param(
    [Parameter(Position=0)]
    [string]$Command = "start"
)

# Function to print colored output
function Write-Info {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor Green
}

function Write-Warning {
    param([string]$Message)
    Write-Host "[WARNING] $Message" -ForegroundColor Yellow
}

function Write-Error {
    param([string]$Message)
    Write-Host "[ERROR] $Message" -ForegroundColor Red
}

# Function to check if Docker is running
function Test-Docker {
    try {
        docker info | Out-Null
        return $true
    }
    catch {
        Write-Error "Docker is not running. Please start Docker and try again."
        exit 1
    }
}

# Function to show help
function Show-Help {
    Write-Host "Shoe Shop Application Management Script" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Usage: .\manage.ps1 [COMMAND]" -ForegroundColor White
    Write-Host ""
    Write-Host "Commands:" -ForegroundColor White
    Write-Host "  start          Start all services (default)" -ForegroundColor Gray
    Write-Host "  start-nginx    Start with Nginx frontend" -ForegroundColor Gray
    Write-Host "  start-proxy    Start with reverse proxy" -ForegroundColor Gray
    Write-Host "  start-tools    Start with additional tools (phpMyAdmin)" -ForegroundColor Gray
    Write-Host "  stop           Stop all services" -ForegroundColor Gray
    Write-Host "  restart        Restart all services" -ForegroundColor Gray
    Write-Host "  logs           Show logs for all services" -ForegroundColor Gray
    Write-Host "  logs-backend   Show backend logs" -ForegroundColor Gray
    Write-Host "  logs-frontend  Show frontend logs" -ForegroundColor Gray
    Write-Host "  logs-db        Show database logs" -ForegroundColor Gray
    Write-Host "  status         Show status of all services" -ForegroundColor Gray
    Write-Host "  clean          Stop and remove all containers, networks, and volumes" -ForegroundColor Gray
    Write-Host "  build          Build all images" -ForegroundColor Gray
    Write-Host "  help           Show this help message" -ForegroundColor Gray
}

# Main script logic
switch ($Command.ToLower()) {
    "start" {
        Write-Info "Starting Shoe Shop application..."
        Test-Docker
        docker-compose up -d
        Write-Info "Application started successfully!"
        Write-Info "Frontend: http://localhost:3000"
        Write-Info "Backend API: http://localhost:8080/api/v1"
        Write-Info "API Documentation: http://localhost:8080/api/v1/swagger-ui.html"
    }
    
    "start-nginx" {
        Write-Info "Starting Shoe Shop application with Nginx frontend..."
        Test-Docker
        docker-compose --profile nginx up -d
        Write-Info "Application started successfully!"
        Write-Info "Frontend (Nginx): http://localhost:80"
        Write-Info "Backend API: http://localhost:8080/api/v1"
    }
    
    "start-proxy" {
        Write-Info "Starting Shoe Shop application with reverse proxy..."
        Test-Docker
        docker-compose --profile proxy up -d
        Write-Info "Application started successfully!"
        Write-Info "Application: http://localhost:80"
    }
    
    "start-tools" {
        Write-Info "Starting Shoe Shop application with tools..."
        Test-Docker
        docker-compose --profile tools up -d
        Write-Info "Application started successfully!"
        Write-Info "Frontend: http://localhost:3000"
        Write-Info "Backend API: http://localhost:8080/api/v1"
        Write-Info "phpMyAdmin: http://localhost:8081"
    }
    
    "stop" {
        Write-Info "Stopping Shoe Shop application..."
        docker-compose down
        Write-Info "Application stopped successfully!"
    }
    
    "restart" {
        Write-Info "Restarting Shoe Shop application..."
        docker-compose restart
        Write-Info "Application restarted successfully!"
    }
    
    "logs" {
        docker-compose logs -f
    }
    
    "logs-backend" {
        docker-compose logs -f backend
    }
    
    "logs-frontend" {
        docker-compose logs -f frontend
    }
    
    "logs-db" {
        docker-compose logs -f mysql
    }
    
    "status" {
        Write-Info "Checking service status..."
        docker-compose ps
    }
    
    "clean" {
        Write-Warning "This will remove all containers, networks, and volumes!"
        $response = Read-Host "Are you sure? (y/N)"
        if ($response -eq "y" -or $response -eq "Y") {
            Write-Info "Cleaning up..."
            docker-compose down -v --remove-orphans
            docker system prune -f
            Write-Info "Cleanup completed!"
        } else {
            Write-Info "Cleanup cancelled."
        }
    }
    
    "build" {
        Write-Info "Building all images..."
        Test-Docker
        docker-compose build --no-cache
        Write-Info "Build completed!"
    }
    
    "help" {
        Show-Help
    }
    
    default {
        Write-Error "Unknown command: $Command"
        Show-Help
        exit 1
    }
}