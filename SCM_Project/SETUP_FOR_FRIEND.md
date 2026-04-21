# SupplyChainOS — Setup Guide for New Machine

> Smart Supply Chain Disruption & Vendor Risk Management System  
> OOAD Mini Project | PES University UE23CS352B

---

## ✅ Prerequisites (Install These First)

| Tool | Version | Download |
|------|---------|----------|
| **Java JDK** | 17 or 21 | https://adoptium.net (Temurin JDK 21) |
| **MySQL** | 8.x | https://dev.mysql.com/downloads/mysql/ |

> Maven is **NOT needed** separately — the project includes `mvnw.cmd` (Maven Wrapper).

---

## 🚀 Step-by-Step Setup

### Step 1 — Extract the ZIP
Extract `SCM_Project.zip` anywhere, e.g.:
```
C:\SCM_Project\
```
> ⚠️ Avoid paths with parentheses like `C:\OOAD(1)\...` — use a clean path.

---

### Step 2 — Start MySQL
Make sure MySQL 8 is running on **port 3306**. In PowerShell:
```powershell
net start MySQL80
```
Or start it from **MySQL Workbench / Windows Services**.

---

### Step 3 — Update Database Password
Open this file in any text editor (Notepad is fine):
```
SCM_Project\src\main\java\com\scm\db\DatabaseManager.java
```
Find line ~30 and change the password to **your MySQL root password**:
```java
private static final String DB_USER     = "root";
private static final String DB_PASSWORD = "YOUR_PASSWORD_HERE";  // ← change this
```
Save the file.

---

### Step 4 — Build the Project (One Time Only)
Open **PowerShell** in the extracted folder and run:
```powershell
Set-Location "C:\SCM_Project"
.\mvnw.cmd package -DskipTests
```
Wait for: `BUILD SUCCESS` — this creates `target\supply-chain-risk-1.0.0.jar`

> First build may take 2–3 minutes (downloads Maven dependencies automatically).

---

### Step 5 — Run the Backend
```powershell
Set-Location "C:\SCM_Project"
& "C:\Program Files\Eclipse Adoptium\jdk-21\bin\java.exe" -jar ".\target\supply-chain-risk-1.0.0.jar"
```

> **Adjust the java.exe path** to wherever your JDK was installed.  
> Common locations:
> - `C:\Program Files\Eclipse Adoptium\jdk-21.0.x.xx-hotspot\bin\java.exe`
> - `C:\Program Files\Java\jdk-21\bin\java.exe`
> - Or just use `java -jar .\target\supply-chain-risk-1.0.0.jar` if `java` is in your PATH

Wait until you see:
```
[SERVER] SupplyChainOS started successfully!
[SERVER] Frontend: http://localhost:8080
```

---

### Step 6 — Open the App
Open your browser and go to:
```
http://localhost:8080/login.html
```

---

## 🔑 Login Credentials

| Email | Password | Role |
|-------|----------|------|
| `admin@scm.com` | `admin123` | Administrator (all access) |
| `pm@scm.com` | `pm123` | Procurement Manager |
| `analyst@scm.com` | `analyst123` | Risk Analyst |
| `wm@scm.com` | `wm123` | Warehouse Manager |
| `vendor@scm.com` | `vendor123` | Vendor (read-only) |

> Demo data (12 vendors, alerts, inventory) is seeded **automatically** on first run.

---

## 🛠️ Troubleshooting

| Problem | Fix |
|---------|-----|
| `BUILD FAILED` | Make sure Java 17+ is installed and in PATH |
| `Failed to connect to MySQL` | Check MySQL is running + password in `DatabaseManager.java` is correct |
| `Port 8080 already in use` | Kill the process: `netstat -ano \| findstr :8080` then `taskkill /PID <pid> /F` |
| Page shows "Not Found" | Backend didn't start — check the terminal for errors |
| "Network error" on login | Backend isn't running — go to Step 5 |
| Build hangs on download | First-time Maven dependency download — wait, needs internet |

---

## 📋 What Gets Created Automatically

On first run the app will:
1. Connect to MySQL and create the `scm_db` database
2. Create all 9 tables (vendors, users, alerts, inventory, etc.)
3. Seed 12 vendors, 5 users, 7 alerts, 10 purchase orders

No manual SQL needed.

---

## 🏗️ Tech Stack (for reference)

- **Backend:** Java 17 + Javalin 5.6.3 (REST API on port 8080)
- **Database:** MySQL 8.x (auto-created as `scm_db`)
- **Frontend:** HTML + CSS + Vanilla JS + Chart.js (served by Javalin)
- **Auth:** BCrypt password hashing + Bearer token RBAC

*SupplyChainOS — OOAD Mini Project | UE23CS352B*
