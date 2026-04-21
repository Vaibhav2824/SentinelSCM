-- =====================================================
-- Smart Supply Chain Disruption & Vendor Risk Management
-- OOAD Mini Project | PES University UE23CS352B
-- Database: MySQL 8.x
-- Run: mysql -u root -p < db/schema.sql
-- =====================================================

CREATE DATABASE IF NOT EXISTS scm_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE scm_db;

-- Drop tables in reverse dependency order (for clean re-init)
DROP TABLE IF EXISTS recommendations;
DROP TABLE IF EXISTS alerts;
DROP TABLE IF EXISTS risk_scores;
DROP TABLE IF EXISTS risk_rules;
DROP TABLE IF EXISTS purchase_orders;
DROP TABLE IF EXISTS evaluation_criteria;
DROP TABLE IF EXISTS inventory;
DROP TABLE IF EXISTS vendors;
DROP TABLE IF EXISTS users;

-- ===========================
-- USERS TABLE
-- Abstraction: maps to abstract User class + 5 subclasses
-- ===========================
CREATE TABLE users (
    user_id    INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role       ENUM('ADMIN','PROCUREMENT_MANAGER','RISK_ANALYST','WAREHOUSE_MANAGER','VENDOR') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===========================
-- VENDORS TABLE
-- Composition: Vendor -> RiskScore, PurchaseOrder
-- ===========================
CREATE TABLE vendors (
    vendor_id  INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(150) NOT NULL,
    contact    VARCHAR(200),
    rating     DECIMAL(3,2) DEFAULT 0.00,
    risk_score DECIMAL(4,3) DEFAULT 0.000,
    status     ENUM('ACTIVE','PENDING','HIGH_RISK','SUSPENDED','BLACKLISTED','INACTIVE') DEFAULT 'PENDING',
    user_id    INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
);

-- ===========================
-- EVALUATION CRITERIA TABLE
-- Strategy Pattern input: delivery_timeliness, defect_rate, compliance_score
-- ===========================
CREATE TABLE evaluation_criteria (
    criteria_id          INT AUTO_INCREMENT PRIMARY KEY,
    vendor_id            INT NOT NULL,
    delivery_timeliness  DECIMAL(4,3) DEFAULT 1.000,
    defect_rate          DECIMAL(4,3) DEFAULT 0.000,
    compliance_score     DECIMAL(4,3) DEFAULT 1.000,
    evaluated_date       DATE,
    FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id) ON DELETE CASCADE
);

-- ===========================
-- PURCHASE ORDERS TABLE
-- Composition: PurchaseOrder -> Vendor
-- ===========================
CREATE TABLE purchase_orders (
    po_id      INT AUTO_INCREMENT PRIMARY KEY,
    vendor_id  INT NOT NULL,
    quantity   INT DEFAULT 0,
    date       DATE,
    status     ENUM('PENDING','DELIVERED','DELAYED','CANCELLED') DEFAULT 'PENDING',
    FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id) ON DELETE CASCADE
);

-- ===========================
-- RISK SCORES TABLE
-- Information Expert: RiskService calculates & stores scores
-- ===========================
CREATE TABLE risk_scores (
    score_id         INT AUTO_INCREMENT PRIMARY KEY,
    vendor_id        INT NOT NULL,
    score            DECIMAL(5,4) NOT NULL,
    calculated_date  DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id) ON DELETE CASCADE
);

-- ===========================
-- ALERTS TABLE
-- Factory Pattern: AlertFactory creates HIGH_RISK alerts
-- Observer Pattern: AlertService notifies observers
-- ===========================
CREATE TABLE alerts (
    alert_id   INT AUTO_INCREMENT PRIMARY KEY,
    vendor_id  INT NOT NULL,
    message    TEXT,
    timestamp  DATETIME DEFAULT CURRENT_TIMESTAMP,
    severity   ENUM('LOW','MEDIUM','HIGH') DEFAULT 'MEDIUM',
    resolved   TINYINT(1) DEFAULT 0,
    FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id) ON DELETE CASCADE
);

-- ===========================
-- RECOMMENDATIONS TABLE
-- Pure Fabrication: RecommendationService generates suggestions
-- ===========================
CREATE TABLE recommendations (
    rec_id             INT AUTO_INCREMENT PRIMARY KEY,
    alert_id           INT NOT NULL,
    suggested_vendor_id INT NOT NULL,
    reason             TEXT,
    FOREIGN KEY (alert_id) REFERENCES alerts(alert_id) ON DELETE CASCADE,
    FOREIGN KEY (suggested_vendor_id) REFERENCES vendors(vendor_id) ON DELETE CASCADE
);

-- ===========================
-- INVENTORY TABLE
-- Tracked by WarehouseManager role
-- ===========================
CREATE TABLE inventory (
    item_id      INT AUTO_INCREMENT PRIMARY KEY,
    item_name    VARCHAR(200) NOT NULL,
    quantity     INT DEFAULT 0,
    last_updated DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ===========================
-- RISK RULES TABLE
-- Protected Variations: threshold configurable at runtime
-- ===========================
CREATE TABLE risk_rules (
    rule_id    INT AUTO_INCREMENT PRIMARY KEY,
    threshold  DECIMAL(4,3) DEFAULT 0.700,
    condition_text VARCHAR(100) DEFAULT 'score > threshold',
    created_by INT NULL,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL
);

-- ===========================
-- INDEXES for Performance
-- ===========================
CREATE INDEX idx_vendors_status ON vendors(status);
CREATE INDEX idx_vendors_risk   ON vendors(risk_score);
CREATE INDEX idx_alerts_vendor  ON alerts(vendor_id);
CREATE INDEX idx_alerts_resolved ON alerts(resolved);
CREATE INDEX idx_risk_scores_vendor ON risk_scores(vendor_id);
CREATE INDEX idx_criteria_vendor ON evaluation_criteria(vendor_id);
CREATE INDEX idx_po_vendor ON purchase_orders(vendor_id);
