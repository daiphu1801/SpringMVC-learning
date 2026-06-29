<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="Thanh toán VietQR - Đơn hàng #${order.id}">
<jsp:attribute name="head">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/pages/payment-vietqr.css?v=${appVersion}">
    <script src="${pageContext.request.contextPath}/resources/js/pages/payment-vietqr.js?v=${appVersion}" defer></script>
</jsp:attribute>
<jsp:body>
<div class="payment-container">
    <!-- Cột trái: Thông tin chuyển khoản -->
    <div class="info-card">
        <h2 class="payment-title">
            <span>💵</span> Thông tin chuyển khoản
        </h2>
        <p class="payment-subtitle">
            Vui lòng thực hiện chuyển khoản chính xác thông tin bên dưới để đơn hàng được tự động xác nhận nhanh nhất.
        </p>

        <c:if test="${not empty error}">
            <div class="alert alert-danger mb-3"><c:out value="${error}"/></div>
        </c:if>

        <table class="bank-info-table">
            <tr>
                <td>Ngân hàng</td>
                <td><c:out value="${bankCode}"/></td>
            </tr>
            <tr>
                <td>Số tài khoản</td>
                <td>
                    <span><c:out value="${accountNumber}"/></span>
                    <span class="copy-badge" data-copy="<c:out value='${accountNumber}'/>">Copy</span>
                </td>
            </tr>
            <tr>
                <td>Chủ tài khoản</td>
                <td><c:out value="${accountName}"/></td>
            </tr>
            <tr>
                <td>Số tiền</td>
                <td class="amount-highlight">
                    <fmt:formatNumber value="${order.totalAmount}" type="number" maxFractionDigits="0"/> đ
                    <span class="copy-badge" data-copy="<fmt:formatNumber value="${order.totalAmount}" pattern="###0"/>">Copy</span>
                </td>
            </tr>
            <tr>
                <td>Nội dung chuyển khoản</td>
                <td class="text-primary">
                    <span>DH${order.id}</span>
                    <span class="copy-badge" data-copy="DH${order.id}">Copy</span>
                </td>
            </tr>
        </table>

        <div class="back-link-wrapper">
            <div class="alert-instruction">
                <strong>💡 Lưu ý:</strong> Hệ thống tự động xác nhận đơn hàng sau khi nhận đủ tiền. Trong môi trường giả lập này, bạn vui lòng nhấn nút <strong>"Xác nhận đã thanh toán"</strong> ở bên phải sau khi quét mã QR để chuyển trạng thái.
            </div>
            <a href="${pageContext.request.contextPath}/orders/${order.id}" class="back-link">
                ← Quay lại chi tiết đơn hàng
            </a>
        </div>
    </div>

    <!-- Cột phải: Mã QR và Nút giả lập -->
    <div class="qr-card d-flex flex-col align-center justify-center">
        <h3 class="qr-title">
            Quét mã QR để thanh toán
        </h3>

        <div class="qr-image-wrapper">
            <img class="qr-image"
                 src="https://img.vietqr.io/image/<c:out value='${bankCode}'/>-<c:out value='${accountNumber}'/>-qr_only.jpg?amount=${order.totalAmount}&amp;addInfo=DH${order.id}&amp;accountName=<c:out value='${accountName}'/>"
                 alt="Mã QR thanh toán đơn hàng DH${order.id}" />
        </div>

        <div class="qr-caption">
            Sử dụng ứng dụng Camera hoặc App Ngân hàng hỗ trợ <strong>VietQR / QRPay</strong> để quét.
        </div>

        <form action="${pageContext.request.contextPath}/orders/${order.id}/payment/confirm" method="post" class="w-100">
            <input type="hidden" name="csrfToken" value="${csrfToken}">
            <button type="submit" class="btn btn-full font-bold">
                ✅ Xác nhận đã thanh toán (Giả lập)
            </button>
        </form>
    </div>
</div>
</jsp:body>
</t:layout>
