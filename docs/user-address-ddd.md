# Tài liệu Học tập: Tính năng User Address Book (DDD, Clean Architecture & CQRS)

Tính năng **Quản lý Sổ địa chỉ người dùng (User Address Book)** được thiết kế và triển khai như một **mẫu chuẩn thực tế (reference implementation)** giúp học viên hiểu rõ cách áp dụng các nguyên lý của Phát triển hướng miền (Domain-Driven Design - DDD), Kiến trúc sạch (Clean Architecture) và Phân tách trách nhiệm Lệnh-Truy vấn (CQRS).

---

## 1. Mục đích Giáo dục của Feature này

Trong thực tế, khi học DDD và Clean Architecture, lập trình viên thường gặp khó khăn trong việc phân biệt:
1. Đâu là **Entity**? Đâu là **Value Object**?
2. Làm thế nào để thực thi và bảo vệ các **ràng buộc nghiệp vụ (Invariants)** thông qua **Aggregate Root**?
3. Cách lưu trữ các Value Object xuống database quan hệ (RDBMS) mà vẫn đảm bảo tính đóng gói của Aggregate?
4. Áp dụng **CQRS** như thế nào để tối ưu hiệu năng đọc/ghi dữ liệu?

Tính năng Address Book giải quyết trọn vẹn 4 câu hỏi trên.

---

## 2. Phân tích Thiết kế miền (Domain Design)

### 2.1. Tại sao `Address` là một Value Object?
Mặc dù trong database, mỗi dòng địa chỉ có một cột khóa chính `id` tự tăng để phục vụ lưu vết và xóa dễ dàng, nhưng trong thiết kế miền (Domain Model), `Address` là một **Value Object (VO)** vì các lý do sau:
- **Không có danh tính độc lập:** Một địa chỉ không tồn tại độc lập mà không gắn liền với một thực thể người dùng (`User`).
- **So sánh bằng cấu trúc (Structural Equality):** Hai đối tượng địa chỉ được coi là giống nhau nếu tất cả các trường dữ liệu của chúng (người nhận, số điện thoại, tỉnh thành, quận huyện, xã phường, chi tiết nhà) giống nhau.
- **Tính bất biến (Immutability):** Địa chỉ không có vòng đời riêng. Khi người dùng muốn sửa đổi địa chỉ, về mặt nghiệp vụ, họ đang thay thế địa chỉ cũ bằng một địa chỉ mới hoàn toàn.
- **Tự kiểm tra hợp lệ (Self-validation):** `Address` tự kiểm tra định dạng số điện thoại (9-11 số) và kiểm tra tính rỗng của dữ liệu ngay khi khởi tạo để đảm bảo trạng thái luôn luôn đúng.

### 2.2. `User` là Aggregate Root (Gốc thực thể)
`User` nắm quyền quản lý toàn bộ danh sách `List<Address>` của mình. Mọi thao tác thêm hoặc xóa địa chỉ bắt buộc phải được thực hiện thông qua các phương thức nghiệp vụ của `User`:
- `user.addAddress(newAddress)`
- `user.removeAddress(addressId)`

Bên ngoài Aggregate **không được phép** truy cập và thay đổi trực tiếp danh sách địa chỉ. Điều này giúp `User` bảo vệ các **Luật bất biến (Business Invariants)** của hệ thống.

### 2.3. Các luật bất biến (Business Invariants) được bảo vệ:
1. **Giới hạn số lượng:** Một người dùng chỉ được lưu tối đa **5 địa chỉ**.
2. **Mặc định tự động:** Địa chỉ đầu tiên được thêm vào sẽ tự động được chọn làm địa chỉ mặc định (`isDefault = true`).
3. **Duy nhất một địa chỉ mặc định:** Chỉ có duy nhất 1 địa chỉ được đặt làm mặc định tại một thời điểm. Khi đặt một địa chỉ mới làm mặc định, trạng thái mặc định của các địa chỉ khác sẽ tự động bị hủy bỏ.
4. **Tự động chuyển đổi mặc định khi xóa:** Nếu người dùng xóa địa chỉ mặc định hiện tại, hệ thống sẽ tự động chọn một địa chỉ bất kỳ còn lại (nếu có) để đặt làm mặc định.

---

## 3. Kiến trúc Triển khai (Clean Architecture & CQRS)

Sự phân tách giữa ghi (Command) và đọc (Query) được thể hiện rõ ràng qua các Use Cases:

```
                          ┌────────────────────────┐
                          │    Presentation UI     │
                          └───────────┬────────────┘
                                      │
              ┌───────────────────────┴───────────────────────┐
              ▼ (COMMAND - Thay đổi trạng thái)              ▼ (QUERY - Đọc dữ liệu nhanh)
     ┌─────────────────────────────┐                ┌─────────────────────────────┐
     │      AddAddressUseCase      │                │   GetUserAddressesUseCase   │
     │     RemoveAddressUseCase    │                └──────────────┬──────────────┘
     └──────────────┬──────────────┘                               │
                    │                                              │
                    ▼                                              ▼
     ┌─────────────────────────────┐                ┌─────────────────────────────┐
     │  Load/Save qua User AR      │                │  Truy vấn trực tiếp qua DB  │
     │  (Thực thi nghiệp vụ miền)  │                │  (Bỏ qua Aggregate Root)    │
     └──────────────┬──────────────┘                └──────────────┬──────────────┘
                    │                                              │
                    └──────────────────────┬───────────────────────┘
                                           ▼
                            ┌─────────────────────────────┐
                            │    Infrastructure (MyBatis) │
                            └─────────────────────────────┘
```

### 3.1. Phân tách Ghi (Write Side - State Modification)
Khi có yêu cầu thêm/xóa địa chỉ:
1. **Lớp Application Port** nhận yêu cầu (`AddAddressCommand`).
2. **UserPersistenceAdapter** tải toàn bộ đối tượng `User` (bao gồm cả danh sách `List<Address>` của họ) lên bộ nhớ.
3. Các quy tắc nghiệp vụ được thực thi hoàn toàn trong bộ nhớ bằng cách gọi phương thức trên `User`:
   ```java
   user.addAddress(newAddress);
   ```
4. **UserPersistenceAdapter** lưu lại trạng thái mới của `User` xuống database thông qua cơ chế **Cascade Save**:
   - Chèn mới các địa chỉ chưa có `id`.
   - Cập nhật các địa chỉ đã thay đổi.
   - Xóa bỏ các địa chỉ đã bị loại khỏi danh sách domain (`deleteByUserIdExceptIds`).
5. Phát tán **Domain Events** (`UserAddressAddedEvent` / `UserAddressRemovedEvent`) để các Bounded Context khác hoặc phân hệ khác có thể lắng nghe và xử lý không đồng bộ (ví dụ: Log audit, đồng bộ hệ thống giao hàng).

### 3.2. Phân tách Đọc (Read Side - Optimized Query)
Để hiển thị danh sách địa chỉ lên giao diện người dùng:
- Chúng ta **không cần** tải toàn bộ Aggregate Root `User` cồng kềnh lên bộ nhớ.
- Thay vào đó, Use Case `GetUserAddressesUseCase` thực hiện truy vấn trực tiếp từ bảng `APP_USER_ADDRESSES` thông qua `UserAddressMapper` và trả về danh sách `AddressDTO` siêu nhẹ.
- Đây là cốt lõi của nguyên lý **CQRS** (Command Query Responsibility Segregation).

---

## 4. Tóm tắt Cấu trúc Thư mục & Vai trò Tệp tin

Dưới đây là sơ đồ tệp tin cấu thành tính năng Address Book trong hệ thống:

*   **Domain Layer**
    *   [Address.java](file:///home/phubd-fsddint/Documents/ProjectSpring/SpringMVC-Demo/src/main/java/com/examp/springmvc/user/domain/model/Address.java): Value Object chứa thông tin địa chỉ và các logic tự kiểm tra dữ liệu.
    *   [User.java](file:///home/phubd-fsddint/Documents/ProjectSpring/SpringMVC-Demo/src/main/java/com/examp/springmvc/user/domain/model/User.java): Aggregate Root chứa danh sách `List<Address>` và quản trị các luật bất biến.
    *   [UserAddressAddedEvent.java](file:///home/phubd-fsddint/Documents/ProjectSpring/SpringMVC-Demo/src/main/java/com/examp/springmvc/user/domain/event/UserAddressAddedEvent.java) / [UserAddressRemovedEvent.java](file:///home/phubd-fsddint/Documents/ProjectSpring/SpringMVC-Demo/src/main/java/com/examp/springmvc/user/domain/event/UserAddressRemovedEvent.java): Các sự kiện nghiệp vụ miền được kích hoạt khi trạng thái thay đổi.

*   **Application Layer**
    *   `ports/in`: Cổng giao tiếp đầu vào cho Use Case.
    *   `query/AddressDTO.java`: Lớp truyền tải dữ liệu địa chỉ ra giao diện, tương thích chuẩn JavaBeans.
    *   `command/AddAddressUseCase.java` / `RemoveAddressUseCase.java`: Thực thi thay đổi trạng thái của Aggregate và thực hiện lưu trữ.
    *   `query/GetUserAddressesUseCase.java`: Thực thi truy vấn danh sách địa chỉ nhanh (Read Model).

*   **Infrastructure Layer**
    *   `persistence/UserAddressDbEntity.java`: Thực thể ánh xạ dữ liệu trực tiếp với bảng cơ sở dữ liệu `APP_USER_ADDRESSES`.
    *   `mapper/UserAddressMapper.java` / `UserAddressMapper.xml`: Thực hiện các câu lệnh SQL INSERT, UPDATE, DELETE bằng MyBatis.
    *   `persistence/UserPersistenceAdapter.java`: Nhận nhiệm vụ Cascade lưu/xóa địa chỉ và kích hoạt xuất các sự kiện nghiệp vụ miền thông qua Spring `ApplicationEventPublisher`.

*   **Presentation Layer**
    *   `presentation/UserAddressController.java`: Tiếp nhận request HTTP, kiểm tra session đăng nhập và xử lý dữ liệu flash validation.
    *   `webapp/WEB-INF/views/user/address-list.jsp`: Giao diện người dùng sử dụng CSS tinh tế hiển thị danh sách địa chỉ và form thêm mới.

---

## 5. Bài học Kinh nghiệm Kỹ thuật Rút ra

1. **Vấn đề Danh tính của Value Object:**
   Trong cơ sở dữ liệu quan hệ, chúng ta bắt buộc phải cấp cho bảng `APP_USER_ADDRESSES` một khóa chính `id` kiểu `NUMBER/BIGINT`. Tuy nhiên, trong Domain Model, chúng ta không so sánh hai `Address` dựa trên `id` này. `id` ở đây chỉ đóng vai trò kỹ thuật hỗ trợ cơ sở hạ tầng (Database Identity) chứ không phải là định danh nghiệp vụ miền (Domain Identity).
   
2. **Tương thích JavaBeans trong JSP EL:**
   Khi truyền dữ liệu ra màn hình hiển thị sử dụng công nghệ JSP, hãy chú ý sử dụng các lớp Java Class tiêu chuẩn với đầy đủ getter/setter chuẩn JavaBeans (`getReceiverName()`, `isDefault()`) thay vì dùng Java Records. Các servlet container cũ hoặc công cụ phân tích Expression Language (EL) mặc định sẽ không nhận diện được cách sinh phương thức của Java Records, dẫn đến lỗi `PropertyNotFoundException`.
