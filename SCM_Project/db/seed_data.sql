-- =====================================================
-- Smart Supply Chain - Seed Data
-- Run AFTER schema.sql: mysql -u root -p scm_db < db/seed_data.sql
-- BCrypt hashes pre-computed for demo passwords
-- =====================================================

USE scm_db;

-- Clear existing data (for re-seeding)
DELETE FROM recommendations;
DELETE FROM alerts;
DELETE FROM risk_scores;
DELETE FROM risk_rules;
DELETE FROM purchase_orders;
DELETE FROM evaluation_criteria;
DELETE FROM inventory;
DELETE FROM vendors;
DELETE FROM users;

-- Reset auto-increment counters
ALTER TABLE users AUTO_INCREMENT = 1;
ALTER TABLE vendors AUTO_INCREMENT = 1;
ALTER TABLE evaluation_criteria AUTO_INCREMENT = 1;
ALTER TABLE purchase_orders AUTO_INCREMENT = 1;
ALTER TABLE risk_scores AUTO_INCREMENT = 1;
ALTER TABLE alerts AUTO_INCREMENT = 1;
ALTER TABLE recommendations AUTO_INCREMENT = 1;
ALTER TABLE inventory AUTO_INCREMENT = 1;
ALTER TABLE risk_rules AUTO_INCREMENT = 1;

-- ===========================
-- USERS (5 roles)
-- Passwords hashed with BCrypt cost=12
-- admin123 / pm123 / analyst123 / wm123 / vendor123
-- ===========================
INSERT INTO users (name, email, password_hash, role) VALUES
('System Admin',      'admin@scm.com',    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj2NJjKIoX9G', 'ADMIN'),
('Priya Sharma',      'pm@scm.com',       '$2a$12$NhqBJoHxNm4vLuRy5DCTsehN3Q/3fN5H3U7e1SJq0VN4lZCf4tGMa', 'PROCUREMENT_MANAGER'),
('Arjun Mehta',       'analyst@scm.com',  '$2a$12$1fqE0NKf3W8oqXBiK1R3leEhMGnxqVP5f8BVsM1VoEVB5ZIyHZJqe', 'RISK_ANALYST'),
('Rajesh Kumar',      'wm@scm.com',       '$2a$12$K8SzU6R1X4NeTxcWqJGJn.n5DLRqlPAh5DxJLJ4e1/L8R1LMO6l62', 'WAREHOUSE_MANAGER'),
('Vendor Portal',     'vendor@scm.com',   '$2a$12$WJi7OzFB9Bp4eJVXbKGkuO8zy.v2K2LV8MO4E2a4.UYpxM0JzGI9G', 'VENDOR');

-- ===========================
-- VENDORS (12 realistic vendors)
-- Mix of HIGH_RISK, MEDIUM, LOW, PENDING
-- ===========================
INSERT INTO vendors (name, contact, rating, risk_score, status) VALUES
('QuickParts Ltd',         'procurement@quickparts.in',      4.50, 0.120, 'ACTIVE'),
('FastSupply Co',          'orders@fastsupply.com',          2.10, 0.780, 'HIGH_RISK'),
('ReliableGoods Intl',     'sales@reliablegoods.com',        4.90, 0.040, 'ACTIVE'),
('BudgetMaterials Inc',    'info@budgetmaterials.in',        3.20, 0.520, 'ACTIVE'),
('GlobalProcure Inc',      'sourcing@globalprocure.com',     1.50, 0.850, 'HIGH_RISK'),
('MetalWorks India',       'support@metalworks.in',          4.20, 0.180, 'ACTIVE'),
('EcoPlast Solutions',     'hello@ecoplast.co',              3.80, 0.310, 'PENDING'),
('TechComponents AG',      'sales@techcomp.de',              4.00, 0.220, 'ACTIVE'),
('Micro-Chip Logistics',   'ops@microchip-logi.com',         1.80, 0.820, 'HIGH_RISK'),
('Prime Materials Co',     'prime@primematerials.com',       4.60, 0.090, 'ACTIVE'),
('NexusBuild Supplies',    'contact@nexusbuild.in',          3.50, 0.440, 'ACTIVE'),
('SteelForge Partners',    'forge@steelforge.com',           4.10, 0.160, 'ACTIVE');

-- ===========================
-- EVALUATION CRITERIA (historical evaluations)
-- Formula: risk = (1-delivery)*0.4 + defect*0.4 + (1-compliance)*0.2
-- ===========================
INSERT INTO evaluation_criteria (vendor_id, delivery_timeliness, defect_rate, compliance_score, evaluated_date) VALUES
-- QuickParts (low risk, stable)
(1, 0.88, 0.08, 0.92, DATE_SUB(CURDATE(), INTERVAL 45 DAY)),
(1, 0.92, 0.05, 0.95, DATE_SUB(CURDATE(), INTERVAL 15 DAY)),
-- FastSupply (high risk, declining)
(2, 0.50, 0.30, 0.65, DATE_SUB(CURDATE(), INTERVAL 40 DAY)),
(2, 0.45, 0.35, 0.60, DATE_SUB(CURDATE(), INTERVAL 10 DAY)),
-- ReliableGoods (excellent)
(3, 0.97, 0.02, 0.98, DATE_SUB(CURDATE(), INTERVAL 30 DAY)),
(3, 0.98, 0.01, 0.99, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
-- BudgetMaterials (medium risk, fluctuating)
(4, 0.72, 0.18, 0.78, DATE_SUB(CURDATE(), INTERVAL 25 DAY)),
(4, 0.70, 0.20, 0.75, DATE_SUB(CURDATE(), INTERVAL 8 DAY)),
-- GlobalProcure (critical, worsening)
(5, 0.35, 0.40, 0.55, DATE_SUB(CURDATE(), INTERVAL 35 DAY)),
(5, 0.30, 0.45, 0.50, DATE_SUB(CURDATE(), INTERVAL 7 DAY)),
-- MetalWorks (low risk)
(6, 0.90, 0.06, 0.93, DATE_SUB(CURDATE(), INTERVAL 20 DAY)),
(6, 0.88, 0.07, 0.90, DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
-- EcoPlast (moderate, new vendor)
(7, 0.82, 0.12, 0.85, DATE_SUB(CURDATE(), INTERVAL 14 DAY)),
-- TechComponents (stable)
(8, 0.87, 0.09, 0.91, DATE_SUB(CURDATE(), INTERVAL 12 DAY)),
(8, 0.89, 0.07, 0.93, DATE_SUB(CURDATE(), INTERVAL 2 DAY)),
-- Micro-Chip (critical)
(9, 0.28, 0.48, 0.45, DATE_SUB(CURDATE(), INTERVAL 9 DAY)),
-- Prime Materials (excellent)
(10, 0.96, 0.02, 0.97, DATE_SUB(CURDATE(), INTERVAL 6 DAY)),
-- NexusBuild (medium)
(11, 0.75, 0.15, 0.80, DATE_SUB(CURDATE(), INTERVAL 11 DAY)),
-- SteelForge (low risk)
(12, 0.91, 0.05, 0.94, DATE_SUB(CURDATE(), INTERVAL 4 DAY));

-- ===========================
-- PURCHASE ORDERS (35 orders - realistic history)
-- ===========================
INSERT INTO purchase_orders (vendor_id, quantity, date, status) VALUES
-- QuickParts
(1, 500,  DATE_SUB(CURDATE(), INTERVAL 60 DAY), 'DELIVERED'),
(1, 750,  DATE_SUB(CURDATE(), INTERVAL 30 DAY), 'DELIVERED'),
(1, 300,  DATE_SUB(CURDATE(), INTERVAL 15 DAY), 'DELIVERED'),
(1, 420,  DATE_SUB(CURDATE(), INTERVAL 3 DAY),  'PENDING'),
-- FastSupply
(2, 200,  DATE_SUB(CURDATE(), INTERVAL 50 DAY), 'DELAYED'),
(2, 150,  DATE_SUB(CURDATE(), INTERVAL 25 DAY), 'DELAYED'),
(2, 100,  DATE_SUB(CURDATE(), INTERVAL 10 DAY), 'PENDING'),
-- ReliableGoods
(3, 1000, DATE_SUB(CURDATE(), INTERVAL 45 DAY), 'DELIVERED'),
(3, 800,  DATE_SUB(CURDATE(), INTERVAL 20 DAY), 'DELIVERED'),
(3, 1200, DATE_SUB(CURDATE(), INTERVAL 5 DAY),  'DELIVERED'),
-- BudgetMaterials
(4, 400,  DATE_SUB(CURDATE(), INTERVAL 40 DAY), 'DELIVERED'),
(4, 250,  DATE_SUB(CURDATE(), INTERVAL 18 DAY), 'DELAYED'),
(4, 350,  DATE_SUB(CURDATE(), INTERVAL 4 DAY),  'PENDING'),
-- GlobalProcure
(5, 100,  DATE_SUB(CURDATE(), INTERVAL 55 DAY), 'CANCELLED'),
(5, 350,  DATE_SUB(CURDATE(), INTERVAL 22 DAY), 'DELAYED'),
(5, 80,   DATE_SUB(CURDATE(), INTERVAL 8 DAY),  'CANCELLED'),
-- MetalWorks
(6, 600,  DATE_SUB(CURDATE(), INTERVAL 35 DAY), 'DELIVERED'),
(6, 450,  DATE_SUB(CURDATE(), INTERVAL 12 DAY), 'DELIVERED'),
(6, 300,  DATE_SUB(CURDATE(), INTERVAL 2 DAY),  'PENDING'),
-- EcoPlast
(7, 300,  DATE_SUB(CURDATE(), INTERVAL 28 DAY), 'DELIVERED'),
(7, 200,  DATE_SUB(CURDATE(), INTERVAL 6 DAY),  'PENDING'),
-- TechComponents
(8, 550,  DATE_SUB(CURDATE(), INTERVAL 32 DAY), 'DELIVERED'),
(8, 400,  DATE_SUB(CURDATE(), INTERVAL 14 DAY), 'DELIVERED'),
(8, 250,  DATE_SUB(CURDATE(), INTERVAL 1 DAY),  'PENDING'),
-- Micro-Chip
(9, 1500, DATE_SUB(CURDATE(), INTERVAL 20 DAY), 'DELAYED'),
(9, 1200, DATE_SUB(CURDATE(), INTERVAL 7 DAY),  'DELAYED'),
-- Prime Materials
(10, 600, DATE_SUB(CURDATE(), INTERVAL 25 DAY), 'DELIVERED'),
(10, 450, DATE_SUB(CURDATE(), INTERVAL 10 DAY), 'DELIVERED'),
(10, 200, DATE_SUB(CURDATE(), INTERVAL 2 DAY),  'PENDING'),
-- NexusBuild
(11, 380, DATE_SUB(CURDATE(), INTERVAL 30 DAY), 'DELIVERED'),
(11, 290, DATE_SUB(CURDATE(), INTERVAL 12 DAY), 'DELIVERED'),
-- SteelForge
(12, 700, DATE_SUB(CURDATE(), INTERVAL 28 DAY), 'DELIVERED'),
(12, 500, DATE_SUB(CURDATE(), INTERVAL 9 DAY),  'DELIVERED'),
(12, 350, DATE_SUB(CURDATE(), INTERVAL 1 DAY),  'PENDING');

-- ===========================
-- RISK SCORES (historical trend per vendor)
-- ===========================
INSERT INTO risk_scores (vendor_id, score, calculated_date) VALUES
-- QuickParts: stable low
(1, 0.180, DATE_SUB(NOW(), INTERVAL 90 DAY)),
(1, 0.150, DATE_SUB(NOW(), INTERVAL 60 DAY)),
(1, 0.140, DATE_SUB(NOW(), INTERVAL 30 DAY)),
(1, 0.120, DATE_SUB(NOW(), INTERVAL 7 DAY)),
-- FastSupply: rising risk
(2, 0.550, DATE_SUB(NOW(), INTERVAL 90 DAY)),
(2, 0.620, DATE_SUB(NOW(), INTERVAL 60 DAY)),
(2, 0.720, DATE_SUB(NOW(), INTERVAL 30 DAY)),
(2, 0.780, DATE_SUB(NOW(), INTERVAL 7 DAY)),
-- ReliableGoods: excellent
(3, 0.060, DATE_SUB(NOW(), INTERVAL 90 DAY)),
(3, 0.050, DATE_SUB(NOW(), INTERVAL 60 DAY)),
(3, 0.050, DATE_SUB(NOW(), INTERVAL 30 DAY)),
(3, 0.040, DATE_SUB(NOW(), INTERVAL 7 DAY)),
-- BudgetMaterials: fluctuating
(4, 0.400, DATE_SUB(NOW(), INTERVAL 90 DAY)),
(4, 0.480, DATE_SUB(NOW(), INTERVAL 60 DAY)),
(4, 0.550, DATE_SUB(NOW(), INTERVAL 30 DAY)),
(4, 0.520, DATE_SUB(NOW(), INTERVAL 7 DAY)),
-- GlobalProcure: critical
(5, 0.700, DATE_SUB(NOW(), INTERVAL 90 DAY)),
(5, 0.750, DATE_SUB(NOW(), INTERVAL 60 DAY)),
(5, 0.800, DATE_SUB(NOW(), INTERVAL 30 DAY)),
(5, 0.850, DATE_SUB(NOW(), INTERVAL 7 DAY)),
-- MetalWorks: low risk
(6, 0.220, DATE_SUB(NOW(), INTERVAL 60 DAY)),
(6, 0.200, DATE_SUB(NOW(), INTERVAL 30 DAY)),
(6, 0.180, DATE_SUB(NOW(), INTERVAL 7 DAY)),
-- EcoPlast: new, moderate
(7, 0.350, DATE_SUB(NOW(), INTERVAL 30 DAY)),
(7, 0.310, DATE_SUB(NOW(), INTERVAL 7 DAY)),
-- TechComponents: stable
(8, 0.250, DATE_SUB(NOW(), INTERVAL 60 DAY)),
(8, 0.220, DATE_SUB(NOW(), INTERVAL 7 DAY)),
-- Micro-Chip: critical rising
(9, 0.700, DATE_SUB(NOW(), INTERVAL 60 DAY)),
(9, 0.750, DATE_SUB(NOW(), INTERVAL 30 DAY)),
(9, 0.820, DATE_SUB(NOW(), INTERVAL 7 DAY)),
-- Prime Materials: excellent
(10, 0.100, DATE_SUB(NOW(), INTERVAL 30 DAY)),
(10, 0.090, DATE_SUB(NOW(), INTERVAL 7 DAY)),
-- NexusBuild: moderate
(11, 0.480, DATE_SUB(NOW(), INTERVAL 30 DAY)),
(11, 0.440, DATE_SUB(NOW(), INTERVAL 7 DAY)),
-- SteelForge: low risk
(12, 0.180, DATE_SUB(NOW(), INTERVAL 30 DAY)),
(12, 0.160, DATE_SUB(NOW(), INTERVAL 7 DAY));

-- ===========================
-- ALERTS (mix of severities, some resolved)
-- ===========================
INSERT INTO alerts (vendor_id, message, severity, resolved, timestamp) VALUES
(2, 'HIGH RISK: FastSupply Co risk score (0.78) exceeds threshold (0.70). Persistent delivery delays detected in last 3 purchase orders. Immediate action required.', 'HIGH', 0, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(5, 'CRITICAL: GlobalProcure Inc risk score (0.85) significantly exceeds safety threshold. 2 cancelled orders in 60 days. Recommend vendor suspension.', 'HIGH', 0, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(9, 'CRITICAL: Micro-Chip Logistics risk score (0.82) at critical level. Severe weather disruption at Port of Shanghai. 14,000 units at risk.', 'HIGH', 0, DATE_SUB(NOW(), INTERVAL 12 HOUR)),
(4, 'WARNING: BudgetMaterials Inc risk trending upward (0.52). Defect rate increased by 4% this quarter. Monitor closely.', 'MEDIUM', 0, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(11, 'NOTICE: NexusBuild Supplies compliance documentation expiring in 30 days. ISO 9001 renewal required.', 'LOW', 0, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2, 'RESOLVED: FastSupply Co compliance review completed. Corrective action plan accepted. Risk score improved to 0.62.', 'MEDIUM', 1, DATE_SUB(NOW(), INTERVAL 14 DAY)),
(7, 'NOTICE: EcoPlast Solutions is a new vendor under evaluation. Performance tracking initiated.', 'LOW', 1, DATE_SUB(NOW(), INTERVAL 20 DAY));

-- ===========================
-- RECOMMENDATIONS (alternatives for high-risk alerts)
-- ===========================
INSERT INTO recommendations (alert_id, suggested_vendor_id, reason) VALUES
-- Alert 1: FastSupply → suggest alternatives
(1, 3, 'ReliableGoods Intl — risk score 0.04, 98% on-time delivery, ISO 9001 certified. Best-in-class replacement.'),
(1, 6, 'MetalWorks India — risk score 0.18, strong local presence, competitive pricing, 4.2★ rated.'),
(1, 10, 'Prime Materials Co — risk score 0.09, 96% delivery timeliness, excellent compliance record.'),
-- Alert 2: GlobalProcure → suggest alternatives
(2, 1, 'QuickParts Ltd — risk score 0.12, 92% delivery timeliness, 4.5★ rated. Proven track record.'),
(2, 3, 'ReliableGoods Intl — risk score 0.04, best-in-class vendor for immediate substitution.'),
(2, 8, 'TechComponents AG — risk score 0.22, EU-certified, scalable capacity, stable supply chain.'),
-- Alert 3: Micro-Chip → suggest alternatives
(3, 10, 'Prime Materials Co — domestic supplier, can fulfill 100% volume via expedited air freight. ETA: 36 hrs.'),
(3, 12, 'SteelForge Partners — regional distributor, risk score 0.16, 91% on-time delivery, competitive.'),
-- Alert 4: BudgetMaterials → suggest alternatives
(4, 1, 'QuickParts Ltd — proven track record, risk score 0.12, better defect rate management.');

-- ===========================
-- INVENTORY (12 items - mix of stock levels)
-- ===========================
INSERT INTO inventory (item_name, quantity) VALUES
('Steel Bolts (M10)',          5000),
('Copper Wire (500m)',          120),
('PCB Circuit Boards',          800),
('Rubber Gaskets (O-ring)',    3500),
('Aluminum Sheets (2mm)',       200),
('LED Display Modules',          15),   -- LOW STOCK
('Plastic Casings (ABS)',        10),   -- LOW STOCK
('Stainless Steel Flanges',    1200),
('Silicon Wafers (200mm)',       45),   -- LOW STOCK
('Carbon Fiber Sheets',         280),
('Logic Controllers (Unit-A)', 1400),
('Hydraulic Seals (Pack-50)',    30);   -- LOW STOCK

-- ===========================
-- RISK RULES
-- Configurable threshold (Protected Variations pattern)
-- ===========================
INSERT INTO risk_rules (threshold, condition_text, created_by) VALUES
(0.700, 'score > threshold → create HIGH_RISK alert + generate recommendations', 1);
