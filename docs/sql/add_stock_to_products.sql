-- ==========================================================================
-- Product Stock Migration: Add STOCK column to APP_PRODUCTS table
-- Run this on your Oracle database before starting the application
-- ==========================================================================

ALTER TABLE APP_PRODUCTS ADD (
    STOCK NUMBER(10) DEFAULT 100 NOT NULL
);

-- Optional: verify
-- DESCRIBE APP_PRODUCTS;
