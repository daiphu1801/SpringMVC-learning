<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Sổ địa chỉ người dùng</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
    <style>
        .address-layout {
            display: flex;
            gap: 30px;
            margin-top: 20px;
        }
        .address-form-section {
            flex: 1.2;
            background: var(--white);
            border: 1px solid var(--border-color);
            border-radius: 16px;
            padding: 25px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
        }
        .address-cards-section {
            flex: 1.8;
            display: flex;
            flex-direction: column;
            gap: 20px;
        }
        .address-card {
            background: var(--white);
            border: 1px solid var(--border-color);
            border-radius: 16px;
            padding: 20px;
            position: relative;
            transition: all var(--transition-speed) ease;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
        }
        .address-card:hover {
            transform: translateY(-2px);
            border-color: var(--primary-light);
            box-shadow: 0 8px 24px rgba(159, 161, 255, 0.15);
        }
        .address-card.default-address {
            border-left: 5px solid var(--primary);
        }
        .address-card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 12px;
        }
        .receiver-name {
            font-size: 1.1rem;
            font-weight: 600;
            color: var(--text-main);
        }
        .address-badge {
            font-size: 0.75rem;
            padding: 4px 8px;
            border-radius: 6px;
            font-weight: 500;
        }
        .badge-default {
            background-color: var(--success-bg);
            color: var(--success-text);
        }
        .address-details {
            font-size: 0.95rem;
            color: var(--text-main);
            margin-bottom: 8px;
        }
        .address-phone {
            font-size: 0.9rem;
            color: var(--text-muted);
            margin-bottom: 15px;
        }
        .address-actions {
            display: flex;
            justify-content: flex-end;
            border-top: 1px solid var(--border-color);
            padding-top: 12px;
        }
        .form-group-checkbox {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 20px;
        }
        .form-group-checkbox input[type="checkbox"] {
            width: 18px;
            height: 18px;
            accent-color: var(--primary);
            cursor: pointer;
        }
        .alert {
            padding: 15px;
            border-radius: 12px;
            margin-bottom: 20px;
            font-size: 0.95rem;
        }
        .alert-danger {
            background-color: var(--danger-bg);
            color: var(--danger-text);
            border: 1px solid rgba(139, 0, 0, 0.1);
        }
        .alert-success {
            background-color: var(--success-bg);
            color: var(--success-text);
            border: 1px solid rgba(30, 78, 44, 0.1);
        }
        @media (max-width: 850px) {
            .address-layout {
                flex-direction: column;
            }
        }
    </style>
</head>
<body>

<div class="container">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 25px; border-bottom: 1px solid var(--border-color); padding-bottom: 15px;">
        <span style="font-size: 0.95rem; color: var(--text-main);">
            Xin chào, <strong><c:out value="${sessionScope.currentUser.fullName}"/></strong> 
            (<span style="color: var(--primary); font-weight: 500; font-size: 0.85rem;"><c:out value="${sessionScope.currentUser.role}"/></span>)
        </span>
        <div>
            <a href="${pageContext.request.contextPath}/users" class="btn btn-secondary" style="padding: 8px 16px; font-size: 0.85rem; box-shadow: none; margin-right: 10px;">
                Quản lý User
            </a>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary" style="padding: 8px 16px; font-size: 0.85rem; box-shadow: none;">
                Đăng xuất
            </a>
        </div>
    </div>

    <h1>Sổ địa chỉ của tôi</h1>
    <p style="color: var(--text-muted); margin-bottom: 20px;">Lưu trữ tối đa 5 địa chỉ giao hàng nhận hàng.</p>

    <!-- Success or Error Alert -->
    <c:if test="${not empty error}">
        <div class="alert alert-danger">
            <c:out value="${error}"/>
        </div>
    </c:if>
    <c:if test="${not empty success}">
        <div class="alert alert-success">
            <c:out value="${success}"/>
        </div>
    </c:if>

    <div class="address-layout">
        
        <!-- Left Side: Add Address Form -->
        <div class="address-form-section">
            <h3 style="margin-bottom: 20px; font-weight: 600;">Thêm địa chỉ mới</h3>
            <form action="${pageContext.request.contextPath}/users/addresses/add" method="post">
                
                <div class="form-group">
                    <label for="receiverName">Tên người nhận <span style="color: red;">*</span></label>
                    <input type="text" class="form-control" id="receiverName" name="receiverName" value="<c:out value="${receiverName}"/>" placeholder="VD: Nguyễn Văn A" required>
                </div>
                
                <div class="form-group">
                    <label for="receiverPhone">Số điện thoại <span style="color: red;">*</span></label>
                    <input type="text" class="form-control" id="receiverPhone" name="receiverPhone" value="<c:out value="${receiverPhone}"/>" placeholder="VD: 0987654321" required>
                </div>
                
                <div class="form-group">
                    <label for="province">Tỉnh/Thành phố <span style="color: red;">*</span></label>
                    <input type="text" class="form-control" id="province" name="province" value="<c:out value="${province}"/>" placeholder="VD: Hà Nội" required>
                </div>
                
                <div class="form-group">
                    <label for="district">Quận/Huyện <span style="color: red;">*</span></label>
                    <input type="text" class="form-control" id="district" name="district" value="<c:out value="${district}"/>" placeholder="VD: Cầu Giấy" required>
                </div>
                
                <div class="form-group">
                    <label for="ward">Phường/Xã <span style="color: red;">*</span></label>
                    <input type="text" class="form-control" id="ward" name="ward" value="<c:out value="${ward}"/>" placeholder="VD: Dịch Vọng" required>
                </div>
                
                <div class="form-group">
                    <label for="streetDetail">Địa chỉ chi tiết (Số nhà, đường...) <span style="color: red;">*</span></label>
                    <input type="text" class="form-control" id="streetDetail" name="streetDetail" value="<c:out value="${streetDetail}"/>" placeholder="VD: Số 12, Ngõ 34" required>
                </div>
                
                <div class="form-group-checkbox">
                    <input type="checkbox" id="isDefault" name="isDefault" value="true" ${isDefault ? 'checked' : ''}>
                    <label for="isDefault" style="margin-bottom: 0; cursor: pointer; font-weight: 500;">Đặt làm địa chỉ mặc định</label>
                </div>
                
                <button type="submit" class="btn" style="width: 100%;">Lưu địa chỉ</button>
            </form>
        </div>
        
        <!-- Right Side: Address List -->
        <div class="address-cards-section">
            <h3 style="font-weight: 600; margin-bottom: 10px;">Danh sách địa chỉ (${addresses.size()}/5)</h3>
            
            <c:forEach var="addr" items="${addresses}">
                <div class="address-card ${addr.isDefault ? 'default-address' : ''}">
                    <div class="address-card-header">
                        <span class="receiver-name"><c:out value="${addr.receiverName}"/></span>
                        <c:if test="${addr.isDefault}">
                            <span class="address-badge badge-default">Mặc định</span>
                        </c:if>
                    </div>
                    <div class="address-details">
                        <c:out value="${addr.streetDetail}"/>, <c:out value="${addr.ward}"/>, <c:out value="${addr.district}"/>, <c:out value="${addr.province}"/>
                    </div>
                    <div class="address-phone">
                        Điện thoại: <strong><c:out value="${addr.receiverPhone}"/></strong>
                    </div>
                    <div class="address-actions">
                        <form action="${pageContext.request.contextPath}/users/addresses/delete/${addr.id}" method="post" style="display:inline">
                            <button type="submit" class="btn-delete" style="padding: 6px 12px; font-size: 0.85rem;" onclick="return confirm('Bạn có chắc chắn muốn xóa địa chỉ này?')">
                                Xóa địa chỉ
                            </button>
                        </form>
                    </div>
                </div>
            </c:forEach>
            
            <c:if test="${empty addresses}">
                <div style="text-align: center; padding: 40px; border: 2px dashed var(--border-color); border-radius: 16px; color: var(--text-muted);">
                    <p style="font-size: 1.1rem; margin-bottom: 10px;">Chưa có địa chỉ nào được thêm</p>
                    <p style="font-size: 0.9rem;">Hãy thêm địa chỉ giao hàng đầu tiên ở form bên trái.</p>
                </div>
            </c:if>
        </div>
        
    </div>
</div>

</body>
</html>
