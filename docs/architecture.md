# Tài liệu Kiến trúc Hệ thống (Clean Architecture, DDD & CQRS)

Dự án này được thiết kế theo mô hình **Clean Architecture** kết hợp với **CQRS** (Command Query Responsibility Segregation) và các nguyên lý **DDD** (Domain-Driven Design). Dưới đây là mô tả chi tiết về chức năng, nhiệm vụ, các dịch vụ bên thứ ba (Third-party Services) và luồng hoạt động của từng tầng trong hệ thống.

---

## 1. Tổng quan cấu trúc các tầng (Layers Structure)

Thứ tự phụ thuộc của các tầng đi từ ngoài vào trong: **Presentation/Infrastructure ──► Application ──► Domain**. Tầng bên trong không biết gì về sự tồn tại của tầng bên ngoài.

```
┌─────────────────────────────────────────────────────────────┐
│                 INFRASTRUCTURE / PRESENTATION               │
│  (Controllers, Security Interceptors, MyBatis, Adapters)    │
└──────────────────────────────┬──────────────────────────────┘
                               │ phụ thuộc
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                         APPLICATION                         │
│  (Use Cases / Input Ports, Command & Query DTOs)            │
└──────────────────────────────┬──────────────────────────────┘
                               │ phụ thuộc
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                           DOMAIN                            │
│  (Entities, Value Objects, Domain Events, Output Ports)     │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. Chi tiết vai trò từng tầng

### A. Tầng Domain (Domain Layer)
Là phần lõi cốt lõi chứa các quy tắc nghiệp vụ quan trọng nhất của ứng dụng. Tầng này không phụ thuộc vào bất kỳ thư viện hay framework bên ngoài nào (như Spring, MyBatis).

*   **Entities (Aggregate Roots):** (ví dụ: `User.java`, `Order.java`)
    *   Đại diện cho các thực thể mang bản sắc (identity) riêng biệt.
    *   Chứa trạng thái nghiệp vụ và các phương thức tự thay đổi trạng thái (ví dụ: `activate()`, `confirm()`, `deliver()`).
    *   Lưu trữ danh sách sự kiện nghiệp vụ (`domainEvents`).
*   **Value Objects (VOs):** (ví dụ: `Email.java`, `Password.java`, `ShippingAddress.java`)
    *   Là các thuộc tính bất biến (immutable), không có identity riêng.
    *   Tự xác thực tính hợp lệ của dữ liệu ngay khi khởi tạo (self-validation).
*   **Domain Events:** (ví dụ: `UserRegisteredEvent.java`, `OrderPlacedEvent.java`)
    *   Đại diện cho một sự kiện nghiệp vụ quan trọng đã xảy ra trong quá khứ.
*   **Output Ports (Interfaces):** (ví dụ: `UserPersistencePort.java`, `NotificationPort.java`, `ImageStoragePort.java`)
    *   Các giao diện định nghĩa cách giao tiếp với các hệ thống bên ngoài. Tầng Domain chỉ định nghĩa cổng (port), phần triển khai thực tế (adapter) sẽ nằm ở tầng ngoài cùng.

---

### B. Tầng Application (Application Layer)
Đóng vai trò điều phối luồng nghiệp vụ của ứng dụng (Orchestrator). Tầng này nhận yêu cầu từ bên ngoài, lấy dữ liệu từ Domain Port, thực thi nghiệp vụ thông qua Domain Entities, sau đó lưu lại.

*   **Input Ports / Use Cases:** (ví dụ: `CreateUserUseCase.java`, `PlaceOrderUseCase.java`)
    *   Thực hiện nghiệp vụ cụ thể cho từng ca sử dụng.
    *   Quản lý giao dịch (`@Transactional`).
*   **Commands & Queries DTOs:** (ví dụ: `PlaceOrderCommand.java`, `OrderDTO.java`)
    *   `Command` đại diện cho yêu cầu thay đổi trạng thái hệ thống.
    *   `Query` đại diện cho yêu cầu đọc dữ liệu.
    *   `DTO` là cấu trúc dữ liệu trả về cho phía UI/API.

---

### C. Tầng Infrastructure / Presentation (Cơ sở hạ tầng & Hiển thị)
Chứa các thành phần phụ thuộc vào framework, thư viện bên ngoài và giao diện người dùng.

*   **Presentation (Controllers / JSP):** (ví dụ: `OrderController.java`, `checkout.jsp`)
    *   Nhận HTTP Request từ phía client, chuyển đổi tham số sang dạng Command/Query và gọi Use Case.
*   **Persistence (MyBatis & Adapter):**
    *   `OrderDbEntity.java`: Object ánh xạ trực tiếp 1-1 với bảng DB.
    *   `OrderPersistenceAdapter.java`: Triển khai cổng `OrderPersistencePort`.
*   **Event Listeners:** (ví dụ: `OrderEventListener.java`)
    *   Lắng nghe các domain events được phát ra để thực hiện các tác vụ phụ (như gửi email, ghi log hệ thống).

---

## 3. Tích hợp Dịch vụ Bên Thứ Ba (Third-Party Services Integration)

Để tránh sự phụ thuộc chặt chẽ (tight coupling) vào các dịch vụ bên ngoài, dự án áp dụng mô hình **Ports & Adapters** đóng vai trò là một **Anti-Corruption Layer (ACL - Lớp chống mục nát)**. Domain Layer chỉ giao tiếp qua Port (Interface), còn cấu hình và SDK của dịch vụ bên thứ ba được cô lập hoàn toàn ở tầng Infrastructure.

```
                  ┌────────────────────────────────────────┐
                  │              DOMAIN LAYER              │
                  │  - ImageStoragePort (Interface)        │
                  │  - NotificationPort (Interface)        │
                  └───────────────────┬────────────────────┘
                                      │
                         ┌────────────┴────────────┐
                         ▼ (Implement / Adapter)   ▼
    ┌──────────────────────────────────┐   ┌──────────────────────────────────┐
    │       INFRASTRUCTURE LAYER       │   │       INFRASTRUCTURE LAYER       │
    │  - CloudinaryImageStorageAdapter │   │  - JavaMailNotificationAdapter   │
    │  - Sử dụng SDK Cloudinary        │   │  - Sử dụng JavaMailSender (SMTP) │
    └────────────────┬─────────────────┘   └────────────────┬─────────────────┘
                     │ REST API                             │ SMTP Protocol
                     ▼                                      ▼
             ┌───────────────┐                      ┌───────────────┐
             │  Cloudinary   │                      │  SMTP Server  │
             │ (Image Cloud) │                      │    (Gmail)    │
             └───────────────┘                      └───────────────┘
```

### 3.1. Dịch vụ lưu trữ hình ảnh Cloudinary (Image Hosting)
* **Mục đích**: Lưu trữ và quản lý hình ảnh sản phẩm trong Catalog.
* **Cổng giao tiếp (Port)**: `ImageStoragePort.java` (Domain Layer).
* **Triển khai (Adapter)**: `CloudinaryImageStorageAdapter.java` (Infrastructure Layer).
* **Cấu hình**: Thông tin kết nối được quản lý thông qua cấu hình `CloudinaryConfig.java` nạp từ `application-local.properties`:
  * `cloudinary.cloud-name`: Tên tài khoản Cloudinary.
  * `cloudinary.api-key`: API Key xác thực.
  * `cloudinary.api-secret`: Khóa bí mật.

### 3.2. Dịch vụ gửi thư điện tử SMTP (Mail Sender)
* **Mục đích**: Gửi các email thông báo giao dịch (Đặt hàng thành công, Xác nhận thanh toán, Giao hàng thành công) tới người dùng.
* **Cổng giao tiếp (Port)**: `NotificationPort.java` (Domain Layer).
* **Triển khai (Adapter)**: `JavaMailNotificationAdapter.java` (Infrastructure Layer).
* **Cấu hình**: Nạp cấu hình thông qua `MailConfig.java` cấu hình SMTP Gmail/Outlook:
  * `mail.host` & `mail.port`: Địa chỉ máy chủ (ví dụ: `smtp.gmail.com:587`).
  * `mail.username` & `mail.password`: Tài khoản và Mật khẩu ứng dụng (App Password).

### 3.3. Dịch vụ Thanh toán VietQR (VietQR Dynamic Code)
* **Mục đích**: Tạo mã QR động tự động điền số tài khoản, số tiền và nội dung chuyển khoản cho đơn hàng.
* **Cách hoạt động**: Tích hợp trực tiếp tại tầng View bằng cách sinh URL động hướng tới Public API của VietQR (`img.vietqr.io`). Không cần cài đặt SDK bên thứ ba, tối ưu tốc độ phản hồi.
* **Cấu hình**:
  * `vietqr.bank-code`: Mã ngân hàng nhận (ví dụ: `MB`, `VCB`).
  * `vietqr.account-number`: Số tài khoản thụ hưởng.
  * `vietqr.account-name`: Tên chủ tài khoản (tiếng Việt không dấu).

---

## 4. Luồng hoạt động của hệ thống (Workflows)

### Luồng Ghi dữ liệu (Write / Command Flow)
Quy trình thực thi khi người dùng đặt hàng mới:

```mermaid
sequenceDiagram
    autonumber
    actor Client as Khách hàng
    participant Ctrl as OrderController (Presentation)
    participant UC as PlaceOrderUseCase (Application)
    participant Dom as Order Aggregate (Domain)
    participant Port as OrderPersistencePort (Domain)
    participant Adapt as OrderPersistenceAdapter (Infrastructure)
    participant DB as Oracle Database

    Client->>Ctrl: POST /orders/checkout (Chọn CASH/VIETQR)
    Ctrl->>UC: execute(PlaceOrderCommand)
    
    rect rgb(240, 245, 255)
        note over UC, Dom: Xử lý nghiệp vụ tại Domain Core
        UC->>Dom: Order.place(...) (Khởi tạo, tính tổng tiền)
        Dom-->>UC: Trả về đối tượng Order (Tích lũy OrderPlacedEvent)
    end
    
    UC->>Port: save(order)
    Port->>Adapt: Thực thi save()
    Adapt->>DB: INSERT vào APP_ORDERS & APP_ORDER_ITEMS (MyBatis)
    Adapt->>Adapt: Lấy Domain Events từ Order ra
    Adapt->>Adapt: Phát sự kiện qua ApplicationEventPublisher
    Adapt-->>UC: Trả về kết quả
    UC-->>Ctrl: Thành công
    Ctrl-->>Client: Chuyển hướng sang Payment (VietQR) hoặc Xem chi tiết (Cash)
```

---

### Luồng Đọc dữ liệu (Read / Query Flow)
Quy trình thực thi khi người dùng xem chi tiết đơn hàng:

```mermaid
sequenceDiagram
    autonumber
    actor Client as Khách hàng
    participant Ctrl as OrderController
    participant UC as FindOrderByIdUseCase
    participant Port as OrderPersistencePort
    participant Adapt as OrderPersistenceAdapter
    participant DB as Oracle Database

    Client->>Ctrl: GET /orders/{id}
    Ctrl->>UC: execute(id)
    UC->>Port: findById(id)
    Port->>Adapt: findById()
    Adapt->>DB: SELECT query bằng MyBatis
    DB-->>Adapt: Trả về OrderDbEntity
    Adapt->>Adapt: Chuyển sang Order domain
    Adapt-->>UC: Trả về Order domain
    UC->>UC: Chuyển đổi sang OrderDTO (Lọc bớt dữ liệu nhạy cảm)
    UC-->>Ctrl: Trả về OrderDTO
    Ctrl-->>Client: Render view order-detail.jsp
```

---

## 5. Luồng xử lý sự kiện bất đồng bộ (Domain Event Handling Flow)

Khi đơn hàng được lưu thành công, các side-effect được kích hoạt thông qua sự kiện miền:

```
[OrderPersistenceAdapter] (Phát sự kiện)
          │
          ▼
 [Spring ApplicationEventPublisher] (Phân phối sự kiện)
          │
          ▼
   [OrderEventListener] (Lắng nghe sự kiện)
          │
          ▼
   ┌──────┴──────────────┐
   ▼                     ▼
[Ghi Log hệ thống]   [Gửi Email xác nhận qua SMTP]
                     (NotificationPort -> JavaMailNotificationAdapter)
```

**Ưu điểm**: Phân tách luồng xử lý chính (tạo đơn hàng) và luồng phụ (gửi mail). Nếu hệ thống gửi mail gặp sự cố, đơn hàng của khách hàng vẫn được tạo thành công bình thường.

---

## 6. Hướng dẫn thiết kế Frontend An toàn & Sạch sẽ (Frontend Security & Design Guidelines)

Để đảm bảo hệ thống không bị ảnh hưởng bởi các lỗ hổng bảo mật phía máy khách (Client-side vulnerabilities) và tuân thủ kiến trúc phân tách tầng hiển thị (Presentation Layer), các nhà phát triển bắt buộc phải tuân theo các quy tắc sau khi viết mã Javascript (JS) và CSS:

### A. Phòng chống tấn công DOM XSS (Cross-Site Scripting)
DOM XSS xảy ra khi dữ liệu đầu vào (từ URL, input, API...) được đưa trực tiếp vào các hàm sinh HTML động ("sinks") mà không qua kiểm duyệt.
*   **KHÔNG sử dụng `innerHTML` hoặc `document.write`** để gán trực tiếp dữ liệu động.
*   **Giải pháp an toàn**: 
    *   Sử dụng `element.textContent` hoặc `element.innerText` khi hiển thị văn bản thuần túy. Trình duyệt sẽ tự động encode để ngăn chặn thực thi script.
    *   Sử dụng `document.createTextNode` khi muốn chèn text node vào DOM.
    *   Nếu dữ liệu dạng tĩnh nhưng có chứa một vài thẻ HTML định dạng (như `<b>`, `<code>`), hãy tách cấu trúc dữ liệu thành các trường riêng biệt (ví dụ: `{ label: "...", text: "..." }`) và tạo các element con tương ứng bằng `document.createElement()`.
    *   Trường hợp bắt buộc phải render chuỗi HTML động từ nguồn ngoài, phải sử dụng thư viện làm sạch (như DOMPurify) để loại bỏ hoàn toàn các thẻ và thuộc tính nguy hiểm trước khi gán.

### B. Phòng chống tấn công CSS Injection (và DOM CSS Manipulation)
CSS Injection cho phép kẻ tấn công chèn mã CSS độc hại nhằm đánh cắp thông tin nhạy cảm (ví dụ sử dụng thuộc tính `background-image` trỏ tới máy chủ của hacker để đánh cắp token ẩn).
*   **KHÔNG gán trực tiếp thuộc tính style bằng chuỗi chưa được kiểm soát** thông qua `element.style.cssText` hoặc `element.setAttribute('style', ...)`.
*   **Giải pháp an toàn**:
    *   **Sử dụng Class thay vì Inline Styles**: Luôn định nghĩa sẵn style trong file CSS, và thay đổi trạng thái giao diện bằng cách thêm/xóa class thông qua `element.classList.add('className')` hoặc `element.classList.remove('className')`.
    *   Nếu cần gán các thuộc tính cụ thể động (như tọa độ chuột, giá trị màu sắc), hãy gán trực tiếp cho từng thuộc tính con đã được validate kiểu dữ liệu (ví dụ: `element.style.left = safeX + 'px'`, `element.style.backgroundColor = safeColor`).

### C. Phân tách hoàn toàn View và Resource (No Inline CSS/JS)
Tuyệt đối không chèn mã CSS và JS trực tiếp vào trong các tệp tin JSP nhằm giữ cho mã nguồn dễ bảo trì, tối ưu hóa caching của trình duyệt và nâng cao bảo mật (dễ thiết lập Content Security Policy - CSP).
*   **Không nhúng thẻ `<style>` hoặc `<script>`** bên trong các tệp JSP của trang con.
*   **Không sử dụng inline style** dạng `<div style="...">` trên các thẻ HTML. Thay vào đó, hãy sử dụng các CSS utility classes có sẵn trong `global.css` hoặc tạo class chuyên biệt trong tệp CSS của trang đó.
*   **Không sử dụng Inline Event Handlers**: Tuyệt đối không viết `onclick="..."`, `onchange="..."` trên HTML. Hãy đăng ký lắng nghe sự kiện động trong tệp JS bằng `element.addEventListener('click', handler)`.
*   **Cách tích hợp CSS/JS**:
    *   Đặt các tệp CSS tại `/resources/css/pages/[page-name].css`.
    *   Đặt các tệp JS tại `/resources/js/pages/[page-name].js` (hoặc `/resources/js/` chung cho toàn hệ thống).
    *   Sử dụng thẻ `<jsp:attribute name="head">` trong `layout.tag` để khai báo chèn CSS/JS riêng của từng trang vào thẻ `<head>` của layout gốc.

