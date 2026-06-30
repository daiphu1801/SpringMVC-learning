# Tài liệu Học tập: Bounded Context Order & Payment (DDD, Clean Architecture & Domain Events)

Tài liệu này hướng dẫn chi tiết cách áp dụng **Domain-Driven Design (DDD)** và **Clean Architecture** thông qua phân hệ **Đơn hàng & Thanh toán (Order & Payment Bounded Context)** được triển khai trong dự án.

---

## 1. Bản đồ Nghiệp vụ Miền (Domain Design Analysis)

### 1.1. Tại sao `Order` là một Aggregate Root?

`Order` (Đơn hàng) đóng vai trò là một **Aggregate Root (AR)** vì nó là chủ thể quản lý vòng đời của chính nó và các thành phần phụ thuộc (`OrderItem`):

- **Tính nhất quán nghiệp vụ (Business Invariants)**:
  - Đơn hàng bắt buộc phải có ít nhất một sản phẩm (`OrderItem`).
  - Tổng tiền phải bằng tổng thành tiền từng sản phẩm cộng lại.
  - Địa chỉ nhận hàng và phương thức thanh toán phải hợp lệ và không được để trống.
- **Cổng giao tiếp duy nhất**: Mọi thao tác thay đổi trạng thái đơn hàng bắt buộc phải gọi thông qua các phương thức nghiệp vụ của `Order` như `confirm()`, `markShipping()`, `deliver()`, `cancel()`, `markPaymentPaid()`. Tầng ứng dụng **không bao giờ** được thay đổi trực tiếp thuộc tính `status` hay `paymentStatus`.

### 1.2. Tại sao `OrderItem` là Entity (không phải Value Object)?

- Khác với `ShippingAddress` (Value Object), mỗi `OrderItem` đại diện cho một dòng sản phẩm trong đơn hàng và có danh tính cục bộ (`id`) trong phạm vi của `Order`.
- Nó chứa **snapshot (ảnh chụp)** thông tin sản phẩm tại thời điểm mua (tên, SKU, giá bán tại thời điểm đó). Điều này đảm bảo nếu Admin thay đổi giá sản phẩm ở **Catalog Context**, giá lịch sử trong đơn hàng cũ vẫn được giữ nguyên.

### 1.3. Tại sao `ShippingAddress` là Value Object?

- `ShippingAddress` không có danh tính nghiệp vụ riêng — nó chỉ là dữ liệu bổ trợ mô tả địa điểm nhận hàng của đơn hàng.
- Nó **bất biến (immutable)**: một khi đơn hàng đã đặt, địa chỉ giao hàng không thể bị sửa đổi.
- So sánh hai `ShippingAddress` bằng giá trị cấu trúc (structural equality), không phải bằng ID.

### 1.4. Máy trạng thái Đơn hàng (Order State Machine)

Tất cả quy tắc chuyển trạng thái được mã hóa cứng trong `Order.java` để ngăn chặn trạng thái bất hợp lệ:

```
            ┌─────────┐
   Đặt hàng │ PENDING │◄─── Trạng thái ban đầu sau khi place()
            └────┬────┘
                 │ confirm()
                 ▼
          ┌────────────┐
          │ CONFIRMED  │
          └─────┬──────┘
                │ markShipping()
                ▼
          ┌──────────┐
          │ SHIPPING │
          └─────┬────┘
                │ deliver()
                ▼
          ┌───────────┐
          │ DELIVERED │ ──── Gửi email Giao hàng thành công
          └───────────┘

     (cancel() chỉ cho phép từ PENDING hoặc CONFIRMED)
```

---

## 2. Kiến trúc Clean Architecture áp dụng trong phân hệ Order

Chiều phụ thuộc luôn đi từ ngoài vào trong. Tầng Domain **không biết gì** về Spring, MyBatis, hay JavaMail:

```
┌────────────────────────────────────────────────────────────────────┐
│               TẦNG PRESENTATION (Cổng ngoài cùng)                 │
│  OrderController.java      → Nhận HTTP Request, gọi Use Case      │
│  checkout.jsp / payment-vietqr.jsp → Giao diện người dùng         │
└──────────────────────────────┬─────────────────────────────────────┘
                               │ phụ thuộc
                               ▼
┌────────────────────────────────────────────────────────────────────┐
│                      TẦNG APPLICATION                              │
│  PlaceOrderUseCase          → Điều phối đặt hàng mới              │
│  ConfirmVietQRPaymentUseCase → Điều phối xác nhận thanh toán QR   │
│  FindOrdersByUserUseCase    → Truy vấn lịch sử đơn hàng           │
│  PlaceOrderCommand / OrderDTO → DTOs truyền dữ liệu               │
└───────────┬──────────────────────────────────────┬─────────────────┘
            │ gọi                                  │ gọi
            ▼                                      ▼
┌─────────────────────────┐          ┌──────────────────────────────┐
│  TẦNG DOMAIN (Lõi)      │          │   TẦNG INFRASTRUCTURE        │
│  Order.java (AR)        │◄─────────┤  OrderPersistenceAdapter     │
│  OrderItem.java (Entity)│ implement│  (triển khai OrderPersistPort│
│  ShippingAddress (VO)   │  cổng    │   & OrderMapper MyBatis)     │
│  OrderPlacedEvent       │          │  JavaMailNotificationAdapter  │
│  OrderPersistencePort   │◄─────────┤  (triển khai NotificationPort│
│  NotificationPort       │ implement│   & gửi mail SMTP thật)      │
└─────────────────────────┘          │  OrderEventListener           │
                                     │  (lắng nghe DomainEvents)    │
                                     └──────────────────────────────┘
```

---

## 3. Đảo ngược phụ thuộc (Dependency Inversion) — Trường hợp `NotificationPort`

Đây là ví dụ điển hình nhất về DIP (Dependency Inversion Principle) trong phân hệ Order:

### Vấn đề nếu KHÔNG áp dụng DIP:
```java
// Tầng Domain biết về Spring Mail — Vi phạm Clean Architecture
import org.springframework.mail.javamail.JavaMailSender; // ❌ SAI
```

### Giải pháp với Ports & Adapters:

| Tầng | File | Vai trò |
|---|---|---|
| **Domain** | `NotificationPort.java` | Chỉ định nghĩa Interface thuần Java, không import thư viện nào |
| **Application** | `OrderEventListener.java` | Tiêm (inject) `NotificationPort` vào để gọi — không biết triển khai cụ thể |
| **Infrastructure** | `JavaMailNotificationAdapter.java` | Triển khai `NotificationPort`, chứa toàn bộ logic SMTP và JavaMailSender |

```java
// Domain Layer — thuần Java, không có framework
public interface NotificationPort {
    void sendOrderPlacedNotification(Order order, String recipientEmail);
    void sendPaymentConfirmation(Order order, String recipientEmail);
    void sendDeliverySuccess(Order order, String recipientEmail);
}

// Infrastructure Layer — chứa toàn bộ chi tiết kỹ thuật
@Component
public class JavaMailNotificationAdapter implements NotificationPort {
    // Triển khai gửi mail thật qua SMTP...
}
```

**Kết quả**: Muốn chuyển từ SMTP sang Amazon SES, chỉ cần tạo `AmazonSESNotificationAdapter implements NotificationPort` ở Infrastructure — **không chạm vào Domain hay Application Layer**.

---

## 4. Domain Events — Giao tiếp lỏng lẻo (Loose Coupling)

### Luồng phát và xử lý sự kiện:

```
  [PlaceOrderUseCase]
        │
        │ Gọi order.place() — Order tích lũy OrderPlacedEvent bên trong
        ▼
  [OrderPersistenceAdapter.save()]
        │
        │ Sau khi INSERT thành công, lấy events ra và phát đi
        ▼
  [Spring ApplicationEventPublisher]
        │
        ├──► [OrderEventListener.onOrderPlaced()]
        │           │
        │           ├── Lấy email người dùng qua FindUserByIdInputPort
        │           └── Gọi notificationPort.sendOrderPlacedNotification()
        │                   │
        │                   └──► [JavaMailNotificationAdapter] — Gửi email HTML qua SMTP
        │
        └──► [Các listener khác có thể thêm sau: ghi log kiểm toán, đồng bộ kho...]
```

### Tại sao cần Domain Events?

| Nếu KHÔNG dùng Event | Nếu CÓ dùng Event |
|---|---|
| `PlaceOrderUseCase` phải tự gọi `notificationPort` — bị phụ thuộc vào logic gửi mail | `PlaceOrderUseCase` chỉ lo việc đặt hàng, không biết gì về gửi mail |
| Nếu gửi mail lỗi → Transaction đặt hàng bị rollback | Gửi mail lỗi → chỉ ghi log cảnh báo, đơn hàng vẫn tạo thành công |
| Khó mở rộng thêm tác vụ phụ | Chỉ cần thêm Listener mới, không sửa Use Case |

---

## 5. Thiết kế Chi tiết Luồng Thanh toán (Payment Flow)

### 5.1. Luồng Tiền mặt (CASH)

```
Khách hàng checkout (chọn CASH)
    │
    ▼ PlaceOrderUseCase.execute()
Tạo đơn: status=PENDING, paymentStatus=PENDING, paymentMethod=CASH
    │
    ▼ ApplicationEventPublisher
Gửi email "Đặt hàng thành công"
    │
    ▼ (Admin cập nhật đơn hàng)
status=DELIVERED
    │
    ▼ ApplicationEventPublisher (OrderStatusChangedEvent)
Gửi email "Giao hàng thành công"
```

### 5.2. Luồng Chuyển khoản VietQR (VIETQR)

```
Khách hàng checkout (chọn VIETQR)
    │
    ▼ PlaceOrderUseCase.execute()
Tạo đơn: status=PENDING, paymentStatus=PENDING, paymentMethod=VIETQR
    │ Redirect → /orders/{id}/payment
    ▼
Hiển thị mã QR động từ Public API VietQR
  URL: https://img.vietqr.io/image/{bankCode}-{accountNumber}-qr_only.jpg
       ?amount={totalAmount}&addInfo=DH{orderId}
    │
    ▼ Khách hàng bấm "Xác nhận đã thanh toán" (giả lập)
POST /orders/{id}/payment/confirm
    │
    ▼ ConfirmVietQRPaymentUseCase.execute()
order.markPaymentPaid()  → paymentStatus=PAID, status=CONFIRMED
    │
    ▼ ApplicationEventPublisher
Gửi email "Xác nhận thanh toán thành công"
    │ Redirect → /orders/{id}
    ▼
Hiển thị chi tiết đơn hàng với banner "Thanh toán thành công"
```

---

## 6. CQRS trong phân hệ Order

Phân tách rõ ràng giữa luồng Ghi (Command) và luồng Đọc (Query):

| | Command (Ghi) | Query (Đọc) |
|---|---|---|
| **Mục đích** | Thay đổi trạng thái hệ thống | Trả về dữ liệu hiển thị |
| **Use Cases** | `PlaceOrderUseCase`, `CancelOrderUseCase`, `ConfirmVietQRPaymentUseCase` | `FindOrdersByUserUseCase`, `FindOrderByIdUseCase`, `FindAllOrdersUseCase` |
| **Dữ liệu trả về** | Domain Entity `Order` | `OrderDTO` (đã được ánh xạ sang dạng an toàn cho UI) |
| **Luồng** | Tải AR → Thực thi nghiệp vụ → Lưu → Phát Event | Truy vấn → Ánh xạ sang DTO → Trả về UI |

---

## 7. Hướng dẫn Đọc Code theo Thứ tự Học tập

Để hiểu rõ DDD và Clean Architecture qua code thực tế, hãy đọc mã nguồn theo thứ tự sau:

### Bước 1 — Domain Layer (Bắt đầu từ lõi)
- [Order.java](../src/main/java/com/examp/springmvc/order/domain/model/Order.java) — Xem các phương thức `place()`, `confirm()`, `markPaymentPaid()` bảo vệ trạng thái nghiệp vụ như thế nào.
- [PaymentMethod.java](../src/main/java/com/examp/springmvc/order/domain/model/PaymentMethod.java) / [PaymentStatus.java](../src/main/java/com/examp/springmvc/order/domain/model/PaymentStatus.java) — Enum đại diện trạng thái nghiệp vụ.
- [NotificationPort.java](../src/main/java/com/examp/springmvc/order/domain/ports/output/NotificationPort.java) — Interface cổng gửi thông báo thuần Java.

### Bước 2 — Unit Test (Kiểm thử nghiệp vụ miền)
- [OrderTest.java](../src/test/java/com/examp/springmvc/order/domain/model/OrderTest.java) — Kiểm thử nghiệp vụ AR hoàn toàn không cần Spring hay Database. Đây là cách DDD cho phép kiểm thử nhanh nghiệp vụ phức tạp.

### Bước 3 — Application Layer (Điều phối nghiệp vụ)
- [PlaceOrderUseCase.java](../src/main/java/com/examp/springmvc/order/application/command/PlaceOrderUseCase.java) — Xem cách Use Case phối hợp Catalog Port (tải sản phẩm) và Order Port (lưu đơn hàng).
- [ConfirmVietQRPaymentUseCase.java](../src/main/java/com/examp/springmvc/order/application/command/ConfirmVietQRPaymentUseCase.java) — Xem luồng xác nhận thanh toán và phát Domain Event.

### Bước 4 — Infrastructure Layer (Chi tiết kỹ thuật)
- [JavaMailNotificationAdapter.java](../src/main/java/com/examp/springmvc/order/infrastructure/notification/JavaMailNotificationAdapter.java) — Triển khai gửi email HTML thật qua SMTP, tách biệt hoàn toàn với Domain.
- [OrderEventListener.java](../src/main/java/com/examp/springmvc/order/infrastructure/event/OrderEventListener.java) — Kết nối Domain Events với tác vụ gửi email.
- [OrderPersistenceAdapter.java](../src/main/java/com/examp/springmvc/order/infrastructure/persistence/OrderPersistenceAdapter.java) — Xem cách ánh xạ Domain Entity sang DB Entity và phát Domain Events sau khi lưu thành công.

### Bước 5 — Presentation Layer (Cổng ngoài cùng)
- [OrderController.java](../src/main/java/com/examp/springmvc/order/presentation/OrderController.java) — Controller chỉ có trách nhiệm nhận tham số HTTP, xây dựng Command và gọi Use Case.

---

## 8. Cấu trúc thư mục phân hệ Order

```
order/
├── domain/                          ← Lõi nghiệp vụ — không phụ thuộc framework
│   ├── model/
│   │   ├── Order.java               ← Aggregate Root
│   │   ├── OrderItem.java           ← Entity (con của Order)
│   │   ├── OrderStatus.java         ← Enum trạng thái đơn hàng
│   │   ├── PaymentMethod.java       ← Enum phương thức thanh toán
│   │   ├── PaymentStatus.java       ← Enum trạng thái thanh toán
│   │   └── ShippingAddress.java     ← Value Object địa chỉ giao hàng
│   ├── event/
│   │   ├── OrderPlacedEvent.java    ← Sự kiện miền khi đặt hàng thành công
│   │   └── OrderStatusChangedEvent.java ← Sự kiện miền khi trạng thái thay đổi
│   └── ports/output/
│       ├── OrderPersistencePort.java ← Interface lưu trữ đơn hàng
│       └── NotificationPort.java    ← Interface gửi thông báo
│
├── application/                     ← Điều phối nghiệp vụ
│   ├── command/
│   │   ├── PlaceOrderCommand.java   ← DTO yêu cầu đặt hàng
│   │   ├── PlaceOrderUseCase.java   ← Use Case tạo đơn hàng
│   │   ├── CancelOrderUseCase.java  ← Use Case huỷ đơn hàng
│   │   ├── UpdateOrderStatusUseCase.java ← Use Case admin cập nhật trạng thái
│   │   └── ConfirmVietQRPaymentUseCase.java ← Use Case xác nhận thanh toán QR
│   └── query/
│       ├── OrderDTO.java            ← DTO trả về cho UI
│       ├── FindOrderByIdUseCase.java
│       ├── FindOrdersByUserUseCase.java
│       └── FindAllOrdersUseCase.java
│
├── infrastructure/                  ← Chi tiết kỹ thuật
│   ├── persistence/
│   │   ├── OrderDbEntity.java       ← Ánh xạ bảng APP_ORDERS
│   │   ├── OrderMapper.java/xml     ← MyBatis SQL
│   │   └── OrderPersistenceAdapter.java ← Triển khai OrderPersistencePort
│   ├── event/
│   │   └── OrderEventListener.java  ← Lắng nghe Domain Events
│   └── notification/
│       └── JavaMailNotificationAdapter.java ← Triển khai NotificationPort
│
└── presentation/                    ← Giao tiếp với HTTP
    ├── OrderController.java         ← Endpoint dành cho khách hàng
    └── AdminOrderController.java    ← Endpoint dành cho admin
```

---

## 9. Bài học kinh nghiệm rút ra

1. **Giữ Domain thuần khiết**: `Order.java` không import bất kỳ class nào của Spring hay MyBatis. Điều này giúp kiểm thử nghiệp vụ chạy cực nhanh (milliseconds) mà không cần khởi động ứng dụng.

2. **Factory Method thay vì Constructor công khai**: Sử dụng `Order.place(...)` thay vì `new Order(...)` để đặt tên nghiệp vụ rõ ràng và kiểm tra Invariants ngay khi tạo đối tượng.

3. **Domain Event là tín hiệu "Điều gì đó đã xảy ra"**: Tên sự kiện luôn ở thì quá khứ (`OrderPlacedEvent`, `OrderStatusChangedEvent`) vì chúng mô tả một thực tế đã xảy ra, không phải yêu cầu trong tương lai.

4. **Anti-Corruption Layer (ACL)**: `OrderPersistenceAdapter` đóng vai trò ACL giữa thế giới Database (MyBatis `OrderDbEntity`) và thế giới Domain (`Order`). Adapter ánh xạ qua lại giữa hai thế giới này, ngăn cấu trúc DB xâm nhập vào Domain Model.

5. **Ưu tiên kiểm thử Domain Layer trước**: Vì Domain chứa logic phức tạp nhất, hãy viết Unit Test kiểm thử các phương thức nghiệp vụ (`confirm()`, `cancel()`, `markPaymentPaid()`) trước khi triển khai Use Cases.
