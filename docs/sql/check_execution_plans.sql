-- ==========================================================================
-- Utility: Check SQL Execution Plans (Explain Plan) for Main Queries
-- Hướng dẫn: Bôi đen từng cặp câu lệnh (EXPLAIN PLAN và DBMS_XPLAN) 
-- rồi chạy để xem Oracle sử dụng Index như thế nào.
-- ==========================================================================

-- 1. Kiểm tra truy vấn Lịch sử đơn hàng (Lọc USER_ID, Sắp xếp CREATED_AT giảm dần)
-- Kỳ vọng: Sử dụng INDEX RANGE SCAN trên "IDX_APP_ORDERS_USER_CREATED"
EXPLAIN PLAN FOR
SELECT * FROM "APP_ORDERS"
WHERE "USER_ID" = 61
ORDER BY "CREATED_AT" DESC;

SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);


-- 2. Kiểm tra truy vấn Chi tiết sản phẩm trong đơn hàng (Lọc ORDER_ID)
-- Kỳ vọng: Sử dụng INDEX RANGE SCAN trên "IDX_APP_ORDER_ITEMS_ORDER_ID"
EXPLAIN PLAN FOR
SELECT * FROM "APP_ORDER_ITEMS"
WHERE "ORDER_ID" = 15;

SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);


-- 3. Kiểm tra truy vấn Danh sách địa chỉ của người dùng (Lọc USER_ID)
-- Kỳ vọng: Sử dụng INDEX RANGE SCAN trên "IDX_APP_USER_ADDR_USER_ID"
EXPLAIN PLAN FOR
SELECT * FROM "APP_USER_ADDRESSES"
WHERE "USER_ID" = 61;

SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);


-- 4. Kiểm tra truy vấn Sản phẩm theo Danh mục (Lọc CATEGORY_ID, Sắp xếp ID giảm dần)
-- Kỳ vọng: Sử dụng INDEX RANGE SCAN trên "IDX_APP_PRODUCTS_CAT_ID"
EXPLAIN PLAN FOR
SELECT * FROM "APP_PRODUCTS"
WHERE "CATEGORY_ID" = 2
ORDER BY "ID" DESC;

SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);


-- 5. Kiểm tra truy vấn Tác vụ Excel gần đây (Sắp xếp CREATED_AT giảm dần)
-- Kỳ vọng: Sử dụng INDEX RANGE SCAN trên "IDX_APP_EXCEL_TASKS_CREATED"
EXPLAIN PLAN FOR
SELECT * FROM (
    SELECT * FROM "APP_EXCEL_TASKS"
    ORDER BY "CREATED_AT" DESC
) WHERE ROWNUM <= 10;

SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);


-- ==========================================================================
-- BỔ SUNG: Kiểm tra tối ưu hóa trên bảng APP_USERS (Khi bảng có nhiều dòng)
-- Lưu ý: Hãy chạy lệnh thu thập thống kê dưới đây trước khi kiểm tra plan.
-- ==========================================================================

-- Lệnh thu thập lại thống kê toàn bộ schema (cần chạy khi nạp thêm dữ liệu lớn)
-- EXEC DBMS_STATS.GATHER_SCHEMA_STATS(USER);

-- 6. Kiểm tra truy vấn thông tin User theo EMAIL
-- Kỳ vọng: Sử dụng INDEX UNIQUE SCAN trên "UK_APP_USERS_EMAIL"
EXPLAIN PLAN FOR
SELECT * FROM "APP_USERS"
WHERE "EMAIL" = 'admin@example.com';

SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);


-- 7. Kiểm tra truy vấn thông tin User theo USERNAME
-- Kỳ vọng: Sử dụng INDEX UNIQUE SCAN trên "UK_APP_USERS_USERNAME"
EXPLAIN PLAN FOR
SELECT * FROM "APP_USERS"
WHERE "USERNAME" = 'admin';

SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);
