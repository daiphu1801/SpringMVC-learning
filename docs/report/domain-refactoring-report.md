# BÁO CÁO CẢI TIẾN & TÁI CẤU TRÚC LỚP DOMAIN
## (Domain Layer Refactoring & Quality Enhancement Report)

Tài liệu này tổng hợp toàn bộ các nội dung thảo luận, phân tích thiết kế và các cải tiến kỹ thuật đã thực hiện trên tầng Domain (Domain Layer) của hệ thống SpringMVC-Demo theo mô hình thiết kế hướng miền (Domain-Driven Design - DDD).

---

## MỤC LỤC
1. [Tổng quan Yêu cầu & Mục tiêu](#1-tổng-quan-yêu-cầu--mục-tiêu)
2. [Chi tiết các Hạng mục Cải tiến & Giải pháp Kỹ thuật](#2-chi-tiết-các-hạng-mục-cải-tiến--giải-pháp-kỹ-thuật)
   - [2.1. Đóng gói Thực thể & Bảo vệ Invariants (User Entity)](#21-đóng-gói-thực-thể--bảo-vệ-invariants-user-entity)
   - [2.2. Tái cấu trúc Domain Event Bất biến (Stateless Domain Events)](#22-tái-cấu-trúc-domain-event-bất-biến-stateless-domain-events)
   - [2.3. Loại bỏ Anemic Domain Model (Product & Category)](#23-loại-bỏ-anemic-domain-model-product--category)
   - [2.4. Chuẩn hóa & Thắt chặt Ràng buộc Domain Types / Value Objects](#24-chuẩn-hóa--thắt-chặt-ràng-buộc-domain-types--value-objects)
   - [2.5. Chuyển đổi Kiến trúc sang Hướng Sự kiện (Event-Driven Integration)](#25-chuyển-đổi-kiến-trúc-sang-hướng-sự-kiện-event-driven-integration)
3. [Kết quả Đánh giá Chất lượng & Kiểm thử](#3-kết-quả-đánh-giá-chất-lượng--kiểm-thử)

---

## 1. TỔNG QUAN YÊU CẦU & MỤC TIÊU

Lớp Domain là trái tim của hệ thống DDD, nơi định nghĩa các quy tắc nghiệp vụ cốt lõi (Business Invariants). Trong quá trình rà soát, chúng tôi đã phát hiện và khắc phục một số điểm yếu về mặt thiết kế (design smells) để đạt được các mục tiêu sau:
* **Tính toàn vẹn dữ liệu (Data Integrity):** Ngăn chặn hoàn toàn việc tạo hoặc đưa thực thể vào trạng thái không hợp lệ (ví dụ: email null, vai trò sai lệch, định dạng mật khẩu không an toàn).
* **Đóng gói chặt chẽ (Encapsulation):** Loại bỏ các setter tự do (`public setter`), đảm bảo mọi thay đổi trạng thái đều đi qua các phương thức nghiệp vụ (Domain Methods) có kiểm định rõ ràng.
* **Tính bất biến của Sự kiện (Event Immutability):** Đảm bảo Domain Event là một "ảnh chụp tại thời điểm xảy ra" (Immutable Snapshot), không bị thay đổi trạng thái khi Aggregate thay đổi sau đó.
* **Phân tách trách nhiệm (Decoupling):** Chuyển đổi từ gọi trực tiếp dịch vụ hạ tầng sang kiến trúc hướng sự kiện (Event-driven) để giảm độ phụ thuộc chéo giữa các Use Case.

---

## 2. CHI TIẾT CÁC HẠNG MỤC CẢI TIẾN & GIẢI PHÁP KỸ THUẬT

### 2.1. Đóng gói Thực thể & Bảo vệ Invariants (User Entity)

#### Vấn đề trước đó:
* Lớp `User.java` chứa các phương thức setter công khai cho mọi thuộc tính. Cho phép thay đổi trực tiếp (ví dụ: `user.setRole("ADMIN")`) từ bất kỳ đâu mà không đi qua bộ lọc kiểm tra nghiệp vụ.
* Phương thức khởi tạo (Constructor) không tự động gọi `validate()`, dẫn đến việc có thể khởi tạo đối tượng `User` lỗi hoặc thiếu thông tin quan trọng.
* Các thuộc tính vai trò (`role`) và trạng thái (`status`) sử dụng kiểu chuỗi thô (`String`), dễ gây sai lệch do nhập sai chữ viết hoa/viết thường.

#### Giải pháp cải tiến:
* **Xóa bỏ toàn bộ `public setter`:** Thay thế bằng các phương thức nghiệp vụ tự đóng gói (Self-Validating Domain Methods):
  * `changeEmail(Email newEmail)`: Kiểm tra tính hợp lệ và gán email mới.
  * `changeRole(UserRole newRole)`: Thay đổi quyền hạn người dùng bằng Enum.
  * `changeFullName(String fullName)` & `changePhone(String phone)`: Cập nhật thông tin cá nhân kèm validate.
  * `activate()` & `deactivate()`: Cập nhật trạng thái thông qua phương thức rõ nghĩa của mô hình nghiệp vụ thay vì truyền chuỗi thô.
* **Tự động validate trong Constructor:** Thêm lệnh gọi phương thức `validate()` ở cuối tất cả các hàm khởi tạo để đảm bảo thực thể được tạo ra luôn hợp lệ.
* **Enum hóa thuộc tính:** Thay đổi kiểu dữ liệu của `role` thành `UserRole` enum (`USER`, `ADMIN`) và `status` thành `UserStatus` enum (`ACTIVE`, `INACTIVE`), tăng tính an toàn kiểu dữ liệu (Type-safety).

---

### 2.2. Tái cấu trúc Domain Event Bất biến (Stateless Domain Events)

#### Vấn đề trước đó:
* Các Domain Event như `UserRegisteredEvent` hay `OrderPlacedEvent` lưu giữ tham chiếu trực tiếp đến đối tượng Aggregate gốc (`User`, `Order`).
* Do Aggregate là các thực thể có thể thay đổi trạng thái (Mutable), khi trạng thái của Aggregate thay đổi trong vòng đời transaction, dữ liệu bên trong Event đã phát ra cũng bị thay đổi theo. Điều này vi phạm nghiêm trọng tính bất biến của lịch sử sự kiện.
* Gây ra cảnh báo Spotbugs `EI_EXPOSE_REP2` do lưu trữ trực tiếp đối tượng mutable.

```mermaid
graph TD
    subgraph Thiet Ke Cu (Mutable reference)
        Event1[OrderPlacedEvent] -->|Tham chieu truc tiep| Order[Order Entity]
        Order -->|Mutate state| Order
    end
    subgraph Thiet Ke Moi (Snapshot)
        Event2[OrderPlacedEvent] -->|Luu tru ban sao bat bien| Fields[orderId, totalAmount, shippingAddress...]
    end
```

#### Giải pháp cải tiến:
* **Snapshot Pattern:** Loại bỏ hoàn toàn việc truyền Aggregate root vào trong thuộc tính của Event. Thay vào đó, trích xuất các dữ liệu cần thiết của sự kiện ngay tại constructor của Event và gán vào các trường nguyên bản bất biến (`primitives` hoặc các `immutable Value Objects`).
* **Sao chép sâu danh sách (Deep Copy Collection):** Đối với các danh sách chi tiết (ví dụ: `OrderItem` trong `OrderPlacedEvent`), thực hiện tạo bản sao mới và bọc trong đầu ra bất biến `Collections.unmodifiableList(...)` để ngăn chặn hành vi sửa đổi danh sách từ bên ngoài.

---

### 2.3. Loại bỏ Anemic Domain Model (Product & Category)

#### Vấn đề trước đó:
* Lớp `Product` và `Category` chỉ chứa thuộc tính gán từ khóa `final` và các hàm Getter tương ứng.
* Mọi hành vi cập nhật thông tin sản phẩm hoặc danh mục đều phải tạo mới hoàn toàn đối tượng thông qua Constructor trong Use Case (Anemic Domain Model), khiến logic nghiệp vụ bị phân tán và khó bảo trì.

#### Giải pháp cải tiến:
* **Rich Domain Model:** Loại bỏ các từ khóa `final` trên các thuộc tính nghiệp vụ có thể thay đổi.
* **Phương thức miền tự trị:** Xây dựng phương thức `updateDetails(...)` bên trong các thực thể:
  ```java
  public void updateDetails(String name, String code, String description) {
      if (name == null || name.trim().isEmpty()) {
          throw new IllegalArgumentException("Tên danh mục không được để trống");
      }
      // logic cap nhat
  }
  ```
* **Tinh chỉnh Use Case:** Chuyển đổi logic của `UpdateProductUseCase` từ tạo mới constructor sang lấy thực thể hiện tại và gọi `existingProduct.updateDetails(...)`.

---

### 2.4. Chuẩn hóa & Thắt chặt Ràng buộc Domain Types / Value Objects

#### Vấn đề trước đó:
* **Email:** Biểu thức chính quy (Regex) quá lỏng lẻo cho phép các chuỗi không hợp lệ như `a@b` vượt qua. Không thực hiện chuyển đổi chữ thường dẫn đến nguy cơ tạo tài khoản trùng lặp do phân biệt chữ hoa/thường.
* **Password:** Chỉ kiểm tra độ dài tối thiểu `>= 6` ký tự mà không giới hạn tối đa, dẫn tới nguy cơ tấn công CPU DoS khi băm mật khẩu cực dài bằng thuật toán BCrypt. Đồng thời, cấu hình mật khẩu chưa yêu cầu độ mạnh tối thiểu.
* **ShippingAddress:** Thiếu hàm so sánh bằng `equals` & `hashCode` làm sai lệch logic so sánh Value Object. Chứa phương thức chết không dùng đến `zero()`.

#### Giải pháp cải tiến:
* **Kiểm soát Email chặt chẽ:** 
  * Áp dụng biểu thức regex tiêu chuẩn: `^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$`.
  * Thực hiện gọi `toLowerCase().trim()` khi gán giá trị email.
* **Nâng cao tính bảo mật Mật khẩu:**
  * Khống chế chiều dài mật khẩu trong khoảng từ `6` đến `50` ký tự.
  * Cấu hình độ phức tạp bắt buộc: Mật khẩu phải bắt đầu bằng một chữ cái viết hoa, phải chứa ít nhất một chữ số và ít nhất một ký tự đặc biệt (ví dụ: `Password123!`).
* **Hoàn thiện Value Object Địa chỉ:**
  * Cài đặt đầy đủ `equals(Object)` và `hashCode()` so sánh theo giá trị thuộc tính.
  * Loại bỏ phương thức chết `zero()`.

---

### 2.5. Chuyển đổi Kiến trúc sang Hướng Sự kiện (Event-Driven Integration)

#### Vấn đề trước đó:
* Khi khách hàng hoàn tất thanh toán (VietQR), lớp `ConfirmVietQRPaymentUseCase` thay đổi trạng thái đơn hàng và thực hiện gọi trực tiếp dịch vụ gửi email xác nhận thông qua `NotificationPort` (gọi đồng bộ).
* Cách thiết kế này làm tăng sự phụ thuộc (coupling) của Use Case nghiệp vụ chính vào hệ thống thông báo bên ngoài và làm tăng thời gian xử lý phản hồi (latency).
* Phương thức `markPaymentPaid()` của lớp `Order` thay đổi trạng thái thanh toán nhưng không phát sinh Event nào để thông báo cho hệ sinh thái miền.

#### Giải pháp cải tiến:
* **OrderPaidEvent:** Định nghĩa sự kiện miền `OrderPaidEvent` khi đơn hàng được đánh dấu thanh toán thành công.
* **Đăng ký sự kiện từ Domain:** Thêm mã phát sự kiện miền vào trong `Order.markPaymentPaid()`.
* **Loại bỏ liên kết trực tiếp:** Xóa hoàn toàn dependency của `NotificationPort` và `FindUserByIdInputPort` ra khỏi `ConfirmVietQRPaymentUseCase`.
* **Asynchronous Listener:** Chuyển dịch vụ gửi mail sang `OrderEventListener.onOrderPaid(OrderPaidEvent event)`, xử lý tách biệt với luồng ghi nhận thanh toán chính của giao dịch.

---

## 3. KẾT QUẢ ĐÁNH GIÁ CHẤT LƯỢNG & KIỂM THỬ

Mọi thay đổi trên đều được kiểm chứng nghiêm ngặt qua quy trình tích hợp liên tục (CI) cục bộ:

1. **Spotless Code Formatting:** Đảm bảo tất cả các tệp tin mới tạo và chỉnh sửa đều tuân thủ định dạng mã nguồn chuẩn (Java Palantir Format) của dự án.
   ```bash
   mvn spotless:apply
   ```
2. **Checkstyle Check:** Xác nhận không có vi phạm quy chuẩn đặt tên, cấu trúc tệp hay khai báo import thừa.
   ```bash
   mvn checkstyle:check
   ```
   *Kết quả:* `0 Checkstyle violations`.
3. **Spotbugs Static Analysis:** Rà soát lỗi bảo mật và tối ưu hiệu suất tĩnh, đặc biệt là kiểm soát triệt để các nguy cơ rò rỉ trạng thái đối tượng (`EI_EXPOSE_REP2`).
   ```bash
   mvn spotbugs:check
   ```
   *Kết quả:* `BUILD SUCCESS (0 bugs found)`.
4. **Bộ kiểm thử đơn vị (JUnit Tests):**
   * Bổ sung thêm 11 ca kiểm thử đơn vị mới để bao phủ toàn bộ các trường hợp nghiệp vụ biên dịch mới (Email lỏng, Email viết thường, độ mạnh mật khẩu phức tạp, kiểm tra cập nhật Product/Category nghiệp vụ, so sánh bằng ShippingAddress và lắng nghe sự kiện Paid).
   * Chạy toàn bộ suite kiểm thử:
   ```bash
   mvn clean test
   ```
   *Kết quả:* **133 / 133** ca kiểm thử đều vượt qua thành công (`BUILD SUCCESS`).
