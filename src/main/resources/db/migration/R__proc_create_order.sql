CREATE OR REPLACE PROCEDURE PROC_CREATE_ORDER (
    p_user_id          IN  NUMBER,
    p_receiver_name    IN  VARCHAR2,
    p_receiver_phone   IN  VARCHAR2,
    p_shipping_address IN  VARCHAR2,
    p_note             IN  VARCHAR2,
    p_payment_method   IN  VARCHAR2,
    p_items_csv        IN  VARCHAR2, 
    p_order_id         OUT NUMBER,
    p_status_code      OUT VARCHAR2,
    p_message          OUT VARCHAR2
) AS
    v_total_amount    NUMBER(19,2) := 0;
    v_order_id        NUMBER;
    v_prod_id         NUMBER;
    v_qty             NUMBER;
    v_stock           NUMBER;
    v_price           NUMBER;
    v_sku             VARCHAR2(50);
    v_name            VARCHAR2(150);
    v_subtotal        NUMBER(19,2);
BEGIN
    -- [MẪU DEMO]: Giả lập parse sản phẩm ID = 3, số lượng = 2
    -- Trong thực tế, bạn sẽ parse chuỗi p_items_csv hoặc truyền một Collection Object
    v_prod_id := 3;
    v_qty := 2;
    
    -- Khóa bản ghi sản phẩm để cập nhật kho một cách an toàn (Tránh Over-selling)
    SELECT "STOCK", "PRICE", "SKU", "NAME"
    INTO v_stock, v_price, v_sku, v_name
    FROM "APP_PRODUCTS"
    WHERE "ID" = v_prod_id
    FOR UPDATE; 
    
    -- Kiểm tra tồn kho
    IF v_stock < v_qty THEN
        p_status_code := 'ERROR_STOCK';
        p_message := 'Sản phẩm ' || v_name || ' đã hết hàng hoặc không đủ tồn kho.';
        ROLLBACK;
        RETURN;
    END IF;
    
    -- Tính tiền
    v_subtotal := v_price * v_qty;
    v_total_amount := v_total_amount + v_subtotal;
    
    -- Sinh ID mới cho đơn hàng từ SEQUENCE
    SELECT APP_ORDER_SEQ.NEXTVAL INTO v_order_id FROM dual;
    
    -- Tạo đơn hàng chính
    INSERT INTO "APP_ORDERS" (
        "ID", "USER_ID", "STATUS", "TOTAL_AMOUNT", "RECEIVER_NAME", 
        "RECEIVER_PHONE", "SHIPPING_ADDRESS", "NOTE", "PAYMENT_METHOD", "PAYMENT_STATUS"
    ) VALUES (
        v_order_id, p_user_id, 'PENDING', v_total_amount, p_receiver_name,
        p_receiver_phone, p_shipping_address, p_note, p_payment_method, 'PENDING'
    );
    
    -- Tạo chi tiết đơn hàng
    INSERT INTO "APP_ORDER_ITEMS" (
        "ID", "ORDER_ID", "PRODUCT_ID", "PRODUCT_NAME", "PRODUCT_SKU", "UNIT_PRICE", "QUANTITY", "SUBTOTAL"
    ) VALUES (
        APP_ORDER_ITEM_SEQ.NEXTVAL, v_order_id, v_prod_id, v_name, v_sku, v_price, v_qty, v_subtotal
    );
    
    -- Trừ kho sản phẩm
    UPDATE "APP_PRODUCTS"
    SET "STOCK" = "STOCK" - v_qty,
        "UPDATED_AT" = SYSTIMESTAMP
    WHERE "ID" = v_prod_id;
    
    -- Trả kết quả ra ngoài Java
    p_order_id := v_order_id;
    p_status_code := 'SUCCESS';
    p_message := 'Đặt hàng thành công!';
    
    COMMIT; -- Hoàn tất giao dịch
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_status_code := 'ERROR_SYSTEM';
        p_message := 'Lỗi hệ thống: ' || SQLERRM;
END;
/
