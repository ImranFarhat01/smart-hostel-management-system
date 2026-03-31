# 🏨 Smart Hostel Management System

[![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=flat&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-6DB33F?style=flat&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?style=flat&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A complete hostel operations management platform built with **Java 17** and **Spring Boot 3.2**. This system automates student registration, room allocation, fee tracking, and complaint resolution through a clean REST API architecture with **40+ endpoints**.

---

## The Problem

Managing a hostel manually is a nightmare. Wardens juggle paper registers, students chase receipts, complaints vanish into suggestion boxes, and nobody knows which rooms are actually available.

Specifically, manual hostel management leads to:

- **Room allocation errors** - overbooking, capacity violations, and duplicate assignments that take hours to cross-check in paper registers
- **Untracked fee payments** - lost receipts, disputed balances, no overdue alerts, and zero visibility into collection rates
- **Unresolved complaints** - no tracking, no accountability, no escalation path. Critical issues like electrical faults go unattended for days
- **Poor data visibility** - no real-time occupancy metrics, no financial reports, no way to make informed decisions

These inefficiencies affect **1000+ students per hostel** and create administrative bottlenecks that waste hours daily. This system fixes all of that.

---

## The Solution

This system **automates all hostel operations** with a layered MVC architecture:

| Layer | Components | Responsibility |
|-------|-----------|---------------|
| **Controllers** | 4 REST Controllers | HTTP request/response handling, input validation |
| **Services** | 4 Service Classes | Business logic, transaction management, validation rules |
| **Repositories** | 4 JPA Repositories | Database queries, CRUD operations, custom SQL |
| **Models** | 4 Entity Classes | Data structure, relationships, domain-level constraints |
| **Utils** | AllocationEngine | Smart room allocation with pluggable strategies |

The system can scale from a **single-hostel deployment** to a **full hostel ERP** with minimal architectural changes.

---

## What This System Does

**Student Management** - Register students, track their details, assign them to rooms, and manage their hostel lifecycle from admission to graduation. Search by name, email, enrollment number, or filter by department and year.

**Room Allocation** - 18 pre-configured rooms across 3 floors with 4 room types (Dormitory, Standard, Deluxe, Suite). The system prevents overbooking, tracks occupancy in real-time, supports room transfers, and includes a smart allocation engine with 4 strategies.

**Fee Tracking** - Create fee records individually or in bulk, process full or partial payments, detect overdue balances automatically, waive fees with reason tracking, and generate semester-wise financial reports showing collection rates and outstanding amounts.

**Complaint System** - Students file complaints with 11 categories and 4 priority levels. Staff get assigned, complaints move through a strict workflow (Open → In Progress → Resolved/Rejected), critical issues get escalated automatically, and the system tracks resolution rates and category breakdowns.

---

## System Architecture

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

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.2.4 |
| ORM | Spring Data JPA + Hibernate 6.4 |
| Database (Dev) | H2 In-Memory |
| Database (Prod) | MySQL 8.0+ |
| Build Tool | Apache Maven |
| Validation | Jakarta Bean Validation |
| Monitoring | Spring Boot Actuator |
| Testing | JUnit 5 |

---

## Project Structure

```
hostel-management-system/
├── src/
│   ├── main/
│   │   ├── java/com/hostel/
│   │   │   ├── main/
│   │   │   │   └── Application.java              # Spring Boot entry point
│   │   │   ├── model/
│   │   │   │   ├── Student.java                   # Student entity with validation
│   │   │   │   ├── Room.java                      # Room entity with capacity logic
│   │   │   │   ├── Fee.java                       # Fee entity with payment tracking
│   │   │   │   └── Complaint.java                 # Complaint entity with workflow
│   │   │   ├── repository/
│   │   │   │   ├── StudentRepository.java         # Student database queries
│   │   │   │   ├── RoomRepository.java            # Room availability queries
│   │   │   │   ├── FeeRepository.java             # Fee and financial queries
│   │   │   │   └── ComplaintRepository.java       # Complaint tracking queries
│   │   │   ├── service/
│   │   │   │   ├── StudentService.java            # Student business logic
│   │   │   │   ├── RoomService.java               # Room allocation logic
│   │   │   │   ├── FeeService.java                # Payment processing logic
│   │   │   │   └── ComplaintService.java          # Complaint workflow logic
│   │   │   ├── controller/
│   │   │   │   ├── StudentController.java         # Student API endpoints
│   │   │   │   ├── RoomController.java            # Room API endpoints
│   │   │   │   ├── FeeController.java             # Fee API endpoints
│   │   │   │   └── ComplaintController.java       # Complaint API endpoints
│   │   │   └── utils/
│   │   │       └── AllocationEngine.java          # Smart room allocation strategies
│   │   └── resources/
│   │       ├── application.properties             # App configuration
│   │       └── data.sql                           # Seed data (18 rooms, 10 students)
│   └── test/
│       └── java/com/hostel/
│           └── HostelTest.java                    # 25+ unit tests
├── docs/
│   ├── architecture.pdf                           # System architecture diagram
│   └── flowchart.pdf                              # Complaint workflow diagram
├── report.pdf                                     # 15-page project report
├── pom.xml                                        # Maven dependencies
├── .gitignore
└── README.md
```

---

## How to Run

### What You Need
- **Java 17** or higher - [Download Amazon Corretto](https://aws.amazon.com/corretto/)
- **Maven** - or just use IntelliJ IDEA (it has Maven built-in)

### Option 1: Using IntelliJ IDEA (Recommended)

1. Clone this repository
   ```
   git clone https://github.com/ImranFarhat01/smart-hostel-management-system-24BAI10276.git
   ```
2. Open IntelliJ → **File → Open** → select the `hostel-management-system` folder
3. IntelliJ will detect `pom.xml` - click **Trust Project** when prompted
4. Wait for Maven to download all dependencies (watch the progress bar at the bottom)
5. Go to **File → Project Structure → Project** and make sure SDK is set to **Java 17**
6. Open `src/main/java/com/hostel/main/Application.java`
7. Click the green ▶️ button next to `main()` → **Run 'Application.main()'**

### Option 2: Using Command Line

```bash
git clone https://github.com/ImranFarhat01/smart-hostel-management-system-24BAI10276.git
cd hostel-management-system
mvn clean install
mvn spring-boot:run
```

### You'll Know It's Working When You See

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

🏨 Smart Hostel Management System v1.0.0
Server running on http://localhost:8080
```

### Verify Everything Works

Open these URLs in your browser:

| URL | What You'll See |
|-----|----------------|
| http://localhost:8080/api/students | 10 sample students as JSON |
| http://localhost:8080/api/rooms | 18 rooms with occupancy data |
| http://localhost:8080/api/rooms/stats | Occupancy statistics |
| http://localhost:8080/api/fees | 8 fee records |
| http://localhost:8080/api/complaints | 6 complaints |
| http://localhost:8080/api/complaints/stats | Complaint analytics |
| http://localhost:8080/api/students/unassigned | 2 students without rooms |
| http://localhost:8080/api/fees/overdue | Overdue payments |
| http://localhost:8080/actuator/health | System health: `{"status":"UP"}` |
| http://localhost:8080/h2-console | Database browser (JDBC URL: `jdbc:h2:mem:hosteldb`, user: `sa`, no password) |

---

## API Endpoints

### Student Endpoints

| Method | URL | What It Does |
|--------|-----|-------------|
| `POST` | `/api/students` | Register a new student |
| `GET` | `/api/students` | List all students |
| `GET` | `/api/students/{id}` | Get one student by ID |
| `GET` | `/api/students/enrollment/{num}` | Find by enrollment number |
| `GET` | `/api/students/search?keyword=` | Search by name, email, or enrollment |
| `GET` | `/api/students/department/{dept}` | Filter by department |
| `GET` | `/api/students/year/{year}` | Filter by year of study |
| `GET` | `/api/students/unassigned` | Students without a room |
| `GET` | `/api/students/active` | All active students |
| `GET` | `/api/students/mess` | Students enrolled in mess |
| `GET` | `/api/students/stats` | Student statistics |
| `PUT` | `/api/students/{id}` | Update student info |
| `PATCH` | `/api/students/{id}/deactivate` | Deactivate a student |

### Room Endpoints

| Method | URL | What It Does |
|--------|-----|-------------|
| `POST` | `/api/rooms` | Add a new room |
| `GET` | `/api/rooms` | List all rooms |
| `GET` | `/api/rooms/{id}` | Get one room |
| `GET` | `/api/rooms/number/{num}` | Find by room number |
| `GET` | `/api/rooms/available` | Rooms with empty beds |
| `GET` | `/api/rooms/available/type/{type}` | Available by type (STANDARD, DELUXE, etc.) |
| `GET` | `/api/rooms/available/floor/{floor}` | Available on a specific floor |
| `GET` | `/api/rooms/floor/{floor}` | All rooms on a floor |
| `GET` | `/api/rooms/stats` | Occupancy statistics |
| `POST` | `/api/rooms/assign?studentId=&roomId=` | Assign student to room |
| `POST` | `/api/rooms/unassign?studentId=` | Remove student from room |
| `POST` | `/api/rooms/transfer?studentId=&newRoomId=` | Move student between rooms |
| `PUT` | `/api/rooms/{id}` | Update room details |
| `PATCH` | `/api/rooms/{id}/maintenance` | Set room to maintenance mode |

### Fee Endpoints

| Method | URL | What It Does |
|--------|-----|-------------|
| `POST` | `/api/fees` | Create a fee record |
| `POST` | `/api/fees/bulk` | Generate fees for all active students |
| `POST` | `/api/fees/{id}/pay` | Make a payment (full or partial) |
| `PATCH` | `/api/fees/{id}/waive` | Waive a fee with reason |
| `GET` | `/api/fees` | List all fees |
| `GET` | `/api/fees/{id}` | Get one fee record |
| `GET` | `/api/fees/student/{id}` | All fees for a student |
| `GET` | `/api/fees/student/{id}/pending` | Unpaid fees for a student |
| `GET` | `/api/fees/overdue` | All overdue fees |
| `GET` | `/api/fees/status/{status}` | Filter by status (PAID, PENDING, PARTIAL, OVERDUE) |
| `GET` | `/api/fees/semester/{sem}` | Fees for a semester |
| `GET` | `/api/fees/report/{semester}` | Financial summary report |
| `POST` | `/api/fees/refresh-overdue` | Refresh overdue statuses |

### Complaint Endpoints

| Method | URL | What It Does |
|--------|-----|-------------|
| `POST` | `/api/complaints` | File a new complaint |
| `PATCH` | `/api/complaints/{id}/assign` | Assign to staff member |
| `PATCH` | `/api/complaints/{id}/resolve` | Mark as resolved with note |
| `PATCH` | `/api/complaints/{id}/reject` | Reject with reason |
| `PATCH` | `/api/complaints/{id}/escalate` | Escalate to critical priority |
| `GET` | `/api/complaints` | List all complaints |
| `GET` | `/api/complaints/{id}` | Get one complaint |
| `GET` | `/api/complaints/student/{id}` | Complaints filed by a student |
| `GET` | `/api/complaints/active` | Open and in-progress complaints |
| `GET` | `/api/complaints/status/{status}` | Filter by status |
| `GET` | `/api/complaints/category/{cat}` | Filter by category |
| `GET` | `/api/complaints/escalated` | All escalated complaints |
| `GET` | `/api/complaints/stats` | Complaint statistics and analytics |

### Health & Monitoring

| Method | URL | What It Does |
|--------|-----|-------------|
| `GET` | `/actuator/health` | System health status |
| `GET` | `/actuator/info` | Application info |
| `GET` | `/actuator/metrics` | Performance metrics |

---

## Core Business Logic

### Room Allocation
```
if room has vacancy (occupied < capacity) AND status is AVAILABLE:
    → check student is ACTIVE and not already assigned
    → assign student to room
    → increment occupancy count
    → if room reaches full capacity → auto-set status to FULL
else:
    → reject with "Room Full" error
```

### Fee Payment Processing
```
receive payment amount
    → validate: must be positive, cannot exceed balance
    → add to paid amount
    → recalculate status automatically:
        paid >= total     → PAID (set payment date)
        paid > 0          → PARTIAL
        past due date     → OVERDUE
        otherwise         → PENDING
```

### Complaint Workflow
```
OPEN ──(assign to staff)──→ IN_PROGRESS ──(resolve)──→ RESOLVED
                                         ──(reject)───→ REJECTED

OPEN or IN_PROGRESS ──(escalate)──→ ESCALATED (priority set to CRITICAL)

Guards:
  ✗ Cannot resolve or reject an already closed complaint
  ✗ Cannot move a non-OPEN complaint to IN_PROGRESS
  ✗ Cannot escalate a closed complaint
```

---

## Smart Allocation Engine

The `AllocationEngine` utility implements the **Strategy design pattern** with 4 pluggable algorithms:

| Strategy | How It Works | Best For |
|----------|-------------|----------|
| **FIRST_FIT** | Picks the first room with a vacancy | Quick batch processing |
| **BEST_FIT** | Picks the room with fewest empty beds | Maximizing room utilization |
| **DEPARTMENT_GROUPING** | Places student with same-department peers | Building academic communities |
| **YEAR_BASED** | Groups students by year of study | Keeping freshers together |

All strategies automatically fall back to **BEST_FIT** if no matching room is found. The engine also supports **bulk allocation** - assign rooms to all unassigned students in a single operation.

---

## Database Schema

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
                              ▲
                              │ 1:N
                       Student┘
```

- A **Room** has many **Students** (one-to-many)
- A **Student** has many **Fees** (one-to-many)
- A **Student** has many **Complaints** (one-to-many)

---

## Sample API Responses

**Room Statistics** - `GET /api/rooms/stats`
```json
{
  "totalRooms": 18,
  "totalCapacity": 48,
  "totalOccupied": 8,
  "availableRooms": 14,
  "fullRooms": 0,
  "occupancyRate": "16.7%"
}
```

**Complaint Statistics** - `GET /api/complaints/stats`
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

**Health Check** - `GET /actuator/health`
```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP", "details": { "database": "H2" } },
    "diskSpace": { "status": "UP" },
    "ping": { "status": "UP" }
  }
}
```

---

## Pre-loaded Demo Data

The system ships with realistic sample data so you can test every feature immediately:

- **18 rooms** across 3 floors (Ground, First, Second) with 4 types - Dormitory, Standard, Deluxe, Suite
- **10 students** from 5 departments (CS, ECE, Mechanical, Civil, IT) across years 1-4
- **8 fee records** demonstrating all payment states - Paid, Partial, Pending, Overdue
- **6 complaints** covering all workflow states - Open, In Progress, Resolved, Rejected, Escalated

---

## Testing

The project includes **25+ unit tests** organized into 5 test suites:

| Test Suite | Tests | What It Covers |
|-----------|-------|---------------|
| Room Tests | 7 | Capacity management, overflow protection, vacancy checks, maintenance blocking |
| Student Tests | 3 | Creation, room assignment, toString output |
| Fee Tests | 7 | Full/partial payments, overpayment rejection, overdue detection, status transitions |
| Complaint Tests | 7 | Workflow transitions (OPEN→RESOLVED), escalation, double-resolve guard |
| Allocation Engine | 5 | FIRST_FIT, BEST_FIT, empty room handling, bulk allocation, report generation |
| Integration Tests | 5 | Full student lifecycle, enum completeness, concurrent capacity guard |

Run tests:
```bash
mvn test
```

---

## Switching to MySQL (Production)

1. Create a database:
   ```sql
   CREATE DATABASE hostel_db;
   ```

2. Update `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/hostel_db?useSSL=false&serverTimezone=UTC
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   spring.datasource.username=root
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```

3. Run the application - tables will be created automatically.

---

## Future Scope

- **Mobile App** - Android/iOS for students to check rooms, pay fees, and file complaints on the go
- **Payment Gateway** - Razorpay/Stripe integration for secure online fee payments with automated receipts
- **Admin Dashboard** - React frontend with real-time charts showing occupancy trends and collection rates
- **Email/SMS Alerts** - Automated notifications for fee due dates, complaint updates, and emergency broadcasts
- **Biometric Attendance** - Entry/exit tracking with fingerprint scanners and parent notification
- **AI-Based Allocation** - Machine learning model trained on student preferences for optimal roommate matching
- **Multi-Hostel Support** - Manage multiple hostel buildings from a single centralized dashboard
- **Visitor Management** - Digital visitor registration with OTP verification and time-bound access passes

---

## Author

**Imran Farhat**

---

## License

This project is open source and available under the [MIT License](LICENSE).
