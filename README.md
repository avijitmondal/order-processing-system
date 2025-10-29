# Order Processing System

A modern **Spring Boot 3.5** RESTful service for managing e-commerce orders with **JWT authentication**, **automated order workflows**, and **comprehensive API documentation**.

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

---

## 📋 Table of Contents
- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Quick Start](#-quick-start)
- [API Documentation](#-api-documentation)
- [Architecture](#-architecture)
- [Database Schema](#-database-schema)
- [Order Lifecycle](#-order-lifecycle)
- [Security](#-security)
- [Testing](#-testing)
- [Docker Deployment](#-docker-deployment)
- [Configuration](#-configuration)
- [Sample Data](#-sample-data)
- [Contributing](#-contributing)

---

## ✨ Features

### Core Functionality
- 🔐 **JWT Authentication** - Secure token-based auth (login, register, logout)
- 👤 **User Management** - Profile retrieval and user operations
- 🛍️ **Product Catalog** - Paginated listings with category filtering
- 📦 **Order Management** - Create, retrieve, update status, cancel orders
- 🔄 **Automated Workflows** - Scheduled order status progression (PENDING → PROCESSING)
- 📊 **Pagination & Sorting** - All list endpoints support page/size/sort/direction params
- ✅ **Stock Validation** - Atomic stock checks and decrement on order creation
- 🚨 **Error Handling** - Centralized exception handling with detailed messages

### Technical Features
- 🔑 UUID-based identifiers (non-guessable)
- 🏗️ Layered architecture (Controller → Service → Repository)
- 📝 Comprehensive OpenAPI/Swagger documentation
- 🧪 Postman collection with 30+ automated tests
- 🎯 Bean validation (Jakarta Validation)
- 📋 Structured logging (SLF4J + Logback)
- 🐳 Docker support with multi-stage builds

---

## 🛠️ Technology Stack

| Category | Technologies |
|----------|-------------|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.5.7 |
| **Security** | Spring Security, JWT (JJWT 0.12.6), BCrypt |
| **Database** | H2 (in-memory), Spring Data JPA, Hibernate |
| **Build Tool** | Gradle 8.5 |
| **Documentation** | Springdoc OpenAPI 3.0 (Swagger UI) |
| **Testing** | JUnit 5, Postman Collection (30 tests) |
| **Logging** | SLF4J, Logback |
| **Container** | Docker (multi-stage build) |

---

## 🚀 Quick Start

### Prerequisites
- **Java 21** or higher
- **Gradle** (wrapper included)
- **Docker** (optional, for containerized deployment)

### Local Development

```bash
# Clone the repository
git clone https://github.com/avijitmondal/order-processing-system.git
cd order-processing-system

# Run the application
./gradlew bootRun

# Application starts at http://localhost:8080
```

### Verify Installation
```bash
# Health check
curl http://localhost:8080/actuator/health

# Response: {"status":"UP"}
```

### Access Points
- **API Base:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **H2 Console:** http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:orderdb`
  - Username: `sa`
  - Password: *(leave empty)*

---

## 📚 API Documentation

### Swagger UI (Recommended)
Access the **interactive API documentation** at http://localhost:8080/swagger-ui.html

**Features:**
- ✅ Interactive endpoint testing
- ✅ Request/response schemas with examples
- ✅ JWT authentication support
- ✅ Complete parameter descriptions
- ✅ HTTP status code documentation

**Quick Start with Swagger:**
1. Open http://localhost:8080/swagger-ui.html
2. Navigate to **Authentication** → `POST /api/auth/login`
3. Click **"Try it out"**
4. Enter credentials:
   ```json
   {
     "email": "john.doe@example.com",
     "password": "password123"
   }
   ```
5. Click **Execute** and copy the JWT token
6. Click **"Authorize"** button (🔒 icon at top)
7. Enter: `Bearer <your-token>`
8. Now test any protected endpoint!

### API Endpoints Summary

#### 🔐 Authentication (Public)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/auth/login` | Login and receive JWT token |
| `POST` | `/api/auth/register` | Register new user account |
| `POST` | `/api/auth/logout` | Logout (clear session cookie) |
| `GET` | `/api/auth/me` | Get current authenticated user |
| `GET` | `/api/auth/hash/{password}` | Generate BCrypt hash (utility) |

#### 👥 Users (Protected)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/users/{id}` | Get user by ID |

#### 🛍️ Products (Protected, Paginated)
| Method | Endpoint | Query Params | Description |
|--------|----------|--------------|-------------|
| `GET` | `/api/products` | page, size, sort, direction, category | List all products (stock > 0) |
| `GET` | `/api/products/{id}` | - | Get product by ID |
| `GET` | `/api/products/category/{category}` | page, size, sort, direction | List products by category |

**Defaults:** `page=0`, `size=20`, `sort=name`, `direction=asc`

#### 📦 Orders (Protected, Paginated)
| Method | Endpoint | Query Params | Description |
|--------|----------|--------------|-------------|
| `POST` | `/api/orders` | - | Create new order (status: PENDING) |
| `GET` | `/api/orders/{id}` | userId | Get order by ID (ownership check) |
| `GET` | `/api/orders` | page, size, sort, direction, status | List user's orders |
| `PATCH` | `/api/orders/{id}/status` | userId | Update order status |
| `DELETE` | `/api/orders/{id}` | userId | Cancel order (PENDING only) |

**Defaults:** `page=0`, `size=20`, `sort=createdAt`, `direction=desc`

### Request/Response Examples

#### Create Order
```bash
POST /api/orders
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "items": [
    {
      "productName": "MacBook Pro 14\"",
      "quantity": 1,
      "price": 1999.00
    },
    {
      "productName": "AirPods Pro",
      "quantity": 2,
      "price": 249.00
    }
  ]
}
```

**Response (201 Created):**
```json
{
  "id": "750e8400-e29b-41d4-a716-446655440007",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "PENDING",
  "totalAmount": 2497.00,
  "items": [
    {
      "id": "850e8400-e29b-41d4-a716-446655440015",
      "productId": "650e8400-e29b-41d4-a716-446655440001",
      "quantity": 1,
      "price": 1999.00
    },
    {
      "id": "850e8400-e29b-41d4-a716-446655440016",
      "productId": "650e8400-e29b-41d4-a716-446655440004",
      "quantity": 2,
      "price": 249.00
    }
  ],
  "createdAt": "2025-10-29T10:30:00",
  "updatedAt": "2025-10-29T10:30:00"
}
```

#### PageResponse Structure
All paginated endpoints return this structure:
```json
{
  "content": [...],           // Array of items
  "page": 0,                  // Current page (0-indexed)
  "size": 20,                 // Items per page
  "totalElements": 42,        // Total items across all pages
  "totalPages": 3,            // Total number of pages
  "first": true,              // Is first page?
  "last": false,              // Is last page?
  "sort": "createdAt",        // Sort field (or "unsorted")
  "direction": "DESC"         // ASC, DESC, or NONE
}
```

#### Error Response
```json
{
  "status": 404,
  "message": "Order not found: 750e8400-e29b-41d4-a716-446655440999",
  "timestamp": "2025-10-29T10:35:12.456"
}
```

#### Validation Error
```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "items[0].quantity": "must be greater than or equal to 1",
    "items": "must not be empty"
  },
  "timestamp": "2025-10-29T10:36:22.789"
}
```

### Postman Collection

**File:** `Order_Processing_System_API.postman_collection.json`

**30 Test Cases Covering:**
- ✅ Authentication (login, register, duplicate email, invalid credentials, get current user)
- ✅ User management (get user, not found scenarios)
- ✅ Products (list, pagination, category filter, get by ID)
- ✅ Orders (create, get, list, sort, filter by status, update status, cancel)
- ✅ Validation (empty items, negative quantity, zero price, invalid UUID)
- ✅ Authorization (ownership checks, unauthorized access)
- ✅ Edge cases (insufficient stock, invalid status transitions)

**How to Use:**
1. Import `Order_Processing_System_API.postman_collection.json` into Postman
2. Ensure `base_url` = `http://localhost:8080`
3. Run Collection Runner for full regression testing
4. All tests include assertions for status codes and response structure

**Auto-Authentication:**
- Login happens automatically via pre-request scripts
- JWT token stored in collection variable `{{jwt_token}}`
- Unique email generated per test run for registration

---

## 🏗️ Architecture

### Layered Design
```
┌─────────────────────────────────────────────────────────┐
│                    Client Layer                         │
│  (Swagger UI, Postman, Web Browser, Mobile Apps)       │
└─────────────────────────────────────────────────────────┘
                          ↓ HTTP/REST
┌─────────────────────────────────────────────────────────┐
│              Security Filter Chain                      │
│  (JWT Validation, Authentication Context Setup)        │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                 Controller Layer                        │
│  (REST Endpoints, Request Validation, Response Mapping) │
│  - JwtAuthController                                    │
│  - UserController                                       │
│  - ProductController                                    │
│  - OrderController                                      │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                  Service Layer                          │
│  (Business Logic, Transaction Management, Validation)   │
│  - UserService (user operations)                        │
│  - ProductService (product catalog)                     │
│  - OrderService (order lifecycle, stock management)     │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                Repository Layer                         │
│  (Spring Data JPA, Database Access)                     │
│  - UserRepository                                       │
│  - ProductRepository                                    │
│  - OrderRepository                                      │
│  - OrderItemRepository                                  │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                   H2 Database                           │
│  (In-Memory, Auto-Created Schema, Sample Data)          │
└─────────────────────────────────────────────────────────┘

                 ┌──────────────────────┐
                 │  Scheduled Tasks     │
                 │  (OrderScheduler)    │
                 │  Every 5 minutes:    │
                 │  PENDING→PROCESSING  │
                 └──────────────────────┘
```

### Project Structure
```
order-processing-system/
├── src/main/
│   ├── java/com/avijitmondal/ops/
│   │   ├── controller/          # REST API endpoints
│   │   │   ├── JwtAuthController.java
│   │   │   ├── UserController.java
│   │   │   ├── ProductController.java
│   │   │   └── OrderController.java
│   │   ├── service/             # Business logic
│   │   │   ├── UserService.java
│   │   │   ├── ProductService.java
│   │   │   └── OrderService.java
│   │   ├── repository/          # Data access (Spring Data JPA)
│   │   │   ├── UserRepository.java
│   │   │   ├── ProductRepository.java
│   │   │   ├── OrderRepository.java
│   │   │   └── OrderItemRepository.java
│   │   ├── model/               # JPA entities
│   │   │   ├── User.java
│   │   │   ├── Product.java
│   │   │   ├── Order.java
│   │   │   ├── OrderItem.java
│   │   │   └── OrderStatus.java (enum)
│   │   ├── dto/                 # Request/Response objects
│   │   │   ├── LoginRequest.java
│   │   │   ├── LoginResponse.java
│   │   │   ├── RegisterRequest.java
│   │   │   ├── CreateOrderRequest.java
│   │   │   ├── OrderItemRequest.java
│   │   │   ├── PageResponse.java
│   │   │   └── UserResponse.java
│   │   ├── security/            # JWT & Security config
│   │   │   ├── JwtUtil.java
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   ├── CustomUserDetailsService.java
│   │   │   └── SecurityConfig.java
│   │   ├── config/              # Application configuration
│   │   │   └── OpenApiConfig.java
│   │   ├── scheduler/           # Scheduled tasks
│   │   │   └── OrderScheduler.java
│   │   ├── exception/           # Exception handling
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── InvalidOrderStatusException.java
│   │   │   ├── InsufficientStockException.java
│   │   │   └── OrderNotFoundException.java
│   │   └── OrderProcessingSystemApplication.java
│   └── resources/
│       ├── application.yml      # Main configuration
│       ├── data.sql            # Sample data initialization
│       └── static/             # Static HTML pages
│           ├── login.html
│           ├── register.html
│           ├── shop.html
│           ├── orders.html
│           └── order-details.html
├── build.gradle                # Gradle build configuration
├── Dockerfile                  # Docker multi-stage build
├── Order_Processing_System_API.postman_collection.json
└── README.md
```

---

## 💾 Database Schema
### Entity Relationships
```
┌─────────────┐
│    User     │
│─────────────│
│ id (UUID)   │◄──────────┐
│ name        │            │
│ email       │            │ 1:N
│ password    │            │
│ createdAt   │            │
└─────────────┘            │
                           │
                    ┌──────┴──────┐
                    │    Order    │
                    │─────────────│
                    │ id (UUID)   │
                    │ userId (FK) │
                    │ status      │◄──────────┐
                    │ totalAmount │            │
                    │ createdAt   │            │ 1:N
                    │ updatedAt   │            │
                    └─────────────┘            │
                                               │
                                        ┌──────┴──────────┐
                                        │   OrderItem     │
                                        │─────────────────│
                                        │ id (UUID)       │
                                        │ orderId (FK)    │
                                        │ productId (FK)  │──┐
                                        │ quantity        │  │
                                        │ price           │  │ N:1
                                        └─────────────────┘  │
                                                             │
                                                      ┌──────▼──────┐
                                                      │   Product   │
                                                      │─────────────│
                                                      │ id (UUID)   │
                                                      │ name        │
                                                      │ description │
                                                      │ price       │
                                                      │ stock       │
                                                      │ category    │
                                                      │ imageUrl    │
                                                      └─────────────┘
```

### Key Constraints
- **User.email:** Unique, indexed
- **Product.stock:** Must be ≥ 0, checked atomically on order creation
- **Order.status:** Enum (PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED)
- **OrderItem.quantity:** Must be ≥ 1
- **OrderItem.price:** Must be > 0
- **Cascade:** Order deletion cascades to OrderItems

---

## 🔄 Order Lifecycle
### Status Flow
```
        ┌─────────────┐
        │   PENDING   │  ◄── Order created
        └──────┬──────┘
               │
               │ Automated (Scheduler every 5 min)
               ↓
        ┌─────────────┐
        │ PROCESSING  │  ◄── Manual PATCH also allowed
        └──────┬──────┘
               │
               │ Manual PATCH
               ↓
        ┌─────────────┐
        │   SHIPPED   │
        └──────┬──────┘
               │
               │ Manual PATCH
               ↓
        ┌─────────────┐
        │  DELIVERED  │  ◄── Final state
        └─────────────┘

   Alternative Flow (Cancellation):
        ┌─────────────┐
        │   PENDING   │
        └──────┬──────┘
               │
               │ DELETE /api/orders/{id} (only from PENDING)
               ↓
        ┌─────────────┐
        │  CANCELLED  │  ◄── Final state
        └─────────────┘
```

### Business Rules
| Action | Rule | HTTP Method | Allowed From |
|--------|------|-------------|--------------|
| **Create Order** | Stock validation, atomic decrement | `POST /api/orders` | - |
| **Auto-Progress** | PENDING → PROCESSING (every 5 min) | Scheduler | PENDING |
| **Update Status** | Manual status change | `PATCH /api/orders/{id}/status` | Any valid transition |
| **Cancel Order** | Only PENDING orders can be cancelled | `DELETE /api/orders/{id}` | PENDING only |

**Invalid Transitions:** Attempting to transition from SHIPPED back to PROCESSING, or cancelling a PROCESSING order, will return `400 Bad Request` with error message.

---

## 🔒 Security
### Authentication & Authorization

**JWT Token-Based Authentication:**
- **Public Endpoints:** `/api/auth/**` (login, register, logout, me, hash)
- **Protected Endpoints:** All `/api/users/**`, `/api/products/**`, `/api/orders/**`
- **Token Format:** `Authorization: Bearer <JWT>`
- **Token Content:**
  - Subject: user email
  - Issued At (iat): timestamp
  - Expiration (exp): configurable (default 24h)
- **Validation:** `JwtAuthenticationFilter` validates token on each request

**Password Security:**
- **Hashing:** BCrypt with salt (cost factor 10)
- **Storage:** Only hashed passwords stored in database
- **Utility:** `/api/auth/hash/{password}` endpoint for generating test hashes

**Ownership Validation:**
- Order operations require `userId` query parameter
- Service layer validates userId matches authenticated user
- Prevents users from accessing/modifying others' orders

**Session Tracking:**
- `session_id` cookie issued on login/register
- **⚠️ Development Only:** Non-HttpOnly, not secure
- **Production:** Should use `HttpOnly`, `Secure`, `SameSite=Strict` flags

### Security Configuration

```java
// Public endpoints (no authentication required)
/api/auth/**
/h2-console/**
/swagger-ui/**
/v3/api-docs/**

// Protected endpoints (JWT required)
/api/users/**
/api/products/**
/api/orders/**
```

### Security Hardening Checklist

| Area | Current (Dev) | Production Recommendation |
|------|---------------|---------------------------|
| **JWT Secret** | Hardcoded in config | Use environment variables / secrets manager |
| **Token Expiry** | 24 hours | Reduce to 1-4 hours + implement refresh tokens |
| **Password Hashing** | BCrypt (cost 10) | Keep, consider increasing cost factor to 12-14 |
| **Session Cookie** | Non-HttpOnly | Enable `HttpOnly`, `Secure`, `SameSite=Strict` |
| **CORS** | Default (allows all) | Restrict to specific origins |
| **Rate Limiting** | None | Implement at API gateway or filter level |
| **TLS/HTTPS** | Not configured | Enforce HTTPS, redirect HTTP → HTTPS |
| **Error Messages** | Detailed for debugging | Sanitize to avoid information leakage |
| **Input Validation** | Bean Validation | Add max length constraints, sanitization |

---

## 🧪 Testing
### Unit Tests
```bash
# Run all tests
./gradlew test

# Run with coverage
./gradlew test jacocoTestReport

# Run specific test class
./gradlew test --tests "OrderServiceTest"
```

### Integration Testing (Manual)

**1. Using cURL:**
```bash
# Step 1: Login and get token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"john.doe@example.com","password":"password123"}' \
  | jq -r .token)

# Step 2: List products
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/products?page=0&size=10"

# Step 3: Create order
ORDER_ID=$(curl -s -X POST http://localhost:8080/api/orders \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"items":[{"productName":"MacBook Pro 14\"","quantity":1,"price":1999.00}]}' \
  | jq -r .id)

# Step 4: Get order details
USER_ID=$(curl -s -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/auth/me | jq -r .id)

curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/orders/$ORDER_ID?userId=$USER_ID"
```

**2. Using Postman:**
- Import `Order_Processing_System_API.postman_collection.json`
- Click "Run Collection" for automated regression testing
- **30 tests** covering all endpoints and edge cases

**3. Using Swagger UI:**
- Open http://localhost:8080/swagger-ui.html
- Interactive testing with built-in authentication

---

## 🐳 Docker Deployment
The project includes a **multi-stage Dockerfile** that builds the application and creates an optimized runtime image.

### Build Docker Image
```bash
# Build with latest tag
docker build -t order-processing-system:latest .

# Build with version tag
docker build -t order-processing-system:1.0.0 .

# Build without cache (clean build)
docker build --no-cache -t order-processing-system:latest .
```

**Build Process:**
1. **Stage 1 (Builder):** Uses `gradle:8.5-jdk21` to compile and package
2. **Stage 2 (Runtime):** Uses `eclipse-temurin:21-jre` for minimal image size
3. Security: Runs as non-root user `spring`
4. Health check included for container orchestration

### Run Container
```bash
# Basic run
docker run -d \
  --name order-app \
  -p 8080:8080 \
  order-processing-system:latest

# With environment variables
docker run -d \
  --name order-app \
  -p 8080:8080 \
  -e JWT_SECRET=your-secret-here \
  -e JWT_EXPIRATION=3600000 \
  -e JAVA_OPTS="-Xmx1g -Xms512m" \
  order-processing-system:latest

# With resource limits
docker run -d \
  --name order-app \
  -p 8080:8080 \
  --memory="1g" \
  --cpus="1.0" \
  order-processing-system:latest
```

**Access:** http://localhost:8080

### Container Management
```bash
# View logs
docker logs -f order-app

# Check status
docker ps | grep order-app

# Health check
curl http://localhost:8080/actuator/health

# Stop and remove
docker stop order-app
docker rm order-app

# Shell access (debugging)
docker exec -it order-app /bin/sh
```

### Production Considerations
- **Secrets:** Use Docker Secrets or external vaults (never hardcode)
- **Networking:** Deploy behind reverse proxy (Nginx/Traefik) for TLS
- **Monitoring:** Integrate Prometheus for metrics scraping
- **Logging:** Use log aggregation (Fluentd, ELK stack)
- **Orchestration:** Consider Kubernetes for high availability

---

## ⚙️ Configuration

### Application Configuration (`application.yml`)

```yaml
server:
  port: 8080

spring:
  application:
    name: order-processing-system
  
  datasource:
    url: jdbc:h2:mem:orderdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  
  h2:
    console:
      enabled: true
      path: /h2-console
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false

jwt:
  secret: 404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
  expiration: 86400000  # 24 hours

logging:
  level:
    com.avijitmondal.ops: INFO
  file:
    name: logs/application.log
```

### Environment Variables

```bash
# JWT Configuration
export JWT_SECRET="your-secret-hex-string"
export JWT_EXPIRATION=3600000  # 1 hour

# Database (production)
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/orderdb"
export SPRING_DATASOURCE_USERNAME="dbuser"
export SPRING_DATASOURCE_PASSWORD="dbpassword"

# JVM Options
export JAVA_OPTS="-Xmx1g -Xms512m"
```

---

## 📊 Sample Data

**3 Users** (password: `password123`):
- John Doe (john.doe@example.com)
- Jane Smith (jane.smith@example.com)
- Bob Johnson (bob.johnson@example.com)

**22 Products** across 5 categories:
- Electronics (MacBook Pro, iPad, iPhone, AirPods, Apple Watch)
- Accessories (Mouse, Keyboard, Monitor, Webcam, USB-C Hub)
- Gaming (PS5, Headset, Controller, Gaming Chair)
- Smart Home (Speaker, Lights, Doorbell, Thermostat)
- Mobile (Cases, Screen Protectors, Cables, Power Banks)

**6 Sample Orders** with various statuses

---

## 🤝 Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m "feat: add amazing feature"`
4. Run tests: `./gradlew test`
5. Push: `git push origin feature/amazing-feature`
6. Open Pull Request

**Guidelines:**
- All tests must pass
- Update documentation for API changes
- Follow existing code style
- No secrets in commits

---

## 📝 Logging

- **INFO:** Order operations, successful authentication
- **DEBUG:** Detailed flow, scheduler activity
- **WARN:** Failed logins, invalid operations
- **ERROR:** Exceptions, system errors

**Production:** Use structured JSON logging with sensitive data masking

---

## 🚧 Limitations

- H2 in-memory database (data lost on restart)
- No refresh tokens (users must re-login)
- No rate limiting (vulnerable to brute force)
- No admin role (all users equal access)
- Manual SHIPPED/DELIVERED status updates

---

## 🔮 Future Enhancements

**Short Term:**
- Refresh tokens & rate limiting
- Role-based access control (USER, ADMIN)
- Email notifications
- Advanced order filtering

**Long Term:**
- Microservices architecture
- Event-driven design (Kafka)
- GraphQL API
- Mobile apps

---

## 📄 License

**MIT License** - Copyright (c) 2025 Avijit

See LICENSE file for details.

SPDX-License-Identifier: MIT

---

## 📧 Contact

- **Author:** Avijit
- **GitHub:** [@avimonda](https://github.com/avijitmondal)
- **Issues:** [GitHub Issues](https://github.com/avijitmondal/order-processing-system/issues)

---

<div align="center">

**⭐ Star this repo if you find it helpful! ⭐**

Made with ❤️ using Spring Boot

</div>
