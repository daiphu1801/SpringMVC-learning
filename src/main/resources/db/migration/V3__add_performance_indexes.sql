-- ==========================================================================
-- Flyway Migration: V3__add_performance_indexes.sql
-- Khởi tạo các chỉ mục tối ưu hóa hiệu năng truy vấn
-- ==========================================================================

CREATE INDEX "IDX_APP_ORDERS_USER_CREATED" ON "APP_ORDERS" ("USER_ID", "CREATED_AT" DESC);
CREATE INDEX "IDX_APP_ORDER_ITEMS_ORDER_ID" ON "APP_ORDER_ITEMS" ("ORDER_ID");
CREATE INDEX "IDX_APP_USER_ADDR_USER_ID" ON "APP_USER_ADDRESSES" ("USER_ID");
CREATE INDEX "IDX_APP_PRODUCTS_CAT_ID" ON "APP_PRODUCTS" ("CATEGORY_ID", "ID" DESC);
CREATE INDEX "IDX_APP_EXCEL_TASKS_CREATED" ON "APP_EXCEL_TASKS" ("CREATED_AT" DESC);
