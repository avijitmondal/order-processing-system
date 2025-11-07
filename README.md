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
- [Redis Integration](#-redis-integration)
- [Docker Deployment](#-docker-deployment)
- [Configuration](#-configuration)
- [Sample Data](#-sample-data)
- [Contributing](#-contributing)

---

## ✨ Features

### Core Functionality
- 🔐 **JWT Authentication** - Secure token-based auth (login, register, logout)
- � **Redis Token Storage** - Persistent JWT token storage with automatic expiration
- 🚫 **Token Invalidation** - Logout immediately invalidates tokens server-side
- 🔄 **Session Management** - Track active user sessions, force logout capability
- �👤 **User Management** - Profile retrieval and user operations
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
| **Caching/Session** | Redis 7 (Token Storage & Session Management) |
| **Database** | H2 (in-memory), Spring Data JPA, Hibernate |
| **Build Tool** | Gradle 8.5 |
| **Documentation** | Springdoc OpenAPI 3.0 (Swagger UI) |
| **Testing** | JUnit 5, Postman Collection (30 tests) |
| **Logging** | SLF4J, Logback |
| **Container** | Docker Compose (Redis + Application) |

---

## 🚀 Quick Start

### Prerequisites
- **Java 21** or higher
- **Gradle** (wrapper included)
- **Docker & Docker Compose** (required for Redis)

### Option 1: Quick Start with Docker Compose (Recommended)

```bash
# Clone the repository
git clone https://github.com/avijitmondal/order-processing-system.git
cd order-processing-system

# Start Redis and Application
docker-compose up -d

# Application starts at http://localhost:8080
# Redis available at localhost:6379
```

### Option 2: Local Development with External Redis

```bash
# Start Redis using the management script
./redis-manager.sh start

# Run the application
./gradlew bootRun

# Application starts at http://localhost:8080
```

### Option 3: Manual Redis Setup

```bash
# Start Redis container manually
docker run -d \
  --name redis \
  -p 6379:6379 \
  redis:7-alpine

# Run the application
./gradlew bootRun
```

### Redis Management Script

The included `redis-manager.sh` provides convenient Redis operations:

```bash
# Start Redis container
./redis-manager.sh start

# Check Redis status
./redis-manager.sh status

# View Redis logs
./redis-manager.sh logs

# Open Redis CLI
./redis-manager.sh cli

# Monitor Redis commands in real-time
./redis-manager.sh monitor

# Flush all Redis data (with confirmation)
./redis-manager.sh flush

# Stop Redis container
./redis-manager.sh stop
```

### Verify Installation
```bash
# Health check
curl http://localhost:8080/actuator/health

# Response: {"status":"UP"}

# Check Redis connectivity
docker exec -it redis redis-cli ping
# Response: PONG
```

### Access Points
- **API Base:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **H2 Console:** http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:orderdb`
  - Username: `sa`
  - Password: *(leave empty)*
- **Redis:** localhost:6379 (accessible via `redis-cli` or `./redis-manager.sh cli`)

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
| `POST` | `/api/auth/login` | Login and receive JWT token (stored in Redis) |
| `POST` | `/api/auth/register` | Register new user account (generates token) |
| `POST` | `/api/auth/logout` | Logout and invalidate token (removes from Redis) |
| `GET` | `/api/auth/me` | Get current authenticated user |
| `GET` | `/api/auth/hash/{password}` | Generate BCrypt hash (utility) |

**🔴 Redis Integration:** Login/register store JWT tokens in Redis with automatic 24-hour expiration. Logout immediately invalidates tokens by removing them from Redis.

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
│   │   │   ├── OrderService.java
│   │   │   └── TokenService.java            # 🔴 Redis token management
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
│   │   │   ├── JwtAuthenticationFilter.java  # 🔴 Redis validation added
│   │   │   ├── CustomUserDetailsService.java
│   │   │   └── SecurityConfig.java
│   │   ├── config/              # Application configuration
│   │   │   ├── OpenApiConfig.java
│   │   │   └── RedisConfig.java             # 🔴 Redis configuration
│   │   ├── scheduler/           # Scheduled tasks
│   │   │   └── OrderScheduler.java
│   │   ├── exception/           # Exception handling
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── InvalidOrderStatusException.java
│   │   │   ├── InsufficientStockException.java
│   │   │   └── OrderNotFoundException.java
│   │   └── OrderProcessingSystemApplication.java
│   └── resources/
│       ├── application.yml      # Main configuration (🔴 Redis settings added)
│       ├── data.sql            # Sample data initialization
│       └── static/             # Static HTML pages
│           ├── login.html
│           ├── register.html
│           ├── shop.html
│           ├── orders.html
│           └── order-details.html
├── build.gradle                # Gradle build (🔴 Redis dependency added)
├── Dockerfile                  # Docker multi-stage build
├── docker-compose.yml          # 🔴 Redis + Application orchestration
├── redis-manager.sh            # 🔴 Redis management CLI tool
├── REDIS_INTEGRATION.md        # 🔴 Comprehensive Redis guide
├── REDIS_IMPLEMENTATION_SUMMARY.md  # 🔴 Implementation overview
├── REDIS_QUICK_REFERENCE.txt   # 🔴 Quick reference card
├── Order_Processing_System_API.postman_collection.json
└── README.md

🔴 = Redis-related files/changes
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

**JWT Token-Based Authentication with Redis Storage:**
- **Public Endpoints:** `/api/auth/**` (login, register, logout, me, hash)
- **Protected Endpoints:** All `/api/users/**`, `/api/products/**`, `/api/orders/**`
- **Token Format:** `Authorization: Bearer <JWT>`
- **Token Content:**
  - Subject: user email
  - Issued At (iat): timestamp
  - Expiration (exp): configurable (default 24h)
- **Validation:** Two-layer approach:
  1. `TokenService` checks token existence in Redis
  2. `JwtUtil` validates JWT signature and expiration
  
**🔴 Redis Token Storage:**
- **Storage Pattern:** Dual-key design for efficient lookups
  - `jwt:token:{token}` → user email (for token validation)
  - `jwt:user:{email}` → token (for session management)
- **Automatic Expiration:** TTL matches JWT expiration (24 hours default)
- **Token Invalidation:** Logout immediately removes token from Redis
- **Session Management:** Track active tokens, revoke all user tokens
- **Graceful Degradation:** Falls back to JWT-only validation if Redis unavailable

**Authentication Flow:**
```
Login/Register:
  Generate JWT → Store in Redis → Return to client

Request Authentication:
  Extract JWT → Check Redis existence → Validate JWT signature → Authenticate

Logout:
  Extract JWT → Remove from Redis → Token immediately invalid
```

**Password Security:**
- **Hashing:** BCrypt with salt (cost factor 10)
- **Storage:** Only hashed passwords stored in database
- **Utility:** `/api/auth/hash/{password}` endpoint for generating test hashes

**Ownership Validation:**
- Order operations require `userId` query parameter
- Service layer validates userId matches authenticated user
- Prevents users from accessing/modifying others' orders

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
| **Redis Authentication** | None | Enable `requirepass` in production |
| **Redis Connection** | Plaintext | Use TLS/SSL encryption |
| **Redis Persistence** | In-memory only | Enable AOF/RDB for data durability |
| **Redis High Availability** | Single instance | Use Redis Sentinel or Cluster |
| **CORS** | Default (allows all) | Restrict to specific origins |
| **Rate Limiting** | None | Implement at API gateway or filter level |
| **TLS/HTTPS** | Not configured | Enforce HTTPS, redirect HTTP → HTTPS |
| **Error Messages** | Detailed for debugging | Sanitize to avoid information leakage |
| **Input Validation** | Bean Validation | Add max length constraints, sanitization |

**🔴 Redis Security Best Practices:**
- Enable authentication: `requirepass` in redis.conf
- Use TLS for Redis connections in production
- Configure Redis persistence (AOF + RDB snapshots)
- Set up Redis Sentinel for automatic failover
- Use Redis Cluster for horizontal scaling
- Monitor Redis metrics (memory usage, eviction rate, connection count)
- Consider managed Redis services (AWS ElastiCache, Azure Cache, GCP Memorystore)

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

**4. Testing Redis Token Storage:**
```bash
# Login and get token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"john.doe@example.com","password":"password123"}' \
  | jq -r .token)

# Verify token stored in Redis
./redis-manager.sh cli
> GET "jwt:user:john.doe@example.com"
> KEYS jwt:*
> TTL "jwt:user:john.doe@example.com"
> EXIT

# Logout and verify token removed
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer $TOKEN"

# Check Redis again (token should be gone)
./redis-manager.sh cli
> GET "jwt:user:john.doe@example.com"
# (nil)
```

---

## � Redis Integration

### Overview
JWT tokens are stored in Redis for server-side session management, enabling:
- ✅ **Token Invalidation**: Logout immediately invalidates tokens
- ✅ **Session Tracking**: View active user sessions
- ✅ **Forced Logout**: Revoke all tokens for a user (e.g., password change)
- ✅ **Security**: Block compromised tokens instantly
- ✅ **Automatic Expiration**: Tokens auto-expire with JWT TTL (24h)

### Redis Key Structure

```
jwt:token:{token}  → user@email.com    (TTL: 24h)
jwt:user:{email}   → {token}           (TTL: 24h)
```

**Example:**
```bash
# After login
jwt:token:eyJhbGci...  → "john.doe@example.com"
jwt:user:john.doe@example.com → "eyJhbGci..."
```

### TokenService API

The `TokenService` provides 6 core methods:

```java
// Store token after login/register
tokenService.storeToken(email, token);

// Validate token on each request
boolean isValid = tokenService.validateToken(token);

// Get user email from token
String email = tokenService.getUserEmail(token);

// Invalidate single token (logout)
tokenService.invalidateToken(token);

// Invalidate all user tokens (forced logout)
tokenService.invalidateUserTokens(email);

// Check if user has active session
boolean hasSession = tokenService.hasActiveToken(email);
```

### Redis CLI Commands

```bash
# Open Redis CLI
./redis-manager.sh cli

# List all JWT tokens
KEYS jwt:*

# Get token for user
GET "jwt:user:john.doe@example.com"

# Check token TTL (seconds remaining)
TTL "jwt:user:john.doe@example.com"

# Delete specific token
DEL "jwt:token:eyJhbGci..."

# Delete all tokens for user
DEL "jwt:user:john.doe@example.com"

# Monitor Redis commands in real-time
./redis-manager.sh monitor

# Flush all data (development only)
./redis-manager.sh flush
```

### Production Redis Setup

**Managed Services (Recommended):**
- **AWS ElastiCache** - Redis as a service
- **Azure Cache for Redis** - Fully managed
- **Google Cloud Memorystore** - Enterprise-ready

**Self-Hosted High Availability:**
```bash
# Redis Sentinel for automatic failover
redis-sentinel /path/to/sentinel.conf

# Redis Cluster for horizontal scaling
redis-cli --cluster create \
  127.0.0.1:7000 127.0.0.1:7001 \
  127.0.0.1:7002 127.0.0.1:7003 \
  --cluster-replicas 1
```

**Configuration:**
```yaml
spring:
  data:
    redis:
      # Single instance
      host: redis.example.com
      port: 6379
      password: ${REDIS_PASSWORD}
      ssl:
        enabled: true
      
      # Sentinel configuration
      sentinel:
        master: mymaster
        nodes:
          - redis-sentinel-1:26379
          - redis-sentinel-2:26379
          - redis-sentinel-3:26379
      
      # Cluster configuration
      cluster:
        nodes:
          - redis-node-1:6379
          - redis-node-2:6379
          - redis-node-3:6379
```

### Monitoring Redis

```bash
# Check Redis stats
./redis-manager.sh cli
> INFO stats
> INFO memory
> INFO clients

# Monitor commands in real-time
./redis-manager.sh monitor

# Check slow queries
> SLOWLOG GET 10

# Memory usage by key pattern
> MEMORY USAGE "jwt:user:john.doe@example.com"
```

### Troubleshooting

| Issue | Solution |
|-------|----------|
| Connection refused | Ensure Redis is running: `./redis-manager.sh status` |
| Token not found after login | Check Redis logs: `./redis-manager.sh logs` |
| Token persists after logout | Verify TokenService invalidation logic |
| High memory usage | Check token count: `redis-cli DBSIZE` |
| Slow performance | Enable Redis persistence (AOF/RDB) |

**For comprehensive Redis documentation, see:**
- `REDIS_INTEGRATION.md` - Full integration guide
- `REDIS_IMPLEMENTATION_SUMMARY.md` - Implementation overview
- `REDIS_QUICK_REFERENCE.txt` - Command cheat sheet

---

## 🐳 Docker Deployment

### Docker Compose (Recommended)

The project includes `docker-compose.yml` for orchestrating Redis and the application:

```bash
# Start all services (Redis + Application)
docker-compose up -d

# View logs
docker-compose logs -f

# Check status
docker-compose ps

# Stop all services
docker-compose down

# Remove volumes (clears Redis data)
docker-compose down -v
```

**Docker Compose Configuration:**
```yaml
services:
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 5s
      timeout: 3s
      retries: 5

  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATA_REDIS_HOST=redis
      - SPRING_DATA_REDIS_PORT=6379
    depends_on:
      redis:
        condition: service_healthy
```

### Standalone Docker Build

#### Build Docker Image
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
# Basic run (requires external Redis)
docker run -d \
  --name order-app \
  -p 8080:8080 \
  -e SPRING_DATA_REDIS_HOST=host.docker.internal \
  order-processing-system:latest

# With environment variables
# With environment variables
docker run -d \
  --name order-app \
  -p 8080:8080 \
  -e SPRING_DATA_REDIS_HOST=redis \
  -e SPRING_DATA_REDIS_PORT=6379 \
  -e JWT_SECRET=your-secret-here \
  -e JWT_EXPIRATION=3600000 \
  -e JAVA_OPTS="-Xmx1g -Xms512m" \
  --link redis:redis \
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
docker stop order-app redis
docker rm order-app redis

# Shell access (debugging)
docker exec -it order-app /bin/sh
docker exec -it redis redis-cli
```

### Redis Container Management

```bash
# Using redis-manager.sh (recommended)
./redis-manager.sh start
./redis-manager.sh status
./redis-manager.sh logs
./redis-manager.sh cli
./redis-manager.sh stop

# Manual Docker commands
docker run -d --name redis -p 6379:6379 redis:7-alpine
docker exec -it redis redis-cli
docker logs -f redis
docker stop redis
```

### Production Considerations
- **Secrets:** Use Docker Secrets or external vaults (never hardcode)
- **Networking:** Deploy behind reverse proxy (Nginx/Traefik) for TLS
- **Redis Persistence:** Mount volume for data durability (`-v redis-data:/data`)
- **Redis High Availability:** Use Redis Sentinel or managed service
- **Monitoring:** Integrate Prometheus for metrics scraping
- **Logging:** Use log aggregation (Fluentd, ELK stack)
- **Orchestration:** Consider Kubernetes for high availability
- **Resource Limits:** Set memory and CPU limits for both containers
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
  
  # 🔴 Redis Configuration
  data:
    redis:
      host: localhost
      port: 6379
      timeout: 60000
      jedis:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
  
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

# 🔴 Redis Configuration
export SPRING_DATA_REDIS_HOST="localhost"
export SPRING_DATA_REDIS_PORT=6379
export SPRING_DATA_REDIS_PASSWORD=""  # Set in production
export SPRING_DATA_REDIS_TIMEOUT=60000

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
- Redis single instance in development (production needs HA setup)
- No refresh tokens (users must re-login after 24h)
- No rate limiting (vulnerable to brute force)
- No admin role (all users equal access)
- Manual SHIPPED/DELIVERED status updates
- Redis graceful degradation may allow token reuse if Redis is down

---

## 🔮 Future Enhancements

**Short Term:**
- ✅ ~~Redis token storage~~ (Completed)
- ✅ ~~Token invalidation on logout~~ (Completed)
- Refresh tokens with sliding expiration
- Rate limiting with Redis
- Role-based access control (USER, ADMIN)
- Email notifications
- Advanced order filtering
- Redis Sentinel for high availability

**Long Term:**
- Microservices architecture
- Event-driven design (Kafka/RabbitMQ)
- GraphQL API
- Mobile apps
- Redis Cluster for horizontal scaling
- Distributed tracing (OpenTelemetry)
- Real-time order tracking with WebSockets

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
