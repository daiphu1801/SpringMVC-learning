# Hướng Dẫn Tích Hợp Stored Procedure Trong Dự Án Thực Tế (Spring Boot & Oracle)

Báo cáo này phân tích vai trò của Stored Procedure trong các dự án phần mềm doanh nghiệp lớn, đề xuất thủ tục nghiệp vụ cốt lõi cho dự án hiện tại, và hướng dẫn chi tiết cách triển khai, quản lý mã nguồn cũng như tích hợp gọi Stored Procedure từ ứng dụng Spring Boot.

---

## 1. Đánh Giá: Khi Nào Nên Dùng Stored Procedure?

Trong các hệ thống Enterprise, việc quyết định đưa logic nghiệp vụ vào Code ứng dụng (Java/Spring) hay vào Database (Stored Procedure/PL-SQL) luôn là bài toán cân não:

### 👍 Ưu điểm (Nên dùng khi):
1.  **Hiệu năng vượt trội (Giảm Network Roundtrips)**: 
    *   Ví dụ: Thao tác tạo đơn hàng gồm 10 sản phẩm. Nếu làm ở Java, ứng dụng phải gửi 10 lệnh SELECT check kho, 10 lệnh UPDATE trừ kho, 1 lệnh INSERT order, 10 lệnh INSERT order_items (tổng cộng **31 lượt kết nối mạng** qua lại giữa Java App và DB).
    *   Nếu dùng Stored Procedure, ứng dụng Java chỉ cần gọi duy nhất **1 lệnh thực thi** `{call PROC_CREATE_ORDER(...)}`. Toàn bộ 31 thao tác trên chạy trực tiếp trong bộ nhớ RAM của máy chủ Database với tốc độ cực nhanh (< 5ms).
2.  **Đảm bảo toàn vẹn dữ liệu cao & Tránh lỗi "Quá bán" (Over-selling)**:
    *   Trong các chương trình Flash Sale, hàng ngàn người dùng mua cùng một sản phẩm tại một thời điểm. Dùng Stored Procedure kết hợp với khóa hàng (`SELECT ... FOR UPDATE`) giúp khóa dữ liệu ngay lập tức tại tầng vật lý DB, triệt tiêu hoàn toàn lỗi bán quá số lượng tồn kho.
3.  **An ninh bảo mật (Security)**:
    *   Bạn không cần cấp quyền truy cập trực tiếp `SELECT`, `INSERT`, `UPDATE` các bảng nhạy cảm (như tài khoản, đơn hàng) cho tài khoản kết nối của Java App. Chỉ cần cấp quyền `EXECUTE` trên Stored Procedure cụ thể.

### 👎 Nhược điểm (Hạn chế dùng khi):
1.  **Khó Scale-out (Mở rộng chiều ngang)**: Việc tăng năng lực xử lý cho Java App rất dễ và rẻ (chỉ cần chạy thêm nhiều replica container), nhưng tăng năng lực xử lý cho Database Server (nhất là Oracle License) thì vô cùng đắt đỏ.
2.  **Ràng buộc công nghệ (Vendor Lock-in)**: Mã PL/SQL chỉ chạy được trên Oracle DB. Nếu sau này dự án chuyển sang PostgreSQL hoặc SQL Server, bạn sẽ phải viết lại toàn bộ stored procedure.
3.  **Khó Debug & Unit Test**: Khó khăn hơn nhiều so với việc viết unit test trên Java bằng JUnit/Mockito.

---

## 2. Thiết Kế Thủ Tục Cốt Lõi: Đặt Hàng & Trừ Kho (`PROC_CREATE_ORDER`)

Đối với cấu trúc bảng hiện tại của bạn (`APP_ORDERS`, `APP_ORDER_ITEMS`, `APP_PRODUCTS`), nghiệp vụ quan trọng nhất cần dùng Stored Procedure là **Đặt hàng và Trừ kho**. 

Dưới đây là mã nguồn PL/SQL thiết lập thủ tục này một cách an toàn:

```sql
CREATE OR REPLACE PROCEDURE PROC_CREATE_ORDER (
    p_user_id          IN  NUMBER,
    p_receiver_name    IN  VARCHAR2,
    p_receiver_phone   IN  VARCHAR2,
    p_shipping_address IN  VARCHAR2,
    p_note             IN  VARCHAR2,
    p_payment_method   IN  VARCHAR2,
    -- Nhận danh sách sản phẩm và số lượng dưới dạng chuỗi định dạng 'ID:QTY,ID:QTY,...'
    p_items_csv        IN  VARCHAR2, 
    p_order_id         OUT NUMBER,
    p_status_code      OUT VARCHAR2, -- SUCCESS hoặc ERROR_STOCK
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
    
    -- Dùng để phân tách chuỗi CSV
    v_item_pair       VARCHAR2(100);
    v_pos             NUMBER;
    
    -- Định nghĩa mảng lưu trữ tạm thời các item hợp lệ
    TYPE t_item_rec IS RECORD (
        prod_id  NUMBER,
        qty      NUMBER,
        price    NUMBER,
        sku      VARCHAR2(50),
        name     VARCHAR2(150),
        subtotal NUMBER(19,2)
    );
    TYPE t_item_list IS TABLE OF t_item_rec INDEX BY BINARY_INTEGER;
    v_items t_item_list;
    v_idx NUMBER := 1;
BEGIN
    -- Bước 1: Parse danh sách sản phẩm từ CSV và kiểm tra/khóa kho trước (Tránh deadlock)
    -- Giả sử chuỗi đầu vào dạng: "3:2,4:1" (Sản phẩm 3 mua 2 cái, sản phẩm 4 mua 1 cái)
    -- Quy tắc: Cần duyệt danh sách sản phẩm theo thứ tự ID tăng dần trước khi khóa (SELECT FOR UPDATE) để tránh Deadlock chéo giữa các transaction.
    
    -- (Trong dự án thực tế, có thể sử dụng TABLE OF Object Type, ở đây dùng cách phân tách chuỗi đơn giản để dễ chạy)
    -- Đoạn này phân tách chuỗi:
    v_pos := 0;
    LOOP
        -- Phân tách từng cặp ID:QTY
        -- (Logic phân tách chuỗi)
        -- ...
        EXIT WHEN TRUE; -- Thay thế bằng logic parse thực tế
    END LOOP;

    -- Ví dụ giả lập xử lý từng sản phẩm (Để chạy demo):
    -- Giả sử chúng ta mua sản phẩm ID = 3, số lượng = 2
    v_prod_id := 3;
    v_qty := 2;
    
    -- Khóa bản ghi sản phẩm để cập nhật kho một cách an toàn
    SELECT "STOCK", "PRICE", "SKU", "NAME"
    INTO v_stock, v_price, v_sku, v_name
    FROM "APP_PRODUCTS"
    WHERE "ID" = v_prod_id
    FOR UPDATE; -- Khóa dòng sản phẩm này lại, các transaction khác muốn mua sản phẩm này phải đợi
    
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
    
    -- Bước 2: Sinh ID mới cho đơn hàng từ SEQUENCE
    SELECT APP_ORDER_SEQ.NEXTVAL INTO v_order_id FROM dual;
    
    -- Bước 3: Tạo đơn hàng chính
    INSERT INTO "APP_ORDERS" (
        "ID", "USER_ID", "STATUS", "TOTAL_AMOUNT", "RECEIVER_NAME", 
        "RECEIVER_PHONE", "SHIPPING_ADDRESS", "NOTE", "PAYMENT_METHOD", "PAYMENT_STATUS"
    ) VALUES (
        v_order_id, p_user_id, 'PENDING', v_total_amount, p_receiver_name,
        p_receiver_phone, p_shipping_address, p_note, p_payment_method, 'PENDING'
    );
    
    -- Bước 4: Tạo chi tiết đơn hàng & trừ kho sản phẩm
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
        ROLLBACK; -- Đảm bảo rollback sạch sẽ nếu có lỗi bất kỳ xảy ra
        p_status_code := 'ERROR_SYSTEM';
        p_message := 'Lỗi hệ thống: ' || SQLERRM;
END;
/
```

---

## 3. Cách Triển Khai và Quản Lý Mã Nguồn Trong Dự Án Thật

Trong các dự án phần mềm thực tế, Stored Procedure **không bao giờ** được tạo tay trực tiếp bằng DBeaver trên database máy chủ Production. Thay vào đó, chúng được quản lý như sau:

### 3.1. Quản lý bằng Database Migration (Flyway / Liquibase)
*   **Flyway**: Đặt file procedure trong thư mục `src/main/resources/db/migration/` với định dạng tên `V2__create_proc_create_order.sql`. Khi ứng dụng Spring Boot khởi động, Flyway sẽ tự động chạy file này để biên dịch procedure vào DB.
*   **Chú ý**: Khi chạy các script chứa dấu chấm phẩy và dấu gạch chéo `/` trong Flyway, cần cấu hình Flyway Parser bỏ qua delimiter mặc định để tránh lỗi cú pháp.

### 3.2. Cấu hình gọi Stored Procedure từ Spring Boot + MyBatis

Trong dự án sử dụng MyBatis (như cấu hình `MyBatisConfig.java` hiện tại của bạn), bạn gọi Stored Procedure bằng cách thiết lập **Mapper XML** sử dụng thuộc tính `statementType="CALLABLE"`.

#### Bước A: Tạo Java DTO nhận tham số đầu vào và đầu ra
```java
public class CreateOrderDto {
    // Input parameters
    private Long userId;
    private String receiverName;
    private String receiverPhone;
    private String shippingAddress;
    private String note;
    private String paymentMethod;
    private String itemsCsv;

    // Output parameters (Sẽ được MyBatis tự động gán giá trị sau khi gọi procedure)
    private Long outOrderId;
    private String outStatusCode;
    private String outMessage;

    // Getters and Setters ...
}
```

#### Bước B: Khai báo Mapper Interface trong Java
```java
@Mapper
public interface OrderMapper {
    // Gọi procedure truyền vào DTO chứa cả IN và OUT parameters
    void callCreateOrderProcedure(CreateOrderDto dto);
}
```

#### Bước C: Định nghĩa ánh xạ XML (`OrderMapper.xml`)
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.examp.springmvc.order.infrastructure.persistence.OrderMapper">

    <select id="callCreateOrderProcedure" statementType="CALLABLE" parameterType="com.examp.springmvc.order.dto.CreateOrderDto">
        {call PROC_CREATE_ORDER(
            #{userId, mode=IN, jdbcType=DECIMAL},
            #{receiverName, mode=IN, jdbcType=VARCHAR},
            #{receiverPhone, mode=IN, jdbcType=VARCHAR},
            #{shippingAddress, mode=IN, jdbcType=VARCHAR},
            #{note, mode=IN, jdbcType=VARCHAR},
            #{paymentMethod, mode=IN, jdbcType=VARCHAR},
            #{itemsCsv, mode=IN, jdbcType=VARCHAR},
            #{outOrderId, mode=OUT, jdbcType=DECIMAL},
            #{outStatusCode, mode=OUT, jdbcType=VARCHAR},
            #{outMessage, mode=OUT, jdbcType=VARCHAR}
        )}
    </select>

</mapper>
```

#### Bước D: Gọi sử dụng tại tầng Service
```java
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderMapper orderMapper;

    public OrderResult createOrder(CreateOrderDto dto) {
        // Thực thi gọi procedure thông qua MyBatis
        orderMapper.callCreateOrderProcedure(dto);

        // MyBatis đã tự động gán kết quả trả về từ DB vào các thuộc tính OUT của dto
        if ("SUCCESS".equals(dto.getOutStatusCode())) {
            return new OrderResult(true, dto.getOutOrderId(), dto.getOutMessage());
        } else {
            return new OrderResult(false, null, dto.getOutMessage());
        }
    }
}
```
