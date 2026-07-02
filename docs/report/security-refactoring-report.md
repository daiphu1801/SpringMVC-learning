# Báo cáo Cải tiến Bảo mật: Phòng chống các Lỗ hổng OWASP Top 10

Báo cáo này tài liệu hóa quá trình phân tích và triển khai đợt nâng cấp bảo mật toàn diện nhằm vá các lỗ hổng bảo mật cấp độ High/Medium/Low theo khuyến nghị từ báo cáo Code Review và tiêu chuẩn bảo mật OWASP Top 10.

---

## 1. Các Vấn đề Bảo mật Đã Giải quyết

### 1.1. Tấn công Session Fixation (High)
* **Vấn đề**: Sau khi người dùng đăng nhập thành công, ID phiên làm việc (Session ID) không được thay đổi. Kẻ tấn công có thể cố tình định dạng trước một Session ID (session hijacking) và lừa người dùng đăng nhập, từ đó chiếm đoạt quyền truy cập của họ.
* **Giải pháp**: 
  - Gọi phương thức `request.changeSessionId()` ngay sau khi người dùng xác thực thành công trong lớp [AuthController.java](../../src/main/java/com/examp/springmvc/auth/presentation/AuthController.java). Cơ chế này giúp huỷ bỏ Session ID cũ và phát hành ID mới nhưng vẫn giữ nguyên dữ liệu thuộc tính của phiên, ngăn chặn hoàn toàn tấn công Session Fixation.

### 1.2. Dò tìm Tài khoản & Tấn công Kênh kề (Account Enumeration & Timing Oracle - Medium)
* **Vấn đề**:
  1. Khi người dùng nhập tên đăng nhập không tồn tại, hệ thống trả lỗi ngay lập tức mà không chạy qua giải thuật băm mật khẩu BCrypt (vốn tốn khoảng 100ms). Việc này tạo ra sự chênh lệch thời gian phản hồi rõ rệt giữa tài khoản có tồn tại và tài khoản không tồn tại.
  2. Hệ thống trả về thông báo lỗi riêng cho tài khoản bị khóa (`"Tài khoản đang bị khóa"`), gián tiếp tiết lộ sự tồn tại của tên đăng nhập trong hệ thống.
* **Giải pháp**:
  - Gộp tất cả các thông báo lỗi đăng nhập (không tìm thấy user, sai mật khẩu, hoặc tài khoản đang bị khóa) thành một thông báo chung: `"Tài khoản hoặc mật khẩu không chính xác"`.
  - Nếu không tìm thấy user hoặc user đang ở trạng thái không hoạt động, hệ thống vẫn chạy giải thuật so khớp mật khẩu bằng cách băm mật khẩu đầu vào với một chuỗi BCrypt dummy cố định có độ dài hợp lệ. Điều này cân bằng thời gian phản hồi của mọi yêu cầu đăng nhập ở mức tương đương (~100-150ms).

### 1.3. Tấn công Brute-force & Thiếu chính sách Rate Limiting (Medium)
* **Vấn đề**: Hệ thống trước đó không hạn chế số lần đăng nhập sai, cho phép kẻ tấn công liên tục thử các mật khẩu khác nhau cho tới khi đoán đúng.
* **Giải pháp**:
  - Tích hợp một cơ chế khóa đăng nhập tạm thời trong bộ nhớ dùng `ConcurrentHashMap` an toàn luồng trong [LoginUseCase.java](../../src/main/java/com/examp/springmvc/auth/application/usecase/LoginUseCase.java).
  - Nếu nhập sai liên tiếp 5 lần cho cùng một username, hệ thống sẽ tạm khoá đăng nhập trong vòng 15 phút.
  - Cơ chế đếm lỗi được áp dụng cho cả các username không tồn tại trong hệ thống để đảm bảo kẻ tấn công không dùng thông điệp báo khóa để dò tìm tài khoản tồn tại.

### 1.4. Điểm yếu Độ mạnh Mã hoá & Cookie Bảo mật (Low)
* **Vấn đề**: 
  1. Độ mạnh thuật toán băm (BCrypt Cost Factor) sử dụng giá trị mặc định là 10, chưa đủ an toàn trước phần cứng tính toán mạnh hiện nay.
  2. Token CSRF được so khớp bằng toán tử so sánh thông thường (`.equals`), tiềm ẩn nguy cơ bị dò rỉ qua tấn công kênh kề thời gian.
  3. Cấu hình cookie phiên `Secure` bị tắt cứng (`cookieConfig.setSecure(false)`) và không có cấu hình session timeout linh hoạt.
  4. Lộ thông tin tài khoản mẫu `adminnn/123456` trực tiếp trên giao diện [login.jsp](../../src/main/webapp/WEB-INF/views/auth/login.jsp) ở môi trường Production.
* **Giải pháp**:
  - Nâng BCrypt Cost Factor từ `10` lên `12` tại [BCryptPasswordHasher.java](../../src/main/java/com/examp/springmvc/auth/infrastructure/security/BCryptPasswordHasher.java).
  - Sử dụng so sánh thời gian hằng số `java.security.MessageDigest.isEqual(...)` để kiểm tra Token CSRF tại [SecurityInterceptor.java](../../src/main/java/com/examp/springmvc/auth/infrastructure/security/SecurityInterceptor.java).
  - Tách các cấu hình môi trường ra [application.properties](../../src/main/resources/application.properties): `app.demo.fill.enabled=true`, `app.cookie.secure=false`, và `app.session.timeout-minutes=30`.
  - Cập nhật [WebAppInitializer.java](../../src/main/java/com/examp/springmvc/shared/infrastructure/config/WebAppInitializer.java) tự động đọc các thuộc tính cấu hình khi khởi động để thiết lập động cờ cookie `Secure` và thời gian tự động hết hạn phiên (Session Timeout) trên Servlet container.
  - Giao diện đăng nhập chỉ hiển thị khung tài khoản mẫu khi thuộc tính `app.demo.fill.enabled` được cấu hình là `true` (mặc định bật ở môi trường phát triển và tắt ở production).

---

## 2. Chi tiết các File đã Chỉnh sửa

### 2.1. Tầng Domain & Application (Logic bảo mật)
* **[MODIFY] [LoginUseCase.java](../../src/main/java/com/examp/springmvc/auth/application/usecase/LoginUseCase.java):**
  - Triển khai kiểm tra và tính toán hash dummy cân bằng thời gian.
  - Thêm cơ chế đếm số lần đăng nhập sai và áp dụng chính sách khoá 15 phút sau 5 lần thất bại.
* **[MODIFY] [BCryptPasswordHasher.java](../../src/main/java/com/examp/springmvc/auth/infrastructure/security/BCryptPasswordHasher.java):**
  - Chuyển cấu hình sinh salt sang `BCrypt.gensalt(12)`.

### 2.2. Tầng Presentation & Config (Giao diện & Cấu hình máy chủ)
* **[MODIFY] [SecurityInterceptor.java](../../src/main/java/com/examp/springmvc/auth/infrastructure/security/SecurityInterceptor.java):**
  - Tích hợp constant-time comparison cho token CSRF.
  - Inject thuộc tính `@Value("${app.demo.fill.enabled}")` để truyền xuống UI JSP.
* **[MODIFY] [WebAppInitializer.java](../../src/main/java/com/examp/springmvc/shared/infrastructure/config/WebAppInitializer.java):**
  - Đọc file cấu hình `application.properties` để cấu hình động session cookie và session timeout cho ServletContext.
* **[MODIFY] [login.jsp](../../src/main/webapp/WEB-INF/views/auth/login.jsp):**
  - Ẩn khung thông tin tài khoản mẫu thông qua biến điều khiển `demoFillEnabled`.
* **[MODIFY] [application.properties](../../src/main/resources/application.properties):**
  - Bổ sung các biến cấu hình: `app.demo.fill.enabled`, `app.cookie.secure`, `app.session.timeout-minutes`.

### 2.3. Bộ Kiểm thử tự động (Unit Test Suite)
* **[MODIFY] [LoginUseCaseTest.java](../../src/test/java/com/examp/springmvc/auth/application/usecase/LoginUseCaseTest.java):**
  - Stubbing thêm phương thức `passwordHasher.check` để mô phỏng chính xác hành vi băm dummy.
  - Cập nhật các khẳng định khẳng định thông báo lỗi đồng bộ mới.
  - Viết mới test case `shouldLockoutUserAfterFiveFailures` để kiểm tra độ tin cậy của tính năng tự động khóa tài khoản sau 5 lần đăng nhập sai liên tiếp.

---

## 3. Kết quả Xác thực & Đánh giá Chất lượng

Toàn bộ mã nguồn sau cải tiến đã vượt qua toàn bộ quy trình kiểm thử và đánh giá tĩnh của dự án:

1. **Spotless Format (`mvn spotless:apply`):**
   * Đạt trạng thái **SUCCESS**. Mọi tệp tin chỉnh sửa đều tuân thủ định dạng chung của hệ thống.
2. **Checkstyle (`mvn checkstyle:check`):**
   * Đạt trạng thái **SUCCESS** với **0 lỗi vi phạm**.
3. **SpotBugs (`mvn spotbugs:check`):**
   * Đạt trạng thái **SUCCESS** với **0 lỗi vi phạm**.
4. **Kiểm thử Đơn vị (`mvn clean verify`):**
   * Toàn bộ **158 / 158** unit tests chạy thành công tốt đẹp. Không phát sinh lỗi hồi quy nào.

