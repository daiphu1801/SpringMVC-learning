# Spring MVC Domain-Driven Design (DDD) E-Commerce Web Application

Dự án này là một ứng dụng Web E-commerce mẫu được xây dựng trên nền tảng **Spring MVC** phiên bản mới nhất kết hợp với **MyBatis**, cơ sở dữ liệu **Oracle DB**, tuân thủ nguyên lý thiết kế **Domain-Driven Design (DDD)** và chuẩn hóa cấu trúc Frontend & Security nâng cao.

---

## 🌟 Tính năng nổi bật

1. **Kiến trúc Domain-Driven Design (DDD)**: Phân tách rõ ràng các Bounded Contexts (User, Catalog, Order) với cấu trúc các lớp Layered Architecture (Domain, Application, Presentation, Infrastructure).
2. **Bảo mật nâng cao (Client-side & Server-side)**:
   - **XSS Protection**: Mã hóa dữ liệu động đầu ra bằng thẻ `<c:out>` trên toàn bộ các tệp JSP.
   - **Custom CSRF Protection**: Cơ chế sinh/xác thực CSRF Token thủ công thông qua `SecurityInterceptor` và các mẫu thẻ ẩn tại form POST.
   - **Security Headers**: Cấu hình các Header quan trọng: CSP (Content Security Policy), X-Frame-Options (Clickjacking protection), X-Content-Type-Options (nosniff), HSTS (Strict-Transport-Security), Referrer-Policy và Permissions-Policy.
   - **Cookie Security**: Bảo vệ phiên đăng nhập Session Cookie với thuộc tính `HttpOnly=true` và `SameSite=Lax`.
   - **File Upload Filter**: Triển khai `ImageFileValidator` chặn đứng các file nguy hiểm (.exe, .sh, .php) dựa vào cả phần mở rộng (Extension) và kiểu MIME thực tế.
3. **Hiệu năng & Trải nghiệm giao diện**:
   - Phân tách hoàn toàn CSS/JS nội tuyến ra khỏi các tệp JSP thành các Page-specific Static Files.
   - Kỹ thuật **Cache Busting** đính kèm `?v=${appVersion}` tự động dựa trên startup-time.
   - **Lazy Loading** hình ảnh sản phẩm và **Preconnect** kết nối CDN.
   - Hỗ trợ thanh toán một chạm qua cổng VietQR mã hóa động.

---

## 📂 Cấu trúc mã nguồn theo DDD

Thư mục chính nằm trong gói `com.examp.springmvc`:
```text
src/main/java/com/examp/springmvc/
├── auth/                       # Phân hệ Xác thực (Authentication Context)
├── catalog/                    # Phân hệ Danh mục & Sản phẩm (Catalog Context)
├── order/                      # Phân hệ Giỏ hàng & Đơn hàng (Order Context)
├── user/                       # Phân hệ Quản lý Người dùng & Địa chỉ (User Context)
├── shared/                     # Mã nguồn dùng chung (Shared Kernel)
│   ├── domain/                 # Domain events, value objects dùng chung
│   ├── presentation/           # Validator, helpers tầng giao diện (ví dụ: ImageFileValidator)
│   └── infrastructure/         # Các cấu hình dùng chung (MyBatis, WebConfig, WebAppInitializer)
```

Mỗi phân hệ (context) đều phân tách rõ các lớp:
* **Domain**: Model/Entities, Value Objects, Domain Events và Port (Interface) nghiệp vụ.
* **Application**: Use Cases xử lý các Command & Query nhận được từ Presentation.
* **Presentation**: Các Spring MVC Controller điều phối luồng xử lý và dữ liệu JSP.
* **Infrastructure**: Adapter hiện thực hóa các Port (MyBatis Mapper, Cloudinary Storage Adapter, Email Sender).

---

## 🛠️ Yêu cầu môi trường

* **Java**: Phiên bản 21 trở lên (Temurin/OpenJDK 21).
* **Maven**: 3.8+ dùng để build và quản lý thư viện.
* **Oracle Database**: Dự án sử dụng Oracle Thin Driver kết nối CSDL Oracle (mặc định trỏ đến `localhost:1521/FREE`).

---

## ⚙️ Cấu hình dự án

Tất cả cấu hình được đặt tại [application.properties](file:///home/phubd-fsddint/Documents/ProjectSpring/SpringMVC-Demo/src/main/resources/application.properties).

Để ghi đè các cấu hình nhạy cảm trên môi trường phát triển cục bộ, bạn hãy tạo file **`application-local.properties`** tại cùng thư mục tài nguyên (file này đã được cấu hình ẩn trong `.gitignore`):

```properties
# Ví dụ cấu hình CSDL cục bộ
db.username=your_local_user
db.password=your_local_password

# Cấu hình dịch vụ Cloudinary để upload ảnh
cloudinary.cloud-name=your_cloud_name
cloudinary.api-key=your_api_key
cloudinary.api-secret=your_api_secret

# Cấu hình gửi Mail qua SMTP Gmail
mail.username=your_gmail@gmail.com
mail.password=your_app_password
```

---

## 🚀 Các lệnh phát triển thông dụng

### 1. Chạy toàn bộ Unit & Integration Tests
```bash
mvn clean test
```

### 2. Kiểm tra chất lượng mã nguồn & Định dạng (Code Style)
Dự án được tích hợp bộ công cụ tự động kiểm tra code style nghiêm ngặt trước khi push:
```bash
# Kiểm tra định dạng code (Spotless)
mvn spotless:check

# Tự động sửa định dạng code (Spotless)
mvn spotless:apply

# Kiểm tra quy chuẩn viết mã (Checkstyle)
mvn checkstyle:check

# Phân tích tìm lỗi logic/bảo mật tiềm ẩn (SpotBugs)
mvn spotbugs:check
```

### 3. Đóng gói dự án thành WAR
```bash
mvn clean package
```
Sản phẩm đầu ra sẽ nằm tại `target/SpringMVC-Demo.war`.

---

## 🐳 Đóng gói & Chạy bằng Docker

Chúng tôi hỗ trợ build ứng dụng thông qua cơ chế **Multi-stage Dockerfile** giúp đóng gói Tomcat 10 và ứng dụng tự động:

```bash
# Build Docker image
docker build -t springmvc-demo .

# Run Docker container
docker run -p 8080:8080 --name springmvc-app springmvc-demo
```
Sau đó truy cập ứng dụng tại: `http://localhost:8080/`.

---

## 📚 Tài liệu tham khảo sâu
* [Tài liệu Kiến trúc & Design Decisions](file:///home/phubd-fsddint/Documents/ProjectSpring/SpringMVC-Demo/docs/architecture.md)
* [Báo cáo chi tiết các biện pháp Bảo mật](file:///home/phubd-fsddint/Documents/ProjectSpring/SpringMVC-Demo/docs/security_report.md)
* [Cẩm nang kỹ năng bảo mật Frontend (Frontend Security Skills)](file:///home/phubd-fsddint/Documents/ProjectSpring/SpringMVC-Demo/skills/frontend_security_rules.md)
* [Hướng dẫn Deploy & CI/CD tự động](file:///home/phubd-fsddint/Documents/ProjectSpring/SpringMVC-Demo/docs/deployment.md)
