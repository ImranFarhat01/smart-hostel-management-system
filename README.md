# 🏨 Smart Hostel Management System

[![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=flat&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-6DB33F?style=flat&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?style=flat&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> An industry-grade hostel operations automation platform built with **Java Spring Boot**, implementing modular architecture for student management, intelligent room allocation, fee tracking, and complaint resolution.

---

## 📋 Table of Contents

- [Problem Statement](#-problem-statement)
- [Solution](#-solution)
- [System Architecture](#-system-architecture)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [API Documentation](#-api-documentation)
- [Database Schema](#-database-schema)
- [Allocation Engine](#-allocation-engine)
- [Testing](#-testing)
- [Sample Outputs](#-sample-outputs)
- [Future Scope](#-future-scope)
- [Contributors](#-contributors)

---

## ❓ Problem Statement

Manual hostel management leads to:
- **Errors in room allocation** — overbooking, gender mismatches, capacity violations
- **Untracked fee payments** — lost receipts, disputed balances, no overdue alerts
- **Unresolved complaints** — no tracking, no accountability, no escalation path
- **Poor data visibility** — no real-time occupancy metrics or financial reports

These inefficiencies affect **1000+ students per hostel** and create administrative bottlenecks that waste hours daily.

---

## 💡 Solution

This system **automates all hostel operations** with a layered architecture:

| Layer | Responsibility |
|-------|---------------|
| **Controllers** | REST API endpoints, request validation, HTTP responses |
| **Services** | Business logic, validation rules, transaction management |
| **Repositories** | Database queries via Spring Data JPA |
| **Models** | Entity definitions with JPA annotations and constraints |
| **Utils** | Smart allocation engine with pluggable strategies |

The system can scale from a **single-hostel deployment** to a **full hostel ERP** with minimal architectural changes.

---

## 🏗 System Architecture

```
┌──────────────────────────────────────────────────────┐
│                    CLIENT LAYER                       │
│          (Postman / Frontend / Mobile App)            │
└────────────────────┬─────────────────────────────────┘
                     │ HTTP REST
┌────────────────────▼─────────────────────────────────┐
│                 CONTROLLER LAYER                      │
│  StudentController │ RoomController │ FeeController   │
│                    │ ComplaintController               │
└────────────────────┬─────────────────────────────────┘
                     │
┌────────────────────▼─────────────────────────────────┐
│                  SERVICE LAYER                        │
│  StudentService │ RoomService │ FeeService            │
│  ComplaintService │ AllocationEngine                  │
└────────────────────┬─────────────────────────────────┘
                     │
┌────────────────────▼─────────────────────────────────┐
│               REPOSITORY LAYER                        │
│          Spring Data JPA Repositories                 │
└────────────────────┬─────────────────────────────────┘
                     │
┌────────────────────▼─────────────────────────────────┐
│                 DATABASE LAYER                        │
│           H2 (Dev) / MySQL (Production)               │
└──────────────────────────────────────────────────────┘
```

---

## ✨ Features

### ✅ Core Modules

| Module | Capabilities |
|--------|-------------|
| **Student Management** | Register, update, deactivate, search by name/email/enrollment, filter by department/year, mess opt-in tracking |
| **Room Management** | Add rooms, check availability by type/floor, capacity validation, maintenance mode |
| **Room Allocation** | Smart assign/unassign/transfer with 4 allocation strategies, bulk allocation |
| **Fee Tracking** | Create individual/bulk fees, process payments (full/partial), overdue detection, fee waiver, financial reports |
| **Complaint System** | File complaints with category/priority, assign to staff, resolve/reject/escalate workflow, statistics dashboard |

### ⭐ Advanced Features

- **Smart Allocation Engine** — 4 pluggable strategies: First-Fit, Best-Fit, Department Grouping, Year-Based
- **Bulk Operations** — Generate fees and allocate rooms for all students at once
- **Overdue Auto-Detection** — Automatic status refresh for past-due payments
- **Complaint Escalation** — Auto-escalates CRITICAL complaints with notification logging
- **Audit Trail** — `createdAt` and `updatedAt` timestamps on all entities via JPA Auditing
- **Financial Reports** — Semester-wise collection rates, outstanding amounts, overdue counts
- **Mess Integration** — Track mess opt-in students separately for food management
- **Health Monitoring** — Spring Actuator endpoints for system health checks

---

## 🛠 Tech Stack

| Component | Technology |
|-----------|-----------|
| **Language** | Java 17+ |
| **Framework** | Spring Boot 3.2.4 |
| **ORM** | Spring Data JPA / Hibernate |
| **Database (Dev)** | H2 In-Memory |
| **Database (Prod)** | MySQL 8.0+ |
| **Build Tool** | Apache Maven 3.9+ |
| **Validation** | Jakarta Bean Validation |
| **Monitoring** | Spring Boot Actuator |
| **Testing** | JUnit 5 / Spring Boot Test |

---

## 📁 Project Structure

```
hostel-management-system/
│
├── src/main/java/com/hostel/
│   ├── controller/              # REST API endpoints
│   │   ├── StudentController.java
│   │   ├── RoomController.java
│   │   ├── FeeController.java
│   │   └── ComplaintController.java
│   │
│   ├── service/                 # Business logic layer
│   │   ├── StudentService.java
│   │   ├── RoomService.java
│   │   ├── FeeService.java
│   │   └── ComplaintService.java
│   │
│   ├── model/                   # JPA entity classes
│   │   ├── Student.java
│   │   ├── Room.java
│   │   ├── Fee.java
│   │   └── Complaint.java
│   │
│   ├── repository/              # Data access layer
│   │   ├── StudentRepository.java
│   │   ├── RoomRepository.java
│   │   ├── FeeRepository.java
│   │   └── ComplaintRepository.java
│   │
│   ├── utils/
│   │   └── AllocationEngine.java    # Smart room allocation strategies
│   │
│   └── main/
│       └── Application.java        # Spring Boot entry point
│
├── src/main/resources/
│   ├── application.properties       # App configuration
│   └── data.sql                     # Seed data (18 rooms, 10 students, fees, complaints)
│
├── src/test/java/com/hostel/
│   └── HostelTest.java              # 25+ unit tests
│
├── docs/                            # Architecture & flowchart diagrams
├── README.md
├── pom.xml                          # Maven dependencies
└── .gitignore
```

---

## 🚀 Getting Started

### Prerequisites

- **Java 17+** — [Download OpenJDK](https://openjdk.org/)
- **Maven 3.9+** — [Download Maven](https://maven.apache.org/)
- **MySQL 8.0+** (optional, for production)

### Quick Start (H2 — No External DB Needed)

```bash
# 1. Clone the repository
git clone https://github.com/your-username/hostel-management-system.git
cd hostel-management-system

# 2. Build the project
mvn clean install

# 3. Run the application
mvn spring-boot:run

# 4. Access the application
# API Base:    http://localhost:8080/api
# H2 Console:  http://localhost:8080/h2-console
# Health:      http://localhost:8080/actuator/health
```

### Production Setup (MySQL)

1. Create the database:
```sql
CREATE DATABASE hostel_db;
```

2. Update `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hostel_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

3. Run with: `mvn spring-boot:run`

---

## 📡 API Documentation

### Student Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/students` | Register a new student |
| `GET` | `/api/students` | Get all students |
| `GET` | `/api/students/{id}` | Get student by ID |
| `GET` | `/api/students/enrollment/{num}` | Get by enrollment number |
| `GET` | `/api/students/search?keyword=` | Search students |
| `GET` | `/api/students/department/{dept}` | Filter by department |
| `GET` | `/api/students/unassigned` | Students without rooms |
| `GET` | `/api/students/mess` | Mess-enrolled students |
| `PUT` | `/api/students/{id}` | Update student info |
| `PATCH` | `/api/students/{id}/deactivate` | Deactivate student |
| `GET` | `/api/students/stats` | Student statistics |

### Room Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/rooms` | Add a new room |
| `GET` | `/api/rooms` | Get all rooms |
| `GET` | `/api/rooms/available` | Available rooms |
| `GET` | `/api/rooms/available/type/{type}` | Available by type |
| `GET` | `/api/rooms/available/floor/{floor}` | Available by floor |
| `POST` | `/api/rooms/assign?studentId=&roomId=` | Assign student to room |
| `POST` | `/api/rooms/unassign?studentId=` | Unassign student |
| `POST` | `/api/rooms/transfer?studentId=&newRoomId=` | Transfer student |
| `PATCH` | `/api/rooms/{id}/maintenance` | Set maintenance mode |
| `GET` | `/api/rooms/stats` | Room statistics |

### Fee Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/fees` | Create a fee record |
| `POST` | `/api/fees/bulk` | Generate fees for all students |
| `POST` | `/api/fees/{id}/pay` | Process payment |
| `PATCH` | `/api/fees/{id}/waive` | Waive a fee |
| `GET` | `/api/fees/student/{id}` | Fees by student |
| `GET` | `/api/fees/student/{id}/pending` | Pending fees |
| `GET` | `/api/fees/overdue` | All overdue fees |
| `GET` | `/api/fees/report/{semester}` | Financial report |

### Complaint Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/complaints` | File a complaint |
| `PATCH` | `/api/complaints/{id}/assign` | Assign to staff |
| `PATCH` | `/api/complaints/{id}/resolve` | Resolve complaint |
| `PATCH` | `/api/complaints/{id}/reject` | Reject complaint |
| `PATCH` | `/api/complaints/{id}/escalate` | Escalate to critical |
| `GET` | `/api/complaints/active` | Active complaints |
| `GET` | `/api/complaints/escalated` | Escalated complaints |
| `GET` | `/api/complaints/stats` | Complaint statistics |

---

## 🗄 Database Schema

### Entity Relationships

```
┌─────────────┐       ┌─────────────┐
│   Student    │──────<│    Fee      │
│              │  1:N  │             │
└──────┬───────┘       └─────────────┘
       │
       │ N:1
       │
┌──────▼───────┐       ┌─────────────┐
│    Room      │       │  Complaint  │
│              │       │             │
└──────────────┘       └─────────────┘
       ▲                      ▲
       │                      │
       └──── Student ─────────┘
              1:N
```

---

## ⚙️ Allocation Engine

The `AllocationEngine` implements 4 pluggable strategies:

| Strategy | Algorithm | Use Case |
|----------|-----------|----------|
| **FIRST_FIT** | Returns first room with vacancy | Quick assignment, batch processing |
| **BEST_FIT** | Returns room with least available beds | Maximizes room utilization |
| **DEPARTMENT_GROUPING** | Groups students by department | Academic community building |
| **YEAR_BASED** | Groups students by study year | Peer grouping (freshers together) |

All strategies fall back to **BEST_FIT** when no matching room is found.

---

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run with verbose output
mvn test -Dtest=HostelTest -Dsurefire.useFile=false
```

**Test Coverage:** 25+ unit tests covering:
- Room capacity management and overflow protection
- Student lifecycle (register → assign → deactivate)
- Fee payment processing (full, partial, overdue, overpayment)
- Complaint workflow (OPEN → IN_PROGRESS → RESOLVED/REJECTED/ESCALATED)
- Allocation engine strategies (First-Fit, Best-Fit)
- Bulk allocation and report generation
- Edge cases and error handling

---

## 📸 Sample Outputs

### Room Statistics Response
```json
{
  "totalRooms": 18,
  "totalCapacity": 48,
  "totalOccupied": 8,
  "availableRooms": 15,
  "fullRooms": 0,
  "occupancyRate": "16.7%"
}
```

### Financial Report Response
```json
{
  "semester": "2025-I",
  "totalFees": 100000.0,
  "totalCollected": 53000.0,
  "totalOutstanding": 47000.0,
  "collectionRate": "53.0%",
  "overdueCount": 1
}
```

### Complaint Statistics Response
```json
{
  "totalComplaints": 6,
  "openComplaints": 1,
  "resolvedComplaints": 2,
  "resolutionRate": "33.3%",
  "categoryBreakdown": {
    "PLUMBING": 1,
    "INTERNET": 1,
    "FOOD": 1,
    "MAINTENANCE": 1,
    "NOISE": 1,
    "ELECTRICAL": 1
  }
}
```

---

## 🔮 Future Scope

- **Mobile Application** — Android/iOS app for students to check room status, pay fees, and file complaints
- **Online Payment Integration** — Razorpay/Stripe gateway for fee payments
- **Biometric Attendance** — Entry/exit tracking with fingerprint scanners
- **AI-Based Allocation** — Machine learning model for roommate compatibility
- **Dashboard Frontend** — React/Angular admin panel with real-time charts
- **Notification System** — Email/SMS alerts for overdue fees and complaint updates
- **Multi-Hostel Support** — Manage multiple hostels from a single dashboard
- **Visitor Management** — Track and authorize hostel visitors

---

## 👥 Contributors

| Name | Role |
|------|------|
| **Your Name** | Full-Stack Developer |

---

## 📄 License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.

---

> *"This system models real hostel operations with modular design and can scale into a full hostel ERP system."*
