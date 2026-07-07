# Database Functions - Lý Thuyết & Giải Pháp Tối Ưu Cho Dự Án (Oracle DB)

Báo cáo này phân tích lý thuyết về **Database Function** (Hàm lưu trữ), đề xuất hai giải pháp thiết kế hàm tối ưu nhất cho cấu trúc dự án hiện tại (`SpringMVC-Demo`) và giải thích lý do tại sao chúng mang lại hiệu năng cao cũng như mã nguồn sạch hơn.

---

## 1. Lý Thuyết Cơ Bản Về Database Function

### 1.1. Function là gì?
**Database Function (Hàm cơ sở dữ liệu)** là một khối mã PL/SQL được biên dịch sẵn, lưu trữ trên Database Server và **bắt buộc** phải trả về **duy nhất một giá trị** có kiểu dữ liệu xác định (Scalar Type như `NUMBER`, `VARCHAR2`, `DATE` hoặc các kiểu phức tạp hơn) thông qua từ khóa `RETURN`.

### 1.2. Phân biệt Stored Procedure và Function trong Oracle
Dù cả hai đều là các chương trình PL/SQL lưu trữ (stored subprograms), chúng có các khác biệt cốt lõi sau:

| Tiêu chí | Stored Procedure (Thủ tục) | Function (Hàm) |
| :--- | :--- | :--- |
| **Giá trị trả về** | Không bắt buộc trả về giá trị trực tiếp (sử dụng các tham số `OUT` để xuất nhiều kết quả). | **Bắt buộc** trả về **duy nhất** một giá trị thông qua mệnh đề `RETURN`. |
| **Sử dụng trong SQL** | **Không thể** gọi trực tiếp từ câu lệnh SQL (`SELECT`, `WHERE`, `GROUP BY`, v.v.). | **Có thể** gọi trực tiếp trong câu lệnh SQL (ví dụ: `SELECT GET_PRODUCT_PRICE(id) FROM dual`). |
| **Quản lý Giao dịch** | Cho phép sử dụng các câu lệnh quản lý transaction như `COMMIT` và `ROLLBACK`. | Hạn chế thực hiện `COMMIT`, `ROLLBACK` hoặc các câu lệnh DML nếu hàm được gọi từ câu lệnh `SELECT`. |
| **Mục đích chính** | Dùng để thực thi chuỗi hành động nghiệp vụ phức tạp tác động lớn đến dữ liệu (như đặt hàng, khóa sổ). | Dùng để tính toán giá trị, biến đổi dữ liệu (như tính thuế, format chuỗi). |

### 1.3. Các chế độ Tham số (Parameter Modes)
Oracle hỗ trợ 3 chế độ truyền tham số vào/ra cho Procedure:
1.  **`IN` (Mặc định):** Tham số truyền vào từ ứng dụng. Giá trị này ở trạng thái chỉ đọc (Read-only) trong suốt quá trình chạy procedure.
2.  **`OUT`:** Tham số đầu ra dùng để trả kết quả về cho ứng dụng gọi. Giá trị ban đầu của nó bên trong procedure luôn là `NULL`.
3.  **`IN OUT`:** Tham số hai chiều. Procedure nhận giá trị đầu vào từ ứng dụng, có thể thay đổi/xử lý giá trị đó, và trả về giá trị mới sau khi hoàn thành.

### 1.4. Quản lý Giao dịch & Xử lý Ngoại lệ (Exceptions)
*   **Transaction:** Bên trong Procedure, ta có quyền quyết định khi nào hoàn tất transaction bằng `COMMIT` hoặc hủy bỏ bằng `ROLLBACK`.
*   **Exception Handling:** PL/SQL cung cấp cấu trúc `EXCEPTION` để bắt các lỗi phát sinh trong thời gian chạy (Runtime Error). Khối `EXCEPTION WHEN OTHERS THEN` đóng vai trò như một khối `try-catch` tổng quát, giúp gọi `ROLLBACK` kịp thời khi có bất kỳ dòng lệnh nào gặp sự cố, giữ cho hệ thống không bị lưu dữ liệu dở dang (lỗi Atomicity).

---

## 2. Giải Pháp Áp Dụng Function Tối Ưu Cho Dự Án

Dưới đây là hai hàm được thiết kế tối ưu nhất dựa trên các bảng hiện tại của hệ thống:

### Hàm 1: Nối Địa Chỉ Đầy Đủ (`FUNC_GET_FULL_ADDRESS`)

#### Mã PL/SQL:
```sql
CREATE OR REPLACE FUNCTION FUNC_GET_FULL_ADDRESS (
    p_street_detail IN VARCHAR2,
    p_ward          IN VARCHAR2,
    p_district      IN VARCHAR2,
    p_province      IN VARCHAR2
) RETURN VARCHAR2 DETERMINISTIC 
AS
    v_full_address VARCHAR2(600);
BEGIN
    -- Nối chuỗi thông minh, tự động bỏ qua nếu trường bị NULL hoặc trống
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
```

#### Cách gọi trực tiếp trong MyBatis XML Mapper:
```xml
<select id="getUserAddressList" resultMap="AddressResultMap">
    SELECT 
        ID, 
        RECEIVER_NAME, 
        RECEIVER_PHONE,
        -- Gọi hàm trực tiếp để lấy địa chỉ hoàn chỉnh
        FUNC_GET_FULL_ADDRESS(STREET_DETAIL, WARD, DISTRICT, PROVINCE) as FULL_ADDRESS_STRING,
        IS_DEFAULT
    FROM APP_USER_ADDRESSES
    WHERE USER_ID = #{userId}
</select>
```

#### Vì sao giải pháp này tối ưu?
1.  **Tránh trùng lặp code (DRY - Don't Repeat Yourself):** Trong ứng dụng, thông tin địa chỉ hiển thị đầy đủ dạng `"Số 12 Nguyễn Trãi, Phường 2, Quận 5, TP. Hồ Chí Minh"` được sử dụng ở rất nhiều nơi (trang danh sách địa chỉ, trang Checkout đơn hàng, hóa đơn gửi mail). Nếu không dùng function, bạn sẽ phải viết phép nối chuỗi dài dòng `STREET_DETAIL || ', ' || WARD ...` ở 3-4 file XML Mapper khác nhau.
2.  **Xử lý dữ liệu NULL nhất quán:** Tránh lỗi hiển thị chuỗi kỳ lạ như `, , Quận 5, TP. HCM` khi người dùng nhập thiếu dữ liệu đường hoặc phường.
3.  **Tận dụng cơ chế Cache của Database:** Khai báo `DETERMINISTIC` giúp Oracle không phải tính toán lại việc nối chuỗi cho cùng một địa chỉ nếu truy vấn lặp lại nhiều lần.

---

### Hàm 2: Tính Tổng Chi Tiêu Tích Lũy Của User (`FUNC_CALC_USER_TOTAL_SPENT`)

#### Mã PL/SQL:
```sql
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
      AND "STATUS" = 'DELIVERED'; -- Chỉ tính các đơn hàng đã giao thành công
      
    RETURN v_total;
END;
/
```

#### Cách gọi ứng dụng trong SQL Thống kê / Báo cáo:
```sql
-- Lấy danh sách TOP 10 khách hàng VIP chiêu tiêu nhiều nhất để tặng ưu đãi
SELECT 
    ID, 
    USERNAME, 
    FULL_NAME, 
    FUNC_CALC_USER_TOTAL_SPENT(ID) AS TOTAL_SPENT
FROM APP_USERS
ORDER BY TOTAL_SPENT DESC
FETCH FIRST 10 ROWS ONLY;
```

#### Vì sao giải pháp này tối ưu?
1.  **Đơn giản hóa câu lệnh SELECT:** Bạn không cần phải viết các câu lệnh `LEFT JOIN` phức tạp kèm mệnh đề `GROUP BY` nặng nề trên bảng `APP_ORDERS` chỉ để lấy ra thông tin tổng chi tiêu khi truy vấn thông tin khách hàng.
2.  **Giảm tải cho RAM ứng dụng Java:** Thay vì Java phải load toàn bộ danh sách đơn hàng của user lên bộ nhớ rồi dùng `Stream().mapToDouble().sum()` để cộng tiền, Database thực hiện việc này trực tiếp trên đĩa thông qua Index và trả về duy nhất 1 con số cho Java.
3.  **Hỗ trợ phân trang tốt hơn:** Khi cần hiển thị danh sách User kèm tổng chi tiêu của họ trên trang Admin, việc phân trang (`Pagination`) dùng hàm này sẽ chạy cực kỳ nhanh mà không bị phình to câu lệnh SQL phức tạp.

