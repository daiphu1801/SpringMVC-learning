<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="Xác nhận đặt hàng">
<jsp:attribute name="head">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/pages/checkout.css?v=${appVersion}">
    <script src="${pageContext.request.contextPath}/resources/js/pages/checkout.js?v=${appVersion}" defer></script>
</jsp:attribute>
<jsp:body>
<div class="container container-sm">
    <h1>Xác nhận đặt hàng</h1>
    <p class="text-muted mb-4">Điền thông tin giao hàng để hoàn tất đơn hàng của bạn.</p>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/orders/place" method="post">
        <input type="hidden" name="csrfToken" value="${csrfToken}">

        <div class="checkout-box">
            <h3 class="checkout-box-title">📦 Thông tin giao hàng</h3>

            <div class="form-group">
                <label for="receiverName">Tên người nhận <span class="required-star">*</span></label>
                <input type="text" class="form-control" id="receiverName" name="receiverName"
                       value="<c:out value='${currentUser.fullName}'/>" placeholder="VD: Nguyễn Văn A" required>
            </div>

            <div class="form-group">
                <label for="receiverPhone">Số điện thoại <span class="required-star">*</span></label>
                <input type="text" class="form-control" id="receiverPhone" name="receiverPhone"
                       value="<c:out value='${currentUser.phone}'/>" placeholder="VD: 0987654321" required>
            </div>

            <div class="grid-2-col">
                <div class="form-group">
                    <label for="province">Tỉnh/Thành phố <span class="required-star">*</span></label>
                    <input type="text" class="form-control" id="province" name="province" placeholder="VD: Hà Nội" required>
                </div>
                <div class="form-group">
                    <label for="district">Quận/Huyện <span class="required-star">*</span></label>
                    <input type="text" class="form-control" id="district" name="district" placeholder="VD: Cầu Giấy" required>
                </div>
            </div>

            <div class="form-group">
                <label for="ward">Phường/Xã <span class="required-star">*</span></label>
                <input type="text" class="form-control" id="ward" name="ward" placeholder="VD: Dịch Vọng" required>
            </div>

            <div class="form-group">
                <label for="streetDetail">Địa chỉ chi tiết <span class="required-star">*</span></label>
                <input type="text" class="form-control" id="streetDetail" name="streetDetail" placeholder="Số nhà, tên đường..." required>
            </div>
        </div>

        <div class="form-group mb-3">
            <label for="note">Ghi chú đơn hàng (tuỳ chọn)</label>
            <textarea class="form-control" id="note" name="note" rows="3" placeholder="VD: Giao hàng giờ hành chính, gọi trước khi giao..."></textarea>
        </div>

        <!-- PHƯƠNG THỨC THANH TOÁN -->
        <div class="mb-4">
            <label class="payment-method-title">💳 Chọn phương thức thanh toán</label>
            <div class="payment-grid">
                <!-- TIỀN MẶT -->
                <label class="payment-card active" id="card-cash">
                    <input type="radio" name="paymentMethod" value="CASH" checked>
                    <div>
                        <div class="payment-card-title">💵 Tiền mặt (COD)</div>
                        <div class="payment-card-desc">Thanh toán bằng tiền mặt trực tiếp khi nhận hàng.</div>
                    </div>
                </label>

                <!-- VIETQR -->
                <label class="payment-card" id="card-vietqr">
                    <input type="radio" name="paymentMethod" value="VIETQR">
                    <div>
                        <div class="payment-card-title">📱 Chuyển khoản VietQR</div>
                        <div class="payment-card-desc">Quét mã QR thanh toán nhanh bằng ví/App ngân hàng.</div>
                    </div>
                </label>
            </div>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn">✅ Đặt hàng ngay</button>
            <a href="${pageContext.request.contextPath}/cart" class="btn btn-secondary">← Quay lại giỏ hàng</a>
        </div>
    </form>
</div>
</jsp:body>
</t:layout>
