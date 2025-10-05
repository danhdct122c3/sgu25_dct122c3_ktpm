# Shoe Shop Application - Docker Setup

Ứng dụng bán giày trực tuyến với React frontend và Spring Boot backend.

## Kiến trúc hệ thống

- **Frontend**: React + Vite + TailwindCSS (Port 3000)
- **Backend**: Spring Boot + MySQL (Port 8080)
- **Database**: MySQL 8.0 (Port 3306)
- **Tools**: phpMyAdmin (Port 8081) - optional

## Yêu cầu hệ thống

- Docker Desktop
- Docker Compose
- Git

## Cấu hình môi trường

1. Copy file `.env.example` thành `.env` và cập nhật các giá trị:
```bash
cp .env.example .env
```

2. Cập nhật các biến môi trường trong file `.env`:
```env
# Database
MYSQL_ROOT_PASSWORD=your_secure_password
MYSQL_DATABASE=shop_shoe_superteam

# AI Configuration (optional)
AI_OPENAI_APIKEY=your_openai_api_key
```

## Cách chạy ứng dụng

### 1. Khởi chạy cơ bản (Recommended)
```bash
# Windows PowerShell
.\manage.ps1 start

# Linux/Mac
./manage.sh start
```

**Hoặc sử dụng Docker Compose trực tiếp:**
```bash
docker-compose up -d
```

**Truy cập:**
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api/v1
- API Documentation: http://localhost:8080/api/v1/swagger-ui.html

### 2. Khởi chạy với Nginx Frontend
```bash
# Windows PowerShell
.\manage.ps1 start-nginx

# Linux/Mac
./manage.sh start-nginx

# Docker Compose
docker-compose --profile nginx up -d
```

**Truy cập:**
- Frontend (Nginx): http://localhost:80
- Backend API: http://localhost:8080/api/v1

### 3. Khởi chạy với Reverse Proxy
```bash
# Windows PowerShell
.\manage.ps1 start-proxy

# Linux/Mac
./manage.sh start-proxy

# Docker Compose
docker-compose --profile proxy up -d
```

**Truy cập:**
- Application: http://localhost:80 (tất cả requests qua proxy)

### 4. Khởi chạy với công cụ quản trị
```bash
# Windows PowerShell
.\manage.ps1 start-tools

# Linux/Mac
./manage.sh start-tools

# Docker Compose
docker-compose --profile tools up -d
```

**Truy cập:**
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api/v1
- phpMyAdmin: http://localhost:8081

## Quản lý ứng dụng

### Dừng ứng dụng
```bash
# Script
.\manage.ps1 stop
./manage.sh stop

# Docker Compose
docker-compose down
```

### Khởi động lại
```bash
# Script
.\manage.ps1 restart
./manage.sh restart

# Docker Compose
docker-compose restart
```

### Xem logs
```bash
# Tất cả services
.\manage.ps1 logs
./manage.sh logs
docker-compose logs -f

# Backend only
.\manage.ps1 logs-backend
./manage.sh logs-backend
docker-compose logs -f backend

# Frontend only
.\manage.ps1 logs-frontend
./manage.sh logs-frontend
docker-compose logs -f frontend

# Database only
.\manage.ps1 logs-db
./manage.sh logs-db
docker-compose logs -f mysql
```

### Kiểm tra trạng thái
```bash
# Script
.\manage.ps1 status
./manage.sh status

# Docker Compose
docker-compose ps
```

### Build lại images
```bash
# Script
.\manage.ps1 build
./manage.sh build

# Docker Compose
docker-compose build --no-cache
```

### Dọn dẹp hệ thống
```bash
# Script (xóa containers, networks, volumes)
.\manage.ps1 clean
./manage.sh clean

# Docker Compose
docker-compose down -v --remove-orphans
docker system prune -f
```

## Cấu trúc thư mục

```
├── back-end/              # Spring Boot backend
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
├── front-end/             # React frontend
│   ├── Dockerfile
│   ├── Dockerfile.nginx
│   ├── package.json
│   └── src/
├── nginx/                 # Nginx reverse proxy config
│   └── nginx.conf
├── sql/                   # Database initialization scripts
│   ├── *.sql
├── docker-compose.yml     # Main compose file
├── .env                   # Environment variables
├── manage.ps1             # Windows management script
└── manage.sh              # Linux/Mac management script
```

## Profiles trong Docker Compose

- **Default**: Frontend (serve) + Backend + MySQL
- **nginx**: Frontend (nginx) + Backend + MySQL
- **proxy**: All services + Nginx reverse proxy
- **tools**: All services + phpMyAdmin

## Troubleshooting

### 1. Port đã được sử dụng
```bash
# Kiểm tra port đang sử dụng
netstat -an | findstr :3000
netstat -an | findstr :8080

# Dừng process sử dụng port
# Windows
taskkill /PID <process_id> /F

# Linux/Mac
kill -9 <process_id>
```

### 2. Database connection error
```bash
# Kiểm tra logs database
docker-compose logs mysql

# Restart database
docker-compose restart mysql
```

### 3. Frontend không load được API
- Kiểm tra biến môi trường `VITE_API_BASE_URL` trong `.env`
- Đảm bảo backend đang chạy và healthy

### 4. Build lỗi
```bash
# Clean và build lại
docker-compose down
docker system prune -f
docker-compose build --no-cache
docker-compose up -d
```

## Development

### Chạy riêng từng service

#### Frontend only
```bash
cd front-end
docker-compose up
```

#### Backend only (cần MySQL)
```bash
# Start MySQL trước
docker-compose up mysql -d

# Build và run backend
cd back-end
docker build -t shoe-shop-backend .
docker run -p 8080:8080 --network sgu25_dct122c3_ktpm_shoe-shop-network shoe-shop-backend
```

### Hot reload trong development
Để development với hot reload, chạy services ngoài Docker:

#### Backend
```bash
cd back-end
./mvnw spring-boot:run
```

#### Frontend
```bash
cd front-end
npm install
npm run dev
```

## Monitoring & Logs

### Health checks
- Backend: http://localhost:8080/api/v1/actuator/health
- Frontend: http://localhost:3000 (should return React app)
- Database: Automatically checked by Docker health check

### Performance monitoring
```bash
# Container stats
docker stats

# Specific service stats
docker stats shoe-shop-backend shoe-shop-frontend shoe-shop-mysql
```

## Security Notes

1. Thay đổi mật khẩu mặc định trong `.env`
2. Không commit file `.env` vào Git
3. Sử dụng HTTPS trong production
4. Cập nhật các dependency thường xuyên

## Support

Nếu gặp vấn đề, hãy:
1. Kiểm tra logs: `docker-compose logs -f`
2. Kiểm tra trạng thái: `docker-compose ps`
3. Restart services: `docker-compose restart`
4. Clean và rebuild: `.\manage.ps1 clean` và `.\manage.ps1 build`