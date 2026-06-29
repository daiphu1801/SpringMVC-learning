/**
 * JavaScript for the Spring MVC architecture interactive page.
 */

// Initialize on DOM load
document.addEventListener("DOMContentLoaded", function() {
    // Tab switching listener
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const tabId = this.getAttribute('data-tab');
            if (tabId) {
                switchTab(tabId, this);
            }
        });
    });

    // Layer card click listener
    document.querySelectorAll('.layer-card').forEach(card => {
        card.addEventListener('click', function() {
            const layerId = this.getAttribute('data-layer');
            if (layerId) {
                showLayerDetails(layerId);
            }
        });
    });

    showLayerDetails('presentation');
});

function switchTab(tabId, tabButton) {
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));

    tabButton.classList.add('active');
    const targetContent = document.getElementById(tabId);
    if (targetContent) {
        targetContent.classList.add('active');
    }
}

const layerDetails = {
    presentation: {
        title: "Lớp Presentation (Giao diện hiển thị)",
        desc: "Đảm nhận nhiệm vụ giao tiếp trực tiếp với tác nhân bên ngoài hệ thống (trình duyệt web, ứng dụng mobile, người dùng).",
        items: [
            { label: "Các Controller: ", text: "UserAddressController, UserCommandController, UserQueryController, AuthController." },
            { label: "Giao diện: ", text: "Các tệp tin JSP nằm trong thư mục WEB-INF/views/ hiển thị dữ liệu cho người dùng." },
            { label: "Validate và Session: ", text: "Kiểm tra an toàn bảo mật, phân quyền tài khoản Admin/User, lưu vết session người dùng hiện tại." },
            { label: "Quy tắc quan trọng: ", text: "Lớp Presentation CHỈ được gọi vào các Input Port (interface của Use Case) thuộc lớp Application. Tuyệt đối không gọi trực tiếp xuống Database hoặc các lớp Domain model gốc một cách tùy tiện." }
        ]
    },
    application: {
        title: "Lớp Application (Các nghiệp vụ kịch bản Use Case)",
        desc: "Nơi chỉ huy hoạt động của hệ thống, định nghĩa rõ ràng các ca sử dụng (Use Cases) độc lập.",
        items: [
            { label: "Input Ports (Cổng vào): ", text: "Các Interface định nghĩa cách Presentation giao tiếp (VD: CreateUserInputPort, AddAddressInputPort)." },
            { label: "Use Cases (Cài đặt): ", text: "Lớp xử lý nghiệp vụ kịch bản, nạp dữ liệu từ Adapter, gọi logic miền và lưu lại (VD: CreateUserUseCase, GetUserAddressesUseCase)." },
            { label: "DTO (Data Transfer Object): ", text: "UserDTO, AddressDTO giúp đóng gói và tối ưu dữ liệu truyền ra bên ngoài." },
            { label: "Độc lập công nghệ: ", text: "Không phụ thuộc vào database cụ thể (MySQL, Oracle) hay cách hiển thị UI. Giúp hệ thống dễ viết Unit Test độc lập." }
        ]
    },
    domain: {
        title: "Lớp Domain (Quy tắc cốt lõi của Miền nghiệp vụ)",
        desc: "Nơi linh hồn và giá trị cốt lõi của doanh nghiệp được định hình. Không bị ảnh hưởng bởi bất kỳ yếu tố kỹ thuật hay framework nào.",
        items: [
            { label: "Aggregate Root (User): ", text: "Gốc thực thể bảo vệ các luật bất biến (Invariants) của toàn bộ các thực thể con bên trong nó." },
            { label: "Value Objects (Address, Password, Email): ", text: "Đại diện cho các giá trị bất biến, tự hợp lệ dữ liệu ngay khi khởi tạo." },
            { label: "Domain Events (UserRegisteredEvent...): ", text: "Các sự kiện phát ra khi hệ thống thay đổi trạng thái nghiệp vụ." },
            { label: "Quy tắc quan trọng nhất: ", text: "Lớp Domain nằm ở trong cùng, KHÔNG IMPORT và KHÔNG BIẾT GÌ về bất kỳ thư viện ngoài nào (không phụ thuộc Spring, MyBatis, Jakarta Servlet...)." }
        ]
    },
    infrastructure: {
        title: "Lớp Infrastructure (Cơ sở hạ tầng & Hỗ trợ kỹ thuật)",
        desc: "Triển khai chi tiết các công nghệ bên ngoài để hỗ trợ cho ứng dụng vận hành.",
        items: [
            { label: "Persistence Adapter (UserPersistenceAdapter): ", text: "Thực thi Interface UserPersistencePort để đọc/ghi dữ liệu với database." },
            { label: "MyBatis Mappers (UserAddressMapper, UserCommandMapper): ", text: "Chứa các truy vấn SQL thuần túy tương tác với cơ sở dữ liệu quan hệ." },
            { label: "Security & Config: ", text: "MyBatisConfig, WebConfig, SecurityInterceptor điều phối bảo mật và hoạt động của toàn ứng dụng." },
            { label: "Quy tắc phụ thuộc: ", text: "Lớp này phụ thuộc trực tiếp vào các Port của Lớp Application và Domain. Mọi chi tiết công nghệ (MyBatis, Spring, Security) đều được che giấu tại đây." }
        ]
    }
};

function showLayerDetails(layerId) {
    const details = layerDetails[layerId];
    document.getElementById('detail-title').textContent = details.title;
    document.getElementById('detail-desc').textContent = details.desc;
    
    const listContainer = document.getElementById('detail-list');
    listContainer.textContent = ''; // Safe clear
    
    details.items.forEach(item => {
        const li = document.createElement('li');
        
        const b = document.createElement('b');
        b.textContent = item.label;
        
        const textNode = document.createTextNode(item.text);
        
        li.appendChild(b);
        li.appendChild(textNode);
        listContainer.appendChild(li);
    });

    // Highlight selected card by using a CSS class instead of setting inline styles
    document.querySelectorAll('.layer-card').forEach(card => {
        card.classList.remove('active');
    });
    const activeCard = document.querySelector(`.layer-card.${layerId}`);
    if (activeCard) {
        activeCard.classList.add('active');
    }
}
