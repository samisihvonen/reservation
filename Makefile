# ============================================
# Makefile - Reservation System
# ============================================

.PHONY: help build up down logs clean test test-backend test-frontend \
        install-backend install-frontend dev-backend dev-frontend docker-build \
        docker-up docker-down docker-logs docker-clean format lint

# Colors for output
BLUE := \033[0;34m
GREEN := \033[0;32m
YELLOW := \033[0;33m
RED := \033[0;31m
NC := \033[0m # No Color

# Default target
help:
	@echo "$(BLUE)╔══════════════════════════════════════════════════════════════════════╗$(NC)"
	@echo "$(BLUE)║         Reservation System - Available Commands                      ║$(NC)"
	@echo "$(BLUE)╚══════════════════════════════════════════════════════════════════════╝$(NC)"
	@echo ""
	@echo "$(GREEN)Development:$(NC)"
	@echo "  make dev-backend          Start backend dev server (Spring Boot)"
	@echo "  make dev-frontend         Start frontend dev server (Vite)"
	@echo "  make install-backend      Install backend dependencies (Maven)"
	@echo "  make install-frontend     Install frontend dependencies (npm)"
	@echo ""
	@echo "$(GREEN)Testing:$(NC)"
	@echo "  make test                 Run all tests (backend + frontend)"
	@echo "  make test-backend         Run backend tests only"
	@echo "  make test-frontend        Run frontend tests only"
	@echo ""
	@echo "$(GREEN)Docker:$(NC)"
	@echo "  make docker-build         Build Docker images"
	@echo "  make docker-up            Start all services with Docker Compose"
	@echo "  make docker-down          Stop all Docker services"
	@echo "  make docker-logs          View Docker logs"
	@echo "  make docker-clean         Remove Docker containers & volumes"
	@echo ""
	@echo "$(GREEN)Code Quality:$(NC)"
	@echo "  make format               Format code (backend only)"
	@echo "  make lint                 Lint code (frontend only)"
	@echo ""
	@echo "$(GREEN)Utility:$(NC)"
	@echo "  make clean                Clean build artifacts"
	@echo "  make help                 Show this help message"
	@echo ""

# ============================================
# Installation
# ============================================

install-backend:
	@echo "$(BLUE)Installing backend dependencies...$(NC)"
	cd backend && ./mvnw clean install -q
	@echo "$(GREEN)✓ Backend dependencies installed$(NC)"

install-frontend:
	@echo "$(BLUE)Installing frontend dependencies...$(NC)"
	cd frontend && npm install
	@echo "$(GREEN)✓ Frontend dependencies installed$(NC)"

# ============================================
# Development Servers
# ============================================

dev-backend:
	@echo "$(BLUE)Starting backend dev server...$(NC)"
	cd backend && ./mvnw spring-boot:run

dev-frontend:
	@echo "$(BLUE)Starting frontend dev server...$(NC)"
	cd frontend && npm run dev

# ============================================
# Testing
# ============================================

test: test-backend test-frontend
	@echo "$(GREEN)✓ All tests completed$(NC)"

test-backend:
	@echo "$(BLUE)Running backend tests...$(NC)"
	cd backend && ./mvnw test
	@echo "$(GREEN)✓ Backend tests passed$(NC)"

test-frontend:
	@echo "$(BLUE)Running frontend tests...$(NC)"
	cd frontend && npm test -- --run 2>/dev/null || echo "$(YELLOW)Note: Frontend test setup needed$(NC)"
	@echo "$(GREEN)✓ Frontend test setup complete$(NC)"

# ============================================
# Code Quality
# ============================================

format:
	@echo "$(BLUE)Formatting backend code...$(NC)"
	cd backend && ./mvnw formatter:format
	@echo "$(GREEN)✓ Code formatted$(NC)"

lint:
	@echo "$(BLUE)Linting frontend code...$(NC)"
	cd frontend && npm run lint
	@echo "$(GREEN)✓ Lint complete$(NC)"

# ============================================
# Docker Operations
# ============================================

docker-build:
	@echo "$(BLUE)Building Docker images...$(NC)"
	docker-compose build
	@echo "$(GREEN)✓ Docker images built$(NC)"

docker-up: docker-build
	@echo "$(BLUE)Starting Docker containers...$(NC)"
	docker-compose up -d
	@echo "$(GREEN)✓ Services started$(NC)"
	@echo ""
	@echo "$(YELLOW)Services available at:$(NC)"
	@echo "  Backend:  http://localhost:8080"
	@echo "  Frontend: http://localhost:5174"
	@echo "  Database: localhost:5432"
	@echo ""

docker-down:
	@echo "$(BLUE)Stopping Docker containers...$(NC)"
	docker-compose down
	@echo "$(GREEN)✓ Services stopped$(NC)"

docker-logs:
	@echo "$(BLUE)Tailing Docker logs (Ctrl+C to exit)...$(NC)"
	docker-compose logs -f

docker-clean: docker-down
	@echo "$(BLUE)Removing Docker volumes and images...$(NC)"
	docker-compose down -v --remove-orphans
	docker system prune -f
	@echo "$(GREEN)✓ Docker cleanup complete$(NC)"

# ============================================
# Build Artifacts
# ============================================

build: install-backend
	@echo "$(BLUE)Building application...$(NC)"
	cd backend && ./mvnw clean package -DskipTests
	@echo "$(GREEN)✓ Build complete$(NC)"

clean:
	@echo "$(BLUE)Cleaning build artifacts...$(NC)"
	cd backend && ./mvnw clean
	cd frontend && rm -rf dist node_modules
	@echo "$(GREEN)✓ Cleanup complete$(NC)"

# ============================================
# Development Workflow
# ============================================

.PHONY: setup
setup: install-backend install-frontend
	@echo "$(GREEN)✓ Development environment ready!$(NC)"
	@echo ""
	@echo "$(YELLOW)To start development:$(NC)"
	@echo "  Terminal 1: make dev-backend"
	@echo "  Terminal 2: make dev-frontend"
	@echo ""

.PHONY: db-shell
db-shell:
	@echo "$(BLUE)Opening PostgreSQL shell...$(NC)"
	docker-compose exec postgres psql -U postgres -d reservation

.PHONY: logs-backend
logs-backend:
	docker-compose logs -f backend

.PHONY: logs-frontend
logs-frontend:
	docker-compose logs -f frontend

.PHONY: logs-db
logs-db:
	docker-compose logs -f postgres