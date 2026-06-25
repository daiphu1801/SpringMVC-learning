# Hướng dẫn Deploy dự án (Review nhanh & Miễn phí)

Tài liệu này hướng dẫn cách deploy nhanh dự án **Spring MVC (WAR)** kết hợp với cơ sở dữ liệu lên môi trường Cloud hoàn toàn miễn phí để phục vụ mục đích review/đánh giá của Mentor.

---

## Cách 1: Deploy lên Railway / Render bằng Docker (Khuyên Dùng - Nhanh Nhất)

Railway và Render là các dịch vụ PaaS cho phép deploy trực tiếp từ Github thông qua Docker. Cách này hoàn toàn tự động hóa (CI/CD) và miễn phí ở gói Starter.

### Bước 1: Tạo `Dockerfile` tại thư mục gốc của dự án
Tạo file tên là `Dockerfile` (không có phần mở rộng) tại thư mục `/home/phubd-fsddint/Documents/ProjectSpring/SpringMVC-Demo/Dockerfile` với nội dung sau:

```dockerfile
# Sử dụng Tomcat làm máy chủ chạy file WAR
FROM tomcat:10-jdk21-openjdk-slim

# Xóa các ứng dụng mặc định của Tomcat để giải phóng bộ nhớ
RUN rm -rf /usr/local/tomcat/webapps/*

# Sao chép file WAR đã build vào thư mục chạy chính của Tomcat
COPY target/SpringMVC-Demo.war /usr/local/tomcat/webapps/ROOT.war

# Mở cổng 8080
EXPOSE 8080

# Chạy Tomcat
CMD ["catalina.sh", "run"]
```

### Bước 2: Build file WAR cục bộ
Trước khi push code, hãy chạy lệnh build để sinh ra file `.war` trong thư mục `target`:
```bash
mvn clean package -DskipTests
```

### Bước 3: Deploy lên Railway (Hoặc Render)
1. **Đẩy mã nguồn lên GitHub.**
2. Đăng ký/Đăng nhập vào [Railway.app](https://railway.app) bằng tài khoản GitHub.
3. Chọn **New Project** -> **Deploy from GitHub repo** -> Chọn repository dự án của bạn.
4. Railway sẽ tự động phát hiện `Dockerfile`, build và sinh ra một đường dẫn URL public (ví dụ: `https://your-project.up.railway.app`) để bạn gửi cho mentor.

*Lưu ý về Database:*
* Bạn có thể tạo thêm dịch vụ Database (PostgreSQL/MySQL) ngay trên Railway bằng nút **New** -> **Database**.
* Nếu dùng database này, hãy cấu hình lại các biến môi trường trong file cấu hình Spring hoặc truyền trực tiếp qua mục **Variables** trên giao diện Railway (ví dụ: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`).

---

## Cách 2: Oracle Cloud Infrastructure (OCI) Free Tier (Tối Ưu Cho Oracle DB)

Nếu dự án bắt buộc phải chạy trên **Oracle Database** với các cấu hình sequence/table đặc thù, gói Always Free của Oracle Cloud là lựa chọn tốt nhất.

### Lợi ích nhận được (Miễn phí trọn đời):
* **2 VM Compute (AMD):** Máy chủ ảo để cài đặt Tomcat chạy file `.war`.
* **2 Autonomous Databases:** Hệ quản trị CSDL Oracle chuẩn doanh nghiệp trên đám mây.

### Các bước triển khai:
1. Đăng ký tài khoản tại [Oracle Cloud Free Tier](https://www.oracle.com/cloud/free/).
2. **Tạo Database:** Tạo một instance **Autonomous Transaction Processing (ATP)**. Tải xuống file Wallet cấu hình kết nối bảo mật.
3. **Tạo VM Instance:** Tạo máy chủ ảo Ubuntu/Oracle Linux.
4. **Cấu hình VM:**
   * Cài đặt Java 21: `sudo apt update && sudo apt install openjdk-21-jdk`
   * Tải và giải nén Apache Tomcat 10.
   * Mở cổng `8080` trên bảng điều khiển Oracle Cloud (Security List) và trên firewall của VM.
5. **Deploy:** Upload file `SpringMVC-Demo.war` và file Wallet lên VM, cấu hình kết nối DB và khởi động Tomcat.

---

## Cách 3: AWS Elastic Beanstalk (Tomcat Pre-configured)

Giải pháp của Amazon Web Services (AWS) dành riêng cho các ứng dụng Web truyền thống.

### Các bước triển khai:
1. Đăng nhập vào AWS Console (Tài khoản Free Tier).
2. Tìm kiếm dịch vụ **Elastic Beanstalk**.
3. Chọn **Create Application**:
   * **Platform:** Chọn **Tomcat**.
   * **Application code:** Chọn **Upload your code** và tải lên file `SpringMVC-Demo.war` từ máy của bạn.
4. AWS sẽ tự động cấu hình Server, Load Balancer và cung cấp link truy cập sau ít phút.
