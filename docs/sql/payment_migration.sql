-- ==========================================================================
-- Payment Migration: Add PAYMENT_METHOD and PAYMENT_STATUS columns
-- Run this on your Oracle database before starting the application
-- ==========================================================================

ALTER TABLE APP_ORDERS ADD (
    PAYMENT_METHOD  VARCHAR2(20) DEFAULT 'CASH'    NOT NULL,
    PAYMENT_STATUS  VARCHAR2(20) DEFAULT 'PENDING'  NOT NULL
);

-- Optional: verify
-- DESCRIBE APP_ORDERS;
