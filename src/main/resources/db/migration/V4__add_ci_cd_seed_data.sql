-- ==========================================================================
-- Flyway Migration: V4__add_ci_cd_seed_data.sql
-- Nạp dữ liệu đặc thù cho môi trường CI/CD (Tài khoản adminnn, Category, Product)
-- ==========================================================================

-- 1. Nạp tài khoản adminnn cho bước quét bảo mật OWASP ZAP trong CI/CD
INSERT INTO APP_USERS ("ID", "USERNAME", "FULL_NAME", "EMAIL", "PHONE", "STATUS", "CREATED_AT", "UPDATED_AT", "PASSWORD", "ROLE")
VALUES (
    "APP_USER_SEQ".NEXTVAL, 
    'adminnn', 
    'Admin User CI/CD', 
    'admin@example.com', 
    '0123456789', 
    'ACTIVE', 
    SYSTIMESTAMP, 
    SYSTIMESTAMP, 
    '$2a$12$TMgl6ebQ/Tvk8zLF48U0z.Yr0M3YZ5cklPuAMKg3BjR45krjYN7kW', -- Mật khẩu tương ứng với '123456'
    'ADMIN'
);

-- 2. Nạp Category và Product mẫu tương tự bước khởi tạo cũ của CI/CD
DECLARE
    v_cat_id NUMBER;
BEGIN
    SELECT "APP_CATEGORY_SEQ".NEXTVAL INTO v_cat_id FROM dual;

    INSERT INTO APP_CATEGORIES ("ID", "NAME", "CODE", "DESCRIPTION", "CREATED_AT", "UPDATED_AT")
    VALUES (v_cat_id, 'Electronics', 'ELEC', 'Electronic devices', SYSTIMESTAMP, SYSTIMESTAMP);

    INSERT INTO APP_PRODUCTS ("ID", "CATEGORY_ID", "SKU", "NAME", "DESCRIPTION", "PRICE", "STATUS", "CREATED_AT", "UPDATED_AT", "IMAGE_URL", "STOCK")
    VALUES ("APP_PRODUCT_SEQ".NEXTVAL, v_cat_id, 'PROD-001', 'Smart Phone', 'High-end smartphone', 999.99, 'ACTIVE', SYSTIMESTAMP, SYSTIMESTAMP, NULL, 50);
END;
/
