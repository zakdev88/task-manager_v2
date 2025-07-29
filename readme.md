# Task Manager Full-Stack Application

A task management application with Spring Boot backend, React frontend, and PostgreSQL database.

## Tech Stack

- **Backend**: Java 21, Spring Boot 3.5.4, Gradle
- **Frontend**: React 19, TypeScript, Vite, Material-UI
- **Database**: PostgreSQL 15
- **Container**: Docker & Docker Compose

## Why Postgres
- Strong consistency - Critical for task management where data accuracy matters
- Mature ecosystem - Better Spring Boot integration with JPA/Hibernate
- SQL familiarity - Personal
- ACID transactions - Important for operations like bulk task updates
- As the task app grows we might have relational needs - subtask, users, categories

## Prerequisites

- Docker and Docker Compose
- Java 21 (for local development)
- Node.js 18+ and npm (for local development)

## Quick Start

```bash
# Start all services
docker-compose up --build

# Access applications
# Frontend: http://localhost:3000
# Backend API: http://localhost:8080
# Database: localhost:5433

# Stop services
docker-compose down
```

## Building Individual Apps

### Backend (Spring Boot)
```bash
cd task-manager-api

# Build
./gradlew clean build

# Run locally
./gradlew bootRun
```

### Frontend (React)
```bash
cd task-manager-ui

# Install dependencies
npm install

# Development server
npm run dev

# Build for production
npm run build
```

## Testing Backend

```bash
cd task-manager-api

# Run all tests
./gradlew test

# Run specific test classes
./gradlew test --tests "com.taskmanager.TaskControllerTest"
./gradlew test --tests "com.taskmanager.TaskServiceTest"
```

## Development

### Full Stack Development
```bash
# Start database only
docker-compose up db -d

# Run backend locally
cd task-manager-api && ./gradlew bootRun

# Run frontend locally (new terminal)
cd task-manager-ui && npm run dev
```

### Environment Configuration

**Docker** (automatic via docker-compose.yml):
- Database: `jdbc:postgresql://db:5432/task_db`
- Username: `postgres`, Password: `admin`

**Local Development** (add to application.properties):
```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/task_db
spring.datasource.username=postgres
spring.datasource.password=admin
```

## API Testing

```bash
# Health check
curl http://localhost:8080/actuator/health

# Example endpoints
curl http://localhost:8080/api/tasks CRUD
```

## Troubleshooting

**Port conflicts**: Check ports 3000, 8080, 5433
```bash
lsof -i :3000
```

**Container issues**:
```bash
docker-compose logs api
docker system prune -a
```

**Database connection**:
```bash
docker-compose exec db pg_isready -U postgres -d task_db
```