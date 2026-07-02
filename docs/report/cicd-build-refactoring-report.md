# Báo cáo Cải tiến Hệ thống Build và Tích hợp liên tục (CI/CD)

Báo cáo này tài liệu hóa quá trình phân tích và triển khai đợt tối ưu hóa hệ thống build Maven, nâng cấp độ tương thích các thư viện kiểm thử, và tích hợp bộ phân tích mã nguồn tĩnh SpotBugs vào luồng CI/CD tự động trên GitHub Actions.

---

## 1. Các Vấn đề Đã Giải quyết

### 1.1. Bất tương thích phiên bản JUnit Jupiter & Mockito
* **Vấn đề**:
  - Dự án sử dụng phiên bản JUnit Jupiter giả lập `6.0.0` (phiên bản ngoài ma trận hỗ trợ chính thức của Spring Framework 6.1.5).
  - Không sử dụng Bill of Materials (BOM) để quản lý phiên bản cho JUnit 5 và Mockito, dẫn đến nguy cơ xung đột phiên bản các thư viện kiểm thử phụ thuộc (như junit-jupiter-api, junit-jupiter-params, mockito-core, mockito-junit-jupiter...).
  - Tồn tại khai báo dependency trùng lặp không đáng có `junit-jupiter-engine` phiên bản `6.0.0`.
* **Giải pháp**:
  - Hạ cấp phiên bản JUnit Jupiter về phiên bản ổn định **`5.10.2`** tương thích hoàn hảo với Spring 6.1.5.
  - Khai báo phần tử `<dependencyManagement>` nạp các BOM chính thức:
    - `org.junit:junit-bom:5.10.2`
    - `org.mockito:mockito-bom:5.11.0`
  - Loại bỏ các cờ `<version>` ghi cứng ở các dependency con `junit-jupiter` và `mockito-junit-jupiter` để đồng bộ hoàn toàn theo BOM.
  - Xóa bỏ khai báo thừa `junit-jupiter-engine` (do `junit-jupiter` đã tự động kéo engine tương ứng về).

### 1.2. SpotBugs không được kích hoạt trên môi trường CI
* **Vấn đề**:
  - Plugin phân tích lỗi tĩnh SpotBugs đã được cấu hình trong tệp `pom.xml` nhưng chỉ chạy thủ công hoặc khi gọi pha `verify` tại máy local.
  - Tệp quy trình CI/CD `.github/workflows/ci-cd.yml` gọi riêng rẽ `mvn test` và `mvn clean package` mà không thực hiện quét lỗi tĩnh SpotBugs, tạo cơ hội cho mã nguồn không an toàn hoặc lỗi logic lọt lên nhánh chính.
* **Giải pháp**:
  - Tích hợp thêm bước chạy `Verify Bugs (SpotBugs)` gọi lệnh `mvn spotbugs:check` vào pipeline của GitHub Actions ngay sau bước kiểm tra phong cách viết code (Checkstyle).
  - Quy trình này sẽ ngăn chặn lập tức (Build Failure) và chặn gộp (Block Merge) các Pull Request vi phạm quy chuẩn an toàn.

---

## 2. Chi tiết các File đã Chỉnh sửa

### 2.1. Cấu hình Build Maven
* **[MODIFY] [pom.xml](../../pom.xml):**
  - Cập nhật các thuộc tính `<junit.jupiter.version>` và `<mockito.version>`.
  - Thêm block `<dependencyManagement>` cấu hình các BOM.
  - Bỏ version cứng ở các dependency tương ứng và xóa dependency trùng lặp `junit-jupiter-engine`.

### 2.2. Quy trình Tích hợp liên tục
* **[MODIFY] [.github/workflows/ci-cd.yml](../../.github/workflows/ci-cd.yml):**
  - Thêm step quét lỗi `Verify Bugs (SpotBugs)` chạy lệnh `mvn spotbugs:check`.

---

## 3. Kết quả Đánh giá & Kiểm thử

* **Spotless Format (`mvn spotless:apply`):**
  - Trạng thái: **SUCCESS**. Tệp tin `pom.xml` đã được định dạng chuẩn XML quy định.
* **Kiểm tra Tĩnh Code (Checkstyle / SpotBugs):**
  - Trạng thái: **SUCCESS** (Không phát hiện bất kỳ cảnh báo hay lỗi vi phạm nào).
* **Kiểm thử Đơn vị (`mvn clean verify`):**
  - Trạng thái: **SUCCESS** (Toàn bộ **158 / 158** tests chạy thành công tốt đẹp).
