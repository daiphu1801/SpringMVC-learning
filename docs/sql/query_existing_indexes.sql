-- ==========================================================================
-- Utility: Query and Verify Database Indexes in Oracle
-- Run these queries to inspect your current indexes and find missing FK indexes.
-- ==========================================================================

-- 1. List all indexes in your schema and their current status
SELECT 
    table_name, 
    index_name, 
    index_type, 
    uniqueness, 
    status 
FROM user_indexes 
ORDER BY table_name, index_name;

-- 2. Detail columns associated with each index
SELECT 
    table_name, 
    index_name, 
    column_name, 
    column_position, 
    descend 
FROM user_ind_columns 
ORDER BY table_name, index_name, column_position;

-- 3. ADVANCED: Detect all Foreign Keys that are MISSING indexes
-- (Crucial for Oracle Database to avoid table-level locks and deadlocks)
SELECT 
    c.table_name,
    c.constraint_name AS fk_constraint_name,
    cc.column_name AS fk_column,
    r.table_name AS parent_table
FROM user_constraints c
JOIN user_cons_columns cc ON c.constraint_name = cc.constraint_name
JOIN user_constraints r ON c.r_constraint_name = r.constraint_name
WHERE c.constraint_type = 'R' -- Foreign Key constraints
  AND NOT EXISTS (
      -- Check if there is an index starting with the FK column
      SELECT 1 
      FROM user_ind_columns ic 
      WHERE ic.table_name = c.table_name 
        AND ic.column_name = cc.column_name
        AND ic.column_position = 1 -- FK column must be the leading column
  )
ORDER BY c.table_name, cc.column_name;
