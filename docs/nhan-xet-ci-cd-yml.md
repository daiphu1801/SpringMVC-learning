# Nhận xét file `ci-cd.yml`

## 1. Kết luận chung

File YAML không có lỗi cú pháp nghiêm trọng, nhưng luồng CI/CD hiện tại có một số điểm có thể khiến pipeline:

- Chạy kiểm tra không đúng thứ tự.
- Báo thành công dù khởi tạo database bị lỗi.
- Không upload được báo cáo ZAP khi ZAP trả mã lỗi.
- Scan một Docker image nhưng lại build và push một image khác.
- Ứng dụng không nhận được cấu hình kết nối Oracle.
- Làm lộ `JSESSIONID` trong log CI.

---

## 2. Lỗi thứ tự chạy Maven

Hiện tại workflow đang chạy:

```yaml
- run: mvn spotless:check
- run: mvn checkstyle:check
- run: mvn spotbugs:check
- run: mvn test
- run: mvn clean package -DskipTests
```

### Vấn đề

`spotbugs:check` thường cần bytecode trong `target/classes`, nhưng project chưa được compile trước đó.

Sau khi chạy `mvn test`, lệnh:

```bash
mvn clean package -DskipTests
```

lại xóa thư mục `target`, bao gồm kết quả compile và báo cáo test trước đó.

### Cách sửa đề xuất

Nên cấu hình Spotless, Checkstyle và SpotBugs trong `pom.xml`, sau đó chỉ chạy:

```yaml
- name: Build, Test and Verify
  run: mvn -B -ntp clean verify
```

Ý nghĩa:

- `-B`: chạy Maven ở chế độ CI.
- `-ntp`: ẩn thanh tiến trình tải dependency.
- `clean verify`: compile, test, package và chạy các plugin kiểm tra.

Nếu các plugin chưa được gắn vào Maven lifecycle, có thể dùng tạm:

```yaml
- name: Build, Test and Verify
  run: |
    mvn -B -ntp clean verify
    mvn -B -ntp spotless:check checkstyle:check spotbugs:check
```

---

## 3. Job `security-scan` không có file WAR từ job trước

Mỗi job GitHub Actions chạy trên một máy runner riêng.

Do đó thư mục `target` của job `build-and-test` không tồn tại trong job `security-scan`.

Nếu Dockerfile có dòng:

```dockerfile
COPY target/SpringMVC-Demo.war ...
```

thì bước Docker build sẽ thất bại.

### Cách sửa

Thêm bước download artifact trước khi build Docker:

```yaml
- name: Download WAR Artifact
  uses: actions/download-artifact@v4
  with:
    name: SpringMVC-Demo-WAR
    path: target
```

Nếu Dockerfile là multi-stage build và tự build Maven bên trong Docker thì không cần bước này.

---

## 4. Docker image được scan không phải image được push

Trong `security-scan`:

```bash
docker build -t springmvc-demo-test .
```

Trong `deploy-docker`, image lại được build lần nữa:

```yaml
uses: docker/build-push-action@v5
```

Như vậy image được ZAP scan và image được push lên GHCR không hoàn toàn chắc chắn giống nhau.

### Cách làm tốt hơn

Luồng chuẩn nên là:

1. Build image một lần.
2. Chạy ứng dụng từ image đó.
3. Scan image đó.
4. Nếu scan đạt thì push chính image đó.

Có thể gộp build, scan và push vào cùng một job hoặc export image thành artifact để dùng lại.

---

## 5. SQLPlus có thể lỗi nhưng pipeline vẫn tiếp tục

SQLPlus mặc định có thể không trả exit code lỗi khi câu SQL thất bại.

Ví dụ:

- Table đã tồn tại.
- Sequence không tồn tại.
- Vi phạm unique.
- Thiếu quyền.
- Sai tên cột.

Pipeline có thể tiếp tục dù schema chưa được tạo đúng.

### Cách sửa

Thêm:

```sql
WHENEVER OSERROR EXIT FAILURE ROLLBACK
WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK
```

Ví dụ:

```yaml
- name: Initialize Oracle Database Schema
  run: |
    {
      echo "WHENEVER OSERROR EXIT FAILURE ROLLBACK"
      echo "WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK"
      cat src/test/resources/test-schema.sql
      echo "EXIT SUCCESS"
    } | docker exec -i oracle sqlplus -L -S       your_database_username/your_database_password@//localhost:1521/FREE
```

Phần insert dữ liệu cũng nên có xử lý lỗi:

```yaml
docker exec -i oracle sqlplus -L -S   your_database_username/your_database_password@//localhost:1521/FREE <<'SQL'

WHENEVER OSERROR EXIT FAILURE ROLLBACK
WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK

INSERT INTO APP_USERS (...);
INSERT INTO APP_CATEGORIES (...);
INSERT INTO APP_PRODUCTS (...);

COMMIT;
EXIT SUCCESS;
SQL
```

---

## 6. Ứng dụng chưa được truyền cấu hình Oracle

Hiện tại app được chạy bằng:

```yaml
docker run -d --network=host --name app springmvc-demo-test
```

Nhưng không truyền:

- Database URL.
- Database username.
- Database password.
- Profile dành cho CI.

### Cách sửa

```yaml
- name: Run Application in Docker container
  run: |
    docker run -d       --network=host       --name app       -e DB_URL="jdbc:oracle:thin:@//localhost:1521/FREE"       -e DB_USERNAME="${{ secrets.DB_USERNAME }}"       -e DB_PASSWORD="${{ secrets.DB_PASSWORD }}"       springmvc-demo-test
```

Trong file cấu hình ứng dụng:

```properties
db.url=${DB_URL}
db.username=${DB_USERNAME}
db.password=${DB_PASSWORD}
```

Không nên ghi username và password thật trực tiếp trong workflow.

---

## 7. Kiểm tra ứng dụng chưa đủ chặt

Hiện tại:

```bash
curl -s http://localhost:8080/login
```

`curl -s` vẫn có thể trả exit code thành công khi server trả HTTP `404` hoặc `500`.

### Cách sửa

```yaml
- name: Wait for application to start
  run: |
    echo "Waiting for app to start..."

    timeout 90s bash -c '
      until curl --fail --silent --show-error         http://localhost:8080/login >/dev/null;
      do
        echo "Waiting for application..."
        docker logs --tail 20 app || true
        sleep 3
      done
    '

    echo "Application is responding successfully."
```

---

## 8. Chưa kiểm tra đăng nhập ZAP thành công

Workflow hiện tại lấy CSRF token, gửi request login rồi lấy `JSESSIONID`, nhưng không kiểm tra:

- Có lấy được CSRF token không.
- Login có bị trả `403` không.
- Session có thực sự đăng nhập không.
- User có quyền ADMIN không.

Ngoài ra, Spring Security mặc định thường dùng tên tham số:

```text
_csrf
```

không phải:

```text
csrfToken
```

trừ khi project đã cấu hình custom.

### Kiểm tra token

```bash
CSRF_TOKEN=$(grep -oE 'name="_csrf" value="[^"]+"'   login_page.html | head -1 | cut -d'"' -f4)

if [ -z "$CSRF_TOKEN" ]; then
  echo "Cannot extract CSRF token"
  exit 1
fi
```

### Kiểm tra đăng nhập

Sau khi login, nên gọi một endpoint yêu cầu quyền ADMIN:

```bash
curl --fail --silent   -b cookies.txt   http://localhost:8080/admin >/dev/null
```

Nếu request bị chuyển về `/login` hoặc trả `403`, workflow phải dừng.

### Không nên in session ra log

Nên xóa:

```bash
cat cookies.txt
echo "Extracted JSESSIONID: $JSESSIONID"
```

Có thể mask session:

```bash
echo "::add-mask::$JSESSIONID"
```

---

## 9. Báo cáo ZAP có thể không được upload

ZAP Baseline có thể trả exit code khác `0` khi có cảnh báo.

Khi bước scan bị fail, bước upload report phía sau sẽ bị bỏ qua nếu không dùng:

```yaml
if: always()
```

### Cách sửa

```yaml
- name: Run OWASP ZAP Baseline Scan
  id: zap
  run: |
    set +e

    docker run --rm       -v "${{ github.workspace }}:/zap/wrk/:rw"       --network=host       -t ghcr.io/zaproxy/zaproxy:stable       zap-baseline.py       -t http://localhost:8080/       -r report_html.html       -z "-configfile /zap/wrk/options.prop"

    ZAP_EXIT_CODE=$?
    echo "exit_code=$ZAP_EXIT_CODE" >> "$GITHUB_OUTPUT"

    exit 0

- name: Upload ZAP Security Report
  if: always()
  uses: actions/upload-artifact@v4
  with:
    name: zapreport
    path: report_html.html
    if-no-files-found: error

- name: Enforce ZAP result
  if: always() && steps.zap.outputs.exit_code != '0'
  run: |
    echo "ZAP exited with code ${{ steps.zap.outputs.exit_code }}"
    exit 1
```

Nếu muốn chỉ fail khi có lỗi nghiêm trọng và bỏ qua cảnh báo mức WARN, có thể cấu hình file rule riêng cho ZAP.

---

## 10. ZAP Baseline không phải Active Scan

`zap-baseline.py` chỉ thực hiện spider và passive scan.

Nó phù hợp để phát hiện:

- Thiếu CSP.
- Thiếu HSTS.
- Thiếu X-Frame-Options.
- Thiếu X-Content-Type-Options.
- Cookie thiếu thuộc tính bảo mật.
- Một số lỗi cấu hình HTTP.

Nó không kiểm tra sâu các lỗi như:

- SQL Injection.
- Path Traversal.
- Command Injection.
- Một số dạng XSS cần payload chủ động.

Nếu đây là môi trường test biệt lập, có thể cân nhắc:

```bash
zap-full-scan.py
```

Không nên chạy Full Scan trên môi trường production vì nó có thể gửi request tấn công và làm thay đổi dữ liệu.

---

## 11. Đường dẫn WAR đang cố định

Hiện tại:

```yaml
path: target/SpringMVC-Demo.war
```

Nếu Maven tạo file có version như:

```text
SpringMVC-Demo-1.0.0.war
```

thì artifact sẽ không được tìm thấy.

### Cách sửa

```yaml
- name: Upload WAR Artifact
  uses: actions/upload-artifact@v4
  with:
    name: SpringMVC-Demo-WAR
    path: target/*.war
    if-no-files-found: error
    retention-days: 7
```

Hoặc cố định tên trong `pom.xml`:

```xml
<build>
    <finalName>SpringMVC-Demo</finalName>
</build>
```

---

## 12. Quyền GitHub Token đang rộng hơn cần thiết

Hiện tại:

```yaml
permissions:
  contents: read
  packages: write
  issues: write
```

Nhưng:

- `issues: write` không được sử dụng.
- `packages: write` chỉ cần khi push image.
- Build và scan chỉ cần `contents: read`.

### Cách sửa

Ở cấp workflow:

```yaml
permissions:
  contents: read
```

Riêng job deploy:

```yaml
deploy-docker:
  permissions:
    contents: read
    packages: write
```

---

## 13. Một số điểm nên tối ưu

### Bỏ QEMU nếu không build đa kiến trúc

Nếu chỉ build cho `linux/amd64`, có thể bỏ:

```yaml
- uses: docker/setup-qemu-action@v3
```

Nếu cần multi-architecture thì khai báo:

```yaml
platforms: linux/amd64,linux/arm64
```

### Nâng Docker Build Push Action

Nên đổi:

```yaml
uses: docker/build-push-action@v5
```

thành:

```yaml
uses: docker/build-push-action@v6
```

### Chỉ deploy từ một branch

Hiện tại cả `main` và `master` đều có thể ghi đè tag `latest`.

Nên dùng:

```yaml
if: github.event_name == 'push' && github.ref == 'refs/heads/main'
```

### Pin version Docker image

Các tag như:

```text
gvenzl/oracle-free:slim
ghcr.io/zaproxy/zaproxy:stable
```

có thể thay đổi theo thời gian.

Trong CI production nên pin version hoặc digest cụ thể để kết quả build ổn định hơn.

---

## 14. Thứ tự ưu tiên sửa

### Cần sửa ngay

1. Thay nhiều lệnh Maven rời rạc bằng `mvn clean verify`.
2. Thêm `WHENEVER SQLERROR EXIT`.
3. Upload ZAP report bằng `if: always()`.
4. Kiểm tra đăng nhập ZAP thực sự thành công.
5. Truyền cấu hình Oracle vào app container.
6. Bảo đảm image được scan là image được push.

### Nên sửa tiếp

1. Giảm quyền `GITHUB_TOKEN`.
2. Đổi đường dẫn WAR thành `target/*.war`.
3. Nâng `docker/build-push-action` lên `v6`.
4. Chỉ deploy từ một branch.
5. Bỏ QEMU nếu không build multi-architecture.
6. Dùng GitHub Secrets thay cho mật khẩu ghi trực tiếp trong YAML.

---

## 15. Luồng CI/CD đề xuất

```text
Checkout source
      ↓
Setup JDK và Maven cache
      ↓
mvn clean verify
      ↓
Upload WAR
      ↓
Build Docker image một lần
      ↓
Start Oracle test container
      ↓
Wait for Oracle health
      ↓
Initialize schema
      ↓
Start application container
      ↓
Check application health
      ↓
Authenticate test account
      ↓
Run ZAP scan
      ↓
Upload ZAP report
      ↓
Nếu tất cả đạt → Push chính image đã scan lên GHCR
```
