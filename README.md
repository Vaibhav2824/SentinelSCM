# 🚀 SentinelSCM: Smart Supply Chain Disruption & Vendor Risk Management System

<div align="center">

[![Java](https://img.shields.io/badge/Java-17%2B-ED8936?style=flat-square&logo=java)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.8%2B-C71A36?style=flat-square&logo=apache-maven)](https://maven.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.x-00758F?style=flat-square&logo=mysql)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)](LICENSE)

**🎓 OOAD Mini Project** | **UE23CS352B** | **PES University**

</div>

---

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│  🌐 Browser (HTML/JS/Chart.js)                              │
└────────────────┬──────────────────────────────────────────┘
                 │ REST API (JSON)
┌────────────────▼──────────────────────────────────────────┐
│  ⚙️  Java 17 + Javalin 5.6.3 REST API                     │
│  • 6 Controllers  • 7 Services  • Observer Pattern          │
│  • Singleton DB   • Strategy Pattern  • Builder Pattern     │
└────────────────┬──────────────────────────────────────────┘
                 │ JDBC
┌────────────────▼──────────────────────────────────────────┐
│  🗄️  MySQL 8.x Database                                   │
│  • 8 Tables  • Vendor Risk Management  • Audit Trail        │
└─────────────────────────────────────────────────────────────┘
```

---

## 🛠️ Tech Stack

| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| 🖥️ **Backend** | Java | 17+ (JDK 21) | Object-oriented backend logic |
| 🌐 **Web Framework** | Javalin | 5.6.3 | Lightweight REST API server |
| 🗄️ **Database** | MySQL | 8.x | Persistent data storage |
| 📄 **Serialization** | Gson | 2.10.1 | JSON marshalling |
| 🔐 **Security** | BCrypt | at.favre.lib | Password hashing & encryption |
| 🎨 **Frontend** | HTML + Vanilla JS | ES6 | Interactive UI |
| 📊 **Charting** | Chart.js | Latest | Data visualization |
| 🎭 **Styling** | CSS Variables | - | Dark-mode theme |

---

## 🎯 OOAD Concepts Demonstrated

### 🏭 Design Patterns (6 Advanced Patterns)

| Pattern | Implementation | Purpose |
|---------|----------------|---------|
| 🔒 **Singleton** | `DatabaseManager.java` | Thread-safe, single DB connection pool |
| 🏗️ **Factory Method** | `AlertFactory.java` | Centralized HIGH_RISK alert creation |
| 🔨 **Builder** | `ReportBuilder.java` | Fluent API for constructing complex reports |
| 🎭 **Strategy** | `IRiskStrategy` + `WeightedRiskStrategy` | Pluggable risk calculation algorithms |
| 👀 **Observer** | `AlertService` + `AlertObserver` | Real-time alert notifications & logging |
| 🎪 **MVC** | Controllers → Services → Repositories | Layered architecture & separation of concerns |

### ✅ SOLID Principles (All 5)

- **SRP** — Single Responsibility: Each service owns one business domain
- **OCP** — Open/Closed: `IRiskStrategy` extends without modifying existing code
- **LSP** — Liskov Substitution: All 5 User subclasses substitute transparently
- **ISP** — Interface Segregation: Focused interfaces (`IVendorRepository`, `IAlertRepository`)
- **DIP** — Dependency Inversion: Services depend on abstractions, not implementations

### 🎨 GRASP Patterns (All 9 Applied)

**Information Expert** | **Creator** | **Controller** | **Low Coupling** | **High Cohesion** |
**Polymorphism** | **Pure Fabrication** | **Indirection** | **Protected Variations**

---

## 📋 Prerequisites

| Requirement | Version | Link |
|-------------|---------|------|
| ☕ **Java JDK** | 17+ (JDK 21 recommended) | [Download](https://www.oracle.com/java/technologies/downloads/) |
| 🏗️ **Maven** | 3.8+ (Maven Wrapper included) | [Download](https://maven.apache.org/download.cgi) |
| 🗄️ **MySQL** | 8.x (running on port 3306) | [Download](https://www.mysql.com/downloads/) |

> 💡 **Tip**: Maven Wrapper (`mvnw.cmd`) is included, so you don't need to install Maven separately!

---

## ⚡ Quick Start Guide

### Step 1️⃣ — Configure MySQL Credentials

Open **`src/main/java/com/scm/db/DatabaseManager.java`** and update:

```java
private static final String DB_USER     = "root";
private static final String DB_PASSWORD = "your_mysql_password";   // ← Update this
```

### Step 2️⃣ — Start the Backend

```bash
run_backend.bat
```

✨ **This will automatically**:
- ✅ Connect to MySQL and create `scm_db` database
- ✅ Initialize all tables (SchemaInitializer)
- ✅ Seed demo data with 12 vendors (MockDataSeeder)
- ✅ Start Javalin server on **http://localhost:8080**
- ✅ Serve frontend from **http://localhost:8080**

### Step 3️⃣ — Open the Application

Navigate to your browser: **[http://localhost:8080/login.html](http://localhost:8080/login.html)**

---

## 🎭 Demo Credentials

| 👤 Email | 🔑 Password | 👨‍💼 Role | 📊 Access Level |
|----------|-----------|---------|-----------------|
| `admin@scm.com` | `admin123` | 🛡️ Administrator | Full system access |
| `pm@scm.com` | `pm123` | 📦 Procurement Manager | Vendors, alerts, reports |
| `analyst@scm.com` | `analyst123` | 📈 Risk Analyst | Risk evaluation, alerts |
| `wm@scm.com` | `wm123` | 🏭 Warehouse Manager | Inventory management |
| `vendor@scm.com` | `vendor123` | 🤝 Vendor | View-only access |

---

## 🎬 Interactive Demo Flow (5 minutes)

1. **🔐 Login** as `analyst@scm.com` → See executive dashboard with KPI cards
2. **📊 Dashboard** → Risk distribution chart, live alert feed, vendor statistics
3. **🏪 Vendors** → Browse 12 vendors including 3 marked as HIGH_RISK
4. **⚠️ Risk Analysis** → Select *FastSupply Co* (V2), adjust risk sliders, click **Recalculate**
5. **🔔 Alerts** → View HIGH RISK alert for V2 → Click **Get Recommendations** → See 3 alternatives
6. **🔄 Switch to** `pm@scm.com` → Navigate to Vendors → Suspend V2
7. **📦 Switch to** `wm@scm.com` → Go to Inventory → Update low-stock items
8. **📋 Switch to** `admin@scm.com` → Reports → Strategic tab → **Export as CSV**

---

## 📊 Risk Score Algorithm

### Formula

$$\text{Risk Score} = (1 - \text{Timeliness}) \times 0.4 + \text{Defect Rate} \times 0.4 + (1 - \text{Compliance}) \times 0.2$$

### Risk Evaluation

| Score Range | 🎯 Level | ⚠️ Alert | 🔴 Action |
|-------------|----------|----------|----------|
| **0.0 - 0.4** | ✅ Low | None | Continue monitoring |
| **0.4 - 0.7** | ⚠️ Medium | Warning | Schedule review |
| **0.7 - 1.0** | 🔴 High | Critical | Get recommendations |

**Default Alert Threshold**: `0.700` (70%)  
**Trigger**: Score > 0.700 → HIGH_RISK alert + vendor recommendations generated

---

## 📁 Project Structure

```
SCM_Project/
│
├── 📋 pom.xml                          # Maven config: MySQL, Javalin, Gson, BCrypt
├── 🚀 run_backend.bat                  # One-click backend startup
│
├── 🗄️ db/
│   ├── schema.sql                      # Complete MySQL DDL (8 tables)
│   └── seed_data.sql                   # Demo dataset (12 vendors, 50+ records)
│
├── 🔧 src/main/java/com/scm/
│   ├── Main.java                       # Application entry point
│   │
│   ├── 🎮 controller/                  # REST Controllers (6)
│   │   ├── AuthController              # Authentication & session
│   │   ├── VendorController            # Vendor CRUD & actions
│   │   ├── RiskController              # Risk calculation
│   │   ├── AlertController             # Alert management
│   │   ├── InventoryController         # Stock management
│   │   └── ReportController            # Analytics & exports
│   │
│   ├── 💼 service/                     # Business Logic (7 services)
│   │   ├── AuthService                 # User authentication
│   │   ├── VendorService               # Vendor management
│   │   ├── RiskService                 # Risk algorithms
│   │   ├── AlertService                # Observer pattern
│   │   ├── InventoryService            # Stock tracking
│   │   ├── ReportService               # Report generation
│   │   ├── RecommendationService       # Alternative suggestions
│   │   ├── IRiskStrategy               # Strategy interface
│   │   ├── WeightedRiskStrategy        # Algorithm implementation
│   │   └── AlertObserver               # Observer interface
│   │
│   ├── 🧩 model/                       # Domain Entities
│   │   ├── User.java                   # Base User class
│   │   ├── 👥 user/                    # 5 User subclasses
│   │   │   ├── Administrator.java
│   │   │   ├── ProcurementManager.java
│   │   │   ├── RiskAnalyst.java
│   │   │   ├── VendorUser.java
│   │   │   └── WarehouseManager.java
│   │   ├── Vendor.java                 # Vendor entity
│   │   ├── Alert.java                  # Risk alerts
│   │   ├── InventoryItem.java          # Stock items
│   │   ├── RiskScore.java              # Risk metrics
│   │   ├── PurchaseOrder.java          # Orders
│   │   ├── PerformanceReport.java      # Reports
│   │   └── Recommendation.java         # Suggestions
│   │
│   ├── 🔌 repository/                  # Data Access Layer
│   │   ├── IVendorRepository.java      # Interface
│   │   ├── MySQLVendorRepository.java  # Implementation
│   │   ├── IRiskRepository.java        # Interface
│   │   ├── MySQLRiskRepository.java    # Implementation
│   │   ├── IAlertRepository.java       # Interface
│   │   ├── MySQLAlertRepository.java   # Implementation
│   │   ├── IInventoryRepository.java   # Interface
│   │   └── MySQLInventoryRepository.java
│   │
│   ├── 🏭 factory/
│   │   └── AlertFactory.java           # Factory pattern implementation
│   │
│   ├── 🔨 builder/
│   │   └── ReportBuilder.java          # Builder pattern (fluent API)
│   │
│   ├── 🗄️ db/
│   │   ├── DatabaseManager.java        # Singleton pattern
│   │   ├── SchemaInitializer.java      # DDL execution
│   │   └── MockDataSeeder.java         # Demo data insertion
│   │
│   └── 🛠️ util/
│       ├── PasswordUtil.java           # BCrypt hashing
│       └── JsonUtil.java               # Gson serialization
│
└── 🌐 frontend/
    ├── index.html                      # Redirect to login
    ├── 🔐 login.html                   # Authentication UI
    ├── 📊 dashboard.html               # Executive KPI dashboard
    ├── 🏪 vendors.html                 # Vendor management
    ├── ⚖️ risk.html                    # Risk evaluation engine
    ├── 🔔 alerts.html                  # Alert feed & recommendations
    ├── 📦 inventory.html               # Warehouse inventory
    ├── 📋 reports.html                 # Analytics & exports
    └── js/
        └── api.js                      # Shared API client & utilities
```

## 🔌 API Endpoints

### 🔐 Authentication Endpoints

| Method | Endpoint | 👤 Role | Description |
|--------|----------|---------|-------------|
| `POST` | `/api/auth/login` | All | Login with email & password |
| `POST` | `/api/auth/logout` | All | Terminate session |

### 🏪 Vendor Management Endpoints

| Method | Endpoint | 👤 Role | Description |
|--------|----------|---------|-------------|
| `GET` | `/api/vendor` | All | List all vendors |
| `POST` | `/api/vendor` | PM, Admin | Create new vendor |
| `PUT` | `/api/vendor/{id}` | PM, Admin | Update vendor details |
| `DELETE` | `/api/vendor/{id}` | Admin | Soft-delete vendor |
| `PUT` | `/api/vendor/{id}/suspend` | PM, Admin | Suspend vendor |
| `PUT` | `/api/vendor/{id}/activate` | PM, Admin | Reactivate vendor |
| `PUT` | `/api/vendor/{id}/blacklist` | Admin | Blacklist vendor |
| `GET` | `/api/vendor/{id}/risk` | All | View risk history |

### ⚖️ Risk Evaluation Endpoints

| Method | Endpoint | 👤 Role | Description |
|--------|----------|---------|-------------|
| `POST` | `/api/risk/calculate/{id}` | RA, Admin | Calculate vendor risk score |
| `POST` | `/api/vendor/{id}/evaluate` | RA, PM, Admin | Submit evaluation criteria |

### 🔔 Alert Management Endpoints

| Method | Endpoint | 👤 Role | Description |
|--------|----------|---------|-------------|
| `GET` | `/api/alert` | PM, RA, Admin | List all alerts |
| `PUT` | `/api/alert/{id}/resolve` | PM, RA, Admin | Mark alert as resolved |
| `GET` | `/api/alert/{id}/recommendation` | PM, RA, Admin | Get vendor recommendations |

### 📦 Inventory Management Endpoints

| Method | Endpoint | 👤 Role | Description |
|--------|----------|---------|-------------|
| `GET` | `/api/inventory` | WM, PM, Admin | List inventory |
| `PUT` | `/api/inventory/{id}` | WM, Admin | Update stock quantity |

### 📊 Reporting Endpoints

| Method | Endpoint | 👤 Role | Description |
|--------|----------|---------|-------------|
| `GET` | `/api/report/performance` | PM, RA, Admin | Performance analytics |
| `GET` | `/api/report/strategic` | Admin | Strategic insights (CSV export) |
| `GET` | `/api/health` | - | Health check |

---

## 🎓 Key Features

✨ **Enterprise-Grade Supply Chain Management**

- 🎭 **Role-Based Access Control** — 5 distinct user roles with granular permissions
- 📊 **Real-Time Risk Analysis** — Weighted algorithm with dynamic recalculation
- 🔔 **Smart Alert System** — Observer pattern for real-time notifications
- 📈 **Analytics Dashboard** — Executive KPI cards with interactive charts
- 💼 **Vendor Management** — Full lifecycle (Create, Evaluate, Suspend, Blacklist)
- 🏭 **Inventory Tracking** — Low-stock alerts and stock management
- 💾 **Audit Trail** — Complete history of all changes
- 📋 **Report Generation** — Performance & strategic reports with CSV export
- 🔐 **Secure Authentication** — BCrypt password hashing
- 🌙 **Modern UI** — Dark mode CSS with responsive design

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for bugs and feature requests.

## 📜 License

This project is licensed under the MIT License — see the LICENSE file for details.

---

<div align="center">

**Built with ❤️ for OOAD @ PES University**

[⬆ Back to Top](#-sentinelscm-smart-supply-chain-disruption--vendor-risk-management-system)

</div>
