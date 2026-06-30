# Kế hoạch Cải thiện Hiệu năng & Bảo mật Frontend

Tài liệu này vạch ra kế hoạch hành động chi tiết để cải thiện hiệu năng và mức độ bảo mật cho lớp Presentation (Frontend) của ứng dụng Spring MVC theo các quy tắc đã đề ra trong `skills/frontend_security_rules.md`.

---

## 1. Mục tiêu (Goals)
1.  **Bảo mật**: Phòng chống tấn công XSS chủ động qua Content Security Policy (CSP) và chống Clickjacking, MIME-sniffing qua HTTP Security Headers.
2.  **Hiệu năng**: Triển khai cơ chế Cache Busting (đánh dấu phiên bản để trình duyệt cache tối đa tài nguyên tĩnh nhưng vẫn tự động cập nhật khi deploy), giảm thời gian render-blocking và tối ưu hóa hiển thị hình ảnh.

---

## 2. Kế hoạch hành động chi tiết (Action Plan)

### Bước 1: Cấu hình HTTP Security Headers (Bảo mật)
Thêm các Header bảo mật trực tiếp vào lớp `SecurityInterceptor.java` ở tầng shared infrastructure để áp dụng cho mọi HTTP Response của ứng dụng Spring MVC.

*   **Tệp cần sửa**: `src/main/java/com/examp/springmvc/shared/infrastructure/SecurityInterceptor.java`
*   **Mã nguồn đề xuất**:
    ```java
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Cấu hình Content Security Policy (CSP)
        response.setHeader("Content-Security-Policy", 
            "default-src 'self'; " +
            "script-src 'self'; " +
            "style-src 'self' https://fonts.googleapis.com; " +
            "font-src 'self' https://fonts.gstatic.com; " +
            "img-src 'self' data: https://img.vietqr.io; " +
            "frame-ancestors 'self';"
        );

        // Chống Clickjacking
        response.setHeader("X-Frame-Options", "SAMEORIGIN");

        // Chống MIME-sniffing
        response.setHeader("X-Content-Type-Options", "nosniff");

        // ... (các logic kiểm tra phân quyền cũ)
        return true;
    }
    ```

---

### Bước 2: Triển khai Cache Busting cho Tài nguyên Tĩnh (Hiệu năng)
Để đảm bảo trình duyệt có thể cache vĩnh viễn các file CSS/JS tĩnh nhưng sẽ tải lại ngay lập tức khi chúng ta có bản cập nhật mới, ta sử dụng cơ chế đính kèm phiên bản dự án (`?v=${project.version}`) vào tất cả các liên kết tài nguyên tĩnh trong tệp `layout.tag`.

*   **Tệp cần sửa**: `src/main/webapp/WEB-INF/tags/layout.tag`
*   **Mã nguồn đề xuất**:
    1.  Khai báo hoặc nạp biến version toàn cục (hoặc dùng biến phiên bản dự án từ Maven, hoặc đính mã hash/timestamp khi deploy).
    2.  Đơn giản và hiệu quả nhất trong Spring MVC: Định nghĩa biến phiên bản ứng dụng trong servlet context khi khởi động hoặc truy cập qua `${applicationScope.appVersion}`.
    3.  Tại tệp `layout.tag`, cập nhật đường dẫn:
        ```jsp
        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/global.css?v=${applicationScope.appVersion}">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/layout.css?v=${applicationScope.appVersion}">
        ```

---

### Bước 3: Cải thiện hiệu năng render của trình duyệt (Hiệu năng)
1.  **Sử dụng thẻ Preconnect**: Thêm các chỉ dẫn mạng vào thẻ `<head>` trong [layout.tag](../src/main/webapp/WEB-INF/tags/layout.tag) để thiết lập kết nối TCP/TLS sớm với CDN của Google Fonts và VietQR:
    ```html
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="preconnect" href="https://img.vietqr.io">
    ```

2.  **Tránh Layout Shift (CLS)**:
    *   Rà soát các thẻ `<img>` của hình ảnh sản phẩm trong `store-front.jsp` và `product-detail.jsp`.
    *   Bắt buộc khai báo thuộc tính `width="..."` và `height="..."` trực tiếp trên các thẻ `<img>` hoặc thiết lập tỉ lệ hiển thị cố định qua lớp CSS (ví dụ: `aspect-ratio: 1 / 1`).

3.  **Lazy Loading ảnh**:
    *   Thêm thuộc tính `loading="lazy"` vào các thẻ `<img>` danh sách sản phẩm nằm ngoài màn hình xem đầu tiên (dưới màn hình cuộn).

---

## 3. Kịch bản xác thực (Verification Plan)
1.  **Xác thực Bảo mật**: 
    *   Mở F12 DevTools -> tab Network -> click chọn tài liệu HTML chính.
    *   Kiểm tra các Headers xem đã xuất hiện: `Content-Security-Policy`, `X-Frame-Options`, `X-Content-Type-Options` đúng với cấu hình hay chưa.
2.  **Xác thực Hiệu năng**:
    *   Kiểm tra tab Network xem các file CSS/JS có tham số phiên bản (ví dụ: `global.css?v=1.0`) hay không.
    *   Chạy công cụ Lighthouse Audit trên Chrome để đánh giá điểm số CLS (Cumulative Layout Shift) và tốc độ tải trang (FCP, LCP).
