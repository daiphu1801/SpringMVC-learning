<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="Xác nhận đặt hàng">
<jsp:attribute name="head">
    <style>
        .payment-card {
            border: 2px solid var(--border);
            border-radius: var(--radius-md);
            padding: 18px;
            display: flex;
            align-items: flex-start;
            gap: 15px;
            cursor: pointer;
            transition: all var(--transition-speed);
            background: var(--bg-surface);
        }
        .payment-card:hover {
            border-color: var(--primary);
            background: rgba(79, 70, 229, 0.02);
            transform: translateY(-2px);
            box-shadow: var(--shadow-sm);
        }
        .payment-card.active {
            border-color: var(--primary);
            background: var(--primary-light);
        }
        .payment-card input[type="radio"] {
            margin-top: 5px;
            accent-color: var(--primary);
            width: 18px;
            height: 18px;
            cursor: pointer;
        }
    </style>
    <script>
        function selectPayment(method) {
            document.querySelectorAll('.payment-card').forEach(card => card.classList.remove('active'));
            document.getElementById('card-' + method.toLowerCase()).classList.add('active');
        }
    </script>
</jsp:attribute>
<jsp:body>
<div class="container" style="max-width: 700px;">
    <h1>Xác nhận đặt hàng</h1>
    <p style="color: var(--text-muted); margin-bottom: 30px;">Điền thông tin giao hàng để hoàn tất đơn hàng của bạn.</p>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/orders/place" method="post">

        <div style="background: var(--primary-light); border-radius: var(--radius-md); padding: 20px; margin-bottom: 30px; border: 1px solid rgba(79, 70, 229, 0.1);">
            <h3 style="font-size: 1rem; color: var(--primary); margin-bottom: 15px; font-weight: 700;">📦 Thông tin giao hàng</h3>

            <div class="form-group">
                <label for="receiverName">Tên người nhận <span style="color: red;">*</span></label>
                <input type="text" class="form-control" id="receiverName" name="receiverName"
                       value="${currentUser.fullName}" placeholder="VD: Nguyễn Văn A" required>
            </div>

            <div class="form-group">
                <label for="receiverPhone">Số điện thoại <span style="color: red;">*</span></label>
                <input type="text" class="form-control" id="receiverPhone" name="receiverPhone"
                       value="${currentUser.phone}" placeholder="VD: 0987654321" required>
            </div>

            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px;">
                <div class="form-group">
                    <label for="province">Tỉnh/Thành phố <span style="color: red;">*</span></label>
                    <input type="text" class="form-control" id="province" name="province" placeholder="VD: Hà Nội" required>
                </div>
                <div class="form-group">
                    <label for="district">Quận/Huyện <span style="color: red;">*</span></label>
                    <input type="text" class="form-control" id="district" name="district" placeholder="VD: Cầu Giấy" required>
                </div>
            </div>

            <div class="form-group">
                <label for="ward">Phường/Xã <span style="color: red;">*</span></label>
                <input type="text" class="form-control" id="ward" name="ward" placeholder="VD: Dịch Vọng" required>
            </div>

            <div class="form-group">
                <label for="streetDetail">Địa chỉ chi tiết <span style="color: red;">*</span></label>
                <input type="text" class="form-control" id="streetDetail" name="streetDetail" placeholder="Số nhà, tên đường..." required>
            </div>
        </div>

        <div class="form-group" style="margin-bottom: 25px;">
            <label for="note">Ghi chú đơn hàng (tuỳ chọn)</label>
            <textarea class="form-control" id="note" name="note" rows="3" placeholder="VD: Giao hàng giờ hành chính, gọi trước khi giao..."></textarea>
        </div>

        <!-- PHƯƠNG THỨC THANH TOÁN -->
        <div style="margin-bottom: 35px;">
            <label style="font-weight: 600; margin-bottom: 12px; display: block; color: var(--text-main);">💳 Chọn phương thức thanh toán</label>
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px;">
                <!-- TIỀN MẶT -->
                <label class="payment-card active" id="card-cash">
                    <input type="radio" name="paymentMethod" value="CASH" checked onclick="selectPayment('CASH')">
                    <div>
                        <div style="font-weight: 700; font-size: 0.95rem; color: var(--text-main); margin-bottom: 4px;">💵 Tiền mặt (COD)</div>
                        <div style="font-size: 0.8rem; color: var(--text-muted); line-height: 1.4;">Thanh toán bằng tiền mặt trực tiếp khi nhận hàng.</div>
                    </div>
                </label>

                <!-- VIETQR -->
                <label class="payment-card" id="card-vietqr">
                    <input type="radio" name="paymentMethod" value="VIETQR" onclick="selectPayment('VIETQR')">
                    <div>
                        <div style="font-weight: 700; font-size: 0.95rem; color: var(--text-main); margin-bottom: 4px;">📱 Chuyển khoản VietQR</div>
                        <div style="font-size: 0.8rem; color: var(--text-muted); line-height: 1.4;">Quét mã QR thanh toán nhanh bằng ví/App ngân hàng.</div>
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
