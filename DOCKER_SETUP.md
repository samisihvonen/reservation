# Docker & Development Setup Guide

## 📋 Prerequisites

- Docker Desktop (https://www.docker.com/products/docker-desktop)
- Docker Compose (included with Docker Desktop)
- Git
- Make (optional, for using Makefile commands)

## 🚀 Quick Start

### Option 1: Using Docker Compose (Recommended)

```bash
# Build and start all services
make docker-up

# Or manually with docker-compose:
docker-compose up -d
```

Services will be available at:
- **Backend API**: http://localhost:8080
- **Frontend**: http://localhost:5174
- **Swagger Docs**: http://localhost:8080/swagger-ui.html
- **Database**: localhost:5432

### Option 2: Local Development

```bash
# Setup development environment
make setup

# Terminal 1: Start backend
make dev-backend

# Terminal 2: Start frontend
make dev-frontend
```

## 🛠️ Available Make Commands

```bash
# Development
make dev-backend          # Start backend dev server
make dev-frontend         # Start frontend dev server
make install-backend      # Install Maven dependencies
make install-frontend     # Install npm dependencies

# Testing
make test                 # Run all tests
make test-backend         # Run backend unit tests
make test-frontend        # Run frontend tests

# Docker
make docker-up            # Start all Docker containers
make docker-down          # Stop all Docker containers
make docker-logs          # View Docker logs
make docker-clean         # Remove all containers & volumes

# Code Quality
make format               # Format backend code
make lint                 # Lint frontend code

# Utilities
make clean                # Clean build artifacts
make help                 # Show all available commands
```

## 🐳 Docker Services

### PostgreSQL Database
- **Port**: 5432
- **Username**: postgres
- **Password**: postgres
- **Database**: reservation
- **Data**: Persisted in `postgres_data` volume

### Spring Boot Backend
- **Port**: 8080
- **Health Check**: http://localhost:8080/actuator/health
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs

### React Frontend
- **Port**: 5174
- **Development Server**: Vite with HMR

## 📝 Testing

### Run All Tests
```bash
make test
```

### Run Backend Tests Only
```bash
make test-backend
```

This runs:
- **ReservationServiceTest**: Unit tests for business logic
- **ReservationControllerTest**: Integration tests for REST API

### Test Coverage
- ✅ Create reservation
- ✅ Detect overlapping reservations
- ✅ Validate reservation times
- ✅ Room already booked scenarios
- ✅ Authentication & authorization
- ✅ Error handling

## 📊 Logging Configuration

### Log Files Location
```
logs/
├── application.log       # General application logs
└── error.log            # Error logs only
```

### Log Levels
- **Development**: DEBUG (verbose logging)
- **Production**: INFO (less verbose)

### Log Output
- **Console**: Real-time during development
- **Files**: Persisted for debugging (30-day rotation)
- **Async Appender**: Better performance in production

## 🔐 API Documentation

### Swagger/OpenAPI UI
Access at: http://localhost:8080/swagger-ui.html

**Features:**
- Interactive API explorer
- Try-out requests directly from browser
- Authentication with JWT tokens
- Request/response schemas

### Available Endpoints

#### Authentication
```
POST /api/auth/register    # Register new user
POST /api/auth/login       # Login user
```

#### Reservations
```
GET  /api/reservations/{roomId}     # Get room reservations
POST /api/reservations               # Create reservation
DELETE /api/reservations/{id}        # Cancel reservation
```

## 🗄️ Database Management

### Connect to Database
```bash
make db-shell
```

Or manually:
```bash
docker-compose exec postgres psql -U postgres -d reservation
```

### Useful SQL Commands
```sql
-- List all tables
\dt

-- List reservations
SELECT * FROM reservations;

-- List users
SELECT * FROM users;

-- Check table structure
\d reservations
```

## 🔧 Environment Variables

### Backend (.env or docker-compose.yml)
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/reservation
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
JWT_SECRET=your-super-secret-key-change-this-in-production!
JWT_EXPIRATION=86400000
```

### Frontend (.env)
```env
VITE_API_URL=http://localhost:8080/api
```

## 📦 Building for Production

### Build Docker Images
```bash
make docker-build

# Or manually:
docker-compose build
```

### Publish Images
```bash
# Tag images
docker tag reservation-backend:latest yourdockerhub/reservation-backend:1.0.0
docker tag reservation-frontend:latest yourdockerhub/reservation-frontend:1.0.0

# Push to Docker Hub
docker push yourdockerhub/reservation-backend:1.0.0
docker push yourdockerhub/reservation-frontend:1.0.0
```

## 🚨 Troubleshooting

### Port Already in Use
```bash
# Free up ports
docker-compose down -v

# Or kill specific processes:
lsof -i :8080   # Find process on port 8080
kill -9 <PID>   # Kill the process
```

### Database Connection Issues
```bash
# Check database health
docker-compose ps

# Restart database
docker-compose restart postgres

# Check logs
docker-compose logs postgres
```

### Frontend Not Loading
```bash
# Clear Node modules and rebuild
cd frontend
rm -rf node_modules
npm install
npm run build
```

### Clear Everything
```bash
make docker-clean

# Or manually:
docker-compose down -v --remove-orphans
docker system prune -f
```

## 📈 Performance Tips

### Development
- Use `make dev-backend` for fast reload on code changes
- Use `make dev-frontend` for Vite HMR
- Check logs with `make logs-backend` and `make logs-frontend`

### Production
- Set `SPRING_JPA_SHOW_SQL=false`
- Use async logging
- Enable Prometheus metrics
- Use Docker multi-stage builds (already configured)

## 🔍 Health Checks

All services have health checks configured:

```bash
# Check all services
docker-compose ps

# Check backend health
curl http://localhost:8080/actuator/health

# Check frontend
curl http://localhost:5174/
```

## 📚 Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Reference](https://docs.docker.com/compose/compose-file/)
- [Spring Boot Docker Guide](https://spring.io/guides/topicals/spring-boot-docker/)
- [Swagger/OpenAPI](https://swagger.io/)

## 🤝 Support

For issues or questions:
1. Check logs: `make docker-logs`
2. Check health: `curl http://localhost:8080/actuator/health`
3. Read error messages carefully
4. Check database connection: `make db-shell`

---

**Last Updated**: 2026-01-21
**Version**: 1.0.0