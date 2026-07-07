-- ==========================================================================
-- Flyway Migration: V5__add_triggers.sql
-- Tạo các Triggers tự động hóa dữ liệu dưới Database (Oracle)
-- ==========================================================================

-- 1. Trigger cho bảng APP_USERS
CREATE OR REPLACE TRIGGER "TRG_USERS_UPDATED_AT"
BEFORE UPDATE ON "APP_USERS"
FOR EACH ROW
BEGIN
    :NEW."UPDATED_AT" := SYSTIMESTAMP;
END;
/

-- 2. Trigger cho bảng APP_CATEGORIES
CREATE OR REPLACE TRIGGER "TRG_CATEGORIES_UPDATED_AT"
BEFORE UPDATE ON "APP_CATEGORIES"
FOR EACH ROW
BEGIN
    :NEW."UPDATED_AT" := SYSTIMESTAMP;
END;
/

-- 3. Trigger cho bảng APP_PRODUCTS
CREATE OR REPLACE TRIGGER "TRG_PRODUCTS_UPDATED_AT"
BEFORE UPDATE ON "APP_PRODUCTS"
FOR EACH ROW
BEGIN
    :NEW."UPDATED_AT" := SYSTIMESTAMP;
END;
/

-- 4. Trigger cho bảng APP_ORDERS
CREATE OR REPLACE TRIGGER "TRG_ORDERS_UPDATED_AT"
BEFORE UPDATE ON "APP_ORDERS"
FOR EACH ROW
BEGIN
    :NEW."UPDATED_AT" := SYSTIMESTAMP;
END;
/

-- 5. Trigger cho bảng APP_EXCEL_TASKS
CREATE OR REPLACE TRIGGER "TRG_EXCEL_TASKS_UPDATED_AT"
BEFORE UPDATE ON "APP_EXCEL_TASKS"
FOR EACH ROW
BEGIN
    :NEW."UPDATED_AT" := SYSTIMESTAMP;
END;
/

-- 6. Compound Trigger cho APP_USER_ADDRESSES: Đảm bảo chỉ có tối đa 1 địa chỉ mặc định (IS_DEFAULT = 1)
-- Sử dụng Compound Trigger để tránh lỗi Mutating Table (ORA-04091) trong Oracle khi cập nhật chính bảng đang trigger
CREATE OR REPLACE TRIGGER "TRG_USER_ADDRESS_DEFAULT"
FOR INSERT OR UPDATE OF "IS_DEFAULT" ON "APP_USER_ADDRESSES"
COMPOUND TRIGGER

    TYPE t_address_rec IS RECORD (
        user_id     "APP_USER_ADDRESSES"."USER_ID"%TYPE,
        address_id  "APP_USER_ADDRESSES"."ID"%TYPE
    );
    TYPE t_addresses IS TABLE OF t_address_rec INDEX BY PLS_INTEGER;
    g_addresses  t_addresses;
    g_count      PLS_INTEGER := 0;

    BEFORE STATEMENT IS
    BEGIN
        g_addresses.DELETE;
        g_count := 0;
    END BEFORE STATEMENT;

    AFTER EACH ROW IS
    BEGIN
        -- Nếu dòng hiện tại được set là mặc định (IS_DEFAULT = 1)
        IF :NEW."IS_DEFAULT" = 1 THEN
            g_count := g_count + 1;
            g_addresses(g_count).user_id := :NEW."USER_ID";
            g_addresses(g_count).address_id := :NEW."ID";
        END IF;
    END AFTER EACH ROW;

    AFTER STATEMENT IS
    BEGIN
        -- Duyệt qua danh sách và cập nhật các địa chỉ cũ khác của cùng User về 0
        IF g_count > 0 THEN
            FOR i IN 1..g_count LOOP
                UPDATE "APP_USER_ADDRESSES"
                SET "IS_DEFAULT" = 0
                WHERE "USER_ID" = g_addresses(i).user_id
                  AND "ID" <> g_addresses(i).address_id
                  AND "IS_DEFAULT" = 1;
            END LOOP;
        END IF;
    END AFTER STATEMENT;
END;
/
