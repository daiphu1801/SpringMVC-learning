# Hướng dẫn Deploy & Cấu hình CI/CD Tự động hóa

Tài liệu này hướng dẫn cách deploy tối ưu dự án **Spring MVC (WAR)** sử dụng Docker Multi-stage và tự động hóa toàn bộ quy trình CI/CD qua GitHub Actions.

---

## 1. Tối ưu hóa Dockerfile (Multi-stage Build)

Thay vì build file `.war` thủ công trên máy cá nhân rồi mới copy vào Docker (dễ lỗi không đồng bộ phiên bản Java/Maven), chúng ta sử dụng **Multi-stage Build**. Toàn bộ quá trình build và chạy được đóng gói độc lập trong Docker container.

File [Dockerfile](../Dockerfile) đã được cấu hình tại thư mục gốc:

```dockerfile
# STAGE 1: Build WAR file inside Docker
FROM maven:3.9.6-eclipse-temurin-21-jammy AS builder
WORKDIR /build

# Cache Maven dependencies trước để tăng tốc các lượt build sau
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy code nguồn và build file WAR
COPY src ./src
RUN mvn package -DskipTests

# STAGE 2: Deploy WAR file to Tomcat 10
FROM tomcat:10.1-jdk21-openjdk-slim
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=builder /build/target/SpringMVC-Demo.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]
```

---

## 2. Cấu hình CI/CD Tự động hóa qua GitHub Actions

Quy trình tự động hóa đã được thiết lập tại file [.github/workflows/ci-cd.yml](../.github/workflows/ci-cd.yml). Khi bạn push code lên nhánh `main` hoặc `master`, GitHub Actions sẽ tự động:

1. **Kiểm tra Style code** (`mvn spotless:check` và `mvn checkstyle:check`).
2. **Chạy Unit Tests** (`mvn test`) để đảm bảo chất lượng nghiệp vụ.
3. **Đóng gói dự án** (`mvn package`).
4. **Build & Push Docker Image** lên GitHub Container Registry (GHCR) hoàn toàn miễn phí.
5. **Kích hoạt Deploy** sang Cloud Hosting (Render/Railway/VPS).

### Cách thiết lập tự động hóa deploy sang các Cloud phổ biến:

### A. Triển khai tự động lên Render.com
1. Đăng nhập vào [Render](https://render.com), tạo một dịch vụ **Web Service**.
2. Kết nối tới repo GitHub của bạn và chọn:
   * **Runtime**: `Docker`.
3. Vào phần **Settings** của Web Service trên Render, tìm mục **Deploy Hook**. Copy đường dẫn URL đó.
4. Trên GitHub repo của bạn, vào **Settings** -> **Secrets and variables** -> **Actions** -> Tạo một Repository Secret mới:
   * **Name**: `RENDER_DEPLOY_HOOK_URL`
   * **Value**: Đường dẫn URL Deploy Hook vừa copy.
5. Bỏ dấu comment (`#`) phần kích hoạt Deploy Hook ở cuối file `.github/workflows/ci-cd.yml`.

### B. Triển khai tự động lên Railway.app
1. Đăng nhập [Railway.app](https://railway.app) và tạo project kết nối với Github Repo.
2. Railway sẽ tự phát hiện Dockerfile và tự động deploy lại mỗi khi có push mới lên nhánh `main`. Không cần cấu hình workflow thủ công.

---

## 3. Quản lý cấu hình mật (Secrets & Database Credentials)

⚠️ **Không bao giờ push file `application-local.properties` chứa API Key hay Password thực tế lên Git.**

### Cách cấu hình an toàn trên Production/Cloud:
Thay vì tạo file local trên Cloud, hãy khai báo các biến môi trường (Environment Variables) trực tiếp trên bảng điều khiển của Cloud Provider (Render / Railway / AWS). Spring Framework sẽ tự động map các biến môi trường này vào các thuộc tính cấu hình tương ứng:

| Key trong application.properties | Biến môi trường tương ứng trên Cloud |
|---|---|
| `db.url` | `DB_URL` |
| `db.username` | `DB_USERNAME` |
| `db.password` | `DB_PASSWORD` |
| `mail.username` | `MAIL_USERNAME` |
| `mail.password` | `MAIL_PASSWORD` |
| `vietqr.account-number` | `VIETQR_ACCOUNT_NUMBER` |

*Ví dụ:* Chỉ cần cấu hình biến `DB_PASSWORD` trên mục **Variables** của Render/Railway, Spring Boot/Spring MVC sẽ tự động đọc giá trị đó ghi đè lên cấu hình mặc định.
