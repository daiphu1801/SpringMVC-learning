-- ==========================================================================
-- Flyway Migration: V6__add_functions.sql
-- Thêm các Database Functions tối ưu hóa cho ứng dụng
-- ==========================================================================

-- 1. Hàm nối địa chỉ đầy đủ (Scalar Function)
CREATE OR REPLACE FUNCTION FUNC_GET_FULL_ADDRESS (
    p_street_detail IN VARCHAR2,
    p_ward          IN VARCHAR2,
    p_district      IN VARCHAR2,
    p_province      IN VARCHAR2
) RETURN VARCHAR2 DETERMINISTIC 
AS
    v_full_address VARCHAR2(600);
BEGIN
    v_full_address := p_street_detail;
    
    IF p_ward IS NOT NULL AND TRIM(p_ward) IS NOT NULL THEN
        v_full_address := v_full_address || ', ' || p_ward;
    END IF;
    
    IF p_district IS NOT NULL AND TRIM(p_district) IS NOT NULL THEN
        v_full_address := v_full_address || ', ' || p_district;
    END IF;
    
    IF p_province IS NOT NULL AND TRIM(p_province) IS NOT NULL THEN
        v_full_address := v_full_address || ', ' || p_province;
    END IF;
    
    RETURN TRIM(v_full_address);
END;
/

-- 2. Hàm tính tổng chi tiêu tích lũy của khách hàng (Scalar Function)
CREATE OR REPLACE FUNCTION FUNC_CALC_USER_TOTAL_SPENT (
    p_user_id IN NUMBER
) RETURN NUMBER 
AS
    v_total NUMBER(19,2) := 0;
BEGIN
    SELECT COALESCE(SUM("TOTAL_AMOUNT"), 0)
    INTO v_total
    FROM "APP_ORDERS"
    WHERE "USER_ID" = p_user_id
      AND "STATUS" = 'DELIVERED';
      
    RETURN v_total;
END;
/
