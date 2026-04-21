# Smart Supply Chain Disruption & Vendor Risk Management System

**OOAD Mini Project** | UE23CS352B | PES University

## Architecture

```
Browser (HTML/JS/Chart.js) ←→ Java 17 + Javalin REST API ←→ MySQL 8.x
```

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Backend | Java 17 (JDK 21) |
| REST API | Javalin 5.6.3 |
| Database | MySQL 8.x |
| JSON | Gson 2.10.1 |
| Password Hashing | BCrypt (at.favre.lib) |
| Frontend | HTML + Vanilla JS + Chart.js |
| Styling | CSS Variables (dark-mode) |

## OOAD Concepts Demonstrated

### Design Patterns (6)
1. **Singleton** — `DatabaseManager.java` — thread-safe, single DB connection
2. **Factory Method** — `AlertFactory.java` — creates HIGH_RISK alerts
3. **Builder** — `ReportBuilder.java` — fluent API for constructing reports
4. **Strategy** — `IRiskStrategy` + `WeightedRiskStrategy` — pluggable risk algorithm
5. **Observer** — `AlertService` (Subject) + `AlertObserver` + `ConsoleLogObserver`
6. **MVC** — Controller → Service → Repository architecture

### SOLID Principles (all 5)
- **SRP** — Each service has one responsibility
- **OCP** — `IRiskStrategy` allows new algorithms without modification
- **LSP** — All `User` subclasses substitute for `User` transparently
- **ISP** — Separate interfaces: `IVendorRepository`, `IRiskRepository`, `AlertObserver`
- **DIP** — Services depend on repository interfaces, not MySQL implementations

### GRASP Patterns (all 9)
- Creator, Information Expert, Low Coupling, High Cohesion, Controller, Polymorphism, Pure Fabrication (RecommendationService), Indirection, Protected Variations

---

## Prerequisites

- **Java**: JDK 17+ (JDK 21 recommended)
- **Maven**: 3.8+ (Maven Wrapper `mvnw.cmd` included)
- **MySQL**: 8.x running locally on port 3306

## Quick Start

### Step 1: Configure MySQL Credentials
Open `src/main/java/com/scm/db/DatabaseManager.java` and update:
```java
private static final String DB_USER     = "root";
private static final String DB_PASSWORD = "root";   // ← your MySQL password
```

### Step 2: Run the Backend
```bat
run_backend.bat
```
This will:
1. Connect to MySQL and create `scm_db` database automatically
2. Initialize all tables (SchemaInitializer)
3. Seed demo data (MockDataSeeder)
4. Start Javalin on **http://localhost:8080**
5. Serve the frontend from **http://localhost:8080**

### Step 3: Open the App
Navigate to: **http://localhost:8080/login.html**

---

## Demo Credentials

| Email | Password | Role | Access |
|-------|----------|------|--------|
| admin@scm.com | admin123 | Administrator | Full system access |
| pm@scm.com | pm123 | Procurement Manager | Vendors, alerts, reports |
| analyst@scm.com | analyst123 | Risk Analyst | Risk evaluation, alerts |
| wm@scm.com | wm123 | Warehouse Manager | Inventory management |
| vendor@scm.com | vendor123 | Vendor | View-only |

---

## Demo Flow (5 minutes)

1. **Login** as `analyst@scm.com` → Dashboard with KPI cards
2. **Dashboard** → Risk distribution chart, live alert feed
3. **Vendors** → See 12 vendors including 3 HIGH_RISK
4. **Risk Analysis** → Select FastSupply Co (V2), adjust sliders, click Recalculate
5. **Alerts** → See HIGH RISK alert for V2 → Click Recommendations → 3 alternatives listed
6. **Switch to** `pm@scm.com` → Go to Vendors → Suspend V2
7. **Switch to** `wm@scm.com` → Inventory → Update low-stock items
8. **Switch to** `admin@scm.com` → Reports → Strategic tab → CSV Export

---

## Risk Score Formula

```
risk = (1 - delivery_timeliness) × 0.4
     + defect_rate               × 0.4
     + (1 - compliance_score)    × 0.2
```

- Score range: **0.0** (perfect) to **1.0** (critical)
- Default threshold: **0.700**
- If score > threshold → HIGH_RISK alert + vendor recommendations

---

## Project Structure

```
SCM_Project/
├── pom.xml                          # Maven + MySQL, Javalin, Gson, BCrypt
├── run_backend.bat                  # Windows start script
├── db/
│   ├── schema.sql                   # MySQL DDL for all tables
│   └── seed_data.sql                # Full demo dataset
├── src/main/java/com/scm/
│   ├── Main.java                    # Entry point, DI wiring
│   ├── controller/                  # MVC Controllers (6)
│   ├── service/                     # Business Logic (7 services + 2 interfaces)
│   ├── model/                       # Domain Entities + Inheritance hierarchy
│   │   └── user/                    # 5 User subclasses
│   ├── repository/                  # 4 interfaces + 4 MySQL implementations
│   ├── factory/                     # AlertFactory (Factory Pattern)
│   ├── builder/                     # ReportBuilder (Builder Pattern)
│   ├── db/                          # DatabaseManager (Singleton), Schema, Seeder
│   └── util/                        # PasswordUtil, JsonUtil
└── frontend/
    ├── index.html                   # Redirect to login
    ├── login.html                   # Authentication
    ├── dashboard.html               # Executive KPI + Charts
    ├── vendors.html                 # Vendor management table
    ├── risk.html                    # Risk evaluation engine
    ├── alerts.html                  # Alert feed + recommendations
    ├── inventory.html               # Warehouse stock management
    ├── reports.html                 # Analytics + CSV export
    └── js/
        └── api.js                   # Shared API client + sidebar + CSS
```

## API Endpoints

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | /api/auth/login | All | Login |
| POST | /api/auth/logout | All | Logout |
| GET | /api/vendor | All | List vendors |
| POST | /api/vendor | PM, Admin | Create vendor |
| PUT | /api/vendor/{id} | PM, Admin | Update vendor |
| DELETE | /api/vendor/{id} | Admin | Soft-delete |
| PUT | /api/vendor/{id}/suspend | PM, Admin | Suspend |
| PUT | /api/vendor/{id}/activate | PM, Admin | Activate |
| PUT | /api/vendor/{id}/blacklist | Admin | Blacklist |
| GET | /api/vendor/{id}/risk | All | Risk history |
| POST | /api/risk/calculate/{id} | RA, Admin | Calculate risk |
| POST | /api/vendor/{id}/evaluate | RA, PM, Admin | Submit evaluation |
| GET | /api/alert | PM, RA, Admin | List alerts |
| PUT | /api/alert/{id}/resolve | PM, RA, Admin | Resolve alert |
| GET | /api/alert/{id}/recommendation | PM, RA, Admin | Get recommendations |
| GET | /api/inventory | WM, PM, Admin | List inventory |
| PUT | /api/inventory/{id} | WM, Admin | Update stock |
| GET | /api/report/performance | PM, RA, Admin | Performance report |
| GET | /api/report/strategic | Admin | Strategic report |
| GET | /api/health | - | Health check |
