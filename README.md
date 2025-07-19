# GearTrack API

A Spring Boot REST API for managing equipment, tools, and employee assignments with inspection tracking capabilities.

## Features

- **Employee Management** - CRUD operations for employee records
- **Tool Management** - Track and assign tools to employees
- **Machine Management** - Manage equipment with inspection scheduling
- **Machine Inspections** - Track inspection records and schedules
- **Authentication** - JWT-based auth with Google OAuth2 integration
- **Authorization** - Role-based access control

## Technology Stack

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.5.3** - Application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence layer
- **Spring OAuth2 Client** - Google OAuth integration

### Database
- **PostgreSQL** - Primary database
- **Flyway** - Database migrations

### Security & Auth
- **JWT** - Token-based authentication
- **Google OAuth2** - Social login integration
- **BCrypt** - Password hashing

### Build & Tools
- **Gradle** - Build automation
- **Lombok** - Code generation
- **MapStruct** - Bean mapping
- **JUnit 5** - Testing framework

### Additional Libraries
- **JJWT** - JWT implementation
- **Google Guava** - Utility libraries

## API Endpoints

- `/api/auth/*` - Authentication endpoints
- `/api/employees/*` - Employee management
- `/api/tools/*` - Tool management  
- `/api/machines/*` - Machine management
- `/api/machine-inspections/*` - Inspection tracking

## Database

Uses PostgreSQL with Flyway migrations for schema management. Database runs on port 5435.

## Configuration

Application runs on port 8080. Key configuration in `application.yml`:
- Database connection settings
- JWT secret configuration
- Google OAuth client settings
- Logging configuration