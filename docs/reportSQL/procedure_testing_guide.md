# Hướng Dẫn Kiểm Thử Chức Năng & Hiệu Năng Stored Procedure

Tài liệu này hướng dẫn chi tiết cách thực hiện kiểm thử tính đúng đắn (Functional Testing), tính an toàn đồng thời (Concurrency Testing) và đo lường hiệu năng (Performance Benchmarking) của Stored Procedure `PROC_CREATE_ORDER` so với giải pháp xử lý thủ công bằng code Java trước đây.

---

## 1. So Sánh Kiến Trúc: Code Java vs Stored Procedure

Bảng dưới đây so sánh cơ chế hoạt động của hai phương án:

| Tiêu chí | Giải pháp cũ (Java ORM) | Giải pháp mới (Stored Procedure) |
| :--- | :--- | :--- |
| **Số lượt mạng tới DB (Roundtrips)** | **4 lượt**: <br>1. SELECT sản phẩm <br>2. UPDATE tồn kho <br>3. INSERT đơn hàng <br>4. INSERT chi tiết | **1 lượt duy nhất**: <br>Gửi gói tham số gọi lệnh `{call PROC_CREATE_ORDER(...)}` |
| **Độ trễ mạng (Network Overhead)** | Cao (chịu ảnh hưởng lớn nếu DB và Web Server không nằm chung máy chủ) | Thấp tối đa |
| **Độ an toàn tranh chấp (Concurrency)** | Dễ bị quá bán (Overselling) nếu không dùng khóa bi quan thủ công trong Java | **An toàn tuyệt đối**: Khóa dòng tự động `SELECT FOR UPDATE` tại tầng DB |
| **Giao dịch (Transaction)** | Quản lý bởi Spring `@Transactional` (giữ kết nối DB lâu hơn) | Tự đóng gói transaction nội bộ trong DB, giải phóng kết nối nhanh hơn |

---

## 2. Test 1: Kiểm Thử Chức Năng (Functional Test)

Để kiểm tra xem Stored Procedure hoạt động chính xác cả ở Database và ứng dụng Java.

### Bước 2.1: Chạy thử độc lập trong DBeaver
Mở **SQL Editor** trong DBeaver, bật tính năng **DBMS Output** (nút màu cam ở thanh công cụ bên trái) và chạy script PL/SQL sau:

```sql
DECLARE
  v_order_id     NUMBER;
  v_status_code  VARCHAR2(50);
  v_message      VARCHAR2(4000);
BEGIN
  PROC_CREATE_ORDER(
    p_user_id          => 101, -- Sử dụng ID user có sẵn trong seed data V2
    p_receiver_name    => 'Nguyen Van A',
    p_receiver_phone   => '0987654321',
    p_shipping_address => '123 Ba Dinh, Ha Noi',
    p_note             => 'Test qua DBeaver',
    p_payment_method   => 'CASH',
    p_items_csv        => '3:2', -- Mẫu đặt sản phẩm ID=3 với số lượng=2
    p_order_id         => v_order_id,
    p_status_code      => v_status_code,
    p_message          => v_message
  );
  
  DBMS_OUTPUT.PUT_LINE('=== KẾT QUẢ GỌI PROCEDURE ===');
  DBMS_OUTPUT.PUT_LINE('Mã đơn hàng sinh ra: ' || v_order_id);
  DBMS_OUTPUT.PUT_LINE('Trạng thái trả về: ' || v_status_code);
  DBMS_OUTPUT.PUT_LINE('Thông điệp: ' || v_message);
END;
/
```

**Cách xác minh kết quả:**
1.  DBMS Output hiển thị: `Trạng thái trả về: SUCCESS` và mã đơn hàng cụ thể.
2.  Chạy truy vấn kiểm tra tồn kho của sản phẩm ID = 3 xem đã giảm đi 2 đơn vị chưa:
    ```sql
    SELECT STOCK FROM APP_PRODUCTS WHERE ID = 3;
    ```
3.  Kiểm tra đơn hàng mới được tạo trong bảng:
    ```sql
    SELECT * FROM APP_ORDERS WHERE ID = [Mã_Đơn_Hàng_Sinh_Ra];
    ```

---

## 3. Test 2: Kiểm Thử Tranh Chấp Đồng Thời (Concurrency & Overselling Test)

Mục tiêu là mô phỏng **100 người dùng cùng thanh toán một sản phẩm hot** (flash sale) chỉ còn 5 sản phẩm tồn kho.

### Cách thực hiện bằng Apache JMeter / k6 / ApacheBench (ab):
1.  Đảm bảo tồn kho của sản phẩm ID = 3 là **5**.
    ```sql
    UPDATE APP_PRODUCTS SET STOCK = 5 WHERE ID = 3;
    COMMIT;
    ```
2.  Sử dụng công cụ **ApacheBench (ab)** (có sẵn trên Linux) để gửi nhanh **100 request đồng thời** vào API tạo đơn hàng:
    ```bash
    ab -n 100 -c 10 -p post_data.json -T "application/json" http://localhost:8080/SpringMVC-Demo/orders
    ```
    *(Tạo file `post_data.json` chứa thông tin request tạo đơn hàng).*

3.  **Kết quả mong đợi:**
    *   **Với Stored Procedure:** Chỉ có đúng **2 đơn hàng** được tạo thành công (mỗi đơn mua 2 sản phẩm, tổng là 4, tồn kho còn 1 không đủ cho đơn thứ 3). 98 request còn lại nhận về phản hồi lỗi `ERROR_STOCK` (HTTP 400 hoặc thông báo lỗi hết hàng). Tồn kho sản phẩm không bao giờ bị âm.
    *   **Với cách làm cũ (nếu không khóa dòng):** Nhiều request cùng đọc thấy stock = 5, tất cả đều thực hiện trừ kho và tạo đơn hàng. Kết quả là tạo ra hơn 2 đơn hàng thành công, tồn kho sản phẩm bị âm (ví dụ: `-15` sản phẩm) — đây là lỗi cực kỳ nghiêm trọng trong thương mại điện tử.

---

## 4. Test 3: Đo Lường & So Sánh Hiệu Năng (Performance Benchmarking)

Để thấy rõ sự khác biệt về số lượng request xử lý được trong một giây (RPS - Requests Per Second) và thời gian phản hồi trung bình (Response Time).

### Bước 4.1: Đo hiệu năng giải pháp cũ (Java ORM)
Bạn có thể khôi phục nhanh code cũ hoặc chạy kiểm thử tải trên một endpoint tương đương sử dụng ORM cũ:
*   Chạy ApacheBench gửi 1,000 requests với 50 connection đồng thời:
    ```bash
    ab -n 1000 -c 50 -p post_data.json -T "application/json" http://localhost:8080/SpringMVC-Demo/orders-old
    ```
*   Ghi lại chỉ số:
    *   **Requests per second (RPS)**: Số lượng request/giây (ví dụ: 120 req/s).
    *   **Time per request (mean)**: Thời gian phản hồi trung bình (ví dụ: 150ms).

### Bước 4.2: Đo hiệu năng giải pháp mới (Stored Procedure)
*   Chạy cùng câu lệnh kiểm thử với endpoint sử dụng Stored Procedure mới:
    ```bash
    ab -n 1000 -c 50 -p post_data.json -T "application/json" http://localhost:8080/SpringMVC-Demo/orders
    ```
*   So sánh kết quả:
    *   **RPS tăng vọt** (thường từ 1.5x đến 3x so với cũ) nhờ giảm thiểu thời gian chiếm dụng kết nối và loại bỏ độ trễ mạng mạng giữa Java và Database.
    *   **Time per request giảm mạnh** (Response Time trung bình giảm xuống dưới 30ms).
