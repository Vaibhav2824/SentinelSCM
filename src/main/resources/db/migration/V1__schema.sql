-- =====================================================
-- SentinelSCM baseline schema (Flyway V1)
-- Enum-like columns are VARCHAR so JPA @Enumerated(STRING) validates cleanly.
-- =====================================================

CREATE TABLE users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(32)  NOT NULL,
    created_at    DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
);

CREATE TABLE vendors (
    vendor_id  INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(150) NOT NULL,
    contact    VARCHAR(200),
    rating     DOUBLE NOT NULL DEFAULT 0,
    risk_score DOUBLE NOT NULL DEFAULT 0,
    status     VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    user_id    INT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_vendors_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
);

CREATE TABLE evaluation_criteria (
    criteria_id         INT AUTO_INCREMENT PRIMARY KEY,
    vendor_id           INT NOT NULL,
    delivery_timeliness DOUBLE NOT NULL DEFAULT 1,
    defect_rate         DOUBLE NOT NULL DEFAULT 0,
    compliance_score    DOUBLE NOT NULL DEFAULT 1,
    evaluated_date      DATE NOT NULL,
    CONSTRAINT fk_criteria_vendor FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id) ON DELETE CASCADE
);

CREATE TABLE purchase_orders (
    po_id      INT AUTO_INCREMENT PRIMARY KEY,
    vendor_id  INT NOT NULL,
    quantity   INT NOT NULL DEFAULT 0,
    order_date DATE NOT NULL,
    status     VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    CONSTRAINT fk_po_vendor FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id) ON DELETE CASCADE
);

CREATE TABLE risk_scores (
    score_id        INT AUTO_INCREMENT PRIMARY KEY,
    vendor_id       INT NOT NULL,
    score           DOUBLE NOT NULL,
    calculated_date DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_risk_vendor FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id) ON DELETE CASCADE
);

CREATE TABLE alerts (
    alert_id   INT AUTO_INCREMENT PRIMARY KEY,
    vendor_id  INT NOT NULL,
    message    TEXT,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    severity   VARCHAR(16) NOT NULL DEFAULT 'MEDIUM',
    resolved   BIT(1) NOT NULL DEFAULT 0,
    CONSTRAINT fk_alert_vendor FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id) ON DELETE CASCADE
);

CREATE TABLE recommendations (
    rec_id              INT AUTO_INCREMENT PRIMARY KEY,
    alert_id            INT NOT NULL,
    suggested_vendor_id INT NOT NULL,
    reason              TEXT,
    CONSTRAINT fk_rec_alert  FOREIGN KEY (alert_id) REFERENCES alerts(alert_id) ON DELETE CASCADE,
    CONSTRAINT fk_rec_vendor FOREIGN KEY (suggested_vendor_id) REFERENCES vendors(vendor_id) ON DELETE CASCADE
);

CREATE TABLE inventory (
    item_id      INT AUTO_INCREMENT PRIMARY KEY,
    item_name    VARCHAR(200) NOT NULL,
    quantity     INT NOT NULL DEFAULT 0,
    last_updated DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

CREATE TABLE risk_rules (
    rule_id        INT AUTO_INCREMENT PRIMARY KEY,
    threshold      DOUBLE NOT NULL DEFAULT 0.7,
    condition_text VARCHAR(100) NOT NULL DEFAULT 'score > threshold',
    created_by     INT NULL,
    CONSTRAINT fk_rule_user FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL
);

CREATE INDEX idx_vendors_status       ON vendors(status);
CREATE INDEX idx_vendors_risk         ON vendors(risk_score);
CREATE INDEX idx_alerts_vendor        ON alerts(vendor_id);
CREATE INDEX idx_alerts_resolved      ON alerts(resolved);
CREATE INDEX idx_risk_scores_vendor   ON risk_scores(vendor_id);
CREATE INDEX idx_criteria_vendor      ON evaluation_criteria(vendor_id);
CREATE INDEX idx_po_vendor            ON purchase_orders(vendor_id);
