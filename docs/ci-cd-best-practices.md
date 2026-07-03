# Quy chuẩn Thiết kế CI/CD Pipeline cho Dự án

Tài liệu này tổng hợp các quy chuẩn và thực hành tốt nhất (Best Practices) khi xây dựng và cấu hình pipeline CI/CD cho dự án (đặc biệt đối với các ứng dụng Java/Spring, sử dụng Docker, kiểm thử tích hợp cơ sở dữ liệu và quét bảo mật tự động).

---

## 1. Tổ chức Job và Luồng dữ liệu (Pipeline Architecture)

* **Quy tắc tuần tự vs. Song song**:
  * Hãy chạy **song song (Parallel)** khi các task không có sự phụ thuộc lẫn nhau (ví dụ: chạy lint/test riêng rẽ cho Frontend và Backend).
  * Hãy chạy **tuần tự (Sequential)** hoặc **gộp chung vào 1 job** khi các bước phụ thuộc chặt chẽ vào nhau (ví dụ: Biên dịch -> Đóng gói -> Khởi chạy -> Quét bảo mật -> Deploy).
* **Đảm bảo tính nhất quán của Artifact**:
  * Sản phẩm (tệp WAR/JAR, Docker image) được sử dụng để kiểm thử, quét bảo mật và triển khai (deploy) phải là **duy nhất**. Tránh việc biên dịch/build lại Docker image nhiều lần ở các job khác nhau vì có thể dẫn tới sai lệch phiên bản hoặc thay đổi bất ngờ trong mã nguồn.
  * Nguyên tắc cốt lõi: **Build một lần -> Kiểm thử và quét trên chính build đó -> Triển khai sản phẩm đó**.

---

## 2. Tiêu chuẩn Biên dịch và Kiểm tra Mã nguồn (Build & Quality Verification)

* **Gộp các bước kiểm tra chất lượng**:
  * Tránh chạy nhiều lệnh build riêng biệt làm xóa đi cache hoặc tốn thời gian compile lại. Hãy kết hợp kiểm tra định dạng và logic chất lượng code trong cùng một lệnh (ví dụ: `mvn -B -ntp clean verify spotless:check checkstyle:check spotbugs:check`).
* **Sử dụng chế độ Non-Interactive (Chế độ CI)**:
  * Thêm flag `-B` (`--batch-mode`) và `-ntp` (`--no-transfer-progress`) đối với Maven để log sạch sẽ, không ghi đè tiến trình tải thư viện làm đầy và rối log của GitHub Actions.
* **Quy tắc Fail-Fast**:
  * Đặt các bước kiểm tra định dạng tĩnh nhẹ nhàng (Format code với Spotless, quy chuẩn Checkstyle) lên trước. Nếu phát hiện vi phạm định dạng, pipeline dừng ngay lập tức để tiết kiệm thời gian chạy các bài kiểm thử nặng phía sau.

---

## 3. Tích hợp và Kiểm thử với Cơ sở dữ liệu (Database Integration)

* **Khởi tạo Database cô lập**:
  * Luôn sử dụng cơ sở dữ liệu dạng container (như Docker) được khởi chạy độc lập và mới hoàn toàn cho mỗi lượt chạy CI để tránh xung đột dữ liệu giữa các lượt chạy song song.
* **Kiểm tra kết nối thông minh (Smart Readiness Check)**:
  * Không chỉ đợi cổng port mở (vì lúc đó DB mới chỉ bật engine, chưa kịp chạy script khởi tạo user).
  * Hãy kiểm tra tính sẵn sàng bằng cách **thử kết nối trực tiếp bằng chính tài khoản ứng dụng** (username/password của app) cho đến khi thành công.
* **Bắt lỗi SQL khi tạo Schema**:
  * Khi chạy kịch bản SQL khởi tạo cơ sở dữ liệu trên CI, luôn thêm các khai báo bắt lỗi (ví dụ trong SQL*Plus: `WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK`). Nếu không, script lỗi (trùng khóa, thiếu cột...) vẫn sẽ báo thành công và làm sai lệch kết quả kiểm thử phía sau.

---

## 4. Quản lý Cấu hình và Thông tin Nhạy cảm (Configuration & Secrets)

* **Sử dụng Placeholders**:
  * Cấu hình kết nối cơ sở dữ liệu trong mã nguồn không được ghi cứng (hardcode). Hãy dùng placeholder hỗ trợ biến môi trường (ví dụ: `db.url=${DB_URL:default_value}`).
* **Tách biệt thông tin nhạy cảm**:
  * Các thông tin đăng nhập, token, mật khẩu tuyệt đối không đưa vào file YAML của CI/CD. Hãy sử dụng **GitHub Secrets** và truyền vào container dưới dạng biến môi trường.

---

## 5. Quét bảo mật tự động (Security Scanning)

* **Xử lý Graceful lỗi của công cụ quét**:
  * Các công cụ quét bảo mật (như OWASP ZAP) thường trả về exit code khác `0` (ví dụ: `2` cho cảnh báo - Warning).
  * Hãy bắt exit code thủ công (`set +e` trong shell), lưu lại kết quả, đảm bảo báo cáo quét được tải lên (upload artifact) thành công rồi mới quyết định có dừng pipeline hay không.
* **Bảo vệ báo cáo quét (Report Upload)**:
  * Luôn đặt cấu hình `if: always()` cho bước upload báo cáo bảo mật. Nếu bước quét bị lỗi hoặc phát hiện lỗ hổng bảo mật nghiêm trọng làm dừng pipeline, bạn vẫn sẽ nhận được file báo cáo chi tiết để sửa lỗi.

---

## 6. Bảo mật và Phân quyền trên CI/CD (Pipeline Security)

* **Nguyên tắc đặc quyền tối thiểu (Least Privilege)**:
  * Giới hạn quyền hạn của `GITHUB_TOKEN` mặc định. Ví dụ: chỉ cấp quyền ghi (`packages: write`) cho job cần đẩy docker image lên registry, các job còn lại chỉ nên có quyền đọc (`contents: read`).
* **Che giấu thông tin nhạy cảm trong Log (Masking Secrets)**:
  * Trong các bước chạy kiểm thử tự động cần lấy session token (như `JSESSIONID`), hãy sử dụng cú pháp bảo vệ log của GitHub Actions:
    ```bash
    echo "::add-mask::$TOKEN_VALUE"
    ```
    Điều này ngăn chặn việc token bảo mật vô tình bị in ra màn hình console của pipeline và bị lộ ra ngoài.

---

## 7. Triển khai và Phân phối (Deployment)

* **Kiểm soát điều kiện Deploy**:
  * Chỉ thực hiện deploy hoặc push Docker image lên môi trường Production/Registry khi mã nguồn được merge vào nhánh chính thức (như `main` hoặc `master`) và toàn bộ các bước kiểm tra chất lượng trước đó đã thành công 100%.
  * Sử dụng các tag rõ ràng cho Docker image bao gồm: `:latest` và thẻ định danh theo commit hash (ví dụ: `:${{ github.sha }}`) để dễ dàng kiểm tra và rollback (quay lui) phiên bản khi cần thiết.
