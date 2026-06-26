/**
 * JavaScript for the Spring MVC architecture interactive page.
 */

function switchTab(tabId) {
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));

    const event = window.event;
    if (event) {
        event.target.classList.add('active');
    }
    document.getElementById(tabId).classList.add('active');
}

const layerDetails = {
    presentation: {
        title: "Lớp Presentation (Giao diện hiển thị)",
        desc: "Đảm nhận nhiệm vụ giao tiếp trực tiếp với tác nhân bên ngoài hệ thống (trình duyệt web, ứng dụng mobile, người dùng).",
        items: [
            "<b>Các Controller:</b> UserAddressController, UserCommandController, UserQueryController, AuthController.",
            "<b>Giao diện:</b> Các tệp tin JSP nằm trong thư mục WEB-INF/views/ hiển thị dữ liệu cho người dùng.",
            "<b>Validate và Session:</b> Kiểm tra an toàn bảo mật, phân quyền tài khoản Admin/User, lưu vết session người dùng hiện tại.",
            "<b>Quy tắc quan trọng:</b> Lớp Presentation CHỈ được gọi vào các Input Port (interface của Use Case) thuộc lớp Application. Tuyệt đối không gọi trực tiếp xuống Database hoặc các lớp Domain model gốc một cách tùy tiện."
        ]
    },
    application: {
        title: "Lớp Application (Các nghiệp vụ kịch bản Use Case)",
        desc: "Nơi chỉ huy hoạt động của hệ thống, định nghĩa rõ ràng các ca sử dụng (Use Cases) độc lập.",
        items: [
            "<b>Input Ports (Cổng vào):</b> Các Interface định nghĩa cách Presentation giao tiếp (VD: CreateUserInputPort, AddAddressInputPort).",
            "<b>Use Cases (Cài đặt):</b> Lớp xử lý nghiệp vụ kịch bản, nạp dữ liệu từ Adapter, gọi logic miền và lưu lại (VD: CreateUserUseCase, GetUserAddressesUseCase).",
            "<b>DTO (Data Transfer Object):</b> UserDTO, AddressDTO giúp đóng gói và tối ưu dữ liệu truyền ra bên ngoài.",
            "<b>Độc lập công nghệ:</b> Không phụ thuộc vào database cụ thể (MySQL, Oracle) hay cách hiển thị UI. Giúp hệ thống dễ viết Unit Test độc lập."
        ]
    },
    domain: {
        title: "Lớp Domain (Quy tắc cốt lõi của Miền nghiệp vụ)",
        desc: "Nơi linh hồn và giá trị cốt lõi của doanh nghiệp được định hình. Không bị ảnh hưởng bởi bất kỳ yếu tố kỹ thuật hay framework nào.",
        items: [
            "<b>Aggregate Root (User):</b> Gốc thực thể bảo vệ các luật bất biến (Invariants) của toàn bộ các thực thể con bên trong nó.",
            "<b>Value Objects (Address, Password, Email):</b> Đại diện cho các giá trị bất biến, tự hợp lệ dữ liệu ngay khi khởi tạo.",
            "<b>Domain Events (UserRegisteredEvent...):</b> Các sự kiện phát ra khi hệ thống thay đổi trạng thái nghiệp vụ.",
            "<b>Quy tắc quan trọng nhất:</b> Lớp Domain nằm ở trong cùng, KHÔNG IMPORT và KHÔNG BIẾT GÌ về bất kỳ thư viện ngoài nào (không phụ thuộc Spring, MyBatis, Jakarta Servlet...)."
        ]
    },
    infrastructure: {
        title: "Lớp Infrastructure (Cơ sở hạ tầng & Hỗ trợ kỹ thuật)",
        desc: "Triển khai chi tiết các công nghệ bên ngoài để hỗ trợ cho ứng dụng vận hành.",
        items: [
            "<b>Persistence Adapter (UserPersistenceAdapter):</b> Thực thi Interface UserPersistencePort để đọc/ghi dữ liệu với database.",
            "<b>MyBatis Mappers (UserAddressMapper, UserCommandMapper):</b> Chứa các truy vấn SQL thuần túy tương tác với cơ sở dữ liệu quan hệ.",
            "<b>Security & Config:</b> MyBatisConfig, WebConfig, SecurityInterceptor điều phối bảo mật và hoạt động của toàn ứng dụng.",
            "<b>Quy tắc phụ thuộc:</b> Lớp này phụ thuộc trực tiếp vào các Port của Lớp Application và Domain. Mọi chi tiết công nghệ (MyBatis, Spring, Security) đều được che giấu tại đây."
        ]
    }
};

function showLayerDetails(layerId) {
    const details = layerDetails[layerId];
    document.getElementById('detail-title').innerText = details.title;
    document.getElementById('detail-desc').innerText = details.desc;
    
    const listContainer = document.getElementById('detail-list');
    listContainer.innerHTML = '';
    details.items.forEach(item => {
        const li = document.createElement('li');
        li.innerHTML = item;
        listContainer.appendChild(li);
    });

    // Highlight selected card
    document.querySelectorAll('.layer-card').forEach(card => {
        card.style.border = '2px solid transparent';
    });
    const activeCard = document.querySelector(`.layer-card.${layerId}`);
    if (activeCard) {
        activeCard.style.border = '2px solid #7F84FF';
    }
}

// Initialize on DOM load
document.addEventListener("DOMContentLoaded", function() {
    showLayerDetails('presentation');
});
