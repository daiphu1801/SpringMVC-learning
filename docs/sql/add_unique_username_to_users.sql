-- ==========================================================================
-- Migration: Add UNIQUE constraint to USERNAME column of APP_USERS table
-- Run this on your Oracle database before starting the application
-- ==========================================================================

ALTER TABLE APP_USERS ADD CONSTRAINT UQ_APP_USERS_USERNAME UNIQUE (USERNAME);
