<div align="center">

<br/>

<img src="https://readme-typing-svg.demolab.com?font=Fira+Code&size=30&pause=1000&color=2E86AB&center=true&vCenter=true&width=600&lines=Patient+Management+System;Spring+Boot+Microservices;Java+%7C+gRPC+%7C+Kafka+%7C+Docker+%7C+SQL" alt="Typing SVG" />

<br/>

### 🏥 A production-grade, cloud-native backend for managing patient records
### Built with Java · Spring Boot · Microservices · gRPC · Kafka · Docker · MySQL

<br/>

[![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com)
[![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)](https://kafka.apache.org)
[![gRPC](https://img.shields.io/badge/gRPC-Inter--Service-4285F4?style=for-the-badge&logo=google&logoColor=white)](https://grpc.io)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com)
[![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)](https://swagger.io)


<br/>

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Architecture](#-architecture)
- [Services](#-services)
- [Tech Stack](#-tech-stack)
- [Getting Started](#-getting-started)
- [API Reference](#-api-reference)
- [gRPC Communication](#-grpc-communication)
- [Kafka Events](#-kafka-events)
- [Authentication & Security](#-authentication--security)
- [Project Structure](#-project-structure)
- [Testing](#-testing)
- [Contributing](#-contributing)


---

## 🏥 Overview

**Patient Management System** is a fully production-ready backend application built using a **microservices architecture** with Java and Spring Boot. It provides a scalable, modular foundation for managing patient records, billing, notifications, authentication, and analytics in a real-world healthcare context.

> 💡 This project is based on the [Java/Spring Microservices Course](https://www.youtube.com/@ChrisBlakely) by Chris Blakely, adapted to use **MySQL** instead of PostgreSQL.

**What this system does:**
- Manages patient records through a secure REST API
- Routes all requests through an API Gateway with JWT authentication
- Uses **gRPC** for fast, typed inter-service communication
- Uses **Apache Kafka** for async, event-driven communication
- Each service runs in its own Docker container with its own MySQL database

---

## 🏗 Architecture

```
                         ┌─────────────────────────────┐
                         │        CLIENT / USER        │
                         │  (Postman / Frontend App)   │
                         └─────────────┬───────────────┘
                                       │  HTTP
                                       ▼
                         ┌─────────────────────────────┐
                         │        API  GATEWAY         │
                         │  JWT Validation · Routing   │
                         │         Port: 4004          │
                         └──────┬──────────────┬───────┘
                                │              │
                     HTTP REST  │              │  HTTP REST
                                │              │
              ┌─────────────────▼───┐    ┌─────▼───────────────────┐
              │   patient-service   │    │     auth-service        │
              │  CRUD · Validation  │    │  JWT · Spring Security  │
              │  Port: 8081         │    │  Port: 8080             │
              └────┬──────────┬─────┘    └─────────────────────────┘
                   │          │
           Kafka   │          │  gRPC
           Event   │          │
                   │     ┌────▼──────────────────┐
                   │     │    billing-service     │
                   │     │  Invoicing · gRPC Srv  │
                   │     │  Port: 8082 / 9005     │
                   │     └────────────────────────┘
                   │
                   ▼
     ┌─────────────────────────────┐
     │  notification-service       │
     │  Kafka Consumer · Alerts    │
     └─────────────────────────────┘
     ┌─────────────────────────────┐
     │  analytics-service          │
     │  Kafka Consumer · Insights  │
     └─────────────────────────────┘

              ┌──────────────────────────────┐
              │       MySQL Database(s)      │
              │   One isolated DB per svc    │
              └──────────────────────────────┘
```

> **Database-per-service pattern** — every microservice owns its own MySQL schema, ensuring complete data isolation and independent scalability.

---

## 📦 Services

| Service | Port | Responsibility |
|---------|------|----------------|
| `api-gateway` | `4004` | JWT validation, request routing |
| `auth-service` | `8080` | Login, JWT generation, Spring Security |
| `patient-service` | `8081` | Patient CRUD, Kafka producer, gRPC client |
| `billing-service` | `8082` / `9005` | Billing logic, gRPC server |
| `notification-service` | — | Kafka consumer, patient alerts |
| `analytics-service` | — | Kafka consumer, data insights |

<br/>

### 🔵 `api-gateway`
The **single entry point** for all external requests. Validates JWT tokens and routes traffic to the correct downstream service. No business logic lives here — it's purely a routing and security layer.

### 🟢 `auth-service`
Handles all **identity and access management**. Users log in, receive a signed JWT, and use it for every subsequent request. Built with Spring Security and `jjwt`.

### 🟡 `patient-service`
The **core service** of the system. Exposes a full REST API for creating, reading, updating, and deleting patient records. When a new patient is created, it:
1. Publishes a `PatientCreated` event to **Kafka**
2. Calls `billing-service` via **gRPC** to initialize a billing account

### 🟠 `billing-service`
Listens for gRPC calls from `patient-service` and manages **billing records and invoicing** for patients.

### 🔴 `notification-service`
Consumes **Kafka events** (e.g., `PatientCreated`) and sends notifications or alerts accordingly.

### 🟣 `analytics-service`
Consumes **Kafka events** to process and analyze patient data, generating insights and reports.

---

## 🛠 Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17+ |
| Framework | Spring Boot 3.x |
| Security | Spring Security + JWT (jjwt 0.12.6) |
| API Docs | OpenAPI 3 / Swagger UI (springdoc 2.6.0) |
| Sync Communication | gRPC 1.69.0 + Protocol Buffers 4.29.1 |
| Async Communication | Apache Kafka + Spring Kafka 3.3.0 |
| Database | **MySQL 8.0** (one per service) |
| ORM | Spring Data JPA / Hibernate |
| Build | Maven |
| Containerization | Docker + Docker Compose |
| Testing | JUnit 5, Spring Boot Test, H2 (in-memory) |
| IDE | IntelliJ IDEA Ultimate (recommended) |

---

## 🚀 Getting Started

### Prerequisites

| Tool | Minimum Version | Download |
|------|----------------|----------|
| Java JDK | 17+ | [adoptium.net](https://adoptium.net) |
| Maven | 3.8+ | [maven.apache.org](https://maven.apache.org) |
| Docker & Docker Compose | 20+ | [docker.com](https://docker.com) |
| Git | Any | [git-scm.com](https://git-scm.com) |

Verify everything is ready:
```bash
java -version    # → Java 17+
mvn -version     # → Maven 3.8+
docker --version # → Docker 20+
```

---

### 1. Clone the Repository

```bash
git clone https://github.com/subhashishp/Patient-Management.git
cd Patient-Management
```

---

### 2. Start Everything with Docker Compose

```bash
docker compose up --build
```

This single command starts all microservices, MySQL databases, Kafka, Zookeeper, and the API Gateway together.

Once running, services are available at:

| Service | URL |
|---------|-----|
| 🌐 API Gateway | `http://localhost:4004` |
| 🔐 Auth Service | `http://localhost:8080` |
| 🏥 Patient Service | `http://localhost:8081` |
| 📖 Swagger UI | `http://localhost:8081/swagger-ui.html` |

---

### 3. Running a Single Service (IntelliJ)

If you want to run a service locally in IntelliJ IDEA:

1. Open the project
2. Navigate to the service's main class (e.g., `PatientServiceApplication.java`)
3. Open **Run/Debug Configurations**
4. Paste the environment variables (see [Environment Variables](#-environment-variables)) into the **Environment Variables** field, separated by semicolons
5. Click **Run**

```bash
# Or run from terminal
cd patient-service
mvn spring-boot:run
```

---

## 📡 API Reference

### Step 1 — Authenticate

```http
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "email": "testuser@test.com",
  "password": "password"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlckB0ZXN0LmNvbSJ9..."
}
```

> 🔑 The default test user is seeded automatically via `data.sql` in `auth-service`.

---

### Step 2 — Use the Patient API

All requests go through the **API Gateway** at `http://localhost:4004` with the JWT token.

#### Get All Patients
```http
GET http://localhost:4004/api/patients
Authorization: Bearer <your_token>
```

#### Get a Patient by ID
```http
GET http://localhost:4004/api/patients/{id}
Authorization: Bearer <your_token>
```

#### Create a Patient
```http
POST http://localhost:4004/api/patients
Authorization: Bearer <your_token>
Content-Type: application/json

{
  "firstName": "Jane",
  "lastName": "Doe",
  "email": "jane.doe@example.com",
  "dateOfBirth": "1990-04-15",
  "address": "123 Main Street, Springfield"
}
```

**Response `201 Created`:**
```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "firstName": "Jane",
  "lastName": "Doe",
  "email": "jane.doe@example.com",
  "dateOfBirth": "1990-04-15",
  "address": "123 Main Street, Springfield",
  "registeredDate": "2026-04-02T10:00:00Z"
}
```

#### Update a Patient
```http
PUT http://localhost:4004/api/patients/{id}
Authorization: Bearer <your_token>
Content-Type: application/json

{
  "firstName": "Jane",
  "lastName": "Smith",
  "address": "456 New Avenue, Springfield"
}
```

#### Delete a Patient
```http
DELETE http://localhost:4004/api/patients/{id}
Authorization: Bearer <your_token>
```

> 📂 Pre-built `.http` request files are in the `api-requests/` folder — open directly in IntelliJ IDEA or VS Code REST Client.

---

## 🔗 gRPC Communication

`patient-service` communicates with `billing-service` via **gRPC** — a high-performance, typed RPC framework using Protocol Buffers.

When a new patient is registered, `patient-service` makes a gRPC call to `billing-service` to initialize a billing account for that patient.

**Protobuf Definition:**
```protobuf
syntax = "proto3";

service BillingService {
  rpc CreateBillingAccount (BillingRequest) returns (BillingResponse);
}

message BillingRequest {
  string patient_id = 1;
  string name       = 2;
  string email      = 3;
}

message BillingResponse {
  string account_id = 1;
  string status     = 2;
}
```

**gRPC connection config (patient-service env vars):**
```
BILLING_SERVICE_ADDRESS=billing-service
BILLING_SERVICE_GRPC_PORT=9005
```

> 📂 Pre-built gRPC request files are available in `grpc-requests/billing-service/`.

---

## 📨 Kafka Events

Services communicate **asynchronously** via Apache Kafka. When patient records are created or changed, events are published to Kafka topics and consumed by other services — keeping them fully decoupled.

| Event | Topic | Producer | Consumers | Description |
|-------|-------|----------|-----------|-------------|
| `PatientCreated` | `patient` | `patient-service` | `notification-service`, `analytics-service` | Fired when a new patient is registered |

**Kafka broker config:**
```
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
```

**Kafka container environment variables:**
```
KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092,EXTERNAL://localhost:9094
KAFKA_CFG_CONTROLLER_LISTENER_NAMES=CONTROLLER
KAFKA_CFG_CONTROLLER_QUORUM_VOTERS=0@kafka:9093
KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,EXTERNAL:PLAINTEXT,PLAINTEXT:PLAINTEXT
KAFKA_CFG_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093,EXTERNAL://:9094
KAFKA_CFG_NODE_ID=0
KAFKA_CFG_PROCESS_ROLES=controller,broker
```

---

## 🔐 Authentication & Security

All API access is secured through the **API Gateway** using **JWT (JSON Web Tokens)**.

**Flow:**
1. Client sends credentials to `auth-service` → receives a signed JWT
2. Client includes `Authorization: Bearer <token>` in every request to the API Gateway
3. Gateway validates the token → forwards to the correct service
4. Invalid or missing tokens return `401 Unauthorized`

**Security is implemented with:**
- `spring-boot-starter-security`
- `jjwt-api` / `jjwt-impl` / `jjwt-jackson` (version `0.12.6`)

**Default seeded test user** (via `data.sql` in auth-service):
```
Email:    testuser@test.com
Password: password
Role:     ADMIN
```

---


## 📁 Project Structure

```
Patient-Management/
│
├── api-gateway/                      # JWT auth + request routing
│   ├── src/main/java/
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── Dockerfile
│   └── pom.xml
│
├── auth-service/                     # Login, JWT, Spring Security
│   ├── src/
│   │   ├── main/java/com/pm/authservice/
│   │   │   ├── config/               # Security configuration
│   │   │   ├── controller/           # /auth/login endpoint
│   │   │   ├── model/                # User entity
│   │   │   ├── repository/           # JPA user repository
│   │   │   └── service/              # JWT generation & validation
│   │   └── resources/
│   │       ├── application.properties
│   │       └── data.sql              # Seeds default admin user
│   ├── Dockerfile
│   └── pom.xml
│
├── patient-service/                  # Core patient CRUD service
│   ├── src/
│   │   ├── main/java/com/pm/patientservice/
│   │   │   ├── controller/           # REST endpoints
│   │   │   ├── service/              # Business logic
│   │   │   ├── repository/           # JPA repositories
│   │   │   ├── model/                # Patient entity
│   │   │   ├── dto/                  # Request & Response DTOs
│   │   │   ├── grpc/                 # gRPC client → billing-service
│   │   │   └── kafka/                # Kafka event producers
│   │   ├── proto/                    # .proto Protobuf definitions
│   │   └── resources/
│   │       └── application.properties
│   ├── Dockerfile
│   └── pom.xml
│
├── billing-service/                  # gRPC server, billing logic
│   ├── src/
│   │   ├── main/java/com/pm/billingservice/
│   │   │   ├── grpc/                 # gRPC server implementation
│   │   │   └── service/              # Billing business logic
│   │   └── proto/                    # .proto definitions
│   ├── Dockerfile
│   └── pom.xml
│
├── notification-service/             # Kafka consumer, sends alerts
│   ├── src/main/java/
│   └── pom.xml
│
├── analytics-service/                # Kafka consumer, data insights
│   ├── src/main/java/
│   └── pom.xml
│
├── api-requests/                     # HTTP request files for testing
│   └── *.http
│
├── grpc-requests/                    # gRPC request files for testing
│   └── billing-service/
│
├── integration-tests/                # End-to-end integration tests
│
├── infrastructure/                   # Infrastructure config & scripts
│
├── docker-compose.yml                # Full stack orchestration
├── .gitignore
├── LICENSE
└── README.md
```

---

## 🧪 Testing

Each service uses **JUnit 5** and an **H2 in-memory database** for fast, isolated unit and integration tests — no running MySQL or Docker required for the test suite.

**Run all tests:**
```bash
mvn test
```

**Run tests for a specific service:**
```bash
cd patient-service
mvn test
```

**Run a specific test class:**
```bash
mvn test -Dtest=PatientServiceTest
```

**End-to-end integration tests** live in the `integration-tests/` module and can be run against a live Docker Compose environment.

---

## 🤝 Contributing

Contributions are welcome and appreciated!

1. **Fork** this repository
2. **Create** a feature branch
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **Commit** with a descriptive message
   ```bash
   git commit -m "feat: add patient search by email"
   ```
4. **Push** to your fork
   ```bash
   git push origin feature/your-feature-name
   ```
5. **Open** a Pull Request and describe your changes

**Before submitting, please make sure:**
- All tests pass: `mvn test`
- New functionality includes test coverage
- Environment variable changes are documented

---


<br/>

**Built with ❤️ by [subhashishp](https://github.com/subhashishp)**


<br/>

⭐ **If this project was helpful, please consider giving it a star!** ⭐

</div>
