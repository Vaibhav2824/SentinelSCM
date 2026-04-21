# SupplyChainOS — Complete Feature Test Guide

> **Base URL:** `http://localhost:8080`  
> **Prerequisites:** Java backend running + MySQL on port 3306  
> **OOAD Project:** Smart Supply Chain Disruption & Vendor Risk Management System

---

## ✅ Test Checklist Overview

| # | Module | Feature | OOAD Pattern | Status |
|---|--------|---------|--------------|--------|
| 1 | Auth | Login / Logout | — | `[ ]` |
| 2 | Auth | Role-based access | Inheritance / Polymorphism | `[ ]` |
| 3 | Dashboard | KPI Cards | — | `[ ]` |
| 4 | Dashboard | Risk distribution chart | — | `[ ]` |
| 5 | Dashboard | Live alert feed | Observer | `[ ]` |
| 6 | Vendors | List all vendors | — | `[ ]` |
| 7 | Vendors | Add new vendor | Creator (GRASP) | `[ ]` |
| 8 | Vendors | Edit vendor | — | `[ ]` |
| 9 | Vendors | Suspend / Activate | — | `[ ]` |
| 10 | Vendors | Blacklist / Delete | — | `[ ]` |
| 11 | Vendors | Search filter | — | `[ ]` |
| 12 | Risk | Submit evaluation criteria | Strategy | `[ ]` |
| 13 | Risk | Calculate risk score | Strategy + Formula | `[ ]` |
| 14 | Risk | Formula preview (live) | — | `[ ]` |
| 15 | Risk | Alert auto-triggered | Factory + Observer | `[ ]` |
| 16 | Risk | Risk history trend chart | — | `[ ]` |
| 17 | Alerts | View all alerts | Observer | `[ ]` |
| 18 | Alerts | Filter (Active/High/Resolved) | — | `[ ]` |
| 19 | Alerts | Resolve an alert | — | `[ ]` |
| 20 | Alerts | View recommendations | Pure Fabrication | `[ ]` |
| 21 | Inventory | View stock levels | — | `[ ]` |
| 22 | Inventory | Detect low stock | — | `[ ]` |
| 23 | Inventory | Update stock quantity | — | `[ ]` |
| 24 | Reports | Performance report | Builder | `[ ]` |
| 25 | Reports | Strategic report (Admin) | Builder | `[ ]` |
| 26 | Reports | Export CSV | — | `[ ]` |
| 27 | API | Health check endpoint | Singleton | `[ ]` |
| 28 | OOAD | Singleton pattern | DatabaseManager | `[ ]` |
| 29 | OOAD | Factory pattern | AlertFactory | `[ ]` |
| 30 | OOAD | Strategy pattern | WeightedRiskStrategy | `[ ]` |
| 31 | OOAD | Observer pattern | AlertService + Observers | `[ ]` |
| 32 | OOAD | Builder pattern | ReportBuilder | `[ ]` |

---

## 🔐 MODULE 1 — Authentication

### Test 1.1 — Admin Login
**URL:** `http://localhost:8080/login.html`

| Step | Action | Expected Result |
|------|--------|----------------|
| 1 | Open `http://localhost:8080/login.html` | Dark glassmorphic login page with SupplyChainOS branding |
| 2 | Enter email: `admin@scm.com`, password: `admin123` | Fields accept input |
| 3 | Click **Sign In** | Redirected to `dashboard.html` |
| 4 | Check browser localStorage | `token` and `user` keys are set |

**✅ Pass:** Dashboard loads with admin user's name in sidebar  
**❌ Fail:** "Network error" → Backend not on port 8080

---

### Test 1.2 — All Role Logins
Test each role and verify sidebar shows correct user name:

| Email | Password | Role | Pages Accessible |
|-------|----------|------|-----------------|
| `admin@scm.com` | `admin123` | ADMINISTRATOR | All pages |
| `pm@scm.com` | `pm123` | PROCUREMENT_MANAGER | Dashboard, Vendors, Alerts, Reports |
| `analyst@scm.com` | `analyst123` | RISK_ANALYST | Dashboard, Vendors, Risk, Alerts, Reports |
| `wm@scm.com` | `wm123` | WAREHOUSE_MANAGER | Dashboard, Inventory |
| `vendor@scm.com` | `vendor123` | VENDOR | Dashboard, Vendors (read-only) |

---

### Test 1.3 — Wrong Password
| Step | Action | Expected Result |
|------|--------|----------------|
| 1 | Enter `admin@scm.com` / `wrongpass` | Red error banner: "Invalid email or password" |
| 2 | Verify no redirect | Stays on login page |

---

### Test 1.4 — Logout
| Step | Action | Expected Result |
|------|--------|----------------|
| 1 | Login as any user | Reach dashboard |
| 2 | Click **Sign Out** in sidebar | Redirect to login page |
| 3 | Try to navigate to `dashboard.html` directly | Redirect back to login |

---

### Test 1.5 — Role-Based Access Control (OOAD: Inheritance/Polymorphism)
| Step | Action | Expected Result |
|------|--------|----------------|
| 1 | Login as `wm@scm.com` (Warehouse Manager) | Dashboard loads |
| 2 | Try API: `GET /api/report/strategic` with WM token | `403 Forbidden — insufficient permissions` |
| 3 | Login as `admin@scm.com` | Same API → returns report JSON |

> **OOAD:** 5 User subclasses (`Administrator`, `ProcurementManager`, `RiskAnalyst`, `WarehouseManager`, `VendorUser`) each override `getPermissions()` — **Polymorphism + Inheritance**

---

## 📊 MODULE 2 — Dashboard

**URL:** `http://localhost:8080/dashboard.html`  
**Login as:** `admin@scm.com`

### Test 2.1 — KPI Cards
| KPI | Expected | Check |
|-----|----------|-------|
| Total Vendors | 12 (from seed data) | `[ ]` |
| Active | ≥ 7 | `[ ]` |
| High Risk | ≥ 2 | `[ ]` |
| Avg Risk Score | 0.3–0.7 | `[ ]` |
| Active Alerts | ≥ 1 | `[ ]` |
| Low Stock Items | Count of items with qty < 50 | `[ ]` |

---

### Test 2.2 — Risk Distribution Bar Chart
| Step | Action | Expected |
|------|--------|----------|
| 1 | View the chart | 5 bars: 0–0.2, 0.2–0.4, 0.4–0.6, 0.6–0.8, 0.8+ |
| 2 | Hover over bars | Tooltip shows vendor count |
| 3 | Colors | Green (safe) → Red (critical) |

---

### Test 2.3 — Vendor Status Donut Chart
| Step | Action | Expected |
|------|--------|----------|
| 1 | View donut | Segments: Active, Pending, High Risk, Suspended |
| 2 | Legend | Visible below chart with color coding |

---

### Test 2.4 — Live Alert Feed (OOAD: Observer Pattern)
| Step | Action | Expected |
|------|--------|----------|
| 1 | View alert feed | Shows top 5 unresolved alerts |
| 2 | Click any alert | Navigates to alerts page |
| 3 | HIGH alerts | Red dot with pulsing animation |

---

### Test 2.5 — Vendor Risk Score Bars
| Step | Action | Expected |
|------|--------|----------|
| 1 | View "Risk Score Overview" card | 7 vendors listed with progress bars |
| 2 | Colors | Red > 0.7, Yellow 0.4–0.7, Green < 0.4 |

---

## 🏭 MODULE 3 — Vendor Management

**URL:** `http://localhost:8080/vendors.html`  
**Login as:** `admin@scm.com`

### Test 3.1 — View All Vendors
| Step | Action | Expected |
|------|--------|----------|
| 1 | Open vendors page | Table with 12 vendors loaded |
| 2 | Check columns | Name, Contact, Rating (stars), Risk Score, Risk Level, Status, Actions |
| 3 | Risk badges | HIGH (red), MEDIUM (yellow), LOW (green) |
| 4 | Status badges | ACTIVE (green), PENDING (cyan), SUSPENDED (yellow), HIGH_RISK (red) |

---

### Test 3.2 — Search Filter
| Step | Action | Expected |
|------|--------|----------|
| 1 | Type "fast" in search box | Filters to vendors with "fast" in name |
| 2 | Clear search | All 12 vendors return |
| 3 | Search by contact email | Filters correctly |

---

### Test 3.3 — Add Vendor (OOAD: GRASP Creator)
| Step | Action | Expected |
|------|--------|----------|
| 1 | Click **+ Add Vendor** | Modal opens |
| 2 | Leave name empty, click Save | Error: "Vendor name is required" |
| 3 | Fill: Name=`Test Vendor`, Rating=`4.2`, Status=`PENDING` | — |
| 4 | Click **Save Vendor** | Toast: "Vendor created successfully", table refreshes |
| 5 | Search for "Test Vendor" | New vendor appears in table |

---

### Test 3.4 — Edit Vendor
| Step | Action | Expected |
|------|--------|----------|
| 1 | Click **Edit** on any vendor | Modal pre-fills with existing data |
| 2 | Change rating to `3.5` | — |
| 3 | Click **Save Vendor** | Toast: "Vendor updated", table refreshes |

---

### Test 3.5 — Suspend / Activate Vendor
| Step | Action | Expected |
|------|--------|----------|
| 1 | Click **Suspend** on an ACTIVE vendor | Status badge changes to SUSPENDED |
| 2 | Click **Activate** on a SUSPENDED vendor | Status badge changes to ACTIVE |

---

### Test 3.6 — Blacklist Vendor (Admin only)
| Step | Action | Expected |
|------|--------|----------|
| 1 | Login as `admin@scm.com` | — |
| 2 | Click **Blacklist** on any vendor | Confirm dialog → Status → BLACKLISTED |
| 3 | Login as `pm@scm.com` | **Blacklist** button NOT visible |

---

### Test 3.7 — Role Restrictions on Vendor Actions
| Role | Add | Edit | Suspend | Blacklist | Delete |
|------|-----|------|---------|-----------|--------|
| ADMIN | ✅ | ✅ | ✅ | ✅ | ✅ |
| PROCUREMENT_MANAGER | ✅ | ✅ | ✅ | ❌ | ❌ |
| RISK_ANALYST | ❌ | ❌ | ❌ | ❌ | ❌ |
| WAREHOUSE_MANAGER | ❌ | ❌ | ❌ | ❌ | ❌ |

---

## ⚠️ MODULE 4 — Risk Analysis Engine

**URL:** `http://localhost:8080/risk.html`  
**Login as:** `analyst@scm.com`

### Test 4.1 — Live Formula Preview
| Step | Action | Expected |
|------|--------|----------|
| 1 | Move **Delivery Timeliness** slider to `0.50` | Formula updates: `(1-0.500) × 0.4 = 0.200` |
| 2 | Move **Defect Rate** slider to `0.30` | Formula adds: `0.300 × 0.4 = 0.120` |
| 3 | Move **Compliance Score** slider to `0.70` | Formula adds: `(1-0.700) × 0.2 = 0.060` |
| 4 | Preview result | `= 0.380` (MEDIUM, yellow) |
| 5 | Set Delivery=`0.10`, Defect=`0.90` | Score > 0.7 → turns red (HIGH RISK) |

**Formula (OOAD: Strategy Pattern):**
```
risk = (1 - delivery_timeliness) × 0.4
     + defect_rate               × 0.4
     + (1 - compliance_score)    × 0.2
```

---

### Test 4.2 — Submit Evaluation Criteria
| Step | Action | Expected |
|------|--------|----------|
| 1 | Select `FastSupply Co` from dropdown | — |
| 2 | Set sliders: Delivery=`0.40`, Defect=`0.50`, Compliance=`0.60` | Preview score ≈ `0.52` |
| 3 | Click **Save Evaluation** | Toast: "Evaluation saved. Preview score: 0.520" |

---

### Test 4.3 — Calculate Risk (OOAD: Strategy + Factory + Observer)
| Step | Action | Expected |
|------|--------|----------|
| 1 | Select a HIGH-RISK vendor (e.g., FastSupply Co) | — |
| 2 | Set sliders for HIGH risk: Delivery=`0.20`, Defect=`0.80`, Compliance=`0.30` | Preview > 0.7 |
| 3 | Click **Save Evaluation** | Evaluation saved |
| 4 | Click **Recalculate Risk** | Loading state → result card appears |
| 5 | Score > 0.700 | "Alert Triggered: YES" (red banner) |
| 6 | Score ring | Turns red, shows score value |
| 7 | Check backend console | `[AlertFactory] Created HIGH alert for vendor: ...` |
| 8 | Check backend console | `[AlertService] Firing alert to 1 observer(s)` |
| 9 | Check backend console | `[ALERT] [HIGH] Vendor#X — ...` |

> **Patterns demonstrated:**  
> **Strategy:** `WeightedRiskStrategy.calculate()` runs the formula  
> **Factory:** `AlertFactory.createAlert()` builds the alert object  
> **Observer:** `AlertService.fireAlert()` notifies `ConsoleLogObserver`  
> **Pure Fabrication:** `RecommendationService.suggestAlternatives()` finds alternatives

---

### Test 4.4 — Risk History Trend Chart
| Step | Action | Expected |
|------|--------|----------|
| 1 | Select a vendor that has been calculated multiple times | — |
| 2 | View trend chart | Line chart with dates on X-axis, scores on Y-axis |
| 3 | After recalculation (Test 4.3) | New data point appears on chart |
| 4 | Points | Color-coded: red (HIGH), yellow (MEDIUM), green (LOW) |

---

### Test 4.5 — No Criteria Error Handling
| Step | Action | Expected |
|------|--------|----------|
| 1 | Select a brand new vendor (just created, no evaluation) | — |
| 2 | Click **Recalculate Risk** WITHOUT saving evaluation first | Error: "No evaluation criteria found for vendor X. Submit evaluation first." |

---

## 🚨 MODULE 5 — Alert Management

**URL:** `http://localhost:8080/alerts.html`  
**Login as:** `analyst@scm.com`

### Test 5.1 — View All Alerts
| Step | Action | Expected |
|------|--------|----------|
| 1 | Open alerts page | KPI cards: Total, Active, High Severity, Resolved |
| 2 | Alert cards | Color-coded left border: red (HIGH), yellow (MEDIUM), cyan (LOW) |
| 3 | Resolved alerts | Appear faded (opacity 0.55) with ✓ Resolved badge |

---

### Test 5.2 — Filter Alerts
| Filter | Expected |
|--------|----------|
| **All Alerts** | Shows all alerts |
| **Active** | Only unresolved alerts |
| **High Severity** | Only HIGH + unresolved |
| **Resolved** | Only resolved alerts |

---

### Test 5.3 — Resolve an Alert
| Step | Action | Expected |
|------|--------|----------|
| 1 | Find an active alert | Has **Resolve** button |
| 2 | Click **Resolve** | Toast: "Alert resolved" |
| 3 | Alert card | Becomes faded, shows "✓ Resolved" badge |
| 4 | KPI "Resolved" count | Increments by 1 |

---

### Test 5.4 — View Recommendations (OOAD: Pure Fabrication)
| Step | Action | Expected |
|------|--------|----------|
| 1 | Click **Recommendations** on a HIGH risk alert | Dropdown panel expands |
| 2 | Panel shows | Up to 3 alternative low-risk vendors |
| 3 | Each recommendation | Shows vendor name + reason (risk score, rating, status) |
| 4 | Click **Recommendations** again | Panel collapses (toggle) |

> **OOAD:** `RecommendationService` is a **Pure Fabrication** — no domain entity owns this responsibility, so a dedicated service class was created.

---

### Test 5.5 — Alert Auto-Generated After Risk Calculation
| Step | Action | Expected |
|------|--------|----------|
| 1 | Go to Risk page → trigger high risk calculation | Alert triggered |
| 2 | Go to Alerts page | New HIGH alert appears at top of Active list |
| 3 | Recommendations | Auto-populated with alternative vendors |

---

## 📦 MODULE 6 — Inventory Management

**URL:** `http://localhost:8080/inventory.html`  
**Login as:** `wm@scm.com` (Warehouse Manager)

### Test 6.1 — View Inventory
| Step | Action | Expected |
|------|--------|----------|
| 1 | Open inventory page | Table with all items, stock counts, progress bars |
| 2 | KPI: Total Items | Count of all inventory items |
| 3 | KPI: Low Stock | Items with quantity < 50 |
| 4 | Low stock rows | Show "⚠ Low Stock" red badge |
| 5 | OK stock rows | Show "✓ OK" green badge |

---

### Test 6.2 — Stock Progress Bars
| Quantity | Bar Color |
|----------|-----------|
| < 50 | Red |
| 50–200 | Yellow |
| > 200 | Green |

---

### Test 6.3 — Update Stock (Warehouse Manager)
| Step | Action | Expected |
|------|--------|----------|
| 1 | Find a low stock item | Has editable input and Save button |
| 2 | Change quantity to `500` | — |
| 3 | Click **Save** | Toast: "Stock updated successfully", row refreshes |
| 4 | Badge changes | From "⚠ Low Stock" to "✓ OK" |

---

### Test 6.4 — Read-Only for Other Roles
| Step | Action | Expected |
|------|--------|----------|
| 1 | Login as `analyst@scm.com` | — |
| 2 | Open inventory page | 403 — Forbidden OR "Read-only" text instead of input fields |

---

## 📈 MODULE 7 — Reports

**URL:** `http://localhost:8080/reports.html`  
**Login as:** `admin@scm.com`

### Test 7.1 — Performance Report (OOAD: Builder Pattern)
| Step | Action | Expected |
|------|--------|----------|
| 1 | Click **Performance Report** tab | Auto-loads on page open |
| 2 | KPI cards | Total, Active, High Risk, Avg Score, Open Alerts |
| 3 | Horizontal bar chart (Risk) | One bar per vendor, colored by risk level |
| 4 | Horizontal bar chart (Ratings) | Cyan bars, 0–5 scale |
| 5 | Detailed table | Delivery %, Defect Rate %, Compliance % for each vendor |

> **OOAD:** `ReportBuilder` uses the **Builder pattern** — `setType()`, `setGeneratedAt()`, `addStat()`, `setVendorSummaries()`, `build()` — fluent API constructing the report map.

---

### Test 7.2 — Strategic Report (Admin Only)
| Step | Action | Expected |
|------|--------|----------|
| 1 | Click **Strategic Report** tab (as admin) | Loads strategic data |
| 2 | KPI: Low/Medium/High risk counts | Displayed correctly |
| 3 | Donut chart | 3 segments: Low (green), Medium (yellow), High (red) |
| 4 | Top 5 Critical Vendors | Ranked list with risk scores |
| 5 | Login as `pm@scm.com`, try Strategic tab | Shows: "restricted to Administrators" |

---

### Test 7.3 — CSV Export
| Step | Action | Expected |
|------|--------|----------|
| 1 | With performance report loaded, click **Export CSV** | File downloads: `vendor_report_YYYY-MM-DD.csv` |
| 2 | Open CSV | Contains: ID, Name, Status, Rating, Risk Score, Delivery, Defect Rate, Compliance |

---

## 🌐 MODULE 8 — API Endpoint Tests

Test these directly in the browser or with curl:

### Test 8.1 — Health Check (OOAD: Singleton)
```
GET http://localhost:8080/api/health
```
**Expected response:**
```json
{
  "status": "running",
  "database": "MySQL",
  "strategy": "Weighted Risk Strategy (D:0.4 + DR:0.4 + C:0.2)"
}
```
> **OOAD:** `DatabaseManager.getInstance()` — **Singleton** ensures single connection

---

### Test 8.2 — Login API
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{"email": "admin@scm.com", "password": "admin123"}
```
**Expected:** `200 OK` with `token`, `name`, `role`, `email`

---

### Test 8.3 — Unauthorized Access
```
GET http://localhost:8080/api/vendor
(no Authorization header)
```
**Expected:** `401 Unauthorized — please login`

---

### Test 8.4 — Forbidden Role
```
GET http://localhost:8080/api/report/strategic
Authorization: Bearer <wm_token>
```
**Expected:** `403 Forbidden — insufficient permissions`

---

## 🎓 MODULE 9 — OOAD Pattern Verification

### Test 9.1 — Singleton Pattern (`DatabaseManager`)
| Verification | How to Check |
|-------------|--------------|
| Single DB instance | Backend console shows "[DatabaseManager] MySQL connection established" only ONCE at startup |
| No duplicate connections | Restart backend → only one connect message |
| Thread-safe | Multiple concurrent API calls work correctly |

---

### Test 9.2 — Strategy Pattern (`WeightedRiskStrategy`)
| Verification | How to Check |
|-------------|--------------|
| Algorithm pluggable | `GET /api/health` shows strategy name in response |
| Formula correct | delivery=1.0, defect=0.0, compliance=1.0 → score = 0.000 |
| Formula correct | delivery=0.0, defect=1.0, compliance=0.0 → score = 1.000 |
| Formula correct | delivery=0.5, defect=0.5, compliance=0.5 → score = 0.5 |

---

### Test 9.3 — Factory Pattern (`AlertFactory`)
| Verification | How to Check |
|-------------|--------------|
| Alert created automatically | Trigger score > 0.700 → alert appears in `/api/alert` |
| Severity = HIGH for score > 0.7 | Check alert severity field in response |
| Factory log | Backend console: `[AlertFactory] Created HIGH alert for vendor: ...` |

---

### Test 9.4 — Observer Pattern (`AlertService` + `ConsoleLogObserver`)
| Verification | How to Check |
|-------------|--------------|
| Observer registered | Backend startup: `[AlertService] Observer registered: ConsoleLogObserver` |
| Observer notified | Trigger alert → console: `[ALERT] [HIGH] Vendor#X — ...` |
| Multiple observers | Add another observer class → both notified |

---

### Test 9.5 — Builder Pattern (`ReportBuilder`)
| Verification | How to Check |
|-------------|--------------|
| Report structure | `GET /api/report/performance` → JSON has `reportType`, `generatedAt`, `statistics`, `vendors` |
| Fluent builder | `ReportService.java` chains: `.setType().addStat().setVendorSummaries().build()` |

---

### Test 9.6 — MVC Architecture
| Layer | Class | Verification |
|-------|-------|-------------|
| Controller | `VendorController` | Handles HTTP, delegates to service |
| Service | `VendorService` | Business logic only, no HTTP/SQL |
| Repository | `MySQLVendorRepository` | SQL only, no business logic |
| Model | `Vendor` | Data + behavior, no HTTP |

---

## 🔁 MODULE 10 — Full End-to-End Demo Flow

> Run this in sequence to demo all OOAD patterns in 5 minutes

| Step | Action | OOAD Pattern |
|------|--------|-------------|
| 1 | Login as `analyst@scm.com` | Polymorphism (RiskAnalyst role) |
| 2 | Dashboard → note current high-risk count | — |
| 3 | Navigate to **Risk Analysis** | — |
| 4 | Select `FastSupply Co` | — |
| 5 | Set Delivery=`0.15`, Defect=`0.85`, Compliance=`0.25` | — |
| 6 | Click **Save Evaluation** | — |
| 7 | Click **Recalculate Risk** | **Strategy** calculates score |
| 8 | Score > 0.7 → "Alert Triggered: YES" | **Factory** creates alert |
| 9 | Backend console shows ALERT log | **Observer** notified |
| 10 | Go to **Alerts** page | New HIGH alert visible |
| 11 | Click **Recommendations** | **Pure Fabrication** suggests 3 alternatives |
| 12 | Click **Resolve** | Alert marked resolved |
| 13 | Switch to `admin@scm.com` | Administrator role |
| 14 | Go to **Reports → Strategic** | **Builder** constructs report |
| 15 | Click **Export CSV** | Performance data downloaded |
| 16 | Go to **Vendors → Blacklist** FastSupply Co | RBAC enforced |
| 17 | Login as `wm@scm.com` | Warehouse Manager role |
| 18 | **Inventory** → Update low-stock item | Inventory management |

---

## 🛠️ Troubleshooting

| Issue | Cause | Fix |
|-------|-------|-----|
| "Network error" on login | Java backend not running | Start backend, check port 8080 |
| "Not Found" for HTML | Wrong port or backend not serving static files | Open `http://localhost:8080/login.html` |
| `401 Unauthorized` | Expired/missing token | Log out and log in again |
| `403 Forbidden` | Role doesn't have permission | Switch to a role with access |
| MySQL error on startup | Wrong credentials / MySQL not running | Check `DatabaseManager.java` password, run `net start MySQL80` |
| "No evaluation criteria" | Vendor has no saved evaluation | Click Save Evaluation before Recalculate |
| Charts not loading | Chart.js CDN unreachable | Check internet connection |

---

## 📋 Quick API Reference

| Method | Endpoint | Auth Required | Role |
|--------|----------|--------------|------|
| POST | `/api/auth/login` | No | — |
| POST | `/api/auth/logout` | Yes | Any |
| GET | `/api/vendor` | Yes | Any |
| POST | `/api/vendor` | Yes | ADMIN, PM |
| PUT | `/api/vendor/{id}` | Yes | ADMIN, PM |
| DELETE | `/api/vendor/{id}` | Yes | ADMIN |
| PUT | `/api/vendor/{id}/suspend` | Yes | ADMIN, PM |
| PUT | `/api/vendor/{id}/activate` | Yes | ADMIN, PM |
| PUT | `/api/vendor/{id}/blacklist` | Yes | ADMIN |
| GET | `/api/vendor/{id}/risk` | Yes | Any |
| POST | `/api/risk/calculate/{id}` | Yes | ADMIN, RA |
| POST | `/api/vendor/{id}/evaluate` | Yes | ADMIN, RA, PM |
| GET | `/api/alert` | Yes | ADMIN, PM, RA |
| PUT | `/api/alert/{id}/resolve` | Yes | ADMIN, PM, RA |
| GET | `/api/alert/{id}/recommendation` | Yes | ADMIN, PM, RA |
| GET | `/api/inventory` | Yes | ADMIN, WM, PM |
| PUT | `/api/inventory/{id}` | Yes | ADMIN, WM |
| GET | `/api/report/performance` | Yes | ADMIN, PM, RA |
| GET | `/api/report/strategic` | Yes | ADMIN only |
| GET | `/api/health` | No | — |

---

*SupplyChainOS — OOAD Mini Project | UE23CS352B | PES University*
