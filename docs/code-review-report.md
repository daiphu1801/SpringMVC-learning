## 1. Tóm tắt

| Tiêu chí | Điểm | Ghi chú |
|---|---|---|
| Kiến trúc DDD / Clean Architecture | 7.5/10 | Khung tốt, nhưng query layer rò rỉ infra + nhiều aggregate anemic |
| Domain modeling | 7/10 | Order rất tốt; User/Product/Category còn anemic, setter công khai |
| Bảo mật (OWASP) | 6/10 | Nền tảng tốt (CSRF, headers, BCrypt) nhưng còn lỗ hổng High/Medium |
| Infrastructure / Persistence | 7.5/10 | Sạch SQLi, BigDecimal chuẩn; còn N+1 + email trong tx |
| Presentation | 7/10 | Controller mỏng, CSRF tốt; còn XSS + access control |
| Build / CI / Test | 6.5/10 | Bộ tooling tốt nhưng CI bỏ sót SpotBugs, thiếu integration test |
| **Tổng quan** | **~7/10** | **Khá — Giỏi so với fresher; cần sửa nhóm Critical/High trước khi "đạt"** |

---

## 2. Vấn đề ưu tiên xử lý
| # | Mức độ | Vấn đề | Vị trí |
|---|---|---|---|
| 1 | 🔴 Critical | `User` aggregate phơi toàn bộ public setter (`setRole`, `setStatus`...) → vỡ encapsulation/invariant | `user/domain/model/User.java:107-181` |
| 2 | 🔴 Critical | Application (query) import thẳng MyBatis mapper + DbEntity của Infrastructure → vi phạm Dependency Rule | `*/application/**/query/*UseCase.java`, `auth/.../LoginUseCase.java` |
| 3 | 🔴 Critical | Gửi email / upload Cloudinary chạy **đồng bộ trong transaction DB** (giữ connection, mail gửi trước commit) | `OrderEventListener.java`, `OrderPersistenceAdapter.java:52-56`, `CreateProductUseCase.java:38` |
| 4 | 🟠 High | Broken Access Control: `GET /users` không chặn non-admin → lộ toàn bộ email/phone mọi user (PII) | `SecurityInterceptor.java:60-71` |
| 5 | 🟠 High | Reflected/Stored XSS: `${error}` và `${product.name}` render thô, message có nhúng input người dùng | `product-form.jsp:13`, `product-detail.jsp:5`, ... |
| 6 | 🟠 High | Session Fixation: không xoay session ID sau khi login | `AuthController.java:45-47` |
| 7 | 🟠 High | Commit nhầm `target/` (125 file `.class`/WAR) + file rác `[Help`, `.iml` vào git | repo root |
| 8 | 🟠 High | SpotBugs không chạy trên CI (pipeline gọi `package`, không gọi `verify`) | `.github/workflows/ci-cd.yml:28-38` |
| 9 | 🟠 High | Lộ tài khoản admin mẫu `adminnn / 123456` ngay trên trang login | `login.jsp:42-47` |
| 10 | 🟡 Medium | `PlaceOrder` không kiểm tra trạng thái sản phẩm/tồn kho + N+1 query | `PlaceOrderUseCase.java`, `OrderPersistenceAdapter.java:71-89` |

---

## 3. Chi tiết theo tầng

### 3.1. Tầng Domain
- **[Critical]** `User.java:107-181` — public setter cho mọi field (kể cả `role`, `status`). Ai cũng có thể `setRole("ADMIN")` mà không qua `validate()`. → Bỏ setter, thay bằng method nghiệp vụ (`changeRole`, `changeEmail`...) tự validate.
- **[High]** `User.java:58-71` — constructor không gọi `validate()`; có thể tạo `User` với email null. → Gọi `validate()` trong constructor hoặc dùng factory `register(...)`.
- **[High]** `User.role`/`status` dùng `String` thay vì enum (đã làm enum cho `OrderStatus`/`ProductStatus` nhưng quên User). → Tạo `UserRole`, `UserStatus`.
- **[High]** Domain Event ôm nguyên aggregate **mutable** (`OrderPlacedEvent` giữ `Order`, `UserRegisteredEvent` giữ `User`). Event mất ý nghĩa "ảnh chụp tại thời điểm xảy ra". `@SuppressFBWarnings` chỉ giấu cảnh báo. → Event chỉ mang dữ liệu cần thiết đã copy (id, total, status).
- **[Medium]** `Email.java:9` regex lỏng (`a@b` lọt) + không `toLowerCase()` → có thể tạo trùng user. `ShippingAddress` thiếu `equals/hashCode` + dead code `zero()`. `Product`/`Category` anemic (chỉ getter).
- **[Low]** `Order.markPaymentPaid()` đổi state nhưng không phát event (các chuyển trạng thái khác đều phát). `Password` chỉ check `>= 6` ký tự, không giới hạn trên.

### 3.2. Tầng Application (Use Case / CQRS)
- **[Critical]** Query use case import thẳng `infrastructure.mapper.*` + `DbEntity` (vd `FindProductByIdUseCase.java:3-4`, `LoginUseCase.java:6-8`). Application không còn độc lập, không test được nếu thiếu infra. Mâu thuẫn ngay trong dự án: nhánh command lại làm đúng (chỉ dùng port). → Định nghĩa **query port** trong domain/application, adapter ở infrastructure implement.
- **[Critical]** Gọi external trong transaction (xem mục 3.4 — chung với infra). Upload Cloudinary trong `@Transactional` → ảnh orphan nếu rollback.
- **[High]** `LoginInputPort` trả về **`User` domain entity** ra controller → lộ aggregate. → Trả `AuthenticatedUserDTO`.
- **[High]** `PlaceOrderUseCase` không validate `ProductStatus`, không có khái niệm tồn kho (không chống oversell), và `findById` từng sản phẩm → **N+1**.
- **[Medium]** Check trùng username + `save()` không atomic (race condition) và hash bcrypt **trước** khi check trùng (lãng phí). → Đảo thứ tự + unique constraint DB. `UpdateUserUseCase` tạo `new User()` rồi `setId` thay vì mutate `existing` → mất field không được set lại.
- **[Medium]** `UpdateOrderStatusUseCase` dùng `String action` + switch (primitive obsession); nhánh `cancel` không check ownership trong khi `CancelOrderUseCase` có → 2 đường hủy luật khác nhau.
- **[Low]** `toDto`/`toItemDto` của Order copy y hệt ở 3 file (DRY). `UserDTO` còn field `password`. `OrderDTO` chứa logic format tiếng Việt (thuộc view).

### 3.3. Tầng Presentation (Controller / JSP)
- **[High]** `GET /users` lộ PII (mục 2.4). `SecurityInterceptor.java:60-71` chỉ chặn write action.
- **[High]** XSS qua `${error}` thô (`product-form.jsp:13`, `product-list.jsp:18`, `checkout.jsp:16`, `order-detail.jsp:42`...) và `${product.name}` trong `<title>`. Message lỗi có nhúng `ext`/`contentType` từ input người dùng (`ImageFileValidator.java:64,71`). → Dùng `<c:out>`/`fn:escapeXml`.
- **[Medium]** Logout là `GET` có side-effect (`AuthController.java:55`) → vi phạm REST + bị CSRF logout qua `<img src=".../logout">`. → Chuyển sang POST có CSRF.
- **[Medium]** `ImageFileValidator` dựa MIME từ header (spoof được), không check magic byte; `ProductController` dùng thẳng `getOriginalFilename()` → nguy cơ path traversal. → Đọc magic byte + sinh tên file UUID phía server.
- **[Medium]** Gần như không có Bean Validation (`@Valid`/`BindingResult`); `OrderController.java:109-113` parse `paymentMethod` lỗi thì **nuốt exception, mặc định CASH** (đổi lựa chọn của user âm thầm).
- **[Medium]** CQRS tách không nhất quán — chỉ module `user` có Command/Query controller riêng.

### 3.4. Tầng Infrastructure (Persistence / Adapter / Config)
- **[Critical/High]** `OrderEventListener` đồng bộ (không `@Async`/`@TransactionalEventListener`), event publish trong `save()` mà `save()` nằm trong `@Transactional` → **gửi mail + query user chạy trong transaction DB**, giữ connection Hikari suốt thời gian SMTP, và mail gửi **trước commit** (rollback thì khách vẫn nhận mail). → `@TransactionalEventListener(AFTER_COMMIT)` + `@Async` + `@EnableAsync`.
- **[High]** N+1 ở `findByUserId()`/`findAll()` (`OrderPersistenceAdapter.java:71-89`) — lặp `findByOrderId` từng order. → JOIN + `<collection>` resultMap hoặc `WHERE ORDER_ID IN (...)`.
- **[Medium]** `ProductMapper.xml:54-80` dùng `INNER JOIN` category → product có category bị xóa sẽ "biến mất". → `LEFT JOIN` hoặc chặn xóa category còn product.
- **[Medium]** `CloudinaryImageStorageAdapter.java:24-49` không đóng `InputStream` (resource leak); guard "chưa cấu hình" so sánh sai chuỗi (`your_cloud_name` vs `your_cloudinary_cloud_name`) → fallback vô dụng; dùng `System.out/err` thay vì logger.
- **[Low]** `MyBatisConfig.java:66` `initializationFailTimeout(-1)` che lỗi DB ở production (fail-slow). `OrderStatus.valueOf` không guard null. Timezone không nhất quán (User/Product dùng `SYSTIMESTAMP`, Order dùng `LocalDateTime` của app).

### 3.5. Bảo mật (OWASP Top 10) — tổng hợp
- **[High]** Session Fixation — không `request.changeSessionId()` sau login (`AuthController.java:45-47`).
- **[Medium]** Lộ danh sách user cho non-admin (mục 2.4).
- **[Medium]** Account enumeration + timing oracle ở login: tài khoản khóa có message riêng; user không tồn tại return ngay không chạy BCrypt (chênh thời gian). → Chạy BCrypt dummy cân bằng thời gian, dùng message gộp.
- **[Medium]** XSS output `${error}`/`${product.name}` (mục 3.3).
- **[Medium]** Default admin `adminnn/123456` lộ trên `login.jsp:42-47`, không rate-limit/lockout, password chỉ cần ≥6 ký tự. → Bỏ box demo ở production, đặt password mạnh, thêm chính sách + lockout.
- **[Low]** CSRF chỉ kiểm POST + logout GET; BCrypt cost factor mặc định 10 (nên ≥12); cookie `Secure=false` (có TODO); so sánh CSRF token không constant-time; chưa cấu hình `session-timeout`.

### 3.6. Build / CI-CD / Test / Cấu hình
- **[High/Blocker]** `target/` (125 file) + `[Help` + `.iml` bị track trong git. `.gitignore` có `target/` nhưng file đã add trước khi ignore. → `git rm -r --cached target "[Help" SpringMVC-Demo.iml` rồi commit.
- **[High]** SpotBugs không chạy trên CI (pipeline gọi `package`, không `verify`). → Đổi sang `mvn clean verify` hoặc thêm `mvn spotbugs:check`.
- **[High]** JUnit Jupiter **6.0.0** ngoài ma trận hỗ trợ chính thức của Spring 6.1.5 (Spring 6.1 build trên JUnit 5.x/Platform 1.x). → Hạ về `junit-jupiter 5.10.x` và thêm `junit-bom`/`mockito-bom` (hiện **không có BOM nào**).
- **[High]** Lệch yêu cầu leader: dự án là **Spring Framework thuần** (war + `WebAppInitializer`), **không phải Spring Boot 3.x** — không auto-config, không embedded server, không actuator/health. → Cần làm rõ: giữ Spring thuần (chủ đích học) hay phải migrate Boot.
- **[Medium]** ZAP scan trên CI gần như vô nghĩa (app cần Oracle để boot nhưng CI không cấp DB; ZAP dùng `|| true` không gate). Dockerfile chạy **root**, thiếu `HEALTHCHECK`/`.dockerignore`. Thiếu hẳn **integration test** cho mapper XML/adapter (toàn mock DB → SQL sai vẫn xanh).
- **[Low]** Spotless glob `scripts/*.sh` sai vị trí (thật ở `src/scripts/`). Dependency thừa (`junit-jupiter-engine`, `spring-core`, `cover-annotations`). `jbcrypt 0.4` (2014, không maintain) → cân nhắc `spring-security-crypto`. README lộ path máy cá nhân (`README.md:57`).