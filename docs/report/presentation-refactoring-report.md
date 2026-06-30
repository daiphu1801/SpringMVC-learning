# Báo cáo Cải tiến Kiến trúc: Tái cấu trúc Tầng Presentation & Bảo mật Đầu vào/Đầu ra

Báo cáo này tài liệu hóa quá trình phân tích, lập kế hoạch và triển khai việc tái cấu trúc tầng **Presentation (Controller / JSPs / Interceptor)** nhằm giải quyết các lỗ hổng bảo mật nghiêm trọng liên quan đến rò rỉ thông tin cá nhân (PII) và chèn mã độc kiểm soát giao diện người dùng (Cross-Site Scripting - XSS).

---

## 1. Vấn đề Bảo mật & Thiết kế trước khi Tái cấu trúc

### 1.1. Rò rỉ thông tin cá nhân (PII) tại `GET /users`
* **Vấn đề**: Bộ lọc bảo mật `SecurityInterceptor.java` chỉ thực hiện chặn các hành động ghi sửa dữ liệu (Write Actions như POST `create`, POST `edit`, POST `delete`) đối với các tài khoản không có quyền Admin. 
* **Lỗ hổng**: Các tác vụ đọc (`GET /users` liệt kê danh sách người dùng) không được phân quyền bảo vệ. Khách hàng thông thường sau khi đăng nhập vẫn có thể truy cập trực tiếp đường dẫn `/users` để xem toàn bộ danh sách khách hàng khác bao gồm tên đăng nhập, họ tên, số điện thoại, email, vai trò... dẫn đến vi phạm nghiêm trọng tính riêng tư và lộ lọt dữ liệu (PII Leakage).

### 1.2. Lỗ hổng Cross-Site Scripting (XSS) qua dữ liệu thô
* **Vấn đề**:
  1. Các trang JSP hiển thị thông điệp lỗi thông qua biểu thức EL thô `${error}` trực tiếp vào mã HTML mà không qua bộ lọc/escape ký tự HTML (`product-form.jsp:13`, `product-list.jsp:18`, `checkout.jsp:16`, `order-detail.jsp:42`, `category-form.jsp:10`, `admin-order-detail.jsp:42`).
  2. Lớp kiểm định tệp tin ảnh `ImageFileValidator.java` trực tiếp đưa phần mở rộng (`ext`) và loại nội dung (`contentType`) nhận từ client vào thông báo lỗi ngoại lệ `IllegalArgumentException` mà không làm sạch.
  3. Tên sản phẩm `${product.name}` được đưa trực tiếp vào thuộc tính tiêu đề của Layout (`<t:layout title="${product.name}">`) và được render thẳng ra thẻ `<title>${title}</title>` của HTML.
* **Lỗ hổng**: Kẻ tấn công có thể đổi tên tệp tin tải lên thành `<script>alert(1)</script>.exe` hoặc chèn script vào trường tên sản phẩm. Khi hệ thống báo lỗi định dạng hoặc render trang chi tiết sản phẩm, trình duyệt của nạn nhân sẽ thực thi mã JavaScript độc hại đó ngay lập tức (Reflected XSS / Stored XSS).

---

## 2. Giải pháp triển khai bảo mật

Chúng tôi áp dụng nguyên tắc **Defense in Depth (Phòng thủ nhiều lớp)** để xử lý triệt để hai vấn đề trên:

### 2.1. Phân quyền chặt chẽ cho Phân hệ Quản trị User
* Cập nhật quy tắc kiểm tra quyền truy cập trong `SecurityInterceptor`:
  - Mọi đường dẫn bắt đầu bằng `/users` đều bị từ chối nếu người dùng hiện tại không có quyền quản trị (`isAdmin == false`).
  - Ngoại lệ duy nhất được cho phép là phân hệ Sổ địa chỉ cá nhân `/users/addresses` (đường dẫn phục vụ khách hàng quản lý địa chỉ giao hàng của chính họ).

### 2.2. Làm sạch dữ liệu đầu ra và làm sạch thông báo ngoại lệ (XSS Mitigation)
* **Tầng View (JSPs & Tags)**:
  - Bọc tất cả các biểu thức hiển thị `${error}` thô trong thẻ `<c:out value="${error}"/>` ở tất cả các trang JSP.
  - Sửa đổi tệp tin tag chung `layout.tag` để hiển thị tiêu đề trang một cách an toàn bằng thẻ `<c:out value="${empty title ? 'Spring MVC Demo' : title}"/>`.
* **Tầng Logic nghiệp vụ / Validate**:
  - Sử dụng utility `org.springframework.web.util.HtmlUtils.htmlEscape()` trong `ImageFileValidator.java` để escape toàn bộ mã HTML trong các tham số động từ input của người dùng (`ext`, `contentType`) trước khi tạo thông điệp ngoại lệ.
* **Cơ chế ngăn ngừa tái diễn (Regression Prevention)**:
  - Cập nhật test suite tĩnh `JspXssEncodingTest.java` để loại bỏ chuỗi `"error"` khỏi whitelist các biểu thức an toàn. Từ nay về sau, nếu bất kỳ trang JSP nào sử dụng `${error}` trực tiếp thay vì bọc trong `<c:out>`, bài test sẽ tự động thất bại và ngăn cản quá trình đóng gói sản phẩm.

---

## 3. Chi tiết các File đã Chỉnh sửa & Tạo mới

### 3.1. Phân quyền và Bảo vệ PII (Presentation Layer Security)
* **[MODIFY] [SecurityInterceptor.java](../../src/main/java/com/examp/springmvc/auth/infrastructure/security/SecurityInterceptor.java):**
  - Chuyển cấu trúc lọc từ kiểm tra `isWriteAction` sang chặn toàn quyền truy cập `/users` ngoại trừ `/users/addresses` đối với người dùng không phải Admin.
* **[MODIFY] [SecurityInterceptorTest.java](../../src/test/java/com/examp/springmvc/auth/infrastructure/security/SecurityInterceptorTest.java):**
  - Cập nhật test case khẳng định vai trò `USER` thông thường bị chặn truy cập `/users`, đồng thời kiểm tra quyền truy cập hợp lệ tới `/users/addresses`.

### 3.2. Ngăn chặn XSS (XSS Protection)
* **[MODIFY] [ImageFileValidator.java](../../src/main/java/com/examp/springmvc/shared/presentation/ImageFileValidator.java):**
  - Gọi `HtmlUtils.htmlEscape(ext)` và `HtmlUtils.htmlEscape(contentType)` làm sạch chuỗi trước khi gộp vào exception message.
* **[MODIFY] [ImageFileValidatorTest.java](../../src/test/java/com/examp/springmvc/shared/presentation/ImageFileValidatorTest.java):**
  - Bổ sung 2 bài unit test: `shouldEscapeHtmlInFilenameExtension` và `shouldEscapeHtmlInContentType` nhằm đảm bảo thẻ `<script>` trong payload đầu vào được mã hóa thực thể HTML (`&lt;script&gt;`).
* **[MODIFY] [layout.tag](../../src/main/webapp/WEB-INF/tags/layout.tag):**
  - Sử dụng `<c:out>` bảo vệ thẻ `<title>`.
* **[MODIFY] Các file JSPs:**
  - `category-form.jsp`
  - `product-form.jsp`
  - `product-list.jsp`
  - `checkout.jsp`
  - `admin-order-detail.jsp`
  - `order-detail.jsp`
  - Đưa toàn bộ việc hiển thị lỗi vào thẻ `<c:out value="${error}"/>`.
* **[MODIFY] [JspXssEncodingTest.java](../../src/test/java/com/examp/springmvc/shared/JspXssEncodingTest.java):**
  - Loại bỏ chuỗi `"error"` khỏi whitelist các biểu thức được xem là an toàn để kích hoạt quét tĩnh lỗi XSS thô tự động.

---

## 4. Kết quả Xác thực & Đánh giá Chất lượng

1. **Spotless Format (`mvn spotless:apply`):**
   * Đạt trạng thái **SUCCESS**. Toàn bộ mã nguồn mới viết và chỉnh sửa tuân thủ hoàn hảo chuẩn format của dự án.
2. **Checkstyle (`mvn checkstyle:check`):**
   * Đạt trạng thái **SUCCESS** với **0 lỗi vi phạm** (Đã giải quyết dòng code quá dài > 120 ký tự ở ImageFileValidator).
3. **SpotBugs (`mvn spotbugs:check`):**
   * Đạt trạng thái **SUCCESS** với **0 lỗi vi phạm**.
4. **Kiểm thử Đơn vị (`mvn clean test`):**
   * Tổng số unit test tăng từ **152 lên 155** cases.
   * Toàn bộ **155 / 155** unit tests chạy thành công. Không có bài test nào bị lỗi hay thất bại.
