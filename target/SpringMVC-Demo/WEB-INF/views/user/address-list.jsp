<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Sổ địa chỉ người dùng">
<jsp:attribute name="head">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/address-list.css">
</jsp:attribute>
<jsp:body>
<div class="container">
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
</jsp:body>
</t:layout>
