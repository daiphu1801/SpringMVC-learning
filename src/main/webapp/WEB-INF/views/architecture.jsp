<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Kiến Trúc Dự Án (Clean Architecture & DDD)" showHeader="false">
<jsp:attribute name="head">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/architecture.css?v=${appVersion}">
    <script src="${pageContext.request.contextPath}/resources/js/architecture.js?v=${appVersion}" defer></script>
</jsp:attribute>
<jsp:body>
<div class="doc-container">
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/users" class="btn btn-secondary nav-back-btn">
            ← Quay lại ứng dụng
        </a>
        <span class="nav-title">
            Spring MVC Architecture Dashboard
        </span>
    </div>

    <div class="header-section">
        <h1>Kiến Trúc Dự Án Spring MVC</h1>
        <p>Phân tích mô hình Clean Architecture kết hợp Domain-Driven Design (DDD) & CQRS</p>
    </div>

    <!-- Tab Bar -->
    <div class="tabs">
        <button class="tab-btn active" data-tab="clean-arch">Clean Architecture</button>
        <button class="tab-btn" data-tab="ddd-patterns">Domain-Driven Design (DDD)</button>
        <button class="tab-btn" data-tab="cqrs-pattern">CQRS Pattern</button>
        <button class="tab-btn" data-tab="dep-flow">Luồng Phụ Thuộc & Thực Thi</button>
        <button class="tab-btn" data-tab="directories">Cấu Trúc Thư Mục</button>
    </div>

    <!-- TAB 1: Clean Architecture -->
    <div id="clean-arch" class="tab-content active">
        <div class="arch-layout">
            <!-- Layers Visual -->
            <div class="layers-box">
                <h3 class="section-title">Các lớp kiến trúc (từ ngoài vào trong)</h3>
                
                <div class="layer-card presentation" data-layer="presentation">
                    <h3>1. Lớp Presentation <span>Controller & UI</span></h3>
                    <p>Tiếp nhận HTTP request, xử lý validate dữ liệu form, session và trả về View (JSP).</p>
                </div>
                
                <div class="layer-card application" data-layer="application">
                    <h3>2. Lớp Application <span>Use Case & Port</span></h3>
                    <p>Định nghĩa các kịch bản nghiệp vụ (Use Cases) của hệ thống. Độc lập hoàn toàn với Framework/Database.</p>
                </div>
                
                <div class="layer-card domain" data-layer="domain">
                    <h3>3. Lớp Domain <span>Nghiệp Vụ Cốt Lõi</span></h3>
                    <p>Trái tim của hệ thống. Chứa các quy tắc nghiệp vụ, thực thể (Entities), Value Objects và Domain Events.</p>
                </div>
                
                <div class="layer-card infrastructure" data-layer="infrastructure">
                    <h3>4. Lớp Infrastructure <span>Database & Config</span></h3>
                    <p>Công cụ hỗ trợ lớp Domain và Application (MyBatis Mapper, Adapter Persistence, Config, Security Interceptor).</p>
                </div>
            </div>

            <!-- Layer Details Panel -->
            <div class="detail-panel" id="layer-details-panel">
                <h3 id="detail-title">Presentation Layer</h3>
                <p id="detail-desc">Vui lòng nhấp vào một lớp ở sơ đồ bên trái để xem chi tiết các thành phần và quy tắc phụ thuộc.</p>
                <ul class="detail-list" id="detail-list">
                    <li>Nhấp chọn để bắt đầu khám phá cấu trúc lớp.</li>
                </ul>
            </div>
        </div>
    </div>

    <!-- TAB 2: DDD Patterns -->
    <div id="ddd-patterns" class="tab-content">
        <div class="ddd-grid">
            <div class="ddd-card">
                <span class="ddd-badge">Aggregate Root</span>
                <h3>Aggregate Roots</h3>
                <p class="ddd-desc">
                    Thực thể gốc quản lý vòng đời và các quy tắc nghiệp vụ của nhóm đối tượng bên trong nó.
                </p>
                <p class="ddd-example">
                    <strong>Ví dụ trong dự án:</strong>
                    <br>• <code>User</code>: Quản lý danh sách địa chỉ <code>List&lt;Address&gt;</code> qua các invariants nghiệp vụ.
                    <br>• <code>Order</code>: Quản lý các trạng thái đơn hàng (PENDING, PAID, CONFIRMED, DELIVERED) và sản phẩm mua. Trạng thái chỉ được chuyển dịch hợp lệ qua phương thức nghiệp vụ của Order.
                </p>
            </div>

            <div class="ddd-card">
                <span class="ddd-badge">Value Object</span>
                <h3>Value Objects & Enums</h3>
                <p class="ddd-desc">
                    Các đối tượng không có danh tính độc lập, so sánh bằng cấu trúc giá trị và có tính bất biến (Immutable).
                </p>
                <p class="ddd-example">
                    <strong>Ví dụ trong dự án:</strong> 
                    <br>• <code>Email</code> & <code>Password</code>: Tự validate định dạng dữ liệu đầu vào.
                    <br>• <code>PaymentMethod</code> (CASH, VIETQR) & <code>PaymentStatus</code> (PENDING, PAID): Các enum quản lý thông tin thanh toán cho Order.
                </p>
            </div>

            <div class="ddd-card">
                <span class="ddd-badge">Domain Event</span>
                <h3>Domain Events</h3>
                <p class="ddd-desc">
                    Những sự kiện nghiệp vụ quan trọng đã xảy ra trong quá khứ được Aggregate Root phát ra để thông báo.
                </p>
                <p class="ddd-example">
                    <strong>Ví dụ trong dự án:</strong> 
                    <br>• <code>UserRegisteredEvent</code>: Phát ra khi tạo mới tài khoản thành công.
                    <br>• <code>OrderPlacedEvent</code>: Kích hoạt khi khách đặt đơn mới thành công.
                    <br>• Sự kiện được gom lại trong Aggregate và phát đi qua Spring <code>ApplicationEventPublisher</code> khi lưu thành công nhằm kích hoạt các logic bất đồng bộ (Gửi mail, Logging...).
                </p>
            </div>
        </div>
    </div>

    <!-- TAB 3: CQRS Pattern -->
    <div id="cqrs-pattern" class="tab-content">
        <div class="content-card">
            <h3 class="card-title">Phân tách Lệnh - Truy vấn (CQRS)</h3>
            <p class="card-desc">
                Ứng dụng chia rõ ràng luồng xử lý dữ liệu thành hai nhánh riêng biệt để đảm bảo hiệu năng và tính toàn vẹn nghiệp vụ:
            </p>

            <div class="split-grid">
                <div class="column-card">
                    <h4 class="write-title">
                        <span class="write-dot"></span>
                        WRITE SIDE (Commands)
                    </h4>
                    <p class="ddd-example">
                        Nhận trách nhiệm thay đổi trạng thái hệ thống. Mọi luồng ghi bắt buộc phải tải Aggregate Root tương ứng lên bộ nhớ, thực hiện kiểm tra Invariants (luật bất biến) và lưu lại qua Mapper.
                    </p>
                    <ul class="detail-list card-list">
                        <li><code>CreateUserUseCase</code> & <code>AddAddressUseCase</code></li>
                        <li><code>PlaceOrderUseCase</code> (Khởi tạo đơn hàng mới)</li>
                        <li><code>ConfirmVietQRPaymentUseCase</code> (Cập nhật thanh toán thành công cho VietQR)</li>
                    </ul>
                </div>

                <div class="column-card">
                    <h4 class="read-title">
                        <span class="read-dot"></span>
                        READ SIDE (Queries)
                    </h4>
                    <p class="ddd-example">
                        Nhận trách nhiệm tối ưu truy vấn đọc dữ liệu siêu nhanh. Bỏ qua Aggregate Root cồng kềnh, truy vấn trực tiếp thông qua MyBatis Mapper và map thẳng ra các DTO gọn nhẹ để hiển thị lên màn hình.
                    </p>
                    <ul class="detail-list card-list">
                        <li><code>FindUserByIdUseCase</code> / <code>FindAllUsersUseCase</code></li>
                        <li><code>FindOrderByIdUseCase</code> (Xem chi tiết đơn hàng dạng DTO)</li>
                        <li><code>FindOrdersByUserUseCase</code> (Xem lịch sử mua hàng của User)</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>

    <!-- TAB: Dependency & Execution Flow -->
    <div id="dep-flow" class="tab-content">
        <div class="content-card">
            <h3 class="card-title">Luồng Phụ Thuộc (DIP) & Luồng Chạy Thực Tế (Execution Flow)</h3>
            <p class="card-desc">
                Nhờ áp dụng nguyên lý <strong>Đảo ngược sự phụ thuộc (Dependency Inversion Principle - DIP)</strong> qua các cổng giao tiếp <strong>Ports & Adapters</strong>, Bounded Context Catalog & Order hoàn toàn độc lập với các công nghệ bên ngoài như MyBatis, Oracle Database, Cloudinary hay JavaMail SMTP.
            </p>

            <div class="split-grid">
                <!-- 1. Chiều phụ thuộc mã nguồn -->
                <div class="column-card-lg">
                    <h4 class="compile-title">
                        A. Chiều Phụ Thuộc Mã Nguồn (Compile-Time)
                    </h4>
                    <p class="text-muted-md">
                        Mọi mũi tên phụ thuộc đều <strong>hướng vào trong</strong> lõi Domain. Tầng Infrastructure (MyBatis, Adapters, Cloudinary) và Presentation (Controllers) tự import và phụ thuộc vào Domain & Application. Domain không hề import bất kỳ công nghệ bên ngoài nào.
                    </p>
                    <div class="code-panel-box">
                        <span class="code-yellow">// Chiều import trong mã nguồn:</span><br>
                        Presentation (Controller) <span class="code-blue">→ import →</span> Application (Use Case)<br>
                        Infrastructure (Adapter) <span class="code-blue">→ implement →</span> Domain (Port)<br>
                        Application (Use Case) <span class="code-blue">→ import →</span> Domain (Entity)<br>
                        <span class="code-blue">Domain Core</span> <span class="code-red">[Độc lập hoàn toàn]</span>
                    </div>
                </div>

                <!-- 2. Luồng chạy thực tế -->
                <div class="column-card-lg">
                    <h4 class="runtime-title">
                        B. Luồng Thực Thi Thực Tế (Runtime)
                    </h4>
                    <p class="text-muted-md">
                        Khi người dùng thao tác (ví dụ: Đặt đơn hàng mới), luồng đi qua các bước từ ngoài vào trong lõi nghiệp vụ, sau đó Spring DI tự động ánh xạ đa hình sang các lớp Adapter ở tầng ngoài để thực thi.
                    </p>
                    <div class="code-panel-box">
                        <span class="code-green">1. User nhấn Thanh toán</span><br>
                        &nbsp;&nbsp;→ <span class="code-blue">OrderController</span> tiếp nhận<br>
                        <span class="code-green">2. Controller bọc dữ liệu và gọi</span><br>
                        &nbsp;&nbsp;→ <span class="code-blue">PlaceOrderUseCase</span><br>
                        <span class="code-green">3. UseCase gọi logic nghiệp vụ của</span><br>
                        &nbsp;&nbsp;→ <span class="code-blue">Order Entity (Domain)</span> để kiểm tra<br>
                        <span class="code-green">4. UseCase gọi Port để lưu</span><br>
                        &nbsp;&nbsp;→ Spring DI chuyển tiếp đến <span class="code-blue">OrderPersistenceAdapter</span><br>
                        &nbsp;&nbsp;→ Gọi <span class="code-blue">OrderMapper</span> chạy SQL Oracle
                    </div>
                </div>
            </div>
            
            <div class="acl-box">
                <h5 class="acl-title">Tích hợp Dịch vụ Bên thứ ba & Anti-Corruption Layer (ACL)</h5>
                <p class="acl-desc">
                    • <strong>Cloudinary Storage</strong>: `CloudinaryImageStorageAdapter` đóng vai trò là một **ACL** nhận dữ liệu thô, thực hiện upload lên đám mây Cloudinary và trả về URL an toàn cho Domain.<br>
                    • <strong>JavaMail SMTP</strong>: `JavaMailNotificationAdapter` đóng vai trò chuyển tiếp sự kiện của hệ thống sang giao thức SMTP của Gmail để gửi mail tự động cho người dùng mà không làm nghẽn/ảnh hưởng đến tiến trình chính của ứng dụng.
                </p>
            </div>
        </div>
    </div>

    <!-- TAB 4: Directory Structure -->
    <div id="directories" class="tab-content">
        <div class="dir-tree">
            <span class="folder">src/main/java/com/examp/springmvc</span><br>
            ├── <span class="folder">shared</span> <span class="comment">// Các phần dùng chung toàn dự án (Config, DB)</span><br>
            │   ├── <span class="folder">domain</span><br>
            │   │   └── <span class="file">DomainEvent.java</span> <span class="comment">// Giao diện Event miền dùng chung</span><br>
            │   └── <span class="folder">infrastructure/config</span> <span class="comment">// Cấu hình Spring, MyBatis, MailConfig, WebConfig</span><br>
            ├── <span class="folder">user</span> <span class="comment">// Bounded Context Quản lý Người dùng</span><br>
            │   ├── <span class="folder">domain</span> / <span class="folder">application</span> / <span class="folder">infrastructure</span> / <span class="folder">presentation</span><br>
            └── <span class="folder">order</span> <span class="comment">// Bounded Context Đơn hàng & Thanh toán (Mới)</span><br>
            &nbsp;&nbsp;&nbsp; ├── <span class="folder">domain</span> <span class="comment">// Nghiệp vụ cốt lõi</span><br>
            &nbsp;&nbsp;&nbsp; │   ├── <span class="folder">event</span> <span class="comment">// OrderPlacedEvent,...</span><br>
            &nbsp;&nbsp;&nbsp; │   ├── <span class="folder">model</span> <span class="comment">// Order (Aggregate Root), PaymentMethod (Enum),...</span><br>
            &nbsp;&nbsp;&nbsp; │   └── <span class="folder">ports</span> <span class="comment">// Cổng giao tiếp ngoài (OrderPersistencePort, NotificationPort)</span><br>
            &nbsp;&nbsp;&nbsp; ├── <span class="folder">application</span> <span class="comment">// Các kịch bản nghiệp vụ</span><br>
            &nbsp;&nbsp;&nbsp; │   ├── <span class="folder">command</span> <span class="comment">// PlaceOrderUseCase, ConfirmVietQRPaymentUseCase</span><br>
            &nbsp;&nbsp;&nbsp; │   └── <span class="folder">query</span> <span class="comment">// FindOrderByIdUseCase, FindOrdersByUserUseCase, OrderDTO</span><br>
            &nbsp;&nbsp;&nbsp; ├── <span class="folder">infrastructure</span> <span class="comment">// Persistence & Adapters</span><br>
            &nbsp;&nbsp;&nbsp; │   ├── <span class="folder">mapper</span> <span class="comment">// MyBatis OrderMapper</span><br>
            &nbsp;&nbsp;&nbsp; │   ├── <span class="folder">notification</span> <span class="comment">// JavaMailNotificationAdapter (SMTP)</span><br>
            &nbsp;&nbsp;&nbsp; │   └── <span class="folder">persistence</span> <span class="comment">// OrderPersistenceAdapter & OrderDbEntity</span><br>
            &nbsp;&nbsp;&nbsp; └── <span class="folder">presentation</span> <span class="comment">// Web Controller nhận yêu cầu</span><br>
            &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; └── <span class="file">OrderController.java</span>
        </div>
    </div>
</div>
</jsp:body>
</t:layout>
