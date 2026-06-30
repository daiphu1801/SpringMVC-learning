# Báo cáo Cải tiến Kiến trúc: Đóng gói ranh giới tầng Application & Chuẩn hóa CQRS

Báo cáo này tài liệu hóa quá trình phân tích, lập kế hoạch và triển khai việc tái cấu trúc tầng **Application (Use Case / CQRS)** nhằm giải quyết lỗi kiến trúc nghiêm trọng (`[Critical]`) liên quan đến rò rỉ ranh giới giữa Application và Infrastructure.

---

## 1. Vấn đề Kiến trúc trước khi Tái cấu trúc

### Mô tả lỗi vi phạm
* **Lỗi:** Các Query Use Cases thuộc tầng Application trực tiếp `import` các interface Mapper (`*Mapper`) và các thực thể cơ sở dữ liệu (`*DbEntity`) của MyBatis nằm ở tầng Infrastructure.
* **Các file vi phạm cụ thể:**
  - `LoginUseCase.java` (Auth Context)
  - `FindAllProductsUseCase.java` & `FindProductByIdUseCase.java` (Catalog Context)
  - `FindAllUsersUseCase.java` & `FindUserByIdUseCase.java` (User Context)
  - `GetUserAddressesUseCase.java` (User Context - Address)

### Hậu quả
1. **Mất tính độc lập:** Tầng Application là nơi chứa logic nghiệp vụ và điều phối ứng dụng. Việc phụ thuộc trực tiếp vào Infrastructure làm tầng Application không thể tái sử dụng hoặc chạy độc lập khi thiếu hạ tầng dữ liệu thực tế.
2. **Không thể kiểm thử độc lập (Unit Test):** Các bài kiểm thử đơn vị của Use Case bị phụ thuộc vào các lớp Mapper và DbEntity, dẫn đến việc khó mock và dễ bị lỗi khi cấu trúc database thay đổi.
3. **Không nhất quán (Inconsistency):** Nhánh Command (tác vụ thay đổi dữ liệu) và một số phần của nhánh Query (như Category Query) đã thực hiện rất đúng chuẩn kiến trúc bằng cách gọi qua Port. Tuy nhiên, phần Product, User và Auth lại vi phạm nghiêm trọng.

---

## 2. Giải pháp và Thiết kế mới (Ports & Adapters / CQRS)

Để giải quyết triệt để vấn đề này, chúng tôi áp dụng nguyên tắc **CQRS** kết hợp với kiến trúc **Ports & Adapters**:

```
[ Tầng Application (Query Use Cases) ]
              │
              ▼ (Phụ thuộc qua Port)
    [ *QueryPort (Interface) ]
              ▲
              │ (Triển khai Adapter)
[ Tầng Infrastructure (Persistence Adapters) ]
              │
              ▼ (Gọi trực tiếp MyBatis)
       [ *Mapper / *DbEntity ]
```

### Các bước thực hiện:
1. **Định nghĩa Query Ports (Application):** Tạo các interface cổng truy vấn ngay trong gói `query` của tầng Application. Các phương thức trả về DTO tương ứng thay vì DbEntity.
2. **Tạo Query Adapters (Infrastructure):** Tạo các lớp Adapter trong gói `infrastructure.persistence` triển khai các Query Ports trên. Adapter chịu trách nhiệm gọi Mapper, nhận về DbEntity và ánh xạ (map) sang DTO phẳng để trả lại cho Application.
3. **Cập nhật Use Cases:** Thay đổi kiểu phụ thuộc từ `*Mapper` thành `*QueryPort` trong các Use Cases.
4. **Cập nhật Unit Tests:** Chuyển đổi mock từ Mapper sang Query Port, viết bổ sung test coverage cho các use case chưa có kiểm thử đơn vị.

---

## 3. Chi tiết các File đã Chỉnh sửa & Tạo mới

### 3.1. Phân hệ Xác thực (Auth Context)
* **[MODIFY] [LoginUseCase.java](../../src/main/java/com/examp/springmvc/auth/application/usecase/LoginUseCase.java):**
  - Loại bỏ dependency vào `UserQueryMapper`, `UserDataAccessMapper`, và `UserDbEntity`.
  - Thay thế bằng việc gọi cổng Persistence hiện có ở tầng Domain: `UserPersistencePort.findByUsername(username)`.
* **[MODIFY] [LoginUseCaseTest.java](../../src/test/java/com/examp/springmvc/auth/application/usecase/LoginUseCaseTest.java):**
  - Chuyển sang mock `UserPersistencePort`, loại bỏ hoàn toàn các lớp Infrastructure khỏi bài test.

### 3.2. Phân hệ Quản lý Người dùng (User Context)
* **[NEW] [UserQueryPort.java](../../src/main/java/com/examp/springmvc/user/application/usermanagement/query/UserQueryPort.java):**
  - Cung cấp hai phương thức: `findAll()` và `findById(Long id)`.
* **[NEW] [UserQueryAdapter.java](../../src/main/java/com/examp/springmvc/user/infrastructure/persistence/UserQueryAdapter.java):**
  - Triển khai `UserQueryPort`, đóng gói logic gọi `UserQueryMapper` và chuyển đổi dữ liệu sang `UserDTO`.
* **[MODIFY] [FindAllUsersUseCase.java](../../src/main/java/com/examp/springmvc/user/application/usermanagement/query/FindAllUsersUseCase.java) & [FindUserByIdUseCase.java](../../src/main/java/com/examp/springmvc/user/application/usermanagement/query/FindUserByIdUseCase.java):**
  - Chỉ phụ thuộc và gọi qua `UserQueryPort`.
* **[MODIFY] [UserQueryUseCasesTest.java](../../src/test/java/com/examp/springmvc/user/application/usermanagement/query/UserQueryUseCasesTest.java):**
  - Cập nhật mock sang `UserQueryPort`.

### 3.3. Phân hệ Địa chỉ Người dùng (User Address Context)
* **[NEW] [UserAddressQueryPort.java](../../src/main/java/com/examp/springmvc/user/application/address/query/UserAddressQueryPort.java):**
  - Cung cấp phương thức `findByUserId(Long userId)`.
* **[NEW] [UserAddressQueryAdapter.java](../../src/main/java/com/examp/springmvc/user/infrastructure/persistence/UserAddressQueryAdapter.java):**
  - Triển khai cổng và đóng gói MyBatis mapper.
* **[MODIFY] [GetUserAddressesUseCase.java](../../src/main/java/com/examp/springmvc/user/application/address/query/GetUserAddressesUseCase.java):**
  - Gọi qua `UserAddressQueryPort`.
* **[NEW] [GetUserAddressesUseCaseTest.java](../../src/test/java/com/examp/springmvc/user/application/address/query/GetUserAddressesUseCaseTest.java):**
  - Viết mới test suite để kiểm thử độc lập cho use case lấy danh sách địa chỉ.

### 3.4. Phân hệ Danh mục Sản phẩm (Catalog Context)
* **[NEW] [ProductQueryPort.java](../../src/main/java/com/examp/springmvc/catalog/application/product/query/ProductQueryPort.java):**
  - Định nghĩa các cổng truy vấn: `findAll()`, `findById(Long id)`, `findByCategoryId(Long categoryId)`.
* **[NEW] [ProductQueryAdapter.java](../../src/main/java/com/examp/springmvc/catalog/infrastructure/persistence/ProductQueryAdapter.java):**
  - Đóng gói MyBatis `ProductMapper`. Đặc biệt hỗ trợ lấy dữ liệu kèm theo thông tin JOIN (ví dụ: `categoryName`) tối ưu hơn so với việc map qua Domain Model `Product`.
* **[MODIFY] [FindAllProductsUseCase.java](../../src/main/java/com/examp/springmvc/catalog/application/product/query/FindAllProductsUseCase.java) & [FindProductByIdUseCase.java](../../src/main/java/com/examp/springmvc/catalog/application/product/query/FindProductByIdUseCase.java):**
  - Sử dụng `ProductQueryPort`.
* **[NEW] [ProductQueryUseCasesTest.java](../../src/test/java/com/examp/springmvc/catalog/application/product/query/ProductQueryUseCasesTest.java):**
  - Viết mới test suite để bao phủ tất cả các trường hợp truy vấn sản phẩm.

### 3.5. Độc lập hóa Upload Cloudinary khỏi Transaction Database (Catalog Context)
* **[MODIFY] [ImageStoragePort.java](../../src/main/java/com/examp/springmvc/catalog/domain/ports/output/ImageStoragePort.java):**
  - Mở rộng cổng lưu trữ thêm phương thức `delete(String imageUrl)` để phục vụ việc thu hồi, dọn dẹp ảnh khi DB rollback.
* **[MODIFY] [CloudinaryImageStorageAdapter.java](../../src/main/java/com/examp/springmvc/catalog/infrastructure/storage/CloudinaryImageStorageAdapter.java):**
  - Triển khai phương thức `delete` giúp trích xuất `public_id` từ URL an toàn của Cloudinary và thực hiện gọi API `destroy` để xóa ảnh trên đám mây.
* **[MODIFY] [CreateProductUseCase.java](../../src/main/java/com/examp/springmvc/catalog/application/product/command/CreateProductUseCase.java):**
  - Loại bỏ `@Transactional` ở mức phương thức.
  - Sử dụng `TransactionTemplate` để bao bọc riêng thao tác lưu DB (`save`). Tác vụ upload ảnh lên Cloudinary được đẩy ra ngoài transaction để tránh giữ kết nối DB lâu gây nghẽn pool.
  - Thêm khối `try-catch` dọn dẹp ảnh đã upload nếu việc lưu DB thất bại và ném lỗi rollback.
* **[MODIFY] [UpdateProductUseCase.java](../../src/main/java/com/examp/springmvc/catalog/application/product/command/UpdateProductUseCase.java):**
  - Loại bỏ `@Transactional` khỏi phương thức.
  - Thực hiện các công việc kiểm tra trùng SKU, lấy thông tin sản phẩm và upload ảnh mới ra ngoài transaction.
  - Chỉ cập nhật thông tin và lưu DB bên trong `TransactionTemplate`. Dọn dẹp ảnh mới vừa upload nếu DB save phát sinh lỗi.
* **[MODIFY] [CreateProductUseCaseTest.java](../../src/test/java/com/examp/springmvc/catalog/application/product/command/CreateProductUseCaseTest.java) & [UpdateProductUseCaseTest.java](../../src/test/java/com/examp/springmvc/catalog/application/product/command/UpdateProductUseCaseTest.java):**
  - Mock thêm `PlatformTransactionManager` và cấu hình stubbing cho `getTransaction(...)` để hỗ trợ test chạy cùng `TransactionTemplate`.
  - Bổ sung các bài test kiểm thử hành vi dọn dẹp ảnh mồ côi (rollback) khi lưu DB thất bại.

### 3.6. Bảo mật Đăng nhập (Auth Context) - Trả AuthenticatedUserDTO thay vì User Domain Entity
* **[NEW] [AuthenticatedUserDTO.java](../../src/main/java/com/examp/springmvc/auth/application/ports/input/AuthenticatedUserDTO.java):**
  - DTO đóng gói thông tin người dùng đã xác thực bao gồm: `id`, `username`, `email`, `role`, và `status`.
* **[MODIFY] [LoginInputPort.java](../../src/main/java/com/examp/springmvc/auth/application/ports/input/LoginInputPort.java):**
  - Cập nhật signature phương thức `execute` trả về `AuthenticatedUserDTO` thay vì thực thể Domain `User`.
* **[MODIFY] [LoginUseCase.java](../../src/main/java/com/examp/springmvc/auth/usecase/LoginUseCase.java):**
  - Chuyển đổi kết quả đăng nhập thành công sang `AuthenticatedUserDTO`.
* **[MODIFY] [AuthController.java](../../src/main/java/com/examp/springmvc/auth/presentation/AuthController.java):**
  - Nhận về `AuthenticatedUserDTO` từ use case và lưu trực tiếp DTO vào session, tránh lộ aggregate `User`.
* **[MODIFY] [UserAddressController.java](../../src/main/java/com/examp/springmvc/user/presentation/UserAddressController.java) & [OrderController.java](../../src/main/java/com/examp/springmvc/order/presentation/OrderController.java):**
  - Cập nhật kiểu dữ liệu của biến lưu thông tin đăng nhập trong Session thành `AuthenticatedUserDTO`.
* **[MODIFY] [LoginUseCaseTest.java](../../src/test/java/com/examp/springmvc/auth/application/usecase/LoginUseCaseTest.java) & [AuthControllerTest.java](../../src/test/java/com/examp/springmvc/auth/presentation/AuthControllerTest.java):**
  - Cập nhật mock và asserts cho việc trả về `AuthenticatedUserDTO`.

### 3.7. Quản lý Tồn kho (Catalog Context) - Tích hợp trường Stock và Migration
* **[NEW] [add_stock_to_products.sql](../../docs/sql/add_stock_to_products.sql):**
  - Script SQL thêm cột `STOCK` kiểu `NUMERIC` với giá trị mặc định là `100` vào bảng `APP_PRODUCTS` và chạy dữ liệu ban đầu.
* **[MODIFY] [Product.java](../../src/main/java/com/examp/springmvc/catalog/domain/model/Product.java):**
  - Thêm trường `stock` (tồn kho).
  - Viết logic kiểm tra tính hợp lệ của stock (không được phép âm).
  - Cung cấp phương thức nghiệp vụ `decreaseStock(int quantity)` để giảm tồn kho khi bán hàng, tự động validate lỗi nếu số lượng đặt mua vượt quá tồn kho.
* **[MODIFY] [ProductDbEntity.java](../../src/main/java/com/examp/springmvc/catalog/infrastructure/persistence/ProductDbEntity.java):**
  - Thêm trường `stock` và các getter/setter để hỗ trợ map dữ liệu từ DB.
* **[MODIFY] [ProductMapper.java](../../src/main/java/com/examp/springmvc/catalog/infrastructure/persistence/ProductMapper.java) & [ProductMapper.xml](../../src/main/resources/mapper/ProductMapper.xml):**
  - Ánh xạ cột `STOCK` vào `productResultMap`.
  - Cập nhật các câu lệnh SQL `INSERT`, `UPDATE`, `SELECT` để truy vấn/lưu trữ trường `STOCK`.
  - Định nghĩa truy vấn bulk `findByIds` nhằm hỗ trợ lấy nhiều sản phẩm theo danh sách ID cùng một lúc.

### 3.8. Refactor Đặt hàng (Order Context) - Khắc phục N+1 SELECT và Validate Trạng thái/Tồn kho
* **[MODIFY] [PlaceOrderUseCase.java](../../src/main/java/com/examp/springmvc/order/application/command/PlaceOrderUseCase.java):**
  - **Khắc phục N+1 SELECT:** Lấy toàn bộ danh sách `productId` từ yêu cầu đặt hàng, gọi truy vấn bulk `productPersistencePort.findByIds(productIds)` duy nhất một lần để lấy tất cả sản phẩm liên quan.
  - **Kiểm tra trạng thái:** Đảm bảo tất cả sản phẩm đều đang ở trạng thái `ProductStatus.ACTIVE`. Nếu có sản phẩm ngừng hoạt động, ném lỗi cảnh báo.
  - **Khấu trừ tồn kho:** Gọi phương thức nghiệp vụ `product.decreaseStock(req.getQuantity())` của Domain để kiểm tra và trừ tồn kho trực tiếp.
  - **Lưu trạng thái mới:** Thực hiện cập nhật số lượng tồn kho mới của sản phẩm xuống Database sau khi kiểm tra đơn hàng thành công.
* **[MODIFY] [PlaceOrderUseCaseTest.java](../../src/test/java/com/examp/springmvc/order/application/command/PlaceOrderUseCaseTest.java):**
  - Viết bổ sung các ca kiểm thử (Test Cases): đặt hàng thành công (trừ tồn kho chính xác), thất bại do sản phẩm không hoạt động, thất bại do quá số lượng tồn kho hiện có.

### 3.9. Tối ưu hóa Đăng ký & Tạo người dùng (Auth & User Context)
* **[NEW] [add_unique_username_to_users.sql](../../docs/sql/add_unique_username_to_users.sql):**
  - Tạo script migration thêm ràng buộc duy nhất (`UNIQUE`) cho cột `USERNAME` trong bảng `APP_USERS`.
* **[MODIFY] [RegisterUseCase.java](../../src/main/java/com/examp/springmvc/auth/application/usecase/RegisterUseCase.java) & [CreateUserUseCase.java](../../src/main/java/com/examp/springmvc/user/application/usermanagement/command/CreateUserUseCase.java):**
  - Đảo ngược thứ tự kiểm tra: thực hiện kiểm tra sự tồn tại của `username` trước khi băm mật khẩu (`BCrypt.hash`), giảm thiểu thời gian CPU vô ích.
  - Sử dụng try-catch ngoại lệ `DataIntegrityViolationException` khi thực hiện `save` để đảm bảo tính atomic chống race condition tại mức DB khi có các yêu cầu đăng ký đồng thời.
* **[MODIFY] [UserDTO.java](../../src/main/java/com/examp/springmvc/user/application/usermanagement/query/UserDTO.java):**
  - Loại bỏ hoàn toàn trường `password` và các getter/setter liên quan nhằm ngăn chặn rò rỉ thông tin nhạy cảm ra ngoài biên API/Controller.
* **[MODIFY] [RegisterUseCaseTest.java](../../src/test/java/com/examp/springmvc/auth/application/usecase/RegisterUseCaseTest.java) & [UserCommandUseCasesTest.java](../../src/test/java/com/examp/springmvc/user/application/usermanagement/command/UserCommandUseCasesTest.java):**
  - Xóa stubbing băm mật khẩu không cần thiết do thay đổi thứ tự kiểm tra. Bổ sung các test case kiểm tra hành vi ném ngoại lệ khi có xung đột dữ liệu duy nhất (`UNIQUE`).

### 3.10. Chuẩn hóa Trạng thái Đơn hàng (Order Context)
* **[NEW] [OrderStatusAction.java](../../src/main/java/com/examp/springmvc/order/application/command/OrderStatusAction.java):**
  - Khai báo Enum gồm các hành động: `CONFIRM`, `SHIP`, `DELIVER`. Giải quyết vấn đề Primitive Obsession.
* **[MODIFY] [UpdateOrderStatusCommand.java](../../src/main/java/com/examp/springmvc/order/application/command/UpdateOrderStatusCommand.java) & [UpdateOrderStatusUseCase.java](../../src/main/java/com/examp/springmvc/order/application/command/UpdateOrderStatusUseCase.java):**
  - Thay đổi kiểu thuộc tính `action` từ `String` sang `OrderStatusAction`.
  - Loại bỏ nhánh `"cancel"` khỏi Switch-case của `UpdateOrderStatusUseCase` để quy tụ tất cả các luồng hủy đơn hàng về một Use Case duy nhất.
* **[MODIFY] [CancelOrderUseCase.java](../../src/main/java/com/examp/springmvc/order/application/command/CancelOrderUseCase.java):**
  - Nhận thêm tham số `boolean isAdmin` để bỏ qua việc kiểm tra quyền sở hữu đơn hàng (`userId`) khi tác vụ hủy được thực hiện bởi Admin.
* **[MODIFY] [AdminOrderController.java](../../src/main/java/com/examp/springmvc/order/presentation/AdminOrderController.java) & [OrderController.java](../../src/main/java/com/examp/springmvc/order/presentation/OrderController.java):**
  - Cập nhật luồng nghiệp vụ tương ứng: Client gọi `CancelOrderUseCase` với `isAdmin = false`; Admin gọi `CancelOrderUseCase` với `isAdmin = true` khi yêu cầu hủy, và gọi `UpdateOrderStatusUseCase` cho các chuyển trạng thái thông thường.
* **[MODIFY] [CancelOrderUseCaseTest.java](../../src/test/java/com/examp/springmvc/order/application/command/CancelOrderUseCaseTest.java) & [UpdateOrderStatusUseCaseTest.java](../../src/test/java/com/examp/springmvc/order/application/command/UpdateOrderStatusUseCaseTest.java) [NEW]:**
  - Cập nhật và viết mới các unit test để kiểm nghiệm đầy đủ hành vi hủy đơn của Admin, hủy đơn của User, và cập nhật trạng thái đơn qua Enum.

### 3.11. DRY Mapping & Định dạng hiển thị phía View
* **[MODIFY] [OrderDTO.java](../../src/main/java/com/examp/springmvc/order/application/query/OrderDTO.java) & [OrderItemDTO.java](../../src/main/java/com/examp/springmvc/order/application/query/OrderItemDTO.java):**
  - Thêm phương thức tĩnh `fromDomain` để tập trung hóa logic chuyển đổi dữ liệu từ Domain Model sang DTO.
  - Loại bỏ các phương thức định dạng ngày tháng và hiển thị tiếng Việt khỏi `OrderDTO` (đưa logic hiển thị về đúng tầng Presentation/View).
* **[MODIFY] [FindAllOrdersUseCase.java](../../src/main/java/com/examp/springmvc/order/application/query/FindAllOrdersUseCase.java), [FindOrderByIdUseCase.java](../../src/main/java/com/examp/springmvc/order/application/query/FindOrderByIdUseCase.java), & [FindOrdersByUserUseCase.java](../../src/main/java/com/examp/springmvc/order/application/query/FindOrdersByUserUseCase.java):**
  - Loại bỏ các mã code ánh xạ trùng lặp, gọi trực tiếp `OrderDTO.fromDomain`.
* **[NEW] [ViewHelper.java](../../src/main/java/com/examp/springmvc/shared/presentation/ViewHelper.java) & [helpers.tld](../../src/main/webapp/WEB-INF/tlds/helpers.tld):**
  - Xây dựng lớp Helper đóng gói các định dạng chuỗi, ngày tháng (`formatDateTime`, `formatPaymentMethod`, `formatPaymentStatus`) và đăng ký làm Custom EL Functions cho JSP.
* **[MODIFY] Các file JSP:** `admin-order-list.jsp`, `admin-order-detail.jsp`, `order-history.jsp`, `order-detail.jsp`.
  - Khai báo taglib `helpers` và gọi các hàm định dạng hiển thị.
  - Đóng gói toàn bộ các hàm EL trong thẻ `<c:out value="${...}"/>` để đảm bảo an toàn trước nguy cơ tấn công XSS (vượt qua kiểm thử `JspXssEncodingTest`).
* **[NEW] [OrderDTOTest.java](../../src/test/java/com/examp/springmvc/order/application/query/OrderDTOTest.java):**
  - Viết unit test xác thực quy trình mapping dữ liệu từ Domain sang DTO hoạt động chính xác.


---

## 4. Kết quả Xác thực & Đánh giá Chất lượng

Quá trình kiểm thử và phân tích tĩnh được thực hiện nghiêm ngặt để đảm bảo không phát sinh lỗi mới:

1. **Spotless Format (`mvn spotless:apply`):**
   * Đạt trạng thái **SUCCESS**. Toàn bộ mã nguồn mới viết và chỉnh sửa tuân thủ hoàn hảo chuẩn format của dự án.
2. **Checkstyle (`mvn checkstyle:check`):**
   * Đạt trạng thái **SUCCESS** với **0 lỗi vi phạm**.
3. **SpotBugs (`mvn spotbugs:check`):**
   * Đạt trạng thái **SUCCESS** với **0 lỗi vi phạm**.
   * Đã bổ sung annotation `@SuppressFBWarnings("EI_EXPOSE_REP2")` tại các constructor của Adapter để ngăn chặn các cảnh báo sai về rò rỉ biểu diễn nội bộ của đối tượng mutable.
4. **Kiểm thử Đơn vị (`mvn clean test`):**
   * Toàn bộ **152 / 152** unit tests chạy thành công. Không có bài test nào bị lỗi hay thất bại.

---

## 5. Hướng phát triển và Cập nhật sau này

Để đảm bảo kiến trúc luôn nhất quán và mở rộng tốt hơn trong tương lai, chúng tôi khuyến nghị:
* **Hỗ trợ Phân trang & Sắp xếp (Pagination & Sorting):** Bổ sung tham số `Pageable` vào các phương thức của `ProductQueryPort` và `UserQueryPort` để nâng cao hiệu năng khi dữ liệu lớn dần.
* **Cơ chế Caching:** Tích hợp bộ nhớ đệm (Cache) tại các triển khai Adapter ở tầng Infrastructure (như Redis/Ehcache) cho các truy vấn sản phẩm mà không làm ảnh hưởng đến mã nguồn ở tầng Application.
* **Độc lập hóa DTO:** Đảm bảo các DTO chỉ chứa các kiểu dữ liệu nguyên thủy (primitive/string) hoặc các DTO con, không chứa bất kỳ thực thể Domain hay Entity cơ sở dữ liệu nào.
